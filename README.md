# server-service

Javin microservice, ki skrpi za registracijo in prijavo uporabnikov.

Uporabljene tehnilogije: Java 21, Spring Boot 3, MongoDB, Maven, Lombok, Springdoc OpenAPI.

### Repository layout
- `api/` — Spring Boot REST API exposa user
- `service/` — poslovna logika in implementacije repositorejev in DTO-ji
- `entities/` — entitete
- `Dockerfile`, `docker-compose.yml`

### Build in zagon
Iz root:
```powershell
mvn clean package -DskipTests
```
Iz root/api:
```powershell
mvn spring-boot:run
```
Po defaultu, je aplikacija na voljo na `http://localhost:8032`.
Api dokumentacija (Swagger UI) je na voljo na `http://localhost:8032/swagger-ui.html`.

Konfiguracija:
- Glavna Spring Boot config datoteka je `api/src/main/resources/application.yml`.
- Pomembna lastnost: `spring.data.mongodb.uri`

## Docker

```powershell
docker build -t user-service .
```

```powershell
docker-compose up --build
```

