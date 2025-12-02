### About

This is a sample project demonstrating the use of Spring Shell to create a simple command-line application for [Spring Petclinic](https://github.com/spring-projects/spring-petclinic).

### Building the Project

To build the project, navigate to the project's root directory and run the following command:

```bash
./mvnw clean install
```

### Running the Application

To run the application, use the following command:

```bash
./mvnw -pl org.springframework.shell:spring-shell-sample-petclinic exec:java -Dexec.mainClass=org.springframework.shell.samples.petclinic.SpringShellApplication
```

You should see a prompt where you can enter commands.