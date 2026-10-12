-- Schéma reconstitué à partir des requêtes SQL de VenteDAO et UtilisateurDAO
CREATE DATABASE IF NOT EXISTS ventesdb CHARACTER SET utf8mb4;
USE ventesdb;

CREATE TABLE IF NOT EXISTS ventes (
    id_vente      INT PRIMARY KEY,
    date_vente    DATE NOT NULL,
    produit       VARCHAR(150) NOT NULL,
    categorie     VARCHAR(100) NOT NULL,
    quantite      INT NOT NULL,
    prix_unitaire DOUBLE NOT NULL,
    total         DOUBLE NOT NULL
);

CREATE TABLE IF NOT EXISTS utilisateurs (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);
