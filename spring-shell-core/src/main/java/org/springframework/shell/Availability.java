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

package org.springframework.shell;

import org.springframework.util.Assert;

/**
 * Indicates whether or not a command is currently available. When not available, provides
 * a reason.
 *
 * @author Eric Bottard
 */
public class Availability {

    private final String reason;

    private Availability(String reason) {
        this.reason = reason;
    }

    public static Availability available() {
        return new Availability(null);
    }

    public static Availability unavailable(String reason) {
        Assert.notNull(reason, "Reason for not being available must be provided");
        return new Availability(reason);
    }

    public boolean isAvailable() {
        return reason == null;
    }

    public String getReason() {
        return reason;
    }
}
