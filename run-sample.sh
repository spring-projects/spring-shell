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
    java -jar spring-shell-samples/spring-shell-sample-hello-world/target/hello-world.jar
    ;;
  2)
    echo ""
    echo "Running: non-interactive"
    java -jar spring-shell-samples/spring-shell-sample-non-interactive/target/non-interactive.jar hi
    ;;
  3)
    echo ""
    echo "Running: petclinic"
    java -jar spring-shell-samples/spring-shell-sample-petclinic/target/petclinic.jar
    ;;
  4)
    echo ""
    echo "Running: secure-input"
    java -jar spring-shell-samples/spring-shell-sample-secure-input/target/secure-input.jar
    ;;
  5)
    echo ""
    echo "Running: spring-boot"
    java -jar spring-shell-samples/spring-shell-sample-spring-boot/target/hello-world-boot.jar
    ;;
  *)
    echo "Invalid choice: '$choice'. Please enter a number between 1 and 5." >&2
    exit 1
    ;;
esac
