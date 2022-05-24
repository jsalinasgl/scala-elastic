USE elasticdb;

DROP TABLE IF EXISTS `players`;
CREATE TABLE `players` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
    `age` int(11) DEFAULT NULL,
    `nationality` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
    `overall` int(11) DEFAULT NULL,
    `club` varchar(256) COLLATE utf8_unicode_ci DEFAULT NULL,
    `value` varchar(10) COLLATE utf8_unicode_ci DEFAULT '0',
    `foot` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
    `number` int(11) DEFAULT NULL,
    `position` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
    `last_modified` datetime NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

LOAD DATA INFILE "/docker-entrypoint-initdb.d/FIFA22_official_data_edited.csv"
    INTO TABLE players
    FIELDS TERMINATED BY ';'
    LINES TERMINATED BY '\r\n'
    IGNORE 1 LINES
(name, age, nationality, overall, club, value, foot, number, position);
