Maven 3:

1.Build the project
  $>mvn package
  
2.run spring shell
  $>java -jar target/helloworld-1.1.0.RELEASE.jar
  

  
Gradle:

1.Build and install the project
  $>./gradlew installApp
  
2.run spring shell
  $>./build/install/helloworld/bin/helloworld

or
  $>./gradlew -q run

