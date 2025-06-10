FROM openjdk:21
RUN curl -fsSLO https://get.docker.com/builds/Linux/x86_64/docker-17.04.0-ce.tgz \
    && tar xzvf docker-17.04.0-ce.tgz \
    && mv docker/docker /usr/local/bin \
    && rm -r docker docker-17.04.0-ce.tgz
COPY target/satrect-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]