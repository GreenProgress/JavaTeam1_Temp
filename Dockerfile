# 1. 빌드 단계
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# 소스 코드 복사
COPY . .

# 🔥 [핵심 수정] 윈도우 줄바꿈(CRLF) -> 리눅스(LF)로 변환 및 실행 권한 부여
RUN sed -i 's/\r$//' gradlew
RUN chmod +x gradlew

# 빌드 실행 (테스트 건너뜀)
RUN ./gradlew clean build -x test --no-daemon

# 2. 실행 단계 (Eclipse Temurin JDK 사용)
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# 빌드된 jar 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
