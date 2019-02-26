Copyright 2015-2019

Ioannis Antoniadis

<giannis.antoniadis@issel.ee.auth.gr>

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
* Clone the repo insight-master and import the project in Eclipse
* Create JAVA_HOME environment variable to point at the jdk1.8.0 installation directory and make sure that jdk1.8.0 is reflected on the Eclipse/Maven configuration. Consult the following page:
https://stackoverflow.com/questions/19655184/no-compiler-is-provided-in-this-environment-perhaps-you-are-running-on-a-jre-ra
* Update and build the project using Maven inside Eclipse (right click on project, first Maven -> Update Project..., then Run As -> Maven install)
* Create a runtime instance of Tomcat 7.0 in Eclipse
* Start Elasticsearch 1.5.0
* The offline module can be run as an independent java app by running the offline.Controller class
* The online module must be run on Tomcat 7.0 (right click on project then Run As -> Run on Server). The front-end page should automatically appear in the Eclipse browser environment.

Notes:
* There are references to local property files inside the code in the below classes: offline.WebParser, offline.IndexManager, online.ContentAnalyzer, online.DocumentAnalysisHandler, online.ElasticManager, online.ParagraphAnalysisHandler, online.ScoringManager and online.TopicAnalyzer. These references should be modified in order to point to the correct directories of the local machine where the code is executed.
* The offline module should be run first to create the Elasticsearch index mappings and to populate the index with content (documents) based on the queries present in queries.txt file.
* Keep in mind that the offline module execution requires an active subscription to Microsoft Azure services since it uses the Bing News Search Engine to collect a list of URLs according to the contents of the queries.txt file. There is currently an active subscription key  inside WebParser class which should work out-of-the-box.
* After the offline module's execution is completed, the online module should be initiated as explained above. The application UI should be available on: http://localhost:8080/insight
