# 1. 빌드 단계 (Gradle을 이용해 jar 파일 생성)
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# 소스 코드 복사
COPY . .

# gradlew 실행 권한 부여 및 빌드 (테스트는 건너뜀)
RUN chmod +x gradlew
RUN ./gradlew clean build -x test --no-daemon

# 2. 실행 단계 (가벼운 Java 환경에서 실행)
FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌드 단계에서 생성된 jar 파일을 실행 단계로 복사
# (파일명이 버전마다 달라도 *.jar로 퉁쳐서 app.jar로 이름 변경)
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출 (Render 기본 포트)
EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]