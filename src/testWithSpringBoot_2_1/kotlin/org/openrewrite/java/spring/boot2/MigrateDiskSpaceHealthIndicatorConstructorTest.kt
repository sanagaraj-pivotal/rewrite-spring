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
import org.openrewrite.Recipe
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest

class MigrateDiskSpaceHealthIndicatorConstructorTest : JavaRecipeTest {
    override val parser: JavaParser
        get() = JavaParser.fromJavaVersion()
            .classpath("spring-boot-actuator", "spring-core")
            .logCompilationWarningsAndErrors(true)
            .build()

    override val recipe: Recipe
        get() = MigrateDiskSpaceHealthIndicatorConstructor()

    @Test
    fun doNotChangeCurrentApi() = assertUnchanged(
        before = """
            import org.springframework.boot.actuate.system.DiskSpaceHealthIndicator;
            import org.springframework.util.unit.DataSize;
            
            class Test {
                void method() {
                    DiskSpaceHealthIndicator indicator = new DiskSpaceHealthIndicator(null, DataSize.ofBytes(1));
                }
            }
        """
    )

    @Test
    fun changeDeprecatedConstructor() = assertChanged(
        before = """
            import org.springframework.boot.actuate.system.DiskSpaceHealthIndicator;
            
            class Test {
                void method() {
                    DiskSpaceHealthIndicator literal = new DiskSpaceHealthIndicator(null, 1L);
                    DiskSpaceHealthIndicator methodInvocation = new DiskSpaceHealthIndicator(null, value());
                    Long arg = 10L;
                    DiskSpaceHealthIndicator variable = new DiskSpaceHealthIndicator(null, arg);
                }
                long value() {
                    return 10L;
                }
            }
        """,
        after = """
            import org.springframework.boot.actuate.system.DiskSpaceHealthIndicator;
            import org.springframework.util.unit.DataSize;

            class Test {
                void method() {
                    DiskSpaceHealthIndicator literal = new DiskSpaceHealthIndicator(null, DataSize.ofBytes(1L));
                    DiskSpaceHealthIndicator methodInvocation = new DiskSpaceHealthIndicator(null, DataSize.ofBytes(value()));
                    Long arg = 10L;
                    DiskSpaceHealthIndicator variable = new DiskSpaceHealthIndicator(null, DataSize.ofBytes(arg));
                }
                long value() {
                    return 10L;
                }
            }
        """
    )
}
