CREATE DATABASE `parser_schema`;

CREATE TABLE `ip_block_tb` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ip` varchar(45) DEFAULT NULL,
  `block_message` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ;


CREATE TABLE `log_tb` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `log_date` datetime DEFAULT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `request` varchar(45) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `user_agent` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ;

