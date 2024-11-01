FROM openjdk:17-slim
RUN apt update -y && apt install -y python3 nodejs gcc g++ && apt clean
COPY build/libs/code-compile-service-0.0.1-SNAPSHOT.jar app.jar
ENV TZ Asia/Seoul
ENTRYPOINT ["java", "-jar", "app.jar"]