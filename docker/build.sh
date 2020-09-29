#!/bin/bash

baseDir=${PWD%/*}

mvn -f $baseDir/order-service/pom.xml clean package docker:build -DpushImage -Dspring.profiles.active=dev-integration
mvn -f $baseDir/product-service/pom.xml clean package docker:build -DpushImage -Dspring.profiles.active=dev-integration
mvn -f $baseDir/authentication-service/pom.xml clean package docker:build -DpushImage -Dspring.profiles.active=dev-integration
mvn -f $baseDir/member-service/pom.xml clean package docker:build -DpushImage -Dspring.profiles.active=dev-integration
#mvn -f $baseDir/eureka-server/pom.xml clean package docker:build -DpushImage
#mvn -f $baseDir/api-gateway/pom.xml clean package docker:build -DpushImage
