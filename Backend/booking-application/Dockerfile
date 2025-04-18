FROM amazoncorretto:21-alpine-jdk

LABEL Maintainer="Saksham"

COPY target/booking-application-0.0.1-SNAPSHOT.jar  /opt/booking-application/booking-application.jar

WORKDIR /opt/booking-application

EXPOSE 8082

CMD ["sh", "-c", "java -XX:+UseContainerSupport -XX:+UseZGC -XX:+ZGenerational -jar booking-application.jar"]
