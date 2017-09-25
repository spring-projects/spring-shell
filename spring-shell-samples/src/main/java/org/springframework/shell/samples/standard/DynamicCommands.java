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

package org.springframework.shell.samples.standard;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

/**
 * Showcases dynamic command availability.
 *
 * @author Eric Bottard
 */
@ShellComponent
public class DynamicCommands {

    private boolean connected;

    private boolean authenticated;

    public Availability authenticateAvailability() {
        return connected ? Availability.available() : Availability.unavailable("you are not connected");
    }

    @ShellMethod(value = "Authenticate with the system", group = "Dynamic Commands")
    public void authenticate(String credentials) {
        authenticated = "sesame".equals(credentials);
    }

    @ShellMethod(value = "Connect to the system", group = "Dynamic Commands")
    public void connect() {
        connected = true;
    }

    @ShellMethod(value = "Disconnect from the system", group = "Dynamic Commands")
    public void disconnect() {
        connected = false;
    }

    @ShellMethod(value = "Blow Everything up", group = "Dynamic Commands")
    @ShellMethodAvailability("dangerousAvailability")
    public String blowUp() {
        return "Boom!";
    }

    private Availability dangerousAvailability() {
        return connected && authenticated ? Availability.available() : Availability.unavailable("you failed to authenticate. Try 'sesame'.");
    }
}
