# --------------------------------------------------------
# 1. 빌드 단계 (Gradle 8.5 & JDK 21 사용)
# --------------------------------------------------------
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# 소스 코드 복사
COPY . .

# gradlew 실행 권한 부여 및 줄바꿈 문자 변환 (윈도우 호환)
RUN sed -i 's/\r$//' gradlew
RUN chmod +x gradlew

# 빌드 실행 (테스트 건너뜀)
RUN ./gradlew clean build -x test --no-daemon

# --------------------------------------------------------
# 2. 실행 단계 (JDK 21 실행 환경)
# * 주의: 빌드 버전과 실행 버전은 맞춰야 합니다.
# --------------------------------------------------------
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# 빌드 단계(build)에서 생성된 jar 파일만 가져오기
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
