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
    `password` VARCHAR(100) UNIQUE NOT NULL,
    `passPhrase` VARCHAR(255) UNIQUE,
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
    `titolo` VARCHAR(20) NOT NULL,
    `descrizione` VARCHAR(500) NOT NULL,
    `stato` CHAR(1) NOT NULL,
    `id_utente` INT NOT NULL,
    CONSTRAINT `fk_ticket_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `rubriche`(
    `idContatto` INT PRIMARY KEY AUTO_INCREMENT,
    `nome` VARCHAR(100) NOT NULL,
    `cognome` VARCHAR(100) NOT NULL,
    `IBAN` CHAR(27) UNIQUE NOT NULL,
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
    `ISIN` VARCHAR(12) PRIMARY KEY,
    `settore` VARCHAR(50) NOT NULL,
    `divisa` VARCHAR(50) NOT NULL,
    `tipo` VARCHAR(11) NOT NULL,
    `nomeTitolo` VARCHAR(50) NOT NULL,
    `descrizione` VARCHAR(255) NOT NULL,
    `dataAcquisto` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `quantitaDenaro` DECIMAL(20, 2) DEFAULT 0 CHECK ( quantitaDenaro > 0 ),
    `quantitaTotale` INT DEFAULT 0 CHECK ( quantitaTotale > 0 ),
    `id_utente` INT NOT NULL,
    CONSTRAINT `fk_investimento_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `finanziamenti`(
    `idFinanziamento` INT PRIMARY KEY AUTO_INCREMENT,
    `tipo` VARCHAR(8) NOT NULL,
    `importo` DECIMAL(20, 2) DEFAULT 0 CHECK ( importo > 0 ),
    `dataErogazione` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `interessi` DECIMAL(15, 2) DEFAULT 0 CHECK ( interessi >= 0 ),
    `spesaIncasso` DECIMAL(5, 2) DEFAULT 2.5,
    `tipoRata` VARCHAR(20) NOT NULL,
    `valoreRata` DECIMAL(10, 2) DEFAULT 0 CHECK ( valoreRata >= 0 ),
    `inizioPagamento` DATE NOT NULL,
    `importoPagato` DECIMAL(15, 2) DEFAULT 0,
    `id_utente` INT NOT NULL,
    CONSTRAINT `fk_finanziamento_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=LATIN1;

-- Inserimento utenti
INSERT INTO utenti (nome, cognome, dataNascita, toponimo, indirizzo, numeroCivico, codiceFiscale, mail, prefisso, telefono, password, passPhrase, OTP, id_permesso) VALUES
('Lorenzo', 'Molteni', '2006-04-29', 'Via', 'Morigiola', 10, 'RSSMRA80A01H501Z', 'moltenilorenzo6@gmail.com', '+39', '3331234567', 'segreta', 'adminSecret', FALSE, 1),
('Laura', 'Neri', '1990-01-01', 'Via', 'Centrale', 5, 'NRALRA90A01H501Z', 'laura.neri@banca.it', '+39', '3311112222', 'adminPass', 'newAdminSecret', FALSE, 1),
('Simone', 'Verdi', '1959-12-28', 'Piazza', 'Garibaldi', 3, 'VRDSMN80A01H501Z', 'simone.verdi@support.it', '+39', '3391234567', 'supportPass', 'supportPhrase', TRUE, 2),
('Lucia', 'Conti', '1952-03-30', 'Via', 'Roma', 7, 'CNTLCU85B41H501Z', 'lucia.conti@advisor.it', '+39', '3405556677', 'advisorPass', 'advisorPhrase', TRUE, 4),
('Giulia', 'Bianchi', '1995-05-15', 'Corso', 'Italia', 21, 'BNCGLI95E55F205W', 'giulia@banca.it', '+39', '3339876543', 'userPass456', 'userSecret', TRUE, 3),
('Eugenia', 'Caracciolo', '2003-04-07', 'Piazza', 'Borgo Garozzo', 71, 'LBcbfn21A81H501Z', 'eugenia.caracciolo@banca.it', '+39', '0265423511', 'passEugenia', 'phraseEugenia', TRUE, 3),
('Rosaria', 'Bertolucci', '1980-01-06', 'Borgo', 'Rotonda Pasquale', 36, 'TunPFz41A39H501Z', 'rosaria.bertolucci@banca.it', '+39', '9696532871', 'passRosaria', 'phraseRosaria', TRUE, 3),
('Severino', 'Antonioni', '2000-05-10', 'Strada', 'Contrada Giradello', 190, 'fUFeWI03A91H501Z', 'severino.antonioni@banca.it', '+39', '4657871331', 'passSeverino', 'phraseSeverino', TRUE, 3),
('Rembrandt', 'Vigliotti', '1996-06-27', 'Strada', 'Canale Adriana', 109, 'AFEnzd01A32H501Z', 'rembrandt.vigliotti@banca.it', '+39', '2343098050', 'passRembrandt', 'phraseRembrandt', TRUE, 3),
('Ippazio', 'Cuda', '1971-05-04', 'Viale', 'Vicolo Trupiano', 24, 'iqhgVJ08A41H501Z', 'ippazio.cuda@banca.it', '+39', '1640052427', 'passIppazio', 'phraseIppazio', TRUE, 3),
('Gianfrancesco', 'Filippelli', '1980-06-08', 'Viale', 'Contrada Gentilini', 130, 'vreXrw30A36H501Z', 'gianfrancesco.filippelli@banca.it', '+39', '0196556981', 'passGianfrancesco', 'phraseGianfrancesco', TRUE, 3),
('Salvatore', 'Donarelli', '1995-03-06', 'Canale', 'Contrada Uberto', 51, 'RtoZmj95A13H501Z', 'salvatore.donarelli@banca.it', '+39', '6320163287', 'passSalvatore', 'phraseSalvatore', FALSE, 3),
('Beatrice', 'Pacillo', '2001-04-26', 'Viale', 'Incrocio Vigliotti', 109, 'tqohUm55A74H501Z', 'beatrice.pacillo@banca.it', '+39', '3749894134', 'passBeatrice', 'phraseBeatrice', FALSE, 3);

-- Inserimento conti
INSERT INTO conti (IBAN, saldo, stato, valuta, id_utente, id_consulente) VALUES
('IT60X0542811101000000123456', 8923748374.00, TRUE, 'EUR', 1, 1),
('IT60X0542811101000000654321', 2500.00, TRUE, 'EUR', 5, 1),
('IT60X0542811101000000000001', 1500.00, TRUE, 'EUR', 6, 4),
('IT60X0542811101000000000002', 3200.00, TRUE, 'EUR', 7, 4),
('IT60X0542811101000000000003', 2750.00, TRUE, 'EUR', 8, 4),
('IT60X0542811101000000000004', 4000.00, TRUE, 'EUR', 9, 4),
('IT60X0542811101000000000005', 1800.00, TRUE, 'EUR', 10, 4),
('IT60X0542811101000000000006', 2200.00, TRUE, 'EUR', 11, 4),
('IT60X0542811101000000000007', 3100.00, TRUE, 'EUR', 12, 4),
('IT60X0542811101000000000008', 2900.00, TRUE, 'EUR', 13, 4),
('IT60X0542811101000000000009', 2600.00, TRUE, 'EUR', 4, 4);

-- Utente qualunque
INSERT INTO utenti (nome, cognome, dataNascita, toponimo, indirizzo, numeroCivico, codiceFiscale, mail, prefisso, telefono, password, passPhrase, OTP, id_permesso)
VALUES ('Giulia', 'Bianchi', '1995-05-15', 'Corso', 'Italia', 21,
        'BNCGLI95E55F205W', 'giulia@banca.it', '+39', '3339876543',
        'userPass456', 'userSecret', TRUE, 3
       );

-- Conto per admin (idUtente = 1)
INSERT INTO conti (IBAN, saldo, stato, valuta, id_utente, id_consulente)
VALUES (
        'IT60X0542811101000000123456', 8923748374.00, TRUE, 'EUR', 1, 1
       );

-- Conto per utente qualunque (idUtente = 2, id_consulente = 1 admin)
INSERT INTO conti (IBAN, saldo, stato, valuta, id_utente, id_consulente)
VALUES (
        'IT60X0542811101000000654321', 2500.00, TRUE, 'EUR', 2, 1
       );

-- Carta di credito per admin
INSERT INTO carte (numeroCarta, CVV, dataScadenza, PIN, tipo, saldoDisponibile, saldoContabile, IBAN)
VALUES (
        '1234567812345678', '123', '2028-12-31', '11111', 'credito', 10000.00, 10000.00, 'IT60X0542811101000000123456'
       );

-- Carta di credito per utente qualunque
INSERT INTO carte (numeroCarta, CVV, dataScadenza, PIN, tipo, saldoDisponibile, saldoContabile, IBAN)
VALUES (
        '8765432187654321', '321', '2027-10-30', '22222', 'credito', 2500.00, 2500.00, 'IT60X0542811101000000654321'
       );

-- Transazioni specifiche per Eryxis Bank S.P.A.
INSERT INTO `transazioni` (`importo`, `dataTransazione`, `tipo`, `destinatario`, `IBAN`) VALUES
-- Carte di debito (14.99)
(-14.99, '2025-03-29 07:33:18', 'addebito', 'Eryxis Bank S.P.A.', 'IT60X0542811101000000000001'),
(-14.99, '2025-04-03 13:49:55', 'addebito', 'Eryxis Bank S.P.A.', 'IT60X0542811101000000000002'),
(-14.99, '2025-04-10 21:17:40', 'addebito', 'Eryxis Bank S.P.A.', 'IT60X0542811101000000000003'),
(-14.99, '2025-04-15 06:02:11', 'addebito', 'Eryxis Bank S.P.A.', 'IT60X0542811101000000123456'),

-- Carte prepagate (4.99)
(-4.99, '2025-04-22 12:58:33', 'addebito', 'Eryxis Bank S.P.A.', 'IT60X0542811101000000000001'),
(-4.99, '2025-04-27 15:40:08', 'addebito', 'Eryxis Bank S.P.A.', 'IT60X0542811101000000000002'),
(-4.99, '2025-05-01 19:25:50', 'addebito', 'Eryxis Bank S.P.A.', 'IT60X0542811101000000000003'),
(-4.99, '2025-05-02 08:01:12', 'addebito', 'Eryxis Bank S.P.A.', 'IT60X0542811101000000123456');