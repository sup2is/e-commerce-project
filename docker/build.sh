#!/bin/bash

baseDir=/root/e-commerce-project

mvn -f $baseDir/order-service/pom.xml clean package docker:build
mvn -f $baseDir/product-service/pom.xml clean package docker:build
mvn -f $baseDir/authentication-service/pom.xml clean package docker:build
mvn -f $baseDir/member-service/pom.xml clean package docker:build
mvn -f $baseDir/eureka-server/pom.xml clean package docker:build
mvn -f $baseDir/api-gateway/pom.xml clean package docker:build

