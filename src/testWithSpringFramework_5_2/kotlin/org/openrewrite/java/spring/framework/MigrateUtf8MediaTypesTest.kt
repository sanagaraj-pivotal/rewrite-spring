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
package org.openrewrite.java.spring.framework

import org.junit.jupiter.api.Test
import org.openrewrite.Recipe
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest

@Suppress("MethodMayBeStatic")
class MigrateUtf8MediaTypesTest : JavaRecipeTest {
    override val parser: JavaParser
        get() = JavaParser.fromJavaVersion()
            .classpath("spring-core", "spring-web")
            .build()

    override val recipe: Recipe
        get() = MigrateUtf8MediaTypes()

    @Test
    fun updateFieldAccess() = assertChanged(
        before = """
            import org.springframework.http.MediaType;
            
            class A {
                void method() {
                    MediaType valueA = MediaType.APPLICATION_JSON_UTF8;
                    String valueB = MediaType.APPLICATION_JSON_UTF8_VALUE;
                    MediaType valueC = MediaType.APPLICATION_PROBLEM_JSON_UTF8;
                    String valueD = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE;
                }
            }
        """,
        after = """
            import org.springframework.http.MediaType;
            
            class A {
                void method() {
                    MediaType valueA = MediaType.APPLICATION_JSON;
                    String valueB = MediaType.APPLICATION_JSON_VALUE;
                    MediaType valueC = MediaType.APPLICATION_PROBLEM_JSON;
                    String valueD = MediaType.APPLICATION_PROBLEM_JSON_VALUE;
                }
            }
        """
    )

    @Test
    fun updateStaticConstant() = assertChanged(
        before = """
            import org.springframework.http.MediaType;
            import static org.springframework.http.MediaType.*;
            
            class A {
                void method() {
                    MediaType valueA = APPLICATION_JSON_UTF8;
                    String valueB = APPLICATION_JSON_UTF8_VALUE;
                    MediaType valueC = APPLICATION_PROBLEM_JSON_UTF8;
                    String valueD = APPLICATION_PROBLEM_JSON_UTF8_VALUE;
                }
            }
        """,
        after = """
            import org.springframework.http.MediaType;
            import static org.springframework.http.MediaType.*;
            
            class A {
                void method() {
                    MediaType valueA = APPLICATION_JSON;
                    String valueB = APPLICATION_JSON_VALUE;
                    MediaType valueC = APPLICATION_PROBLEM_JSON;
                    String valueD = APPLICATION_PROBLEM_JSON_VALUE;
                }
            }
        """
    )

    @Test
    fun updateFullyQualifiedTarget() = assertChanged(
        before = """
            import org.springframework.http.MediaType;
            class A {
                void method() {
                    MediaType valueA = org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
                    String valueB = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
                    MediaType valueC = org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_UTF8;
                    String valueD = org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE;
                }
            }
        """,
        after = """
            import org.springframework.http.MediaType;
            class A {
                void method() {
                    MediaType valueA = org.springframework.http.MediaType.APPLICATION_JSON;
                    String valueB = org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
                    MediaType valueC = org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
                    String valueD = org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
                }
            }
        """
    )
}
