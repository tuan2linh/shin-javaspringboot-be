-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: shinchan
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9emlp6m95v5er2bcqkjsw48he` (`user_id`),
  CONSTRAINT `FKg5uhi8vpsuy0lgloxk2h4w5o6` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (1,1),(2,2),(3,3);
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_item`
--

DROP TABLE IF EXISTS `cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `so_luong` int NOT NULL,
  `cart_id` bigint DEFAULT NULL,
  `san_pham_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1uobyhgl1wvgt1jpccia8xxs3` (`cart_id`),
  KEY `FKe2n1qh7jac82b6ylci206qav` (`san_pham_id`),
  CONSTRAINT `FK1uobyhgl1wvgt1jpccia8xxs3` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`),
  CONSTRAINT `FKe2n1qh7jac82b6ylci206qav` FOREIGN KEY (`san_pham_id`) REFERENCES `san_pham` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_item`
--

LOCK TABLES `cart_item` WRITE;
/*!40000 ALTER TABLE `cart_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chi_tiet_don_hang`
--

DROP TABLE IF EXISTS `chi_tiet_don_hang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chi_tiet_don_hang` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `don_gia` double NOT NULL,
  `so_luong` int NOT NULL,
  `don_hang_id` bigint DEFAULT NULL,
  `san_pham_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt57maavf6s28hxyar724mdr1b` (`don_hang_id`),
  KEY `FKnlr5jpmfbvgad2w40cqo54mwg` (`san_pham_id`),
  CONSTRAINT `FKnlr5jpmfbvgad2w40cqo54mwg` FOREIGN KEY (`san_pham_id`) REFERENCES `san_pham` (`id`),
  CONSTRAINT `FKt57maavf6s28hxyar724mdr1b` FOREIGN KEY (`don_hang_id`) REFERENCES `don_hang` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chi_tiet_don_hang`
--

LOCK TABLES `chi_tiet_don_hang` WRITE;
/*!40000 ALTER TABLE `chi_tiet_don_hang` DISABLE KEYS */;
INSERT INTO `chi_tiet_don_hang` VALUES (1,150000,2,1,1),(2,80000,1,1,3),(3,220000,2,2,2),(4,80000,1,2,3),(5,220000,2,3,2),(6,80000,1,3,3),(7,220000,1,4,2);
/*!40000 ALTER TABLE `chi_tiet_don_hang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `don_hang`
--

DROP TABLE IF EXISTS `don_hang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `don_hang` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_hidden` bit(1) NOT NULL,
  `ngay_dat` date DEFAULT NULL,
  `tong_tien` double NOT NULL,
  `trang_thai` enum('DONE','PENDING','PROCESSING') DEFAULT NULL,
  `khach_hang_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKeyporccvr9mq4k9j4fc5wpum5` (`khach_hang_id`),
  CONSTRAINT `FKeyporccvr9mq4k9j4fc5wpum5` FOREIGN KEY (`khach_hang_id`) REFERENCES `khach_hang` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `don_hang`
--

LOCK TABLES `don_hang` WRITE;
/*!40000 ALTER TABLE `don_hang` DISABLE KEYS */;
INSERT INTO `don_hang` VALUES (1,_binary '\0','2025-05-04',380000,'PENDING',1),(2,_binary '','2025-05-04',520000,'PROCESSING',1),(3,_binary '\0','2025-05-04',520000,'PENDING',1),(4,_binary '\0','2025-05-04',220000,'PENDING',1);
/*!40000 ALTER TABLE `don_hang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `khach_hang`
--

DROP TABLE IF EXISTS `khach_hang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `khach_hang` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dia_chi` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `ho_ten` varchar(255) DEFAULT NULL,
  `so_dien_thoai` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKmbn9m88sbsnbjkxjf4jhddeey` (`user_id`),
  CONSTRAINT `FKhqgywy19kkqmqkgxixoy54jwf` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `khach_hang`
--

LOCK TABLES `khach_hang` WRITE;
/*!40000 ALTER TABLE `khach_hang` DISABLE KEYS */;
INSERT INTO `khach_hang` VALUES (1,'Khu A','user1@gmail.com','User 1','0123456788',1);
/*!40000 ALTER TABLE `khach_hang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refresh_token`
--

DROP TABLE IF EXISTS `refresh_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refresh_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKf95ixxe7pa48ryn1awmh2evt7` (`user_id`),
  CONSTRAINT `FKjtx87i0jvq2svedphegvdwcuy` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_token`
--

LOCK TABLES `refresh_token` WRITE;
/*!40000 ALTER TABLE `refresh_token` DISABLE KEYS */;
INSERT INTO `refresh_token` VALUES (15,'2025-05-11 14:59:55.913464','820e4e45-86fe-4ee1-a484-b6fe2d99010f',2),(19,'2025-05-11 19:35:45.748852','07e60363-927e-4c5e-aed4-8a06cd976433',1),(20,'2025-05-11 21:08:02.040590','39c8cb2a-7ec8-4c01-beea-2f13aed1ac46',3);
/*!40000 ALTER TABLE `refresh_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `san_pham`
--

DROP TABLE IF EXISTS `san_pham`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `san_pham` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `gia` double NOT NULL,
  `hinh_anh` varchar(255) DEFAULT NULL,
  `mota` varchar(1000) DEFAULT NULL,
  `ten` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `san_pham`
--

LOCK TABLES `san_pham` WRITE;
/*!40000 ALTER TABLE `san_pham` DISABLE KEYS */;
INSERT INTO `san_pham` VALUES (1,150000,'https://down-vn.img.susercontent.com/file/vn-11134207-7ra0g-m7xj7z7botqf49','Áo thun cotton 100% với hình Shin nhí nhảnh, thích hợp cho mọi giới tính.','Áo thun Shin tạo dáng hài'),(2,220000,'https://th.bing.com/th/id/OIP.hNsM7aTHVaRi8PEbTUH2SAHaHa?rs=1&pid=ImgDetMain','Gấu bông Shin mềm mịn, cao 40cm, mặc đồ ngủ siêu đáng yêu.','Gấu bông Shin phiên bản ngủ'),(3,80000,'https://th.bing.com/th/id/OIP.wx2PSTwEFya_01N7lkvbAwHaHa?w=189&h=190&c=7&r=0&o=5&pid=1.7','Hộp bút bền, in hình Shin với biểu cảm độc đáo, có khóa kéo.','Hộp bút Shin mặt troll'),(4,45000,'https://down-vn.img.susercontent.com/file/sg-11134201-7rbl2-lmobnjau5c8394','Móc khóa cao su hình Shin mặc đồ siêu nhân Action Kamen.','Móc khóa Shin mặc đồ siêu nhân'),(5,120000,'https://down-vn.img.susercontent.com/file/sg-11134201-7rbl2-lmobnjau5c8394','https://down-vn.img.susercontent.com/file/vn-11134201-7qukw-ljf90zezuo7g1e','Ốp lưng Shin mặt ngầu – iPhone 13');
/*!40000 ALTER TABLE `san_pham` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `san_pham_the_loai`
--

DROP TABLE IF EXISTS `san_pham_the_loai`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `san_pham_the_loai` (
  `san_pham_id` bigint NOT NULL,
  `the_loai_id` bigint NOT NULL,
  KEY `FKn75ro9dkopi9ufu3v2kps74co` (`the_loai_id`),
  KEY `FK335ls1d2nufttak2gqm0l1ram` (`san_pham_id`),
  CONSTRAINT `FK335ls1d2nufttak2gqm0l1ram` FOREIGN KEY (`san_pham_id`) REFERENCES `san_pham` (`id`),
  CONSTRAINT `FKn75ro9dkopi9ufu3v2kps74co` FOREIGN KEY (`the_loai_id`) REFERENCES `the_loai` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `san_pham_the_loai`
--

LOCK TABLES `san_pham_the_loai` WRITE;
/*!40000 ALTER TABLE `san_pham_the_loai` DISABLE KEYS */;
INSERT INTO `san_pham_the_loai` VALUES (1,1),(2,2),(3,3),(4,4),(5,5);
/*!40000 ALTER TABLE `san_pham_the_loai` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `the_loai`
--

DROP TABLE IF EXISTS `the_loai`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `the_loai` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ten` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `the_loai`
--

LOCK TABLES `the_loai` WRITE;
/*!40000 ALTER TABLE `the_loai` DISABLE KEYS */;
INSERT INTO `the_loai` VALUES (1,'Áo thun Shin'),(2,'Gấu bông Shin'),(3,'Đồ dùng học tập Shin'),(4,'Móc khóa Shin'),(5,'Ốp điện thoại Shin');
/*!40000 ALTER TABLE `the_loai` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `password` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','USER') DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'$2a$10$83Pl2/2rW1gqXqJxgKo.su9ykZTfY14jwzA/hGjoo6QVMrT..Oa56','USER','user1'),(2,'$2a$10$/aATnMumhRe8cJm4.r8/d.JwkUiDnrc/mkCDeM9xnpGTrKPG36I0y','USER','user2'),(3,'$2a$10$uQMAGv62MMSQnGirkfBTCu.zFDOkQyTm2EwC2WQeG6TU/Ub8O7dt.','ADMIN','admin');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-05 22:19:13
