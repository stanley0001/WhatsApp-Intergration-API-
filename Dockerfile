FROM fabric8/java-alpine-openjdk11-jre
MAINTAINER stan
COPY target/*.jar /starw.jar
# set the startup command to execute the jar
ENTRYPOINT ["java","-jar","/starw.jar"]
