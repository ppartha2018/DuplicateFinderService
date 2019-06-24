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
1. "skipTests" while mvn install since I have written all the unit test as part of web integration tests, I realize that it could be included in the regular build pipline by using @RestClient Integration test plugin with Spring Boot. For now, please invoke tests separately after startingthe application.

2. Docker Containerization is a work in progress. I ran into issues with working with docker on my Windows 10 Home machine. But maven plugin and docker file are created properly and can be used with few changes in future.
