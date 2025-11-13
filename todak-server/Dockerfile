# ===============================
# 🏗 1단계: Builder (Gradle로 JAR 빌드)
# ===============================
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Gradle 캐시 최적화
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

# 권한 부여 (Windows 환경에서는 이게 중요!)
RUN chmod +x gradlew

# Gradle 빌드 (테스트는 생략해서 빌드 속도 향상)
RUN ./gradlew clean build -x test

# ===============================
# 🚀 2단계: Runtime (경량 런타임 이미지)
# ===============================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# 빌드된 jar 복사
COPY --from=builder /app/build/libs/todak-server-0.0.1-SNAPSHOT.jar app.jar

# Firebase 키 파일 복사
COPY src/main/resources/firebase/serviceAccountKey.json /app/firebase/serviceAccountKey.json

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=secret
ENV FIREBASE_CONFIG_PATH=/app/firebase/serviceAccountKey.json

# JVM 최적화 옵션 (적은 메모리 환경에서도 안정적으로 실행)
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

# 포트 오픈
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
