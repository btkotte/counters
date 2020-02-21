FROM openjdk:11-jdk-oracle
VOLUME /usr/local/
COPY target/*exec.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]