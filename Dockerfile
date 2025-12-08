# 1. 빌드 단계 (Gradle)
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# 소스 코드 복사
COPY . .

# gradlew 실행 권한 부여 및 빌드 (테스트 건너뜀)
RUN chmod +x gradlew
RUN ./gradlew clean build -x test --no-daemon

# 2. 실행 단계 (Eclipse Temurin JDK 사용 - 안정적)
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# 빌드 단계에서 생성된 jar 파일을 실행 단계로 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
