DROP TABLE IF EXISTS mappe;
CREATE TABLE mappe
(
    id              bigint       NOT NULL AUTO_INCREMENT,
    nome            varchar(50)  NOT NULL,
    descrizione     varchar(100) NOT NULL,
    numMinGiocatori int          NOT NULL,
    numMaxGiocatori int          NOT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS continenti;
CREATE TABLE continenti
(
    id          bigint      NOT NULL AUTO_INCREMENT,
    nome        varchar(50) NOT NULL,
    armateBonus int         NOT NULL,
    mappa_id    bigint      NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (mappa_id) REFERENCES mappe (id) ON DELETE CASCADE ON UPDATE CASCADE
);


DROP TABLE IF EXISTS stati;

CREATE TABLE stati
(
    id            bigint      NOT NULL AUTO_INCREMENT,
    nome          varchar(50) NOT NULL,
    continente_id bigint      NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (continente_id) REFERENCES continenti (id) ON DELETE CASCADE ON UPDATE CASCADE
);


DROP TABLE IF EXISTS adiacenza;
CREATE TABLE adiacenza
(
    stato1 bigint NOT NULL,
    stato2 bigint NOT NULL,
    PRIMARY KEY (stato1, stato2),
    FOREIGN KEY (stato2) REFERENCES stati (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (stato1) REFERENCES stati (id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO mappe VALUES (1,'Risiko Test','La classica mappa del Risiko',3,6);
INSERT INTO continenti VALUES (1,'Asia',7,1),(2,'Europa',5,1),(3,'Oceania',2,1),(4,'America del Nord',5,1),(5,'America del Sud',2,1),(6,'Africa',3,1);
INSERT INTO stati VALUES (1,'Giappone',1),(2,'Cina',1),(3,'Cita',1),(4,'Mongolia',1),(5,'Kamchatka',1),(6,'Jacuzia',1),(7,'Siberia',1),(8,'Urali',1),(9,'Afghanistan',1),(10,'Siam',1),(11,'India',1),(12,'Medio Oriente',1),(13,'Europa settentrionale',2),(14,'Europa meridionale',2),(15,'Europa occidentale',2),(16,'Gran Bretagna',2),(17,'Scandinavia',2),(18,'Islanda',2),(19,'Ucraina',2),(20,'Australia occidentale',3),(21,'Australia orientale',3),(22,'Nuova Guinea',3),(23,'Indonesia',3),(24,'Alaska',4),(25,'Territori del Nord Ovest',4),(26,'Groenlandia',4),(27,'Alberta',4),(28,'Ontario',4),(29,'Quebec',4),(30,'Stati Uniti Occidentali',4),(31,'Stati Uniti Orientali',4),(32,'America centrale',4),(33,'Brasile',5),(34,'Perù',5),(35,'Venezuela',5),(36,'Argentina',5),(37,'Africa del Nord',6),(38,'Egitto',6),(39,'Africa orientale',6),(40,'Congo',6),(41,'Madagascar',6),(42,'Africa del Sud',6);
INSERT INTO adiacenza VALUES (4,1),(5,1),(4,2),(7,2),(8,2),(9,2),(10,2),(11,2),(12,2),(4,3),(5,3),(6,3),(7,3),(1,4),(2,4),(3,4),(5,4),(7,4),(1,5),(3,5),(4,5),(6,5),(24,5),(3,6),(5,6),(7,6),(2,7),(3,7),(4,7),(6,7),(8,7),(2,8),(7,8),(9,8),(19,8),(2,9),(8,9),(12,9),(19,9),(2,10),(11,10),(23,10),(2,11),(10,11),(12,11),(2,12),(9,12),(11,12),(14,12),(19,12),(38,12),(39,12),(14,13),(15,13),(16,13),(17,13),(19,13),(12,14),(13,14),(15,14),(19,14),(37,14),(38,14),(13,15),(14,15),(16,15),(37,15),(13,16),(15,16),(17,16),(18,16),(13,17),(16,17),(18,17),(19,17),(16,18),(17,18),(26,18),(8,19),(9,19),(12,19),(13,19),(14,19),(17,19),(21,20),(22,20),(23,20),(20,21),(22,21),(20,22),(21,22),(23,22),(10,23),(20,23),(22,23),(5,24),(25,24),(27,24),(24,25),(26,25),(27,25),(28,25),(18,26),(25,26),(28,26),(29,26),(24,27),(25,27),(28,27),(30,27),(25,28),(26,28),(27,28),(29,28),(30,28),(31,28),(26,29),(28,29),(31,29),(27,30),(28,30),(31,30),(32,30),(28,31),(29,31),(30,31),(32,31),(30,32),(31,32),(35,32),(34,33),(35,33),(36,33),(37,33),(33,34),(35,34),(36,34),(32,35),(33,35),(34,35),(33,36),(34,36),(14,37),(15,37),(33,37),(38,37),(39,37),(40,37),(12,38),(14,38),(37,38),(39,38),(12,39),(37,39),(38,39),(40,39),(41,39),(42,39),(37,40),(39,40),(42,40),(39,41),(42,41),(39,42),(40,42),(41,42);
