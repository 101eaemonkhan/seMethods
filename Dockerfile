
FROM openjdk:latest
COPY ./out/seMethods.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "seMethods.jar", "db:8080", "8000"]