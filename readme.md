# JVM-Based Web Profiling Tools with Javassist

This project is about a profiling tools with [bytecode instrumentation](https://docs.oracle.com/javase/7/docs/api/java/lang/instrument/Instrumentation.html). With bytecode instrumentation you can alter the content of a JAR/WAR file by modifying the loaded class files in the JVM.

## Features

Measure the memory and CPU usage of running Java web application

![running apps](https://raw.githubusercontent.com/yafithekid/project-y/master/images/metinv.jpg)

Measure the response time of HTTP request

![response time](https://raw.githubusercontent.com/yafithekid/project-y/master/images/responsetime.jpg)

Know the execution time and object size returned from a method

![method invocation chain](https://raw.githubusercontent.com/yafithekid/project-y/master/images/chain.png)
 
## How to build or clean

To clean all projects, use `gradle clean`. To build all projects, use `gradle build`.

To build a specific module, use `gradle :module_name:build`. For example `gradle :agent:build`, or `gradle :collector:build`.

## Module descriptions

The profiling tools contains five modules:

1. `agent` - Used for modification of bytecode.
2. `collector` - Used for receiving data from profiling result.
3. `commons` - Commonly used module. Currently it only contains configuration parser.
4. `db` - Used for saving data into database (use mongodb)
5. `visualizer` - Used for visualizing the data

## How to run

1. [First setup - Instrumenting a JAR](https://github.com/yafithekid/project-y/wiki/first-setup)
2. [Running the hardware monitoring thread]()
3. [Send the data to collector]()
4. [Save the data with MongoDB]()
5. [Instrumenting a web application with Tomcat Servlet Container]()
6. [Visualize the result with visualizer]()

## Acknowledgement

Thanks to Riza Satria Perdana, S.T., M.T. and Yudistira Dwi Wardhana Asnar, S.T. Ph.D as my supervisor of thesis project
