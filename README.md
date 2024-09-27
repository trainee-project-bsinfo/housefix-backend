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
- Add the .env file in your Run Configuration
- Correct the [configuration.properties](src/main/resources/config.properties) file

## Clear Docker
Run: `bash clear-docker.sh`
