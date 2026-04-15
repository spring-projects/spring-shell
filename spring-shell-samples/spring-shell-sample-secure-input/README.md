### About

This is a sample project demonstrating the use of Spring Shell commands to read secure input from the user, such as passwords.

### Building the Project

To build the project, navigate to the project's root directory and run the following command:

```bash
./mvnw clean install
```

### Running the Application

To run the application, use the following command:

```bash
java -jar spring-shell-samples/spring-shell-sample-secure-input/target/secure-input.jar
```

You should see a prompt where you can login with `auth login`. This command will ask you to enter a username and password securely.

The sample application uses an in-memory user store with a single user `foo` with the password `bar`. You can use these credentials to log in.

After logging in, you can use the `auth change-password` command to securely change the password.

This sample also shows how to use the `AvailabilityProvider` API to restrict access to certain commands based on the user's authentication status.
For example, the `auth change-password` command is only available when the user is authenticated.