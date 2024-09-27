FROM amazoncorretto:17
FROM maven

COPY . .

RUN chmod +x wait-for-it.sh
RUN mvn clean package

ENTRYPOINT ["./wait-for-it.sh", "mariadb:3306", "--", "java","-jar","target/housefix.jar"]