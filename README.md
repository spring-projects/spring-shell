The Spring Shell project provides an extensible interactive shell enviornment with a simple Spring based plugin model. 

# Docs

You can find out more details from the [user documentation](http://static.springsource.org/spring-shell/docs/current/reference/) or by browsing the [javadocs](http://static.springsource.org/spring-shell/docs/current/api/). If you have ideas about how to improve or extend the scope, please feel free to contribute.


# Artifacts

~~~~~ xml
<dependency>
  <groupId>org.springframework.shell</groupId>
  <artifactId>spring-shell</artifactId>
  <version>${version}</version>
</dependency> 

<!-- used for nightly builds -->
<repository>
  <id>spring-maven-snapshot</id>
  <snapshots><enabled>true</enabled></snapshots>
  <name>Springframework Maven SNAPSHOT Repository</name>
  <url>http://maven.springframework.org/snapshot</url>
</repository> 

<!-- used for milestone/rc releases -->
<repository>
  <id>spring-maven-milestone</id>
  <name>Springframework Maven Milestone Repository</name>
  <url>http://maven.springframework.org/milestone</url>
</repository>  
~~~~~

* Gradle: 

~~~~~ groovy
repositories {
   maven { url "http://repo.springsource.org/libs-milestone" }
   maven { url "http://repo.springsource.org/libs-snapshot" }
}

dependencies {
   compile "org.springframework.shell:spring-shell:${version}"
}
~~~~~

The latest milestone is _1.0.0.M1_

The latest nightly is _1.0.0.BUILD-SNAPSHOT_

# Building
Spring Shell is built with Gradle. To build Spring Shell, run

     ./gradlew 
     
# Running Example

    cd samples/helloworld
    ../../gradlew installApp
    cd build/install/helloworld/bin
    helloworld
    
     
# Contributing

Here are some ways for you to get involved in the community:

* Get involved with the Spring community on the Spring Community Forums.  Please help out on the [forum](http://forum.springsource.org/forumdisplay.php?f=80) by responding to questions and joining the debate.
Please add 'Hadoop' as a prefix to easily spot the post topic.
* Create [JIRA](https://jira.springframework.org/browse/SHL) tickets for bugs and new features and comment and vote on the ones that you are interested in.  
Github is for social coding: if you want to write code, we encourage contributions through pull requests from [forks of this repository](http://help.github.com/forking/). If you want to contribute code this way, please reference a tracker ticket as well covering the specific issue you are addressing. Before we accept a non-trivial patch or pull request we will need you to sign the [contributor's agreement](https://support.springsource.com/spring_committer_signup).  Signing the contributor's agreement does not grant anyone commit rights to the main repository, but it does mean that we can accept your contributions, and you will get an author credit if we do.  Active contributors might be asked to join the core team, and given the ability to merge pull requests.
