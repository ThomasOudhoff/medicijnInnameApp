# Medicatie Inname App

Spring Boot-applicatie voor medicatiebeheer met rollen (**ADMIN**, **VERZORGER**, **GEBRUIKER**), bijsluiter-upload/download, toedieningen, innameschema’s en notificatie-instellingen. Authenticatie via **HTTP Basic** en **method-level security** (Spring Security).

## Inhoud
- [Quickstart](#quickstart)
- [Credentials](#credentials)
- [Database & seeding](#database--seeding)
- [Build & run](#build--run)
- [Postman-collectie](#postman-collectie)
- [Belangrijkste endpoints](#belangrijkste-endpoints)
- [Autorisatie (rollen)](#autorisatie-rollen)
- [Foutafhandeling](#foutafhandeling)
- [Testen](#testen)
- [Troubleshooting](#troubleshooting)

---

## Quickstart
1. **Database:** maak een MySQL database aan (bijv. `medicijneninname`).
2. **Configureer** de datasource (zie [Database & seeding](#database--seeding)).
3. **Seed:** bij het starten voert de app `data.sql` uit (afhankelijk van je config/profiel).
4. **Start** de app.
5. **Test** met Postman: `GET /api/gebruiker` (met **Admin** Basic Auth).

**Base URL:** `http://localhost:8080`

---

## Credentials
Uit de seed (`data.sql`):

| Rol       | Email                    | Wachtwoord  |
|-----------|--------------------------|-------------|
| ADMIN     | `admin@example.com`      | `Admin123!` |
| VERZORGER | `verzorger1@example.com` | `admin123!` |

> Voor demo-stromen gebruik je **admin** en **verzorger1**. Een (self-service) patiënt kun je via de Admin-API aanmaken en een wachtwoord geven.

---

## Database & seeding
`src/main/resources/data.sql` levert demo-gebruikers, zorgrelaties en voorbeeldmedicatie + 1 demo-bijsluiter.

# --- Database (MySQL 8, lokaal) ---
spring.datasource.url=jdbc:mysql://localhost:3306/medicijninname?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=<user>
spring.datasource.password=<pass>
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# --- JPA / schema & seeding ---
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true

# --- File uploads ---
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# --- PasswordEnconder ---
PasswordEncoder is Delegating → wachtwoorden in de database hebben een {bcrypt}-prefix (seed is daarop afgestemd).

**Build & run**

mvn spring-boot:run

of:

mvn clean package

java -jar target/medicatieinnameApp-*.jar

**Postman-collectie** 

**Collection/Environment variables (voorbeeld):**

baseUrl = http://localhost:8080

authUser = admin@example.com

authPass = Admin123!

verzorgUser = verzorger1@example.com

verzorgPass = admin123!

(optioneel) gebruikerId = 2, gebruikerId2 = 3

**Authorization per folder:** Admin vs Verzorger (Basic Auth).

**Upload bijsluiter:** Body → form-data → key file (type File). Geen handmatige Content-Type header toevoegen.

**Opslaan in repo: bijv.** /postman/medicatieinname.postman_collection.json.

**Belangrijkste endpoints**

**Gebruikers**

GET /api/gebruiker — lijst (ADMIN)

GET /api/gebruiker/{id} — details (ADMIN of self)

POST /api/gebruiker, PUT /api/gebruiker/{id}, DELETE /api/gebruiker/{id} — (ADMIN)

**Zorgrelatie (verzorger ↔ patiënt)**

POST /api/relaties — koppel verzorger ↔ patiënt (ADMIN), body:
{ "verzorgerId": 4, "gebruikerId": 2 }

**Medicatie**

POST /api/gebruiker/{gebruikerId}/medicatie — aanmaken (ADMIN of zelf)

GET /api/medicatie — lijst (ADMIN)

GET /api/medicatie/{id} — detail (ADMIN, owner, of verzorger indien gekoppeld)

PUT /api/medicatie/{id} — bijwerken (ADMIN of owner)

DELETE /api/medicatie/{id} — verwijderen (ADMIN of owner)

**Bijsluiter**

POST /api/medicatie/{id}/bijsluiter — upload (multipart, key file) (ADMIN of owner)

GET /api/medicatie/{id}/bijsluiter — download (ADMIN, owner, of verzorger gekoppeld)

PUT /api/medicatie/{id}/bijsluiter-url — externe URL vastleggen (ADMIN of owner)

DELETE /api/medicatie/{id}/bijsluiter — verwijderen (ADMIN of owner)

**Schema & Toedieningen**

POST /api/medicatie/{medicatieId}/schema — schema aanmaken (ADMIN, owner, of verzorger gekoppeld)

GET /api/medicatie/{medicatieId}/schema — schema’s opvragen

POST /api/schema/{schemaId}/toedieningen — toediening registreren

GET /api/schema/{schemaId}/toedieningen — toedieningen bij schema

GET /api/medicatie/{id}/toedieningen?from=YYYY-MM-DD&to=YYYY-MM-DD

GET /api/gebruiker/{id}/toedieningen?from=YYYY-MM-DD&to=YYYY-MM-DD

**Notificatie-instellingen**

PUT /api/gebruiker/{id}/instellingen — upsert

GET /api/gebruiker/{id}/instellingen

DELETE /api/gebruiker/{id}/instellingen
(Let op: kanaal is lowercase — email of push.)
