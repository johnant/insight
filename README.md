Copyright 2015

Ioannis Antoniadis

<antoniii@auth.gr>

Aristotle University of Thessaloniki, Greece

Department of Electrical and Computer Engineering

# insight
insight is a Question Answering system that incorporates topic modelling techniques and user interaction to provide specific answers to user queries. It expands the functionality of typical search engines by using the thematic information of the underlying sets of documents, from which the answers are extracted.

## Functionality
The system is designed as a web application and it consists of two separate modules.
### offline module
The offline module is responsible for populating the document corpus with web documents, according to a set of url links or queries. This module is executed independently and no user interaction is required.

### online module
The online module is responsible for handling user requests by semantic and topic analysis of the documents corpus. It searches for documents with the appropriate thematic information requested by the user and returns text segments as final answers.

## Technologies
The application consists of a front-end mechanism written in HTML and Javascript using the AngularJS framework. The front-end mechanism communicates RESTfully with a back-end mechanism that is written in Eclipse JAVA and contains the core logic of the system. Apache Tomcat 7.0 server is used for the application resources configuration. Elasticsearch 1.5.0 server is used for documents storage and various statistical metrics calculations.

## Configuration
There is a number of .properties files containing parameters related to the Elasticsearch server configuration and topic analysis configuration. There is also a queries.txt file which is used by the offline module for the initial population of the Elasticsearch server with web documents, from which answers are extracted by components of the online module.

## Run the application
Required Software: 
* Java SE Development Kit 8
* Eclipse
* Apache Maven
* Tomcat 7.0
* Elasticsearch 1.5.0

Steps:
* Create JAVA_HOME environment variable to point at the jdk1.8.0 installation directory
* Clone the repo insight-master and import the project in Eclipse
* Upgrade the project using Maven inside Eclipse
* Create a runtime instance of Tomcat 7.0 in Eclipse
* Start Elasticsearch 1.5.0
* The offline module can be run as an independent java app
* The online module must be run on Tomcat 7.0 (right click on project then Run As -> Run on Server

The application UI is available on:
http://localhost:8080/insight
