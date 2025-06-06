/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- DUMP
-- Struttura DataBase banca
DROP DATABASE IF EXISTS `banca`;
CREATE DATABASE IF NOT EXISTS `banca`;
USE `banca`;

-- Create permessi table first since other tables reference it
CREATE TABLE `permessi`(
`idPermesso` INT PRIMARY KEY,
`ruolo` VARCHAR(10) NOT NULL,
`codicePermesso` CHAR(2) UNIQUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `permessi` (`idPermesso`, `ruolo`, `codicePermesso`) VALUES
(1, 'admin', '07'),
(2, 'support', '05'),
(3, 'client', '70'),
(4, 'advisor', '75');

-- Now create utenti table
CREATE TABLE `utenti`(
`idUtente` INT PRIMARY KEY AUTO_INCREMENT,
`nome` VARCHAR(50) NOT NULL,
`cognome` VARCHAR(50) NOT NULL,
`dataNascita` DATE DEFAULT '2000-01-01',
`toponimo` VARCHAR(10) NOT NULL,
`indirizzo` VARCHAR(100) NOT NULL,
`numeroCivico` INT NOT NULL CHECK ( numeroCivico > 0 ),
`codiceFiscale` CHAR(16) UNIQUE NOT NULL,
`mail` VARCHAR(150) UNIQUE NOT NULL,
`prefisso` VARCHAR(6) NOT NULL,
`telefono` VARCHAR(15) NOT NULL,
`password` VARCHAR(100)  NOT NULL,
`passPhrase` VARCHAR(255),
`OTP` BOOLEAN NOT NULL,
`id_permesso` INT NOT NULL,
CONSTRAINT `fk_utente_permesso` FOREIGN KEY (`id_permesso`) REFERENCES `permessi`(`idPermesso`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=LATIN1;

-- Create conti table next since other tables reference it
CREATE TABLE `conti` (
`IBAN` CHAR(27) PRIMARY KEY,
`saldo` DECIMAL(20, 2) DEFAULT 0,
`stato` BOOLEAN DEFAULT TRUE,
`valuta` VARCHAR(3) NOT NULL,
`dataApertura` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
`id_utente` INT NOT NULL,
`id_consulente` INT DEFAULT NULL,
CONSTRAINT `fk_conto_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE,
CONSTRAINT `fk_conto_consulente` FOREIGN KEY (`id_consulente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Now create the other tables
CREATE TABLE `tickets`(
`idTicket` INT PRIMARY KEY AUTO_INCREMENT,
`titolo` VARCHAR(50) NOT NULL,
`descrizione` VARCHAR(500) NOT NULL,
`stato` SMALLINT NOT NULL,
`dataApertura` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
`dataChiusura` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
`id_utente` INT NOT NULL,
CONSTRAINT `fk_ticket_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `rubriche`(
`idContatto` INT PRIMARY KEY AUTO_INCREMENT,
`nome` VARCHAR(100) NOT NULL,
`cognome` VARCHAR(100) NOT NULL,
`IBAN` CHAR(27) NOT NULL,
`id_utente` INT NOT NULL,
CONSTRAINT `fk_rubrica_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `logs`(
`idLog` INT PRIMARY KEY AUTO_INCREMENT,
`dataModifica` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
`tipoModifica` VARCHAR(50) NOT NULL,
`descrizione` VARCHAR(255) NOT NULL,
`id_utente` INT NOT NULL,
CONSTRAINT `fk_log_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `transazioni`(
`idTransazione` INT PRIMARY KEY AUTO_INCREMENT,
`importo` DECIMAL(20, 2) DEFAULT 0,
`dataTransazione` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
`tipo` VARCHAR(10) NOT NULL,
`destinatario` VARCHAR(100) NOT NULL,
`causale` VARCHAR(100) NOT NULL,
`IBAN` CHAR(27) NOT NULL,
CONSTRAINT `fk_transazione_conto` FOREIGN KEY (`IBAN`) REFERENCES `conti`(`IBAN`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `carte`(
`numeroCarta` CHAR(16) PRIMARY KEY,
`CVV` CHAR(3) UNIQUE NOT NULL,
`dataScadenza` DATE NOT NULL,
`PIN` CHAR(5) UNIQUE NOT NULL,
`tipo` VARCHAR(9) NOT NULL,
`saldoDisponibile` DECIMAL(20, 2) DEFAULT 0 CHECK ( saldoDisponibile >= 0 ),
`saldoContabile` DECIMAL(20, 2) DEFAULT 0,
`stato` BOOLEAN DEFAULT TRUE,
`IBAN` CHAR(27) NOT NULL,
CONSTRAINT `fk_carta_conto` FOREIGN KEY (`IBAN`) REFERENCES `conti`(`IBAN`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `investimenti`(
`idInvestimento` INT PRIMARY KEY AUTO_INCREMENT,
`symbol` VARCHAR(12) NOT NULL,
`nomeAzione` VARCHAR(500) NOT NULL,
`prezzoAcquisto` DECIMAL(10, 2) NOT NULL,
`quantita` DECIMAL(10, 2) NOT NULL CHECK (quantita > 0),
`type` VARCHAR(10) NOT NULL,
`dataAcquisto` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
`id_utente` INT NOT NULL,
CONSTRAINT `fk_investimento_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `valoriAzioni` (
`idInvestimento` INT,
`dataValore` DATE,
`valore` DECIMAL(10, 2) NOT NULL,
PRIMARY KEY (`idInvestimento`, `dataValore`),
CONSTRAINT `fk_investimento_valoriAzioni` FOREIGN KEY (`idInvestimento`) REFERENCES `investimenti`(`idInvestimento`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `finanziamenti`(
`idFinanziamento` INT PRIMARY KEY AUTO_INCREMENT,
`tipo` VARCHAR(10) NOT NULL,
`importo` DECIMAL(20, 2) DEFAULT 0 CHECK ( importo > 0 ),
`dataErogazione` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
`interessi` DECIMAL(15, 2) DEFAULT 0 CHECK ( interessi >= 0 ),
`spesaIncasso` DECIMAL(5, 2) DEFAULT 2.5,
`tipoRata` VARCHAR(20) NOT NULL,
`valoreRata` DECIMAL(10, 2) DEFAULT 0 CHECK ( valoreRata >= 0 ),
`inizioPagamento` DATE NOT NULL,
`importoPagato` DECIMAL(15, 2) DEFAULT 0,
`descrizione` VARCHAR(250) NOT NULL,
`stato` BOOLEAN DEFAULT 1,
`id_utente` INT NOT NULL,
CONSTRAINT `fk_finanziamento_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=LATIN1;

-- Inserimento utenti
INSERT INTO utenti (nome, cognome, dataNascita, toponimo, indirizzo, numeroCivico, codiceFiscale, mail, prefisso, telefono, password, passPhrase, OTP, id_permesso) VALUES
('Lorenzo', 'Molteni', '2006-04-29', 'Via', 'Morigiola', 10, 'RSSMRA80A01H501Z', 'moltenilorenzo6@gmail.com', '+39', '3331234567', '$2a$10$9qOEB6xfoLraLSS.ia8vDOBFldSrN6I3A6De4AIhDceuFFMecITuS', '9HrL0CfMa_fvRj76RQIAyXOX6Pf2Mj7PrwZxzcObSfm-OO8yM8-1AShYNAdGlUNu', FALSE, 1),
('Laura', 'Neri', '1990-01-01', 'Via', 'Centrale', 5, 'NRALRA90A01H501Z', 'laura.neri@banca.it', '+39', '3311112222', '$2a$10$ccgJKXWGDPBgIn9iB7RdeeeCTdJEDtbcGhrwvbHxGRy4S301OfEz6', 'VJqb5cV-ycy48dsEsahhgAXZyaP0E5vR9CKQptjdbsG-OO8yM8-1AShYNAdGlUNu', FALSE, 1),
('Simone', 'Verdi', '1959-12-28', 'Piazza', 'Garibaldi', 3, 'VRDSMN80A01H501Z', 'simone.verdi@support.it', '+39', '3391234567', '$2a$10$MtKELl4Tthn03IP3yDLTjOj6EMV3mRm7PfzVx/kyS6xSUmxA.iRHm', 'WHI_wfRdx5QMNGVhEn9hoXdB4iSUQ-p0onYXxQrtXf--OO8yM8-1AShYNAdGlUNu', TRUE, 2),
('Lucia', 'Conti', '1952-03-30', 'Via', 'Roma', 7, 'CNTLCU85B41H501Z', 'lucia.conti@advisor.it', '+39', '3405556677', '$2a$10$8xY/MfohcdDdAGGG/2tfVOcEKbLfcqrTnTXxhoNBiTJR9TKPJsz4C', 'TvvxCgRpVVB0TwkqiGisyXXDzuOaiAbVsSk8vfCHVVW-OO8yM8-1AShYNAdGlUNu', TRUE, 4),
('Giulia', 'Bianchi', '1995-05-15', 'Corso', 'Italia', 21, 'BNCGLI95E55F205W', 'giulia@banca.it', '+39', '3339876543', '$2a$10$UuhNaeVYLfDk7inB8ShQlevPGjd95I3HvJXWnqw6RaFUVOcpOa6Ra', '_YUhcjy5qVZJQXb6uu_2BT3iKkqTxYpFEmc7nTmop8S-OO8yM8-1AShYNAdGlUNu', TRUE, 3),
('Eugenia', 'Caracciolo', '2003-04-07', 'Piazza', 'Borgo Garozzo', 71, 'LBcbfn21A81H501Z', 'eugenia.caracciolo@banca.it', '+39', '0265423511', '$2a$10$Gh2sic5n4zAOEmatn4EzledfPzvb3O4PJ4n2R94LFL02E7gscPC7K', 'uVp_XAekYPMDGGGw-sp7XaEtwbSb0A77s07SbAocWAW-OO8yM8-1AShYNAdGlUNu', TRUE, 3),
('Rosaria', 'Bertolucci', '1980-01-06', 'Borgo', 'Rotonda Pasquale', 36, 'TunPFz41A39H501Z', 'rosaria.bertolucci@banca.it', '+39', '9696532871', '$2a$10$WdhGIV6pL.Cw5zgWYMLVFuOaM4wVJo.47DgY.hl2KpQB9lFVeS8am', 'eKlIVAMJA8fBtmM8JIrGhpLaSvn3SqJ140SBw93V0Am-OO8yM8-1AShYNAdGlUNu', TRUE, 3),
('Severino', 'Antonioni', '2000-05-10', 'Strada', 'Contrada Giradello', 190, 'fUFeWI03A91H501Z', 'severino.antonioni@banca.it', '+39', '4657871331', '$2a$10$RAChnqFJD26oESnwuBYHF..jEmCoxmqThWSpRIfKm4Y67fvOO6ou6', 'mQf_7TALxj1doNF1YbtPKvJTAX-75m-OmNF1uyBStzq-OO8yM8-1AShYNAdGlUNu', TRUE, 3),
('Rembrandt', 'Vigliotti', '1996-06-27', 'Strada', 'Canale Adriana', 109, 'AFEnzd01A32H501Z', 'rembrandt.vigliotti@banca.it', '+39', '2343098050', '$2a$10$8d78okeLg8T2IzoAsK9omuDAhT2sxMONub1WWTqjSm8tkPh9o4DOG', '9p2BLpoHYX_1QBvjlUc4a8xJv66Ats66u4LYlZe35Xy-OO8yM8-1AShYNAdGlUNu', TRUE, 3),
('Ippazio', 'Cuda', '1971-05-04', 'Viale', 'Vicolo Trupiano', 24, 'iqhgVJ08A41H501Z', 'ippazio.cuda@banca.it', '+39', '1640052427', '$2a$10$pL7AzW7nEC.LU2s0xraweupnJ6MYHGjc7.D4IUEJSYoRZOtt4u05m', 'E7lhpE4UjTY7Sqy9f51FE3deyro6EAOFdfExH2Ojb8m-OO8yM8-1AShYNAdGlUNu', TRUE, 3),
('Gianfrancesco', 'Filippelli', '1980-06-08', 'Viale', 'Contrada Gentilini', 130, 'vreXrw30A36H501Z', 'gianfrancesco.filippelli@banca.it', '+39', '0196556981', '$2a$10$vEx/t0yCtCFWLeCEDMkA8OErbE0sn3uqistuHUk3uJVtDOKzOhFUu', 'mpl0VL6trhebjNIyJRrYf-q6KV6JPiZDSnqhq0pwWfa-OO8yM8-1AShYNAdGlUNu', TRUE, 3),
('Salvatore', 'Donarelli', '1995-03-06', 'Canale', 'Contrada Uberto', 51, 'RtoZmj95A13H501Z', 'salvatore.donarelli@banca.it', '+39', '6320163287', '$2a$10$9nmDY0dr9S9F2DPTtld.PuUT6yDK4K/yH5mKiSdcn5xRcmQ8AFa7O', 'KLWBTWO9X6vCk2X8qIAhkxWosFLYDi1dAlkewN1M-vK-OO8yM8-1AShYNAdGlUNu', FALSE, 3),
('Beatrice', 'Pacillo', '2001-04-26', 'Viale', 'Incrocio Vigliotti', 109, 'tqohUm55A74H501Z', 'beatrice.pacillo@banca.it', '+39', '3749894134', '$2a$10$4/CiQcapeDL910tmVYzGbOkDMC13pDZ8VN3lfP0/TBPMLcd4FaMSu', 'LdqJsrwqHjiM9P2OiShySv5nEPobPngOPP1GjW9TM26-OO8yM8-1AShYNAdGlUNu', FALSE, 3),
('Matteo', 'Bertoldini', '2000-01-01', 'Via', 'Risorgimento', 1, 'BRTMTT06H07E507V', 'matteobertoldini06@gmail.com', '+39', '3703316356', '$2a$10$uhn8RIuaY5h4ormWpBJqV.fqHms/FFoe6DDDnJWna7XIpzC6R.lLK', '6ZuIu2OByNbR8E1pYedMIg8uvcLN7HLSQ3Swr5_pE1m-OO8yM8-1AShYNAdGlUNu', TRUE, 3),
('Nico', 'Molteni', '2006-04-03', 'Via', 'Morigiola', 10, 'RSSMRA80A01H501A', 'molteni.nico06@gmail.com', '+39', '3331234567', '$2a$10$9qOEB6xfoLraLSS.ia8vDOBFldSrN6I3A6De4AIhDceuFFMecITuS', 'tSDt4GCRZ9gxlFK2M_8r9SWBWsuq0NbYsYJepBAqg3K-OO8yM8-1AShYNAdGlUNu', FALSE, 3),
('Lorenzo', 'Nava', '2006-12-04', 'Frazione', 'San Dionigi', 10, 'NVALNZ06T04E507G', 'lorenzo.nava2006@gmail.com', '+39', '3331234567', '$2a$10$9qOEB6xfoLraLSS.ia8vDOBFldSrN6I3A6De4AIhDceuFFMecITuS', 'dK4-13XjIBENV7bcL2ewid2RD3YRz-vWxDp47R-joW6-OO8yM8-1AShYNAdGlUNu', FALSE, 3);



-- Inserimento conti
INSERT INTO conti (IBAN, saldo, stato, valuta, id_utente, id_consulente) VALUES
('IT60X0542811101000000123456', 8923748374000.00, TRUE, 'EUR', 1, 1),
('IT60X0542811101000000692321', 2500.00, TRUE, 'EUR', 5, 1),
('IT60X0542811101000000000001', 1500.00, TRUE, 'EUR', 6, 4),
('IT60X0542811101000000000002', 3200.00, TRUE, 'EUR', 7, 4),
('IT60X0542811101000000000003', 2750.00, TRUE, 'EUR', 8, 4),
('IT60X0542811101000000000004', 4000.00, TRUE, 'EUR', 9, 4),
('IT60X0542811101000000000005', 1800.00, TRUE, 'EUR', 10, 4),
('IT60X0542811101000000000006', 2200.00, TRUE, 'EUR', 11, 4),
('IT60X0542811101000000000007', 3100.00, TRUE, 'EUR', 12, 4),
('IT60X0542811101000000000008', 2900.00, TRUE, 'EUR', 13, 4),
('IT60X0542811101000000000009', 2600.00, TRUE, 'EUR', 4, 4),
('IT60X0542811101000000652321', 200000000500.00, true, 'EUR', 14, 1),
('IT60X0542811101000000652322', 5000.00, true, 'EUR', 15, 1),
('IT60X0542811101000000090310', 100309.00, true, 'EUR', 16, 1);


-- Inserimento carte di credito per clienti e advisor
INSERT INTO `carte` (`numeroCarta`, `CVV`, `dataScadenza`, `PIN`, `tipo`, `saldoDisponibile`, `saldoContabile`, `stato`, `IBAN`) VALUES
-- Clienti
('4123456789012346', '481', '2027-12-01', '19473', 'credito', 5000.00, 2600.00, TRUE, 'IT60X0542811101000000000001'),
('5123456789012345', '317', '2028-11-01', '29183', 'credito', 7500.00, 3200.00, TRUE, 'IT60X0542811101000000000002'),
('341234567890123', '962', '2026-09-01', '37465', 'credito', 3000.00, 2750.00, TRUE, 'IT60X0542811101000000000003'),
('378282246310005', '223', '2029-05-01', '44821', 'credito', 10000.00, 4000.00, TRUE, 'IT60X0542811101000000000004'),
('6011111111111117', '155', '2027-07-01', '55932', 'credito', 2000.00, 1800.00, TRUE, 'IT60X0542811101000000000005'),
('5555345678901235', '127', '2028-08-01', '67890', 'credito', 8000.00, 2200.00, TRUE, 'IT60X0542811101000000000006'),
('4123456789012347', '742', '2029-02-01', '78901', 'credito', 6000.00, 3100.00, TRUE, 'IT60X0542811101000000000007'),
('341234567890124', '835', '2027-11-01', '89012', 'credito', 4000.00, 2900.00, TRUE, 'IT60X0542811101000000000008'),
('3245453234549005', '121', '2050-01-01', '84013', 'credito', 29645.00, 29645.00, TRUE, 'IT60X0542811101000000123456'),
('2342354312943542', '129', '2050-01-01', '54896', 'credito', 250435.00, 250435.00, true, 'IT60X0542811101000000652321'),
('2342354312943543', '130', '2050-01-01', '11111', 'credito', 250435.00, 250435.00, true, 'IT60X0542811101000000652322'),
('9999333311110000', '309', '2050-01-01', '90310', 'credito', 1090031039.00, 1090031039.00, true, 'IT60X0542811101000000090310'),

-- Advisor (Lucia Conti)
('5555345678901234', '739', '2029-01-01', '66721', 'credito', 15000.00, 2600.00, TRUE, 'IT60X0542811101000000000009');

-- Carte di debito e prepagate per alcuni clienti
INSERT INTO `carte` (`numeroCarta`, `CVV`, `dataScadenza`, `PIN`, `tipo`, `saldoDisponibile`, `saldoContabile`, `stato`, `IBAN`) VALUES
-- Carte di debito
('4012888888881881', '128', '2026-08-01', '12345', 'debito', 1500.00, 1500.00, TRUE, 'IT60X0542811101000000000001'),
('5111111111111118', '499', '2027-03-01', '54321', 'debito', 3200.00, 3200.00, TRUE, 'IT60X0542811101000000000002'),
('4444333322221111', '777', '2026-12-01', '98765', 'debito', 2750.00, 2750.00, TRUE, 'IT60X0542811101000000000003'),
('2345345234234543', '124', '2050-01-01', '53424', 'debito', 26645.00, 26645.00, TRUE, 'IT60X0542811101000000123456'),
('9999333311110001', '319', '2050-01-01', '90311', 'debito', 26645.00, 26645.00, TRUE, 'IT60X0542811101000000090310'),


-- Carte prepagate
('4916011111111113', '334', '2028-10-01', '11223', 'prepagata', 500.00, 500.00, TRUE, 'IT60X0542811101000000000001'),
('5222222222222225', '665', '2026-06-01', '33445', 'prepagata', 1000.00, 1000.00, TRUE, 'IT60X0542811101000000000002'),
('375987654321001', '889', '2027-04-01', '55667', 'prepagata', 750.00, 750.00, TRUE, 'IT60X0542811101000000000003'),
('2342354312543542', '122', '2050-01-01', '54893', 'prepagata', 25435.00, 25435.00, TRUE, 'IT60X0542811101000000123456'),
('9999333311110002', '329', '2050-01-01', '90312', 'prepagata', 26645.00, 26645.00, TRUE, 'IT60X0542811101000000090310');

-- Transazioni generiche
INSERT INTO `transazioni` (`importo`, `dataTransazione`, `tipo`, `destinatario`, `causale`,`IBAN`) VALUES
(-150.00, '2025-01-03 08:15:23', 'bonifico', 'Amazon Europe', 'Acquisto', 'IT60X0542811101000000000001'),
(-89.99, '2025-01-17 14:42:10', 'addebito', 'Netflix', 'Acquisto', 'IT60X0542811101000000000002'),
(450.00, '2025-02-01 10:30:45', 'accredito', 'Stipendio', '', 'IT60X0542811101000000000003'),
(-29.90, '2025-02-14 18:12:09', 'addebito', 'Spotify', 'Acquisto', 'IT60X0542811101000000000004'),
(-1200.00, '2025-02-27 09:05:56', 'bonifico', 'Affitto', 'Paga fattura', 'IT60X0542811101000000000005'),
(-379452.16, '2025-03-05 11:44:30', 'bonifico', 'Ferrari S.P.A.', 'Acquisto', 'IT60X0542811101000000123456'),
(645234.34, '2025-03-18 16:20:00', 'accredito', 'Eryxis Bank S.P.A.', '', 'IT60X0542811101000000123456');

-- Inserimento nuovi ticket nella tabella tickets
INSERT INTO tickets (titolo, descrizione, stato, id_utente) VALUES
('Problema login', 'Non riesco ad accedere al mio conto con le credenziali corrette.', 1, 15),
('Carta smarrita', 'Ho perso la mia carta di credito e vorrei bloccarla il prima possibile.', 1, 15),
('Errore saldo', 'Il saldo del mio conto corrente è errato rispetto agli ultimi movimenti.', 1, 15),
('Modifica mail', 'Vorrei aggiornare il mio indirizzo email di contatto.', 1, 15),
('Richiesta finanziamento', 'Desidero informazioni su come richiedere un prestito personale.', 3, 15),
('Gianni Morandi', 'Desidero informazioni su come richiedere un prestito personale.', 1, 15),
('Gianni Schettino', 'Desidero informazioni su come richiedere un prestito personale.', 2, 15);

INSERT INTO `transazioni` (`importo`, `dataTransazione`, `tipo`, `destinatario`, `causale`,`IBAN`) VALUES
-- Carte di debito (14.99)
(-14.99, '2025-03-29 07:33:18', 'addebito', 'Eryxis Bank S.P.A.', 'Acquisto', 'IT60X0542811101000000000001'),
(-14.99, '2025-04-03 13:49:55', 'addebito', 'Eryxis Bank S.P.A.', 'Acquisto', 'IT60X0542811101000000000002'),
(-14.99, '2025-04-10 21:17:40', 'addebito', 'Eryxis Bank S.P.A.', 'Acquisto', 'IT60X0542811101000000000003'),
(-14.99, '2025-04-15 06:02:11', 'addebito', 'Eryxis Bank S.P.A.', 'Acquisto', 'IT60X0542811101000000123456'),

-- Carte prepagate (4.99)
(-4.99, '2025-04-22 12:58:33', 'addebito', 'Eryxis Bank S.P.A.', 'Acquisto', 'IT60X0542811101000000000001'),
(-4.99, '2025-04-27 15:40:08', 'addebito', 'Eryxis Bank S.P.A.', 'Acquisto', 'IT60X0542811101000000000002'),
(-4.99, '2025-05-01 19:25:50', 'addebito', 'Eryxis Bank S.P.A.', 'Acquisto', 'IT60X0542811101000000000003'),
(-4.99, '2025-05-02 08:01:12', 'addebito', 'Eryxis Bank S.P.A.', 'Acquisto', 'IT60X0542811101000000123456');

-- Contatti in rubrica per Lorenzo Molteni (idUtente 1)
INSERT INTO `rubriche` (`nome`, `cognome`, `IBAN`, `id_utente`) VALUES
('Mario', 'Rossi', 'IT60X0542811101000000692321', 1), -- Conto di Giulia Bianchi
('Lucia', 'Conti', 'IT60X0542811101000000000009', 1),  -- Conto dell'advisor Lucia Conti
('Amazon', 'Europe', 'IT12M1234567890123456789012', 1),
('Netflix', 'International', 'IT34X9876543210987654321098', 1),
('Spotify', 'AB', 'IT56P4567890123456789012345', 1),
('Lorenzo', 'Molteni', 'IT60X0542811101000000123456', 14), -- Conto di Lorenzo Molteni
('Giulia', 'Bianchi', 'IT60X0542811101000000692321', 14),   -- Conto di Giulia Bianchi
('Eugenia', 'Caracciolo', 'IT60X0542811101000000000001', 14),
('Università', 'Milano', 'IT78B5432109876543210987654', 14),
('Affitto', 'Casa', 'IT90X6789012345678901234567', 14);