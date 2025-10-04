
DELETE FROM medicatie_bijsluiter;
DELETE FROM toedieningen;
DELETE FROM schema_inname;
DELETE FROM notificatie_instellingen;
DELETE FROM zorgrelaties;


DELETE FROM medicaties;
DELETE FROM gebruikers;

SET FOREIGN_KEY_CHECKS=0;

TRUNCATE TABLE zorgrelaties;
TRUNCATE TABLE notificatie_instellingen;
TRUNCATE TABLE toedieningen;
TRUNCATE TABLE schema_inname;
TRUNCATE TABLE medicaties;
TRUNCATE TABLE gebruikers;

SET FOREIGN_KEY_CHECKS=1;

INSERT INTO gebruikers (naam, email, wachtwoord, rol) VALUES
                                                          ('Admin','admin@example.com','{bcrypt}$2a$10$sDmxpjvPRG4tG54hxJbH.eedKqkJU72fbOQTlq1gxBJ.cLKKyR6/a','ADMIN'),
                                                          ('Patiënt One','patient1@example.com','{bcrypt}$2a$10$4Jx3X8n0pQ6Ck3o3iX9A1e0w4u4mW3x2z6Jc5j5Z0f3a1R0Yw9s8G','GEBRUIKER'),
                                                          ('Patiënt Two','patient2@example.com','{bcrypt}$2a$10$4Jx3X8n0pQ6Ck3o3iX9A1e0w4u4mW3x2z6Jc5j5Z0f3a1R0Yw9s8G','GEBRUIKER'),
                                                          ('Verzorger One','verzorger1@example.com','{bcrypt}$2a$10$f9i1N7Vta6bC6dKSHIFqKe4zQdZKPF7dzJ15/6IMhfFbD9KHekNvS','VERZORGER'),
                                                          ('Verzorger Two','verzorger2@example.com','{bcrypt}$2a$10$4Jx3X8n0pQ6Ck3o3iX9A1e0w4u4mW3x2z6Jc5j5Z0f3a1R0Yw9s8G','VERZORGER');

INSERT INTO zorgrelaties (verzorger_id, gebruiker_id)
SELECT v.id, p.id
FROM gebruikers v, gebruikers p
WHERE v.email='verzorger1@example.com' AND p.email='patient1@example.com';

INSERT INTO medicaties (naam_medicijn, omschrijving, bijsluiter_url, gebruiker_id)
SELECT 'Paracetamol 500mg','Pijnstillend/koortsverlagend', NULL, u.id
FROM gebruikers u WHERE u.email='patient1@example.com';

INSERT INTO medicaties (naam_medicijn, omschrijving, bijsluiter_url, gebruiker_id)
SELECT 'Ibuprofen 200mg','NSAID','https://example.com/bijsluiters/ibuprofen.pdf', u.id
FROM gebruikers u WHERE u.email='patient1@example.com';

INSERT INTO medicaties (naam_medicijn, omschrijving, bijsluiter_url, gebruiker_id)
SELECT 'Metformine 500mg','Orale antidiabetica', NULL, u.id
FROM gebruikers u WHERE u.email='patient2@example.com';

INSERT INTO medicatie_bijsluiter (medicatie_id, data, content_type, filename, size_bytes)
SELECT m.id, X'255044462D312E0A', 'application/pdf', 'paracetamol.pdf', 8
FROM medicaties m
         JOIN gebruikers u ON u.id = m.gebruiker_id
WHERE m.naam_medicijn='Paracetamol 500mg' AND u.email='patient1@example.com';
