# JVM-Based Web Profiling Tools with Javassist

This project is about a profiling tools with [bytecode instrumentation](https://docs.oracle.com/javase/7/docs/api/java/lang/instrument/Instrumentation.html).
 
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

1. [First setup]()
2. [Instrumenting a JAR]()
3. [Send the data to collector]()
4. [Save the data with MongoDB]()
5. [Instrumenting a web application with Tomcat Servlet Container]()
6. [Visualize the result with visualizer]()

