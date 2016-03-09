# Skripsi

## How to build

mvn clean compile package

## How to run collector

java -jar collector\target\collector-1.0.0.jar

## How to run

java -javaagent:agent\target\agent-1.0.0.jar -jar example\target\example-1.0.0.jar

## How to run collector (from gradle result)

java -jar collector\build\libs\collector.jar

## How to run

java -javaagent:agent\build\libs\agent.jar -jar example\build\libs\example.jar
