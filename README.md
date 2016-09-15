# CASUMOBUSTER #
Simple video rental system


### Functionalities ###
The system provides:

- Movie CRUD
- Bonus Insert
- Rent and return movies

### Entities ###
There are base entities managed in the system:

- Movie: the movies
- Bonus: the bonus points earned by the customers 
- RentMovement: the rent/return movements

All the managed entities have the common columns:

- Id
- Insert Date
- Update Date


The entities supposed to be implemented out of the system are:

- Customer: the person who rent movies and who earn bonus points
- User: the identity whom can access the system (can be different from customers)


### Movie ###
A single row in the movie table represents a movie to be rented.
The movie columns are:

- Title
- Movie Type: NEW, REGULAR or OLD

### Bonus ###
A single row in the movie table represents a single action on earning bonus points.
The bonus columns are:

- CustomerID
- Bonus Quantity

The total bonus earned by a customer is the sum of the bonus quantities grouped by customer ID. The customer ID is intended to be the primary key of the (outside managed) customer table. If in the future there will be a way to consume this bonus, a new row can be added with negative quantity.

### Rent Movement ###
A single row in the movement table represents a movement of rent or return.
The movement columns are:

- MovieID: the key of the movie rented
- CustomerID: the key of the customer
- Movie Type: the type of the movie rented
- Movement Type: RENT or RETURN
- Movement Date
- Rent days: the days declared at renting time
- Extra days: the eventual extra days to be payed (present only in RETURN movements)
- RentID: the reference to the related RENT movement ID (present only in RETURN movements)

The movie type is copied at rent time from the movie object. This allow to update the movie type on the movie master, and not to change the agreement with the user who already rented the movie (+ is better for performance).

### Customer ###
No assumptions was made on customer data structure. Only a numeric id is needed.

### User ###
The only assumptions made on user data structure are:

- Name
- Role

### System Structure ###
The service is based on 3 microservices that implements the functionalities on the single entities:

- moviebuster-bonus
- moviebuster-movie
- moviebuster-rent

They are connected to a mysql instance, to store the data. All the requests arrive to the microservices by the messaging system (rabbitmq). The entry point for the system is the api-gateway (moviebuster-apigw), that listens to the http request and translates it on the messaging system.

### Authentication ###
The authentication of the requests on the api gateway is based on jwt token. The system delegated to generate this token, was considered outside of the scope of this task.
The api gateway performs simple check (on role) on the token received.

### Project Structure ###
The project has different repositories:

- moviebuster-apigw: the api gateway service
- moviebuster-bonus: the bonus ms
- moviebuster-movie: the movie ms
- moviebuster-rent: the movement ms
- moviebuster-common: a common repository of code, used by the previous repos
- moviebuster-docker: the repository of the dockerfiles needed to start the system
- moviebuster-test: a simple integration test for the whole system

### Environment ###
The service was developed and tested on this configuration:

- Mac OS X Yosemite 10.10.5
- Java 1.8.0_101 64bit
- Maven 3.3.9
- Docker 1.12.0-a
- Node 5.7

### Setup  ###

Start all the services in docker:
```
cd moviebuster-docker/moviebuster-rabbit/
./run.sh

cd ../moviebuster-mysql/
./run.sh

cd ../moviebuster-apigw/
./setup.sh

cd ../moviebuster-bonus/
./setup.sh

cd ../moviebuster-movie/
./setup.sh

cd ../moviebuster-rent/
./setup.sh
```

Or, if you are brave enough, start all the services at once (you have to be sure that everything is going to be all right - NOT suggested for the first start):
```

cd moviebuster-docker/
./buildall.sh
```

### Note on the Build ###
It's common to see a IOException stacktrace while building: it's just one of the test case that raises it on purpose to the test error management.

### Integration Test ###

Install the test dependency and request a token:
```
cd moviebuster-test/
npm install
./gettoken.sh
```
Copy the token and use it to start the test
```
APIGW_KEY=<token-received> npm test
```
### Token Creation ###
For testing purposes, there is a command on the API gateway that creates a simple jwt token, to be used with service invocation. 
Request a token by
```
java -jar moviebuster-apigw/target/moviebuster-apigw.jar jwtcreate /moviebuster-apigw/jwtconf.yml
```
### Postman Use ###
In the *moviebuster-common* is located the postman collection file *Casumobuster.postman_collection.json*.
To try moviebuster just import it in Postman, create a new token (watch Token Creation) and use it in a header:
```
Authentication: bearer <token>
```
In this file you can find sample requests for every resource.

### Manual Service Start ###
You can start each service without docker:
```
mvn clean install package
java -jar target/<servicename>.jar server <configname>.jar
```
or directly
```
mvn clean install exec:java
```
### (Personal) Considerations on Technologies and Method ###
For this work I decided to try some framework / tools that I haven't used before. In fact I don't know if my approach was the right approach, or if I followed some best practices. I just tried to apply what I learnt from other systems / tools on this context.
By the way, the lack of someone to have comparisons and suggestions, maybe led me to wrong or too complex solutions. I hope to have the possibility to discuss this work with someone that can point out errors and fails.

### What is missing ###

- Customer System
- Authentication System, to generate the token used by the api-gw
- Swagger integration for the api-gateway
- 100% Test Coverage
- Metrics
- Centralized log system
- Container Orchestration, to understand if a container is healthy or if needs to be scaled
- Load balancing for the api-gateway (the microservices are r-robin balanced by the queue)
- MySQL datafile on external volume, actually mysql connects to a local datafile, so is volatile.
- MySQL tuning and sharding configuration, to eventually manage each entity on a single (sharded) db
- Benchmarking
- A sort of frontend :D

### Conclusions ###
I really enjoyed this challenge. 
