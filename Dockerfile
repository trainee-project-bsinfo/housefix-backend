FROM amazoncorretto:17
FROM maven

COPY . .

RUN mvn package

ENTRYPOINT ["java","-jar","target/housefix.jar"]