FROM openjdk:8
ADD moviebuster-apigw.tar.gz /usr/src
WORKDIR /usr/src/moviebuster-apigw
CMD java -jar target/moviebuster-apigw.jar server busterconf.yml
EXPOSE 8080
