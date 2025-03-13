FROM openjdk:17-jdk-slim

#컨테이터 내 작업 디렉터리를 /app으로 설정
WORKDIR /app

#가장 최신의 JAR파일을 app.jar로 복사
COPY build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]