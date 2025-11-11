FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle* settings.gradle* ./
COPY src ./src
RUN chmod +x gradlew
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
ENV TZ=Asia/Seoul
ENV SPRING_PROFILES_ACTIVE=prod
COPY --from=builder /app/build/libs/*.jar app.jar
COPY src/main/resources/firebase/serviceAccountKey.json /app/firebase/serviceAccountKey.json
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
