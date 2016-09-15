#!/bin/bash
java -jar target/moviebuster-bonus.jar db migrate bonusconf.yml
java -jar target/moviebuster-bonus.jar server bonusconf.yml
