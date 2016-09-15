#!/bin/bash
java -jar target/moviebuster-rent.jar db migrate rentconf.yml
java -jar target/moviebuster-rent.jar server rentconf.yml
