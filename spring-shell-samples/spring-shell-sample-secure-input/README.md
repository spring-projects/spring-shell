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
./mvnw -pl org.springframework.shell:spring-shell-sample-secure-input spring-boot:run
```

You should see a prompt where you can use the `change-password` command to securely input and change a password.