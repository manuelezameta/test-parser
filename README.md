# test-parser
This a small project which parse a log file according to given parameters

# Requirements
- Java 8
- Gradle
- MySQL 8

# Notes
- Creation script and queries are in folder **test-parser/db**

- In order to get the jar file following steps should be performed:
	
	1. 	Edit this file **test-parser/parser/src/main/java/com/ef/utils/ConnectionFactory.java** <br/>
		Putting there your database credentials accordantly.
	
	2.	Run following script in parser folder
		```
		$ ./gradlew clean build
		```
	3. Go to **test-parser/parser/build/libs** and you will find the jar file **parser.jar**
