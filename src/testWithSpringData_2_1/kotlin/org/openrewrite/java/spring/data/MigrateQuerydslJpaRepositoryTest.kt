/*
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.spring.data

import org.junit.jupiter.api.Test
import org.openrewrite.Recipe
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest

class MigrateQuerydslJpaRepositoryTest : JavaRecipeTest {
    override val parser: JavaParser
        get() = JavaParser.fromJavaVersion()
            .classpath("javax.persistence", "spring-data-jpa", "spring-data-commons")
            .build()

    override val recipe: Recipe
        get() = MigrateQuerydslJpaRepository()


    @Test
    fun newClasses() = assertChanged(
        before = """
            import javax.persistence.EntityManager;
            import org.springframework.data.jpa.repository.support.JpaEntityInformation;
            import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
            import org.springframework.data.querydsl.SimpleEntityPathResolver;

            class Test {
                JpaEntityInformation<String, Long> entityInformation;
                EntityManager entityManager;
                SimpleEntityPathResolver resolver;
                QuerydslJpaRepository<String, Long> declWith2Args = new QuerydslJpaRepository(entityInformation, entityManager);
                QuerydslJpaRepository<String, Long> declWith3Args = new QuerydslJpaRepository(entityInformation, entityManager, resolver);
            }
        """,
        after = """
            import javax.persistence.EntityManager;
            import org.springframework.data.jpa.repository.support.JpaEntityInformation;
            import org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor;
            import org.springframework.data.querydsl.SimpleEntityPathResolver;

            class Test {
                JpaEntityInformation<String, Long> entityInformation;
                EntityManager entityManager;
                SimpleEntityPathResolver resolver;
                QuerydslJpaPredicateExecutor<String> declWith2Args = new QuerydslJpaPredicateExecutor(entityInformation, entityManager, SimpleEntityPathResolver.INSTANCE, null);
                QuerydslJpaPredicateExecutor<String> declWith3Args = new QuerydslJpaPredicateExecutor(entityInformation, entityManager, resolver, null);
            }
        """
    )

    @Test
    fun methodReturnType() = assertChanged(
        before = """
            import javax.persistence.EntityManager;
            import org.springframework.data.jpa.repository.support.JpaEntityInformation;
            import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
            import org.springframework.data.querydsl.SimpleEntityPathResolver;

            class Test {
                public QuerydslJpaRepository<String, Long> method() {
                    return null;
                }
            }
        """,
        after = """
            import javax.persistence.EntityManager;
            import org.springframework.data.jpa.repository.support.JpaEntityInformation;
            import org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor;
            import org.springframework.data.querydsl.SimpleEntityPathResolver;

            class Test {
                public QuerydslJpaPredicateExecutor<String> method() {
                    return null;
                }
            }
        """
    )
}
