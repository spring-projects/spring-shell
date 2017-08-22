/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell.standard;

import java.lang.annotation.*;

/**
 * Used to customize the name of the method used to indicate availability of a command.
 *
 * In the absence of this annotation, the dynamic availability of a command method named {@literal foo}
 * is discovered via method {@literal fooAvailability}.
 * <ul>
 * <li>If this annotation is added to the {@literal foo}
 * method, then its {@link #value()} should be the name of an availability method (in place of
 * {@literal fooAvailability()}) that returns {@link org.springframework.shell.Availability}.</li>
 * <li>If placed on a method that returns {@link org.springframework.shell.Availability} and takes no argument,
 * then the {@link #value()} of this annotation should be the <em>command names</em> (or aliases) of the
 * commands this availability indicator is for. The special value of {@literal "*"} (the default) matches
 * all commands implemented in the current class.</li>
 * </ul>
 *
 * @author Eric Bottard
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ShellMethodAvailability {

    /**
     * @return  the name of the availability method for this command method, or if placed on an availability method, the names of
     * the commands it is for.
     */
    String[] value() default "*";
}
