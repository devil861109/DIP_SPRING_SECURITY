-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- SO del servidor:              Win64
-- HeidiSQL Versión:             11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

USE `springsecurity`;

-- Volcando estructura para tabla agenda.c_tipo_contacto
DROP TABLE IF EXISTS `cat_contact_type`;
CREATE TABLE IF NOT EXISTS `cat_contact_type` (
  `cct_contact_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `cct_name` varchar(50) NOT NULL,
  `cct_status` varchar(50) NOT NULL,
  PRIMARY KEY (`cct_contact_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Volcando datos para la tabla agenda.c_tipo_contacto: ~0 rows (aproximadamente)
/*!40000 ALTER TABLE `cat_contact_type` DISABLE KEYS */;
INSERT INTO `cat_contact_type` (`cct_contact_type_id`, `cct_name`, `cct_status`) VALUES
	(1, 'Familiar', 'ACTIVO'),
	(2, 'Escolar', 'ACTIVO'),
	(3, 'Laboral', 'ACTIVO');
/*!40000 ALTER TABLE `cat_contact_type` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;