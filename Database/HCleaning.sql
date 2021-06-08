-- MySQL dump 10.13  Distrib 8.0.23, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: daddb
-- ------------------------------------------------------
-- Server version	8.0.23

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
-- Table structure for table `actuator`
--

DROP TABLE IF EXISTS `actuator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `actuator` (
  `idActuator` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) NOT NULL,
  `Type` varchar(45) NOT NULL,
  `idDevice` int NOT NULL,
  PRIMARY KEY (`idActuator`),
  KEY `fk_ACTUATOR_DEVICE2_idx` (`idDevice`),
  CONSTRAINT `fk_ACTUATOR_DEVICE2` FOREIGN KEY (`idDevice`) REFERENCES `device` (`idDevice`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `actuator`
--

LOCK TABLES `actuator` WRITE;
/*!40000 ALTER TABLE `actuator` DISABLE KEYS */;
INSERT INTO `actuator` VALUES (1,'Led Rojo','LedRojo',1),(2,'Dyson Pure','Purifier',1),(3,'LG Confort S09','AC',1),(4,'Led Verde','LedVerde',1);
/*!40000 ALTER TABLE `actuator` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `actuator_value`
--

DROP TABLE IF EXISTS `actuator_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `actuator_value` (
  `idActuator_Value` int NOT NULL AUTO_INCREMENT,
  `Mode` int NOT NULL,
  `Timestamp` bigint NOT NULL,
  `idActuator` int NOT NULL,
  PRIMARY KEY (`idActuator_Value`),
  KEY `fk_ACTUATOR_VALUE_ACTUATOR2_idx` (`idActuator`,`Timestamp`),
  CONSTRAINT `fk_ACTUATOR_VALUE_ACTUATOR2` FOREIGN KEY (`idActuator`) REFERENCES `actuator` (`idActuator`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `actuator_value`
--

LOCK TABLES `actuator_value` WRITE;
/*!40000 ALTER TABLE `actuator_value` DISABLE KEYS */;
INSERT INTO `actuator_value` VALUES (1,1,1623015774093,1),(2,1,1622998359122,2),(3,1,1622998362301,3),(4,0,1622998427541,4);
/*!40000 ALTER TABLE `actuator_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device`
--

DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device` (
  `idDevice` int NOT NULL AUTO_INCREMENT,
  `IP` varchar(45) NOT NULL,
  `Name` varchar(45) NOT NULL,
  `idRoom` int NOT NULL,
  PRIMARY KEY (`idDevice`),
  KEY `fk_DEVICE_ROOM1_idx` (`idRoom`),
  CONSTRAINT `fk_DEVICE_ROOM1` FOREIGN KEY (`idRoom`) REFERENCES `room` (`idRoom`),
  CONSTRAINT `CHK1_Device` CHECK (regexp_like(`IP`,_utf8mb3'^(([0-9]{1}|[0-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]).){3}([0-9]{1}|[0-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])$'))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
INSERT INTO `device` VALUES (1,'192.168.215.204','ESP8266',1),(2,'192.168.215.203','ESP8266',2),(3,'192.168.215.205','ESP8266',3);
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `idRoom` int NOT NULL AUTO_INCREMENT,
  `LastAccess` bigint NOT NULL,
  PRIMARY KEY (`idRoom`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (1,1621929600),(2,1621929600),(3,1621929600);
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sensor`
--

DROP TABLE IF EXISTS `sensor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sensor` (
  `idSensor` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) NOT NULL,
  `Type` varchar(45) NOT NULL,
  `idDevice` int NOT NULL,
  PRIMARY KEY (`idSensor`),
  KEY `fk_SENSOR_DEVICE1_idx` (`idDevice`),
  CONSTRAINT `fk_SENSOR_DEVICE1` FOREIGN KEY (`idDevice`) REFERENCES `device` (`idDevice`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sensor`
--

LOCK TABLES `sensor` WRITE;
/*!40000 ALTER TABLE `sensor` DISABLE KEYS */;
INSERT INTO `sensor` VALUES (1,'Sharp GP2Y1010','Dust',1),(2,'DHT22','Temperature',1),(3,'DHT22','Humidity',1);
/*!40000 ALTER TABLE `sensor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sensor_value`
--

DROP TABLE IF EXISTS `sensor_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sensor_value` (
  `idSensor_Value` int NOT NULL AUTO_INCREMENT,
  `Value` float NOT NULL,
  `Timestamp` bigint NOT NULL,
  `idSensor` int NOT NULL,
  PRIMARY KEY (`idSensor_Value`),
  KEY `fk_SENSOR_VALUE_SENSOR1_idx` (`idSensor`,`Timestamp`),
  CONSTRAINT `fk_SENSOR_VALUE_SENSOR1` FOREIGN KEY (`idSensor`) REFERENCES `sensor` (`idSensor`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sensor_value`
--

LOCK TABLES `sensor_value` WRITE;
/*!40000 ALTER TABLE `sensor_value` DISABLE KEYS */;
INSERT INTO `sensor_value` VALUES (1,474,1623015623926,1),(2,25.7,1622998412202,2),(3,41.7,1622998417291,3);
/*!40000 ALTER TABLE `sensor_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `surgery`
--

DROP TABLE IF EXISTS `surgery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `surgery` (
  `idSurgery` int NOT NULL AUTO_INCREMENT,
  `TimestampStart` bigint NOT NULL,
  `TimestampEnd` bigint NOT NULL,
  `idRoom` int NOT NULL,
  PRIMARY KEY (`idSurgery`),
  KEY `fk_SURGERY_ROOM2_idx` (`idRoom`),
  CONSTRAINT `fk_SURGERY_ROOM2` FOREIGN KEY (`idRoom`) REFERENCES `room` (`idRoom`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `surgery`
--

LOCK TABLES `surgery` WRITE;
/*!40000 ALTER TABLE `surgery` DISABLE KEYS */;
INSERT INTO `surgery` VALUES (1,20210524080000,1621936800,1),(2,20210524103100,1621945860,1),(3,20210524140000,1621958400,1);
/*!40000 ALTER TABLE `surgery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `idUser` int NOT NULL AUTO_INCREMENT,
  `DNI` varchar(9) NOT NULL,
  `Name` varchar(100) NOT NULL,
  `Address` varchar(255) NOT NULL,
  `Email` varchar(80) NOT NULL,
  `Tlf` varchar(9) NOT NULL,
  `Password` varchar(45) NOT NULL,
  PRIMARY KEY (`idUser`),
  UNIQUE KEY `Email_UNIQUE` (`Email`),
  CONSTRAINT `CHK1_Usuarios` CHECK (regexp_like(`DNI`,_utf8mb3'[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][A-Z]')),
  CONSTRAINT `CHK2_Usuarios` CHECK (regexp_like(`Tlf`,_utf8mb3'[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]')),
  CONSTRAINT `CHK3_Usuarios` CHECK (((length(`Email`) - length(replace(`Email`,_utf8mb4'@',_utf8mb4''))) = 1))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'75854256L','Joaquin','Calle Estrasburgo','joaquin@gmail.com','954253685','joa1234');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-06-08  8:25:21
