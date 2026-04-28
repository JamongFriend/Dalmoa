# 1. Build stage: 빌드를 위한 환경 (JDK 21)
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Gradle 빌드에 필요한 파일들 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 권한 부여 및 빌드 (테스트는 제외하여 속도 향상)
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test

# 2. Run stage: 실행을 위한 가벼운 환경 (JRE 21)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 빌드 스테이지에서 생성된 jar 파일만 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 컨테이너 실행 시 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
