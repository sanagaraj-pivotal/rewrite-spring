/*
 * Copyright 2022 the original author or authors.
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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openrewrite.Recipe
import org.openrewrite.Result
import org.openrewrite.java.JavaRecipeTest
import org.openrewrite.yaml.ChangePropertyKey
import org.openrewrite.yaml.YamlParser
import java.nio.file.Paths


/**
 * @author Sandeep Nagaraj
 */
class MigrateElasticSearchPropertiesTest : JavaRecipeTest {

    override val recipe: Recipe
        get() = ChangePropertyKey("data.elasticsearch",
        "elasticsearch",
        true,
        null)

    @Test
    fun movePropertyTestSingle() {
        val result = runRecipe(
            """
                spring:
                  data:
                    elasticsearch:
                      client:
                        reactive:
                          connection-timeout: 10
            """.trimIndent()
        )
        assertThat(result).hasSize(1)
        assertThat(result[0].after!!.printAll()).isEqualTo("""
                spring:
                  elasticsearch:
                    connection-timeout: 10
            """.trimIndent())
    }

    private fun runRecipe(inputYaml: String): List<Result> {
        val applicationYaml = YamlParser().parse(inputYaml)
            .map { it.withSourcePath(Paths.get("src/main/resources/application.yml")) }

        return recipe.run(applicationYaml)
    }
}
