DROP USER IF EXISTS 'admin_esam_db'@'localhost';

DROP DATABASE IF EXISTS esam_db;

CREATE DATABASE IF NOT EXISTS esam_db;
CREATE USER admin_esam_db@localhost IDENTIFIED BY "1234";
GRANT ALL PRIVILEGES ON esam_db.* TO 'admin_esam_db'@'localhost';

FLUSH PRIVILEGES;