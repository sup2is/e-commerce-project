FROM openjdk:8-jdk-alpine
RUN apk update && apk upgrade && apk add netcat-openbsd
RUN mkdir -p /usr/local/api-gateway
ADD ./*.jar /usr/local/api-gateway/app.jar
ADD ./run.sh run.sh
RUN chmod +x ./run.sh
CMD ./run.sh

