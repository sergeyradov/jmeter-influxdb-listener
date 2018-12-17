# Jmeter-DI-Backend-listener

## Introduction

This component adds functionality to send samplers metrics to InfluxDB. This will help Digital Insight to live monitor the test using Grafana dashboard.

## Required Components

1. InfluxDB (To store test metrics)
2. Grafana (Visualize the metrics in required graphs)


## jar Dependencies Required

* influxdb-java:2.14
* ApacheJMeter_components.jar
* ApacheJMeter_core.jar

## Jmeter Target

* Jmeter version 3.3
* Java 8

## Installation Instructions

* Download the source code from the Gitlab.
* Just do a mvn clean install (Git bash is required)
* Jar will be generated under the target directory. Copy the Jar to \<Jmeter Installed Directory\>/lib/ext/ for DI Jmeter \<Jmeter Installed Directory\>/di/plugins
*


## References

Below are the references which guided to build this plugin.

* https://jmeter.apache.org/api/org/apache/jmeter/visualizers/backend/BackendListenerClient.html
* https://jmeter.apache.org/api/org/apache/jmeter/visualizers/backend/influxdb/InfluxdbBackendListenerClient.html
* https://www.programcreek.com/java-api-examples/?api=org.influxdb.dto.Point
* https://www.baeldung.com/java-influxdb
* https://www.geekality.net/2013/09/12/maven-package-runtime-dependencies-in-lib-folder-inside-packaged-jar/

