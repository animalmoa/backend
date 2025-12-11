# apt 패키지 포함 플랫폼 옵션 추가
FROM openjdk:17-bullseye

ARG CHROMIUM_VER=120.0.6099.224-1~deb11u1

RUN apt-get update && apt-get install -y --no-install-recommends \
      xvfb ca-certificates curl gnupg wget \
      libnss3 libxss1 libatk-bridge2.0-0 libgtk-3-0 \
      libasound2 libxi6 fonts-liberation libgbm1 xdg-utils && \
    apt-get install -y --no-install-recommends \
      chromium=${CHROMIUM_VER} chromium-driver=${CHROMIUM_VER} && \
    mkdir -p /app && \
    ln -sf /usr/bin/chromium /usr/bin/google-chrome && \
    ln -sf /usr/bin/chromedriver /app/chromedriver && \
    apt-mark hold chromium chromium-driver && \
    rm -rf /var/lib/apt/lists/*


# 3) 애플리케이션 JAR 복사
COPY build/libs/crawler-0.0.1-SNAPSHOT.jar /server.jar

ENV TZ=Asia/Seoul
ENV DISPLAY=:99
ENV SPRING_PROFILES_ACTIVE=deploy

# 4) Xvfb + Spring Boot 실행
ENTRYPOINT [ "sh", "-c", "Xvfb :99 -screen 0 1920x1080x24 & java -Djava.awt.headless=false -jar /server.jar" ]


# 로컬
#  ./gradlew clean build -x test (headless 안 될 시 )
#  docker buildx build --platform=linux/amd64 -t kawaiibbit/spet-scraper .
#  docker push kawaiibbit/spet-scraper:latest

# 서버(TODO 이미지 private으로 전환해야함)

# docker pull kawaiibbit/spet-scraper:latest
# Wallet파일을 docker에서 써야함

# docker rm -f #{container Id} 또는 /spet-scraper

# (로그 같이 출력 - 줄 바꿈 없는 것도 꼭 같이 수정할것)
# docker run -d --rm -p 8080:8080 \
# -v /usr/local/bin/chromedriver:/app/chromedriver \
# -v /home/ubuntu/wallet/Wallet_spetDB:/app/wallet/Wallet_spetDB \
# -v /home/ubuntu/.oci:/root/.oci --name spet-app kawaiibbit/spet-scraper:latest

# (터미널 복붙용 줄 바꿈 없는 버전)
# docker run -d --rm -p 8080:8080 -v /usr/local/bin/chromedriver:/app/chromedriver -v /home/ubuntu/wallet/Wallet_spetDB:/app/wallet/Wallet_spetDB -v /home/ubuntu/.oci:/root/.oci --name spet-scraper kawaiibbit/spet-scraper:latest


# Chrome Driver
# (삭제 및 설치)
# wget -qO chromedriver.zip "https://storage.googleapis.com/chrome-for-testing-public/136.0.7103.113/linux64/chromedriver-linux64.zip" \
  #&& unzip -o chromedriver.zip \
  #&& mv chromedriver-linux64/chromedriver chromedriver \
  #&& chmod +x chromedriver \
  #&& sudo mv chromedriver /usr/local/bin/chromedriver \
  #&& rm chromedriver.zip
#chmod +x /usr/local/bin/chromedriver 필요!!!!!



# 서버 -확인
# 1. 컨테이너 생성만
#  docker create --name spet-check kawaiibbit/spet-scraper:latest
#
#  # 2. jar 꺼내기
#  docker cp spet-check:/server.jar .
#
#  # 3. yml 확인
#  unzip -p server.jar BOOT-INF/classes/application-deploy-db.yml
#
#  # 4. 정리
#  docker rm spet-check