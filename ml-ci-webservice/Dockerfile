FROM openjdk:8-jre-alpine

WORKDIR /home/app

ADD ./target/*.jar ./app.jar

RUN mkdir -p /home/app/trained-models

EXPOSE 8080

CMD java $JAVA_OPTS -Dspring.profiles.active=$PROFILE -Dserver.port=$PORT -jar app.jar