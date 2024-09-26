FROM amazoncorretto:17
FROM maven

COPY . .

RUN mvn clean package

ENTRYPOINT ["java","-jar","target/housefix.jar"]