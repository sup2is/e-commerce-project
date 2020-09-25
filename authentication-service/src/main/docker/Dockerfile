FROM openjdk:8-jdk-alpine
RUN apk update && apk upgrade && apk add netcat-openbsd
RUN mkdir -p /usr/local/auth-service
ADD ./*.jar /usr/local/auth-service/app.jar
ADD ./run.sh run.sh
RUN chmod +x ./run.sh
CMD ./run.sh

