FROM openjdk:17

COPY api/build/libs/application-0.0.1-SNAPSHOT.jar server.jar


ENV TZ=Asia/Seoul
ENV SPRING_PROFILES_ACTIVE=deploy

ENTRYPOINT ["java","-jar", \
"/server.jar"]
