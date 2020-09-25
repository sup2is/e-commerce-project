FROM openjdk:8-jdk-alpine
RUN apk update && apk upgrade && apk add netcat-openbsd
RUN mkdir -p /usr/local/member-service
ADD ./*.jar /usr/local/member-service/app.jar
ADD ./run.sh run.sh
RUN chmod +x ./run.sh
CMD ./run.sh

