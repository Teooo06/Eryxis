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
                           `idPermesso` INT PRIMARY KEY NOT NULL,
                           `ruolo` VARCHAR(10) NOT NULL,
                           `codicePermesso` VARCHAR(2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Now create utenti table
CREATE TABLE `utenti`(
                         `idUtente` INT PRIMARY KEY NOT NULL,
                         `nome` VARCHAR(50) NOT NULL,
                         `cognome` VARCHAR(50) NOT NULL,
                         `dataNascita` DATE DEFAULT CURRENT_DATE,
                         `indirizzo` VARCHAR(100) NOT NULL,
                         `codiceFiscale` VARCHAR(16) UNIQUE NOT NULL,
                         `mail` VARCHAR(150) UNIQUE NOT NULL,
                         `prefisso` VARCHAR(6) NOT NULL,
                         `telefono` VARCHAR(15) NOT NULL,
                         `password` VARCHAR(100) UNIQUE NOT NULL,
                         `passPhrase` VARCHAR(255) UNIQUE NOT NULL,
                         `id_permesso` INT NOT NULL,
                         CONSTRAINT `fk_utente_permesso` FOREIGN KEY (`id_permesso`) REFERENCES `permessi`(`idPermesso`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Create conti table next since other tables reference it
CREATE TABLE `conti` (
                         `IBAN` VARCHAR(27) PRIMARY KEY NOT NULL,
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
                          `idTicket` INT PRIMARY KEY,
                          `titolo` VARCHAR(20) NOT NULL,
                          `descrizione` VARCHAR(500) NOT NULL,
                          `stato` VARCHAR(1) NOT NULL,
                          `id_utente` INT NOT NULL,
                          CONSTRAINT `fk_ticket_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `rubriche`(
                           `idContatto` INT PRIMARY KEY NOT NULL,
                           `nome` VARCHAR(100) NOT NULL,
                           `cognome` VARCHAR(100) NOT NULL,
                           `IBAN` VARCHAR(27) UNIQUE NOT NULL,
                           `id_utente` INT NOT NULL,
                           CONSTRAINT `fk_rubrica_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `logs`(
                       `idLog` INT PRIMARY KEY NOT NULL,
                       `dataModifica` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       `tipoModifica` VARCHAR(50) NOT NULL,
                       `descrizione` VARCHAR(255) NOT NULL,
                       `id_utente` INT NOT NULL,
                       CONSTRAINT `fk_log_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `transazioni`(
                              `idTransazione` INT PRIMARY KEY NOT NULL,
                              `importo` DECIMAL(20, 2) DEFAULT 0,
                              `dataTransazione` DATE DEFAULT CURRENT_DATE,
                              `tipo` VARCHAR(10) NOT NULL,
                              `destinatario` VARCHAR(100) NOT NULL,
                              `IBAN` VARCHAR(27) NOT NULL,
                              CONSTRAINT `fk_transazione_conto` FOREIGN KEY (`IBAN`) REFERENCES `conti`(`IBAN`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `carte`(
                        `numeroCarta` VARCHAR(16) PRIMARY KEY NOT NULL,
                        `CVV` VARCHAR(4) UNIQUE NOT NULL,
                        `dataScadenza` DATE NOT NULL,
                        `PIN` VARCHAR(5) UNIQUE NOT NULL,
                        `tipo` VARCHAR(9) NOT NULL,
                        `saldoDisponibile` DECIMAL(20, 2) DEFAULT 0,
                        `saldoContabile` DECIMAL(20, 2) DEFAULT 0,
                        `circuito` VARCHAR(3),
                        `IBAN` VARCHAR(27) NOT NULL,
                        CONSTRAINT `fk_carta_conto` FOREIGN KEY (`IBAN`) REFERENCES `conti`(`IBAN`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `investimenti`(
                               `ISIN` VARCHAR(12) PRIMARY KEY NOT NULL,
                               `settore` VARCHAR(50) NOT NULL,
                               `divisa` VARCHAR(50) NOT NULL,
                               `tipo` VARCHAR(11) NOT NULL,
                               `nomeTitolo` VARCHAR(50) NOT NULL,
                               `descrizione` VARCHAR(255) NOT NULL,
                               `dataAcquisto` DATE DEFAULT CURRENT_DATE,
                               `quantitaDenaro` DECIMAL(20, 2) DEFAULT 0,
                               `quantitaTotale` INT DEFAULT 0,
                               `id_utente` INT NOT NULL,
                               CONSTRAINT `fk_investimento_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `finanziamenti`(
                                `idFinanziamento` INT PRIMARY KEY NOT NULL,
                                `tipo` VARCHAR(8) NOT NULL,
                                `importo` DECIMAL(20, 2) DEFAULT 0,
                                `dataErogazione` DATE DEFAULT CURRENT_DATE,
                                `interessi` DECIMAL(15, 2) DEFAULT 0,
                                `spesaIncasso` DECIMAL(5, 2) DEFAULT 2.5,
                                `tipoRata` VARCHAR(20) NOT NULL,
                                `valoreRata` DECIMAL(10, 2) DEFAULT 0,
                                `inizioPagamento` DATE NOT NULL,
                                `importoPagato` DECIMAL(15, 2) DEFAULT 0,
                                `id_utente` INT NOT NULL,
                                CONSTRAINT `fk_finanziamento_utente` FOREIGN KEY (`id_utente`) REFERENCES `utenti`(`idUtente`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;