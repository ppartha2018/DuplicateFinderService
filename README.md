# DuplicateFinderService
A spring boot microservice application that takes in large payload of JSON in the request of the form:
 [      {"productId": 1, "skuId": a10},
        {"productId": 2, "skuId": a10},
        {"productId": 3, "skuId": a11},
        {"productId": 4, "skuId": a12}
]
and find the duplicate products with same skuId.
[
  {"productId": 1, "skuId": a10},
  {"productId": 2, "skuId": a10}
] 

The services uses Java8 streaming and functional approach to map and combine the keys.

To run:

git clone https://github.com/ppartha2018/DuplicateFinderService.git
cd DuplicateFinderService
mvn install -DskipTests
java -jar target/DuplicateFinder-0.0.1-SNAPSHOT.jar
mvn test

Application available on: http://localhost:8080/DuplicateFinderService

Update:
Docker Containerzation is achieved.
I have push the container to my hub here:
https://hub.docker.com/r/ppartha2018/homework

I also pulled the docker image in my aws ec2 instance and hosted here:
http://ec2-54-91-42-115.compute-1.amazonaws.com:8080/DuplicateFinderService

Note:
1. "skipTests" while mvn install since I have written all the unit test as part of web integration tests, I realize that it could be included in the regular build pipline by using @RestClient Integration test plugin with Spring Boot. For now, please invoke tests separately after starting the application.
