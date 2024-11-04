FROM maven AS builder

COPY . .

RUN mvn clean package -DskipTests

FROM amazoncorretto:17

ARG DB_HOSTNAME_PORT

ARG JAR_PATH=/app/housefix.jar
ARG SCRIPT_PATH=/app/wait-for-it.sh

COPY --from=builder /target/housefix.jar $JAR_PATH
COPY wait-for-it.sh $SCRIPT_PATH

RUN chmod +x $SCRIPT_PATH

ENV SCRIPT_PATH=$SCRIPT_PATH
ENV DB_H_P=$DB_HOSTNAME_PORT
ENV JAR_PATH=$JAR_PATH

ENTRYPOINT ["sh", "-c", "${SCRIPT_PATH} ${DB_H_P} -t 60 && java -jar ${JAR_PATH}"]