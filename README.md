The Spring Shell project provides an extensible interactive shell enviornment with a simple Spring based plugin model.

This is essentially a place holder README file until this project gets its sealegs.

# Building

Spring Shell currently uses Maven as its build system.  The build been done using version 3.0.3 (r1075438; 2011-02-28 12:31:09-0500) and the plan is to switch to gradle.

To build the shell that contains a hint, exit and 'hw' (hello world) command, run

     mvn package
     
# Running

After buldingn, run the shell 

    cd target
    java -jar spring-shell-1.0.0.CI-SNAPSHOT.jar
     
# Contributing

Here are some ways for you to get involved in the community:

* Get involved with the Spring community on the Spring Community Forums.  Please help out on the [forum](http://forum.springsource.org/forumdisplay.php?f=80) by responding to questions and joining the debate.
Please add 'Hadoop' as a prefix to easily spot the post topic.
* Create [JIRA](https://jira.springframework.org/browse/SHL) tickets for bugs and new features and comment and vote on the ones that you are interested in.  
* Watch for upcoming articles on Spring by [subscribing](http://www.springsource.org/node/feed) to springframework.org

Github is for social coding: if you want to write code, we encourage contributions through pull requests from [forks of this repository](http://help.github.com/forking/). If you want to contribute code this way, please reference a tracker ticket as well covering the specific issue you are addressing. Before we accept a non-trivial patch or pull request we will need you to sign the [contributor's agreement](https://support.springsource.com/spring_committer_signup).  Signing the contributor's agreement does not grant anyone commit rights to the main repository, but it does mean that we can accept your contributions, and you will get an author credit if we do.  Active contributors might be asked to join the core team, and given the ability to merge pull requests.
