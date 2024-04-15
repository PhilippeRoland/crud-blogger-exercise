#Build stage
FROM gradle:8.7.0-jdk21-alpine AS build
WORKDIR /usr/app/
COPY . .
RUN ls
RUN gradle clean build

# Package stage
FROM gradle:8.7.0-jdk21-alpine
ENV JAR_NAME=blogger-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME .
EXPOSE 8080
ENTRYPOINT exec java -jar $APP_HOME/build/libs/$JAR_NAME