#!/bin/sh

java $JVM_OPTIONS -Djava.security.egd=file:/dev/./urandom\
    -Dserver.port=$SERVER_PORT\
    -Deureka.client.serviceUrl.defaultZone=$EUREKA_SERVER_URL\
    -Dspring.redis.host=$REDIS_SERVER_URL\
    -Dspring.redis.port=$REDIS_SERVER_PORT\
    -Dspring.zipkin.base-url=$ZIPKIN_SERVER_URL\
    -Dspring.profiles.active=$PROFILE -jar /usr/local/member-service/app.jar
