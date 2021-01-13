CREATE DATABASE  IF NOT EXISTS `risiko_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `risiko_db`;
-- MySQL dump 10.13  Distrib 8.0.22, for Linux (x86_64)
--
-- Host: localhost    Database: risiko_db
-- ------------------------------------------------------
-- Server version	8.0.22-0ubuntu0.20.10.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `adiacenza`
--

DROP TABLE IF EXISTS `adiacenza`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `adiacenza` (
                             `stato1` bigint NOT NULL,
                             `stato2` bigint NOT NULL,
                             PRIMARY KEY (`stato1`,`stato2`),
                             KEY `adiacenza_FK_1` (`stato2`),
                             CONSTRAINT `adiacenza_FK` FOREIGN KEY (`stato1`) REFERENCES `stati` (`id`),
                             CONSTRAINT `adiacenza_FK_1` FOREIGN KEY (`stato2`) REFERENCES `stati` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `adiacenza`
--

LOCK TABLES `adiacenza` WRITE;
/*!40000 ALTER TABLE `adiacenza` DISABLE KEYS */;
INSERT INTO `adiacenza` VALUES (4,1),(5,1),(4,2),(7,2),(8,2),(9,2),(10,2),(11,2),(12,2),(4,3),(5,3),(6,3),(7,3),(1,4),(2,4),(3,4),(5,4),(7,4),(1,5),(3,5),(4,5),(6,5),(24,5),(3,6),(5,6),(7,6),(2,7),(3,7),(4,7),(6,7),(8,7),(2,8),(7,8),(9,8),(19,8),(2,9),(8,9),(12,9),(19,9),(2,10),(11,10),(23,10),(2,11),(10,11),(12,11),(2,12),(9,12),(11,12),(14,12),(19,12),(38,12),(39,12),(14,13),(15,13),(16,13),(17,13),(19,13),(12,14),(13,14),(15,14),(19,14),(37,14),(38,14),(13,15),(14,15),(16,15),(37,15),(13,16),(15,16),(17,16),(18,16),(13,17),(16,17),(18,17),(19,17),(16,18),(17,18),(26,18),(8,19),(9,19),(12,19),(13,19),(14,19),(17,19),(21,20),(22,20),(23,20),(20,21),(22,21),(20,22),(21,22),(23,22),(10,23),(20,23),(22,23),(5,24),(25,24),(27,24),(24,25),(26,25),(27,25),(28,25),(18,26),(25,26),(28,26),(29,26),(24,27),(25,27),(28,27),(30,27),(25,28),(26,28),(27,28),(29,28),(30,28),(31,28),(26,29),(28,29),(31,29),(27,30),(28,30),(31,30),(32,30),(28,31),(29,31),(30,31),(32,31),(30,32),(31,32),(35,32),(34,33),(35,33),(36,33),(37,33),(33,34),(35,34),(36,34),(32,35),(33,35),(34,35),(33,36),(34,36),(14,37),(15,37),(33,37),(38,37),(39,37),(40,37),(12,38),(14,38),(37,38),(39,38),(12,39),(37,39),(38,39),(40,39),(41,39),(42,39),(37,40),(39,40),(42,40),(39,41),(42,41),(39,42),(40,42),(41,42);
/*!40000 ALTER TABLE `adiacenza` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `continenti`
--

DROP TABLE IF EXISTS `continenti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `continenti` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `nome` varchar(50) NOT NULL,
                              `armateBonus` int NOT NULL,
                              `mappa_id` bigint NOT NULL,
                              PRIMARY KEY (`id`),
                              KEY `continenti_FK` (`mappa_id`),
                              CONSTRAINT `continenti_FK` FOREIGN KEY (`mappa_id`) REFERENCES `mappe` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `continenti`
--

LOCK TABLES `continenti` WRITE;
/*!40000 ALTER TABLE `continenti` DISABLE KEYS */;
INSERT INTO `continenti` VALUES (1,'Asia',7,1),(2,'Europa',5,1),(3,'Oceania',2,1),(4,'America del Nord',5,1),(5,'America del Sud',2,1),(6,'Africa',3,1);
/*!40000 ALTER TABLE `continenti` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mappe`
--

DROP TABLE IF EXISTS `mappe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mappe` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `nome` varchar(50) NOT NULL,
                         `descrizione` varchar(100) NOT NULL,
                         `numMinGiocatori` int NOT NULL,
                         `numMaxGiocatori` int NOT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mappe`
--

LOCK TABLES `mappe` WRITE;
/*!40000 ALTER TABLE `mappe` DISABLE KEYS */;
INSERT INTO `mappe` VALUES (1,'Risiko Classic','La classica mappa del Risiko',3,6);
/*!40000 ALTER TABLE `mappe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stati`
--

DROP TABLE IF EXISTS `stati`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stati` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `nome` varchar(50) NOT NULL,
                         `continente_id` bigint NOT NULL,
                         PRIMARY KEY (`id`),
                         KEY `stati_FK` (`continente_id`),
                         CONSTRAINT `stati_FK` FOREIGN KEY (`continente_id`) REFERENCES `continenti` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stati`
--

LOCK TABLES `stati` WRITE;
/*!40000 ALTER TABLE `stati` DISABLE KEYS */;
INSERT INTO `stati` VALUES (1,'Giappone',1),(2,'Cina',1),(3,'Cita',1),(4,'Mongolia',1),(5,'Kamchatka',1),(6,'Jacuzia',1),(7,'Siberia',1),(8,'Urali',1),(9,'Afghanistan',1),(10,'Siam',1),(11,'India',1),(12,'Medio Oriente',1),(13,'Europa settentrionale',2),(14,'Europa meridionale',2),(15,'Europa occidentale',2),(16,'Gran Bretagna',2),(17,'Scandinavia',2),(18,'Islanda',2),(19,'Ucraina',2),(20,'Australia occidentale',3),(21,'Australia orientale',3),(22,'Nuova Guinea',3),(23,'Indonesia',3),(24,'Alaska',4),(25,'Territori del Nord Ovest',4),(26,'Groenlandia',4),(27,'Alberta',4),(28,'Ontario',4),(29,'Quebec',4),(30,'Stati Uniti Occidentali',4),(31,'Stati Uniti Orientali',4),(32,'America centrale',4),(33,'Brasile',5),(34,'Perù',5),(35,'Venezuela',5),(36,'Argentina',5),(37,'Africa del Nord',6),(38,'Egitto',6),(39,'Africa orientale',6),(40,'Congo',6),(41,'Madagascar',6),(42,'Africa del Sud',6);
/*!40000 ALTER TABLE `stati` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-01-13 10:26:56