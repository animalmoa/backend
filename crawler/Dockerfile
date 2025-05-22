# apt 패키지 포함 플랫폼 옵션 추가
# --platform=linux/amd64
FROM openjdk:17-bullseye

# 1) 기본 도구 + Xvfb + Chrome 의존 라이브러리
RUN apt-get update && apt-get install -y \
    wget curl gnupg ca-certificates \
    xvfb \
    # Chrome이 직접 명시적으로 요구하는 라이브러리만
    libnss3 libxss1 libatk-bridge2.0-0 libgtk-3-0 \
    libasound2 libgconf-2-4 libxi6 \
 && rm -rf /var/lib/apt/lists/*

# 2) Google-Chrome 저장소 추가 & 설치
RUN install -m 0755 -d /etc/apt/keyrings && \
    curl -fsSL https://dl.google.com/linux/linux_signing_key.pub \
      | gpg --dearmor -o /etc/apt/keyrings/google.gpg && \
    echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google.gpg] \
         https://dl.google.com/linux/chrome/deb/ stable main" \
      > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y --no-install-recommends google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

# 3) 애플리케이션 JAR 복사
COPY build/libs/crawler-0.0.1-SNAPSHOT.jar /server.jar

ENV TZ=Asia/Seoul
ENV DISPLAY=:99
ENV SPRING_PROFILES_ACTIVE=deploy

# 4) Xvfb + Spring Boot 실행
ENTRYPOINT [ "sh", "-c", "Xvfb :99 -screen 0 1920x1080x24 & java -Djava.awt.headless=false -jar /server.jar" ]


# 로컬
#  ./gradlew clean build
#  docker buildx build --platform=linux/amd64 -t kawaiibbit/spet-crawler .
#  docker push kawaiibbit/spet-crawler:latest

# 서버(TODO 이미지 private으로 전환해야함)

# docker pull kawaiibbit/spet-crawler:latest
# Wallet파일을 docker에서 써야함
# (로그 같이 출력)
# docker run -it --rm   -p 8080:8080   -v /home/ubuntu/wallet/Wallet_spetDB:/app/wallet/Wallet_spetDB   --name spet-app   kawaiibbit/spet-crawler:latest
# (백그라운드 출력)
# docker run -d --rm   -p 8080:8080   -v /home/ubuntu/wallet/Wallet_spetDB:/app/wallet/Wallet_spetDB   --name spet-app   kawaiibbit/spet-crawler:latest



# 서버 -확인
# 1. 컨테이너 생성만
#  docker create --name spet-check kawaiibbit/spet-crawler:latest
#
#  # 2. jar 꺼내기
#  docker cp spet-check:/server.jar .
#
#  # 3. yml 확인
#  unzip -p server.jar BOOT-INF/classes/application-deploy-db.yml
#
#  # 4. 정리
#  docker rm spet-check