CREATE DATABASE  IF NOT EXISTS `pet_care_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `pet_care_db`;
-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: pet_care_db
-- ------------------------------------------------------
-- Server version	8.0.39

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
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `admin_id` bigint NOT NULL,
  PRIMARY KEY (`admin_id`),
  CONSTRAINT `FK1yevfomeqligdjkhp9g3e2d7r` FOREIGN KEY (`admin_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `appointment`
--

DROP TABLE IF EXISTS `appointment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `appointment_date` date DEFAULT NULL,
  `appointment_no` varchar(255) DEFAULT NULL,
  `appointment_time` time(6) DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `status` enum('APPROVED','CANCELLED','COMPLETED','NOT_APPROVED','ON_GOING','PENDING','UP_COMING','WAITING_FOR_APPROVAL') DEFAULT NULL,
  `sender` bigint DEFAULT NULL,
  `recipient` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8gpakx6yhbqxe18vi8x7ry3av` (`sender`),
  KEY `FKmgteimbnqxf2hvb5yejefa2mn` (`recipient`),
  CONSTRAINT `FK8gpakx6yhbqxe18vi8x7ry3av` FOREIGN KEY (`sender`) REFERENCES `user` (`id`),
  CONSTRAINT `FKmgteimbnqxf2hvb5yejefa2mn` FOREIGN KEY (`recipient`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pet`
--

DROP TABLE IF EXISTS `pet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pet` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `age` int NOT NULL,
  `breed` varchar(255) DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `appointment_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKagw6y0jkrhnhw9f1tgaq0f0ql` (`appointment_id`),
  CONSTRAINT `FKagw6y0jkrhnhw9f1tgaq0f0ql` FOREIGN KEY (`appointment_id`) REFERENCES `appointment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pet_owner`
--

DROP TABLE IF EXISTS `pet_owner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pet_owner` (
  `pet_id` bigint NOT NULL,
  PRIMARY KEY (`pet_id`),
  CONSTRAINT `FK6njyfs1qcgnyv7k8x4n97rgtn` FOREIGN KEY (`pet_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `photo`
--

DROP TABLE IF EXISTS `photo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `photo` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `image` longblob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `feedback` varchar(255) DEFAULT NULL,
  `stars` int NOT NULL,
  `reviewer_id` bigint DEFAULT NULL,
  `veterinarian_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt58e9mdgxpl7j90ketlaosmx4` (`reviewer_id`),
  KEY `FK4ecl9u2hnpevdm76emtvrsu9p` (`veterinarian_id`),
  CONSTRAINT `FK4ecl9u2hnpevdm76emtvrsu9p` FOREIGN KEY (`veterinarian_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKt58e9mdgxpl7j90ketlaosmx4` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKq8w8865ol7f7m85re30va6trr` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `is_enabled` bit(1) NOT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `user_type` varchar(255) DEFAULT NULL,
  `photo_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr3j2vcblstq1wg7ut6htd9qve` (`photo_id`),
  CONSTRAINT `FKc8cowxax7rq5ktaxeybouogky` FOREIGN KEY (`photo_id`) REFERENCES `photo` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  KEY `FKrhfovtciq1l558cw6udg0h0d3` (`role_id`),
  KEY `FK55itppkw3i07do3h7qoclqd4k` (`user_id`),
  CONSTRAINT `FK55itppkw3i07do3h7qoclqd4k` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKrhfovtciq1l558cw6udg0h0d3` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `verification_token`
--

DROP TABLE IF EXISTS `verification_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verification_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiration_date` datetime(6) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrdn0mss276m9jdobfhhn2qogw` (`user_id`),
  CONSTRAINT `FKrdn0mss276m9jdobfhhn2qogw` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `veterinarian`
--

DROP TABLE IF EXISTS `veterinarian`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `veterinarian` (
  `specialization` varchar(255) DEFAULT NULL,
  `vet_id` bigint NOT NULL,
  PRIMARY KEY (`vet_id`),
  CONSTRAINT `FKhaog7xm0mx31nywjvllwtpmem` FOREIGN KEY (`vet_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-05 15:38:51
