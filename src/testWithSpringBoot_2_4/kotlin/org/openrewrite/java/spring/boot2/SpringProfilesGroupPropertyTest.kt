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
package org.openrewrite.java.spring.boot2

import org.junit.jupiter.api.Test
import org.openrewrite.config.Environment
import org.openrewrite.test.RecipeSpec
import org.openrewrite.test.RewriteTest
import org.openrewrite.yaml.Assertions.yaml


class SpringProfilesGroupPropertyTest : RewriteTest {
    override fun defaults(spec: RecipeSpec) {
        spec.recipe(Environment.builder().scanRuntimeClasspath().build()
            .activateRecipes("org.openrewrite.java.spring.boot2.SpringBootProperties_2_4"))
    }

    @Test
    fun `does not corrupt spring profiles group config in yaml with indentation`() = rewriteRun(
        yaml("""
            spring:
              profiles:
                group:
                  local: local-db, local-auth
        """)
    )

    @Test
    fun `does not corrupt spring profiles group config in yaml with dot separation`() = rewriteRun(
        yaml("""
            spring.profiles.group.local: local-db, local-auth
        """)
    )
}