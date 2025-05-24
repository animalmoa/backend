FROM openjdk:17

COPY build/libs/api-0.0.1-SNAPSHOT.jar server.jar

ENV TZ=Asia/Seoul
ENV SPRING_PROFILES_ACTIVE=deploy

ENTRYPOINT ["java", "-jar", "/server.jar"]


# 로컬
#  ./gradlew clean build
#  docker buildx build --platform=linux/amd64 -t kawaiibbit/spet-web .
#  docker push kawaiibbit/spet-web:latest

# 서버(TODO 이미지 private으로 전환해야함)

# docker pull kawaiibbit/spet-web:latest
# Wallet파일을 docker에서 써야함
# (로그 같이 출력)
# docker run -it --rm   -p 8080:8080   -v /home/ubuntu/wallet/Wallet_spetDB:/app/wallet/Wallet_spetDB   --name spet-app   kawaiibbit/spet-web:latest
# (백그라운드 출력)
# docker run -d --rm   -p 8080:8080   -v /home/ubuntu/wallet/Wallet_spetDB:/app/wallet/Wallet_spetDB   --name spet-app   kawaiibbit/spet-web:latest



# 서버 -확인
# 1. 컨테이너 생성만
#  docker create --name spet-check kawaiibbit/spet-web:latest
#
#  # 2. jar 꺼내기
#  docker cp spet-check:/server.jar .
#
#  # 3. yml 확인
#  unzip -p server.jar BOOT-INF/classes/application-deploy-db.yml
#
#  # 4. 정리
#  docker rm spet-check