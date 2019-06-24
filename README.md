# DuplicateFinderService
Spring boot restful service 

I have created the API as per the requirements.
To run:

git clone https://github.com/ppartha2018/DuplicateFinderService.git

cd DuplicateFinderService

mvn install -DskipTests

java -jar target/DuplicateFinder-0.0.1-SNAPSHOT.jar

mvn test

Application available on: http://localhost:8080/DuplicateFinderService

Note:
1. "skipTest" while mvn install since I have written all the unit test as part of web integration tests, I realize that it could included
in the regular build pipline by using @RestClient Integration test plugin with Spring Boot. For now, please invoke tests separately after starting
the application.

2. Docker Containerization is work in progress. Issues with working with docker on Windows 10 Home. But maven plugin and docker file are created
properly and can be used containerize from a different machine.
