#!/bin/bash
java -jar target/moviebuster-movie.jar db migrate movieconf.yml
java -jar target/moviebuster-movie.jar server movieconf.yml
