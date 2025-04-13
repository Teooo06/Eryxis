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
    `dataNascita` DATE DEFAULT CURRENT_DATE CHECK ( dataNascita BETWEEN '1900-01-01' AND CURRENT_DATE ),
    `indirizzo` VARCHAR(100) NOT NULL,
    `codiceFiscale` VARCHAR(16) UNIQUE NOT NULL,
    `mail` VARCHAR(150) UNIQUE NOT NULL,
    `prefisso` VARCHAR(6) NOT NULL,
    `telefono` VARCHAR(15) NOT NULL,
    `password` VARCHAR(100) UNIQUE NOT NULL,
    `passPhrase` VARCHAR(255) UNIQUE NOT NULL,
    `OTP` TINYINT(1) DEFAULT 1,
    `id_permesso` INT NOT NULL,
    CONSTRAINT `fk_utente_permesso` FOREIGN KEY (`id_permesso`) REFERENCES `permessi`(`idPermesso`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Create conti table next since other tables reference it
CREATE TABLE `conti` (
    `IBAN` CHAR(27) PRIMARY KEY,
    `SWIFT` VARCHAR(8) DEFAULT 'ERXSITMM',
    `saldo` DECIMAL(20, 2) DEFAULT 0,
    `stato` TINYINT(1) DEFAULT 1,
    `valuta` VARCHAR(3) NOT NULL,
    `dataApertura` DATE DEFAULT CURRENT_DATE,
    `id_utente` INT NOT NULL,
    `id_consulente` INT NOT NULL,
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
    `idLog` INT PRIMARY KEY,
    `dataModifica` TIMESTAMP DEFAULT CURRENT_TIMESTAMP CHECK ( dataModifica >= conti.dataApertura ),
    `tipoModifica` VARCHAR(50) NOT NULL,
    `descrizione` VARCHAR(255) NOT NULL,
    `id_utente` INT NOT NULL,
    CONSTRAINT `fk_log_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `transazioni`(
    `idTransazione` INT PRIMARY KEY AUTO_INCREMENT,
    `importo` DECIMAL(20, 2) DEFAULT 0 CHECK ( importo > 0 ),
    `dataTransazione` DATE DEFAULT CURRENT_TIMESTAMP,
    `tipo` VARCHAR(10) NOT NULL,
    `destinatario` VARCHAR(100) NOT NULL,
    `IBAN` CHAR(27) NOT NULL,
    CONSTRAINT `fk_transazione_conto` FOREIGN KEY (`IBAN`) REFERENCES `conti`(`IBAN`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `carte`(
    `numeroCarta` VARCHAR(16) PRIMARY KEY,
    `CVV` VARCHAR(4) UNIQUE NOT NULL,
    `dataScadenza` DATE NOT NULL CHECK ( dataScadenza >= CURRENT_DATE ),
    `PIN` CHAR(5) UNIQUE NOT NULL,
    `tipo` VARCHAR(9) NOT NULL,
    `saldoDisponibile` DECIMAL(20, 2) DEFAULT 0 CHECK ( saldoDisponibile >= 0 ),
    `saldoContabile` DECIMAL(20, 2) DEFAULT 0,
    `circuito` VARCHAR(3),
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
    `dataAcquisto` DATE DEFAULT CURRENT_DATE,
    `quantitaDenaro` DECIMAL(20, 2) DEFAULT 0 CHECK ( quantitaDenaro > 0 ),
    `quantitaTotale` INT DEFAULT 0 CHECK ( quantitaTotale > 0 ),
    `id_utente` INT NOT NULL,
    CONSTRAINT `fk_investimento_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `finanziamenti`(
    `idFinanziamento` INT PRIMARY KEY AUTO_INCREMENT,
    `tipo` VARCHAR(8) NOT NULL,
    `importo` DECIMAL(20, 2) DEFAULT 0 CHECK ( importo > 0 ),
    `dataErogazione` DATE DEFAULT CURRENT_DATE,
    `interessi` DECIMAL(15, 2) DEFAULT 0 CHECK ( interessi >= 0 ),
    `spesaIncasso` DECIMAL(5, 2) DEFAULT 2.5,
    `tipoRata` VARCHAR(20) NOT NULL,
    `valoreRata` DECIMAL(10, 2) DEFAULT 0 CHECK ( valoreRata >= 0 ),
    `inizioPagamento` DATE NOT NULL CHECK ( inizioPagamento >= finanziamenti.dataErogazione ),
    `importoPagato` DECIMAL(15, 2) DEFAULT 0,
    `id_utente` INT NOT NULL,
    CONSTRAINT `fk_finanziamento_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;