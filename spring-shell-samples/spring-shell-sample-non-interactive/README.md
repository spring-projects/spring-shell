### About

This is a sample project demonstrating the use of Spring Shell to create a simple non-interactive command-line application based on Spring Boot.

### Building the Project

To build the project, navigate to the project's root directory and run the following command:

```bash
./mvnw clean install
```

### Running the Application

To run the application and invoke the `hi` command in a non-interactive way, use the following command:

```bash
./mvnw -pl org.springframework.shell:spring-shell-sample-non-interactive exec:java -Dexec.mainClass=org.springframework.shell.samples.noninteractive.SpringShellApplication -Dexec.args=hi
```

You should see the result of the `hi` command printed in the console output.
The application will then exit automatically after executing the command.