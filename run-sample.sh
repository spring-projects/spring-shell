#!/usr/bin/env bash

set -euo pipefail

echo "Spring Shell Samples"
echo "===================="
echo ""
echo "Select a sample to run:"
echo ""
echo "  1) hello-world       — Simple greeting shell application"
echo "  2) non-interactive   — Non-interactive sample (runs 'hi' and exits)"
echo "  3) petclinic         — Spring PetClinic shell application"
echo "  4) secure-input      — Secure input / password prompt sample"
echo "  5) spring-boot       — Spring Boot-based shell application"
echo ""
read -rp "Enter your choice [1-5]: " choice

case "$choice" in
  1)
    echo ""
    echo "Running: hello-world"
    ./mvnw -pl org.springframework.shell:spring-shell-sample-hello-world \
      exec:java -Dexec.mainClass=org.springframework.shell.samples.helloworld.SpringShellApplication
    ;;
  2)
    echo ""
    echo "Running: non-interactive"
    ./mvnw -pl org.springframework.shell:spring-shell-sample-non-interactive \
      spring-boot:run -Dspring-boot.run.arguments=hi
    ;;
  3)
    echo ""
    echo "Running: petclinic"
    ./mvnw -pl org.springframework.shell:spring-shell-sample-petclinic \
      exec:java -Dexec.mainClass=org.springframework.shell.samples.petclinic.SpringShellApplication
    ;;
  4)
    echo ""
    echo "Running: secure-input"
    ./mvnw -pl org.springframework.shell:spring-shell-sample-secure-input \
      spring-boot:run
    ;;
  5)
    echo ""
    echo "Running: spring-boot"
    ./mvnw -pl org.springframework.shell:spring-shell-sample-spring-boot \
      spring-boot:run
    ;;
  *)
    echo "Invalid choice: '$choice'. Please enter a number between 1 and 5." >&2
    exit 1
    ;;
esac
