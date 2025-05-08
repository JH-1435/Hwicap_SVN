# Base image: Tomcat + JDK 8
FROM tomcat:8.5-jdk8-openjdk
MAINTAINER ParkJunHyuck <cjstk1035@naver.com>

# 타임존 세팅 (환경변수 설정)
ENV DEBIAN_FRONTEND=noninteractive

# 루트 사용자 권한으로 전환
USER root

# /app 디렉토리 생성
RUN mkdir -p /app/config

# Tomcat 작업 디렉토리 설정
WORKDIR /usr/local/tomcat

# envsubst 설치
RUN apt-get update && apt-get install -y gettext-base unzip

# 환경 변수 설정 (AWS RDS용) — 실제 run-time에 유동적으로 바꿀 수 있음
# Git에 배포해야하므로 보안상 비밀번호는 없는 비밀번호로 설정
ENV DB_URL=jdbc:mysql://database-1.c1ge0ckaqe04.ap-northeast-2.rds.amazonaws.com:3306/hwicap \
    DB_USERNAME=admin \
    DB_PASSWORD=1234 \
    DB_DRIVER=com.mysql.cj.jdbc.Driver


# (/conf/server.xml이 대신해주기에 불필요 주석처리)server.xml 포트 수정: 8080 → 8090
# RUN sed -i 's/port="8080"/port="8090"/' /usr/local/tomcat/conf/server.xml

# war 복사 후 미리 압축 해제
COPY jh-1.0.0-BUILD-SNAPSHOT.war /tmp/ROOT.war
RUN mkdir -p webapps/ROOT \
 && unzip /tmp/ROOT.war -d webapps/ROOT

# (보안상 이유로 배포 SSH 서버에 직접 수동으로 생성함.)HTTPS 설정을 위해 keystore, server.xml 사전 준비
# COPY ./keystore.jks /usr/local/tomcat/ssl/keystore.jks
# COPY ./conf/server.xml /usr/local/tomcat/conf/server.xml

# 템플릿 복사
COPY ROOT-Template.xml /tmp/ROOT-Template.xml

# Tomcat 디렉토리 생성
RUN mkdir -p /usr/local/tomcat/conf/Catalina/localhost


# 포트 노출
EXPOSE 8090

# envsubst는 빌드 시점이 아닌 run 시점에 실행되도록
ENTRYPOINT ["/bin/bash", "-c", "envsubst < /tmp/ROOT-Template.xml > /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml && catalina.sh run"]