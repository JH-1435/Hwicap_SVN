# Jenkins LTS 이미지를 기반으로 사용
FROM jenkins/jenkins:lts

# 루트 사용자 권한으로 전환
USER root

# ssh 디렉터리 생성
RUN mkdir -p /root/.ssh

#  컨테이너로 SSH 키 복사
COPY ./.ssh-keys/id_rsa /root/.ssh/id_rsa
COPY ./.ssh-keys/id_rsa.pub /root/.ssh/id_rsa.pub
COPY ./.ssh-keys/authorized_keys /root/.ssh/authorized_keys

# SSH 키 권한 설정
RUN chmod 700 /root/.ssh && \
    chmod 600 /root/.ssh/id_rsa && \
    chmod 644 /root/.ssh/id_rsa.pub && \
    chmod 600 /root/.ssh/authorized_keys

# Docker 소켓을 공유할 수 있도록 설정
VOLUME /var/run/docker.sock:/var/run/docker.sock


# Jenkins 실행
# CMD ["java", "-jar", "/usr/share/jenkins/jenkins.war"]