FROM chlgytjd/csimage
COPY build/libs/codeCompileService-0.0.1-SNAPSHOT.jar app.jar
ENV TZ Asia/Seoul
ENTRYPOINT ["java", "-jar", "app.jar"]