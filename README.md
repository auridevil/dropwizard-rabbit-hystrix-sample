Dropwizard-rabbit-hystrix-sample
==========
*A simple movie rental system developed to try the mix of Dropwizard, RabbitMQ and Hystrix (+MySQL + Docker)*

*[DropWizard](http://www.dropwizard.io/1.0.0/docs/) for rest service
*[RabbitMQ](https://www.rabbitmq.com/) as a messaging system
*[Hystrix](https://github.com/Netflix/Hystrix) for fault tollerance

Authentication
---
The authentication of the requests on the api gateway is based on jwt token. The system delegated to generate this token, was considered outside of the scope of this task.
The api gateway performs simple check (on role) on the token received.

Project Structure
---
The project has different repositories:

- moviebuster-apigw: the api gateway service
- moviebuster-bonus: the bonus microservice
- moviebuster-movie: the movie microservice
- moviebuster-rent: the movement microservice
- moviebuster-common: a common repository of code, used by the previous repos
- moviebuster-docker: the repository of the dockerfiles needed to start the system
- moviebuster-test: a simple integration test for the whole system

Setup
---
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

Test
---
Install the test dependency and request a token:
```
cd moviebuster-test/
npm install
./gettoken.sh
```
Copy the token and use it to start the test
```
export APIGW_KEY=<token-received>
npm test
```

Manual Service Start
---
You can start each service without docker:
```
mvn clean install package
java -jar target/<servicename>.jar server <configname>
```
or directly
```
mvn clean install exec:java
```

Info
---
More detail about the project soon.

Contributions
---
Feel free to contribute: open an issue or create a pull request if you know any better approach or if you have any suggestions.

