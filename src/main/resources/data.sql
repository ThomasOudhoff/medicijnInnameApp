SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM medicatie_bijsluiter;
DELETE FROM medicaties;
DELETE FROM verzorger_patient;
DELETE FROM gebruikers;
SET FOREIGN_KEY_CHECKS = 1;

ALTER TABLE gebruikers AUTO_INCREMENT = 1;
ALTER TABLE medicaties AUTO_INCREMENT = 1000;

INSERT INTO gebruikers (naam, email, wachtwoord, rol) VALUES
('Admin', 'admin@example.com', '{bcrypt}$2a$10$sDmxpjvPRG4tG54hxJbH.eedKqkJU72fbOQTlq1gxBJ.cLKKyR6/a', 'ADMIN'),
('Patiënt One', 'patient1@example.com', '{bcrypt}$2a$10$4Jx3X8n0pQ6Ck3o3iX9A1e0w4u4mW3x2z6Jc5j5Z0f3a1R0Yw9s8G', 'GEBRUIKER'),
('Patiënt Two', 'patient2@example.com', '{bcrypt}$2a$10$4Jx3X8n0pQ6Ck3o3iX9A1e0w4u4mW3x2z6Jc5j5Z0f3a1R0Yw9s8G', 'GEBRUIKER'),
('Verzorger One', 'verzorger1@example.com', '{bcrypt}$2a$10$f9i1N7Vta6bC6dKSHIFqKe4zQdZKPF7dzJ15/6IMhfFbD9KHekNvS', 'VERZORGER'),
('Verzorger Two', 'verzorger2@example.com', '{bcrypt}$2a$10$4Jx3X8n0pQ6Ck3o3iX9A1e0w4u4mW3x2z6Jc5j5Z0f3a1R0Yw9s8G', 'VERZORGER');

SET @admin_id      = (SELECT id FROM gebruikers WHERE email='admin@example.com');
SET @patient1_id   = (SELECT id FROM gebruikers WHERE email='patient1@example.com');
SET @patient2_id   = (SELECT id FROM gebruikers WHERE email='patient2@example.com');
SET @verzorger1_id = (SELECT id FROM gebruikers WHERE email='verzorger1@example.com');
SET @verzorger2_id = (SELECT id FROM gebruikers WHERE email='verzorger2@example.com');

INSERT INTO verzorger_patient (verzorger_id, patient_id) VALUES
    (@verzorger1_id, @patient1_id);

INSERT INTO medicaties (naam_medicijn, omschrijving, bijsluiter_url, gebruiker_id) VALUES
('Paracetamol 500mg', 'Pijnstillend/koortsverlagend', NULL, @patient1_id),
('Ibuprofen 200mg', 'NSAID', 'https://example.com/bijsluiters/ibuprofen.pdf', @patient1_id),
('Metformine 500mg', 'Orale antidiabetica', NULL, @patient2_id);

SET @m_paracetamol = (SELECT id FROM medicaties WHERE naam_medicijn='Paracetamol 500mg' AND gebruiker_id=@patient1_id);

INSERT INTO medicatie_bijsluiter (medicatie_id, data, content_type, filename, size_bytes) VALUES
    (@m_paracetamol, X'255044462D312E0A', 'application/pdf', 'paracetamol.pdf', 8);
