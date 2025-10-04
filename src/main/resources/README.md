# Medicatie Inname App

Spring Boot-app voor medicatiebeheer met rollen (**ADMIN**, **VERZORGER**, **GEBRUIKER**), bijsluiter-upload/download, toedieningen, innameschema’s en notificatie-instellingen. Authenticatie via **HTTP Basic** en method-level security.

---

## Inhoud
- [Features](#features)
- [Vereisten](#vereisten)
- [Quickstart](#quickstart)
- [Demo-credentials (seed)](#demo-credentials-seed)
- [Build & Run](#build--run)
- [Configuratie (application-example.properties)](#configuratie-application-exampleproperties)
- [Postman](#postman)
- [Belangrijkste endpoints](#belangrijkste-endpoints)
  - [Bijsluiter upload – belangrijk](#bijsluiter-upload--belangrijk)
- [Autorisatie (rollen)](#autorisatie-rollen)
- [Foutafhandeling](#foutafhandeling)
- [Troubleshooting](#troubleshooting)
- [Database & seeding](#database--seeding)
- [Testen](#testen)
- [Security & secrets](#security--secrets)
- [.gitignore (advies)](#gitignore-advies)

---

## Features
- **Gebruikers** CRUD + rollen/autorisatie.
- **Medicaties** CRUD, gekoppeld aan gebruiker.
- **Bijsluiter** upload/download (multipart, 1-op-1 per medicatie).
- **Innameschema’s** & **toedieningen**.
- **Notificatie-instellingen** (email/push).
- Seeddata via `data.sql` (dev).

## Vereisten
- **Java 21** (of de Java-versie in `pom.xml`).
- **Maven** (wrapper meegeleverd: `mvnw`).
- **MySQL 8.x** (of optioneel H2 voor snel lokaal testen).

## Quickstart
1. **Database** aanmaken (voorbeeld):
   ```sql
   CREATE DATABASE medicijninname CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
   ```
2. **Configuratie**: gebruik env-variabelen of een lokale `application.properties` (niet committen). Zie [Configuratie](#configuratie-application-exampleproperties).
3. **Start de app**:
   ```powershell
   # Windows PowerShell
   $env:DB_USER="root"; $env:DB_PASSWORD="mijnWachtwoord"
   .\mvnw spring-boot:run
   ```
   ```bash
   # macOS/Linux
   export DB_USER=root DB_PASSWORD=mijnWachtwoord
   ./mvnw spring-boot:run
   ```
4. **Base URL**: `http://localhost:8080`
5. **Test** met Postman/cURL: `GET /api/gebruiker` (met Admin Basic Auth).

## Demo-credentials (seed)
*(Alleen voor dev/test — wijzig in productie.)*
- **Admin**: `admin@example.com` / `Admin123!`
- **Patiënt**: `patient1@example.com` / `Patient123!`

> Seed wordt geladen bij app-start (via `data.sql`), zolang `spring.sql.init.mode=always` en je profiel dit toestaat.

## Build & Run
**Jar bouwen**
```bash
./mvnw -q -DskipTests clean package
java -jar target/medicatieinnameApp-*.jar
```

## Configuratie (application-example.properties)
Lever dit bestand mee i.p.v. je echte `application.properties`:

```properties
spring.application.name=medicatieinnameApp

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/medicijninname?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:changeme}

spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> **Let op**: gebruik lokaal eigen `application.properties` of env-vars met echte waarden. Commit **nooit** secrets.

## Postman
- Collectie + environment (zonder secrets) in de map `postman/`.
- Start met:
  - `GET /api/gebruiker` (Admin Basic Auth)
  - `GET /api/medicatie`

## Belangrijkste endpoints

| Methode | Pad                                   | Beschrijving                        | Auth/rol        |
|:------:|---------------------------------------|-------------------------------------|-----------------|
| GET    | `/api/gebruiker`                      | Lijst gebruikers                    | ADMIN           |
| POST   | `/api/gebruiker`                      | Nieuwe gebruiker                    | ADMIN           |
| GET    | `/api/medicatie`                      | Lijst medicaties                    | ingelogd        |
| POST   | `/api/medicatie`                      | Nieuwe medicatie                    | VERZORGER/ADMIN |
| GET    | `/api/medicatie/{id}`                 | Medicatie detail                    | eigenaar/rol    |
| **POST** | **`/api/medicatie/{id}/bijsluiter`**| **Upload bijsluiter (multipart)**   | eigenaar/rol    |
| GET    | `/api/medicatie/{id}/bijsluiter`      | Download bijsluiter                 | eigenaar/rol    |

### Bijsluiter upload – **belangrijk**
- Body: **`multipart/form-data`**.  
- **Form key = `file`** (controller: `@RequestParam("file") MultipartFile file`).  
- DB-kolom `medicatie_bijsluiter.data` moet groot genoeg zijn (**BLOB / MEDIUMBLOB / LONGBLOB**).

**cURL (Windows, één regel):**
```powershell
curl.exe -u admin@example.com:Admin123! -H "Accept: application/json" -F "file=@C:/pad/naar/bijsluiter.pdf;type=application/pdf" "http://localhost:8080/api/medicatie/1/bijsluiter"
```

## Autorisatie (rollen)
- **ADMIN**: volledige toegang.  
- **VERZORGER**: beheren van medicaties van toegewezen gebruikers.  
- **GEBRUIKER**: eigen gegevens/medicatie inzien en relevante acties.

## Foutafhandeling
- **400** — Bad Request (validatie/multipart/mime/te groot/te klein).  
- **401** — Unauthorized (Basic Auth ontbreekt/verkeerd).  
- **403** — Forbidden (rol/owner mismatch).  
- **404** — Not Found (resource bestaat niet).  
- **409** — Conflict (optioneel bij “bijsluiter bestaat al” indien niet overschreven).  
- **500** — Server error (onverwacht; zie logs).

## Troubleshooting
- `Required part 'file' is not present` → Form key heet geen `file` of body is niet `multipart/form-data`.  
- **401** → verkeerde credentials of seed niet geladen; test `GET /api/medicatie` met Basic Auth.  
- **404** op `/api/medicatie/{id}` → id bestaat niet (na TRUNCATE veranderen ID’s).  
- **400 “Upload mislukt” + SQL 1406** → kolom `data` te klein (**TINYBLOB**). Fix:
  ```sql
  ALTER TABLE medicatie_bijsluiter MODIFY COLUMN data LONGBLOB NOT NULL;
  ```
- **Max upload size** → verhoog via `spring.servlet.multipart.*`.

## Database & seeding
- `spring.sql.init.mode=always` → `schema.sql` / `data.sql` draaien bij start (afhankelijk van profiel).  
- `spring.jpa.hibernate.ddl-auto=update` voor dev: schema wordt bijgewerkt (geen downsize).  
- **Resetten (dev):**
  ```sql
  SET FOREIGN_KEY_CHECKS=0;
  TRUNCATE TABLE medicatie_bijsluiter;
  TRUNCATE TABLE toedieningen;
  TRUNCATE TABLE schema_inname;
  TRUNCATE TABLE notificatie_instellingen;
  TRUNCATE TABLE zorgrelaties;
  TRUNCATE TABLE medicaties;
  TRUNCATE TABLE gebruikers;
  SET FOREIGN_KEY_CHECKS=1;
  ```
  Herstart de app om seed opnieuw te laden.

## Testen
```bash
./mvnw test
```

*(Optioneel) H2-profiel voor snel nakijken:*
```properties
# src/main/resources/application-h2.properties
spring.datasource.url=jdbc:h2:mem:medicijninname;MODE=MySQL;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
```

## Security & secrets
- Commit **nooit** echte wachtwoorden of keys.  
- Lever `application-example.properties` met placeholders mee + README met run-instructies.  
- Gebruik `.gitignore` om `application.properties` en build/IDE output uit te sluiten.

## .gitignore (advies)
```gitignore
# Build
/target/

# IDE
/.idea/
/.vscode/
*.iml
.project
.classpath
.settings/

# Logs & OS
/logs/
*.log
.DS_Store
Thumbs.db

# Frontend (indien aanwezig)
/node_modules/
/dist/
/build/

# Secrets (lokaal houden)
/src/main/resources/application.properties
/src/main/resources/application-local.properties
```

---

