FROM java:8-jdk-alpine
MAINTAINER Toshiaki Maki <makingx at gmail.com>

EXPOSE 8080
WORKDIR /opt/uaa

ADD output/app.jar /opt/uaa
ENTRYPOINT ["java", "-jar", "app.jar", "-Djava.security.egd=file:/dev/./urandom"]