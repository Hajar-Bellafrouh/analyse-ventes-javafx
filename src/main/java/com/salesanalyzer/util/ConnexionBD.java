package com.salesanalyzer.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Connexion à la base MySQL.
 * Les paramètres ne sont plus écrits en dur dans le code : ils sont lus dans
 * le fichier "db.properties" (à la racine du projet, non versionné), puis dans
 * les variables d'environnement DB_URL, DB_USER, DB_PASSWORD.
 * Voir db.properties.example.
 */
public class ConnexionBD {
    private static final Properties CONFIG = chargerConfig();

    private static final String URL = lire("db.url", "DB_URL", "jdbc:mysql://localhost:3306/ventesdb");
    private static final String USER = lire("db.user", "DB_USER", "root");
    private static final String PASSWORD = lire("db.password", "DB_PASSWORD", "");

    private static Properties chargerConfig() {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            p.load(in);
        } catch (IOException e) {
            // Fichier absent : on utilisera les variables d'environnement / valeurs par défaut
        }
        return p;
    }

    private static String lire(String cleProp, String variableEnv, String defaut) {
        String valeur = CONFIG.getProperty(cleProp);
        if (valeur == null || valeur.isEmpty()) valeur = System.getenv(variableEnv);
        return (valeur == null || valeur.isEmpty()) ? defaut : valeur;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
