-- seed_demo.sql
-- Doel: demo-data voor autorisatie en kernfunctionaliteit upload/download bijsluiter
-- Vereist tabellen:
--   gebruikers(id, naam, email, wachtwoord, rol)
--   medicaties(id, naam_medicijn, omschrijving, bijsluiter_url, bijsluiter_foto, gebruiker_id)
--   medicatie_bijsluiters(medicatie_id PK/FK -> medicaties.id, url, content_type, size_bytes, data)
--   verzorger_patient(verzorger_id FK -> gebruikers.id, patient_id FK -> gebruikers.id, UNIQUE(verzorger_id, patient_id))

/* ---------- FK uit, leegmaken in juiste volgorde ---------- */
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM medicatie_bijsluiters;
DELETE FROM medicaties;
DELETE FROM verzorger_patient;
DELETE FROM gebruikers;
SET FOREIGN_KEY_CHECKS = 1;

/* (Optioneel) autoincrement resetten voor nette, voorspelbare ID’s */
ALTER TABLE gebruikers AUTO_INCREMENT = 1;
ALTER TABLE medicaties AUTO_INCREMENT = 1000;

/* ---------- Gebruikers ---------- */
/* Let op: 'wachtwoord' is verplicht (NOT NULL). Hieronder staan voorbeeld-Bcrypts.
   Vervang ze gerust door hashes die jouw PasswordEncoder genereert. */

-- Admin
INSERT INTO gebruikers (naam, email, wachtwoord, rol)
VALUES ('Admin', 'admin@example.com',
        '$2a$10$4Jx3X8n0pQ6Ck3o3iX9A1e0w4u4mW3x2z6Jc5j5Z0f3a1R0Yw9s8G',
        'ADMIN');

-- Patiënten
INSERT INTO gebruikers (naam, email, wachtwoord, rol)
VALUES ('Patiënt One', 'patient1@example.com',
        '$2a$10$4Jx3X8n0pQ6Ck3o3iX9A1e0w4u4mW3x2z6Jc5j5Z0f3a1R0Yw9s8G',
        'GEBRUIKER');

INSERT INTO gebruikers (naam, email, wachtwoord, rol)
VALUES ('Patiënt Two', 'patient2@example.com',
        '$2a$10$4Jx3X8n0pQ6Ck3o3iX9A1e0w4u4mW3x2z6Jc5j5Z0f3a1R0Yw9s8G',
        'GEBRUIKER');

-- Verzorgers
INSERT INTO gebruikers (naam, email, wachtwoord, rol)
VALUES ('Verzorger One', 'verzorger1@example.com',
        '$2a$10$4Jx3X8n0pQ6Ck3o3iX9A1e0w4u4mW3x2z6Jc5j5Z0f3a1R0Yw9s8G',
        'VERZORGER');

INSERT INTO gebruikers (naam, email, wachtwoord, rol)
VALUES ('Verzorger Two', 'verzorger2@example.com',
        '$2a$10$4Jx3X8n0pQ6Ck3o3iX9A1e0w4u4mW3x2z6Jc5j5Z0f3a1R0Yw9s8G',
        'VERZORGER');

-- IDs vastpakken (MySQL)
SET @admin_id      = (SELECT id FROM gebruikers WHERE email='admin@example.com');
SET @patient1_id   = (SELECT id FROM gebruikers WHERE email='patient1@example.com');
SET @patient2_id   = (SELECT id FROM gebruikers WHERE email='patient2@example.com');
SET @verzorger1_id = (SELECT id FROM gebruikers WHERE email='verzorger1@example.com');
SET @verzorger2_id = (SELECT id FROM gebruikers WHERE email='verzorger2@example.com');

/* ---------- Koppeling Verzorger ↔ Patiënt ---------- */
/* Verzorger #1 is gekoppeld aan Patiënt #1 */
INSERT INTO verzorger_patient (verzorger_id, patient_id)
VALUES (@verzorger1_id, @patient1_id);

/* Verzorger #2 heeft GEEN koppelingen → mag niets zien */

/* ---------- Medicaties ---------- */
/* Voor Patiënt #1 */
INSERT INTO medicaties (naam_medicijn, omschrijving, bijsluiter_url, bijsluiter_foto, gebruiker_id)
VALUES ('Paracetamol 500mg', 'Pijnstillend/koortsverlagend', NULL, NULL, @patient1_id);

INSERT INTO medicaties (naam_medicijn, omschrijving, bijsluiter_url, bijsluiter_foto, gebruiker_id)
VALUES ('Ibuprofen 200mg', 'NSAID', 'https://example.com/bijsluiters/ibuprofen.pdf', NULL, @patient1_id);

-- Voor Patiënt #2
INSERT INTO medicaties (naam_medicijn, omschrijving, bijsluiter_url, bijsluiter_foto, gebruiker_id)
VALUES ('Metformine 500mg', 'Orale antidiabetica', NULL, NULL, @patient2_id);

-- IDs vastpakken
SET @m_paracetamol = (SELECT id FROM medicaties WHERE naam_medicijn='Paracetamol 500mg' AND gebruiker_id=@patient1_id);
SET @m_ibuprofen   = (SELECT id FROM medicaties WHERE naam_medicijn='Ibuprofen 200mg'   AND gebruiker_id=@patient1_id);
SET @m_metformine  = (SELECT id FROM medicaties WHERE naam_medicijn='Metformine 500mg'  AND gebruiker_id=@patient2_id);

/* ---------- Bijsluiters (upload/download kernfunctionaliteit) ---------- */
/* Voorbeeld: echte file-bytes zouden via API komen; hier demo-blob met PDF-header */
INSERT INTO medicatie_bijsluiter (medicatie_id, url, content_type, size_bytes, data)
VALUES (
           @m_paracetamol,
           'https://example.com/bijsluiters/paracetamol.pdf',
           'application/pdf',
           8,
           X'255044462D312E0A'  -- "%PDF-1.\n" (alleen demo!)
       );

/* Ibuprofen gebruikt al bijsluiter_url op medicaties → los 1-op-1 record is optioneel */

/* ---------- (Optioneel) Toedieningsschema/Innameschema ---------- */
/*
LET OP: Pas deze sectie aan jouw daadwerkelijke tabel/kolomnamen.
Voorbeeld als je tabel 'inname_schemas' hebt met (id, medicatie_id, tijdstip, frequentie, hoeveelheid, eenheid, start_datum, eind_datum):

INSERT INTO inname_schemas (medicatie_id, tijdstip, frequentie, hoeveelheid, eenheid, start_datum, eind_datum)
VALUES
  (@m_paracetamol, '08:00:00', 'DAILY', 1, 'tablet', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY)),
  (@m_paracetamol, '20:00:00', 'DAILY', 1, 'tablet', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY)),
  (@m_ibuprofen,   '12:00:00', 'DAILY', 1, 'capsule', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY)),
  (@m_metformine,  '07:30:00', 'DAILY', 1, 'tablet', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 60 DAY));
*/

-- Klaar
