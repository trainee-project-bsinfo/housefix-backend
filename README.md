# Housefix

___

## Environment Variables
Create .env file in root dir of project and fill it out
> see [.env.example](./.env.example)
> - MYSQL_ROOT_PASSWORD
> - MYSQL_DATABASE
> - MYSQL_USER
> - MYSQL_PASSWORD

## Start Service with DB using Docker
Command to Start: `docker-compose up --build -d`\
Command to Stop: `docker-compose down`
> see [Docker Compose](./docker-compose.yml)\
> see [Dockerfile](./Dockerfile)

## Start Service locally without DB
- Add the .env file in your Run Configuration (or source the env file manually)
- Correct the main [config.properties](src/main/resources/config.properties) file

## Tests
`mvn clean test` -> runs unit and integration tests
> Note: test [config.properties](src/test/resources/config.properties) needs to be correctly filled out and a DB on that hostname and port must be running for the integration tests!

## Clear Docker
Run: `bash clear-docker.sh`

## Differences to the script
Gender: `["DIVERS", "MALE", "FEMALE", "UNSPECIFIED"]`\
KindOfMeter: `["HEATER", "ELECTRICITY", "WATER", "UNKNOWN"]`