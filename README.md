Spring Shell is an interactive shell that can be easily extended with commands using a Spring based programming model.  The latest release is 1.0.0.RELEASE

# Useful links

* [User documeantation](http://static.springsource.org/spring-shell/docs/current/reference/)
* [Issue Tracker](https://jira.springsource.org/browse/SHL)

More information can be found on the [project home page](http://www.springsource.org/spring-shell)

If you have ideas about how to improve or extend the scope, please feel free to contribute.

# Artifacts

~~~~~ xml
<!-- used for Spring GA Releases releases, artifacts are also in maven central -->
<repository>
 <-- Release -->
 <id>spring-release</id>
 <name>Spring Maven RELEASE Repository</name>
 <url>http://repo.springframework.org/release</url>
</repository>

<-- libs-release for JLine fork, artifacts may not be in maven central -->
<repository>
 <id>libs-release</id>
 <name>Spring Maven libs-release Repository</name>
 <url>http://repo.springframework.org/libs-release</url>
</repository>

<dependency>
 <groupId>org.springframework.data</groupId>
 <artifactId>spring-shell</artifactId>
 <version>1.0.0.RELEASE</version>
</dependency> 

<!-- used for nightly builds -->
<repository>
 <-- Snapshots -->
 <id>spring-snapshot</id>
 <name>Spring Maven SNAPSHOT Repository</name>
 <url>http://repo.springframework.org/libs-snapshot</url>
</repository>

<dependency>
  <groupId>org.springframework.shell</groupId>
  <artifactId>spring-shell</artifactId>
  <version>1.0.1.SNAPSHOT</version>
</dependency> 

~~~~~

* Gradle: 

~~~~~ groovy
repositories {
   maven { url "http://repo.springsource.org/lib-release" }
}

dependencies {
   compile "org.springframework.shell:spring-shell:1.0.0.RELEASE"
}
~~~~~


# Building
Spring Shell is built with Gradle. To build Spring Shell, run

     ./gradlew 
     
# Running Example

    cd samples/helloworld
    ./gradlew installApp
    cd build/install/helloworld/bin
    helloworld
    
     
# Contributing

Here are some ways for you to get involved in the community:

* Get involved with the Spring community on the Spring Community Forums.  Please help out on the [forum](http://forum.springsource.org/forumdisplay.php?90-Shell) by responding to questions and joining the debate.
* Create [JIRA](https://jira.springframework.org/browse/SHL) tickets for bugs and new features and comment and vote on the ones that you are interested in.  
Github is for social coding: if you want to write code, we encourage contributions through pull requests from [forks of this repository](http://help.github.com/forking/). If you want to contribute code this way, please reference a JIRA tracker ticket covering the specific issue you are addressing. Before we accept a non-trivial patch or pull request we will need you to sign the [contributor's agreement](https://support.springsource.com/spring_committer_signup).  Signing the contributor's agreement does not grant anyone commit rights to the main repository, but it does mean that we can accept your contributions, and you will get an author credit if we do.  Active contributors might be asked to join the core team, and given the ability to merge pull requests.
