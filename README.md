# Cloud file storage

Created according to the [technical specifications](https://zhukovsd.github.io/java-backend-learning-course/projects/cloud-file-storage/) presented in this [course](https://zhukovsd.github.io/java-backend-learning-course/).

## Overview

A multi-user cloud file service. Users can use it to upload and store files.

## Tech stack

- Java
- Maven
- Spring Boot 3
  - Web
  - JPA (Hibernate + MySQL)
  - Security
  - Session + Redis
  - Test (JUnit 5, Mockito, Testcontainers)
- Liquibase
- MinIO
- MapStruct
- Lombok

## Requirements

- Java 17+
- Maven 3.9.x
- Tomcat 11
- Docker

## Local deployment

1. Clone repository
```
https://github.com/damvih03/cloud-file-storage.git
```

2. Navigate to the project directory
3. Fill empty values in `.env` file

```
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/cloud-file-storage
SPRING_DATASOURCE_DB=cloud-file-storage
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=

REDIS_PASSWORD=
REDIS_HOST=redis
REDIS_PORT=

MINIO_ENDPOINT=
MINIO_ROOT_USER=minio
MINIO_ROOT_PASSWORD=
MINIO_BUCKET_NAME=user-files

BACKEND_PORT=
```

4. Run docker compose

    ! Before running ensure that BACKEND_PORT is free on your local machine
```
docker compose -f docker-compose-prod.yaml --env-file .env up
```

Swagger default link: `http://localhost:{BACKEND_PORT}/api/swagger-ui/index.html`.
