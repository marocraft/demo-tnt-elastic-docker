FROM openjdk:8
VOLUME /tmp
ADD target/demo-tnt.jar demo-tnt.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar demo-tnt.jar -Djava.security.egd=file:/dev/./urandom