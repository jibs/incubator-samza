---
layout: page
title: Logging
---

Samza uses [SLF4J](http://www.slf4j.org/) for all of its logging. By default, only slf4j-api is used, so you must add an SLF4J runtime dependency to your Samza packages for whichever underlying logging platform you wish to use.

### Log4j

The [hello-samza](/startup/hello-samza/0.7.0) project shows how to use [log4j](http://logging.apache.org/log4j/1.2/) with Samza. To turn on log4j logging, you just need to make sure slf4j-log4j12 is in your Samza TaskRunner's classpath. In Maven, this can be done by adding the following dependency to your Samza package project.

    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-log4j12</artifactId>
      <scope>runtime</scope>
      <version>1.6.2</version>
    </dependency>

If you're not using Maven, just make sure that slf4j-log4j12 ends up in your Samza package's lib directory.

#### log4j.xml

Samza's [run-class.sh](packaging.html) script will automatically set the following setting if log4j.xml exists in your [Samza package's](packaging.html) lib directory.

    -Dlog4j.configuration=file:$base_dir/lib/log4j.xml

<!-- TODO add notes showing how to use task.opts for gc logging
#### task.opts
-->

### Log Directory

Samza will look for the _SAMZA_\__LOG_\__DIR_ environment variable when it executes. If this variable is defined, all logs will be written to this directory. If the environment variable is empty, or not defined, then Samza will use /tmp. This environment variable can also be referenced inside log4j.xml files.

### Garbage Collection Logging

Samza's will automatically set the following garbage collection logging setting, and will output it to _$SAMZA_\__LOG_\__DIR_/gc.log.

    -XX:+PrintGCDateStamps -Xloggc:$SAMZA_LOG_DIR/gc.log

#### Rotation

In older versions of Java, it is impossible to have GC logs roll over based on time or size without the use of a secondary tool. This means that your GC logs will never be deleted until a Samza job ceases to run. As of [Java 6 Update 34](http://www.oracle.com/technetwork/java/javase/2col/6u34-bugfixes-1733379.html), and [Java 7 Update 2](http://www.oracle.com/technetwork/java/javase/7u2-relnotes-1394228.html), [new GC command line switches](http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6941923) have been added to support this functionality. If you are using a version of Java that supports GC log rotation, it's highly recommended that you turn it on.

### YARN

When a Samza job executes on a YARN grid, the _$SAMZA_\__LOG_\__DIR_ environment variable will point to a directory that is secured such that only the user executing the Samza job can read and write to it, if YARN is [securely configured](http://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-common/ClusterSetup.html).

#### STDOUT

YARN pipes all STDOUT and STDERR output to logs/stdout and logs/stderr, respectively. These files are never rotated.

## [Application Master &raquo;](../yarn/application-master.html)
