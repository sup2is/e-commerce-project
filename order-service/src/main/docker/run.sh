#!/bin/sh


echo "********************************************************"
echo "Waiting for the eureka-server to start on port $EUREKA_SERVER_PORT"
echo "********************************************************"
while ! `nc -z eureka-server $EUREKA_SERVER_PORT`; do sleep 3; done
echo "******* Eureka Server has started"

echo "********************************************************"
echo "Waiting for the redis-server to start on port $REDIS_SERVER_PORT"
echo "********************************************************"
while ! `nc -z redis-server $REDIS_SERVER_PORT`; do sleep 3; done
echo "******** Redis Server has started "

echo "********************************************************"
echo "Waiting for the zipkin-server to start on port $ZIPKIN_SERVER_PORT"
echo "********************************************************"
while ! `nc -z zipkin-server $ZIPKIN_SERVER_PORT`; do sleep 3; done
echo "*******  Zipkin Server has started"

echo "********************************************************"
echo "Waiting for the db to start on port $DATASOURCE_PORT"
echo "********************************************************"
while ! `nc -z db $DATASOURCE_PORT`; do sleep 3; done
echo "*******  db Server has started"


java -jar /usr/local/order-service/app.jar \
  --spring.profiles.active=$PROFILE \
  --server.port=$SERVER_PORT

