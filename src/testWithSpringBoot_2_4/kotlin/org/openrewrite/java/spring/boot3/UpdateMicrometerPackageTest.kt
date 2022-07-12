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
package org.openrewrite.java.spring.boot3

import org.junit.jupiter.api.Test
import org.openrewrite.Recipe
import org.openrewrite.java.ChangePackage
import org.openrewrite.java.ChangeType
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest

/**
 * @author Alex Boyko
 */
class UpdateMicrometerPackageTest : JavaRecipeTest {

    override val parser: JavaParser
        get() = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("spring-boot")
            .build()

    override val recipe: Recipe
        get() = ChangeType(
            "io.micrometer.core.instrument.binder.Abc",
            "io.micrometer.binder.Abc",
            null,
        )
    @Test
    fun topLevelTypeAnnotation() = assertChanged(
        dependsOn = arrayOf("""
            package io.micrometer.core.instrument.binder;
             
            public class Abc {
            }
        """.trimIndent()
        ),
        before = """
            package a;
            import io.micrometer.core.instrument.binder.*;
            
            class A {
                Abc method() {
                    return null;
                }
            }
        """.trimIndent(),
        after = """
            package a;
          
            import io.micrometer.binder.Abc;
      
            class A {
                Abc method() {
                    return null;
                }
            }
        """.trimIndent()
    )
}
