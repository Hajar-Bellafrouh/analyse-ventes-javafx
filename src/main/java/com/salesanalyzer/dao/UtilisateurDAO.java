package com.salesanalyzer.dao;

import com.salesanalyzer.util.ConnexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtilisateurDAO {

    /**
     * Vérifie si un utilisateur existe avec ce nom d'utilisateur et ce mot de passe.
     * @param username le nom d'utilisateur
     * @param password le mot de passe
     * @return true si utilisateur trouvé, false sinon
     */
    public boolean verifierConnexion(String username, String password) {
        // Requête SQL avec paramètres pour éviter injection SQL
        String sql = "SELECT * FROM utilisateurs WHERE username = ? AND password = ?";
        try (
                // Obtient une connexion à la base de données
                Connection conn = ConnexionBD.getConnection();
                // Prépare la requête avec paramètres
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // Remplit les paramètres de la requête
            stmt.setString(1, username);
            stmt.setString(2, password);

            // Exécute la requête
            ResultSet rs = stmt.executeQuery();

            // Retourne true si au moins un résultat (utilisateur trouvé)
            return rs.next();
        } catch (SQLException e) {
            // En cas d'erreur SQL, affiche la stack trace et retourne false
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Crée un nouvel utilisateur dans la base avec le nom et mot de passe donnés.
     * @param username nom d'utilisateur à créer
     * @param password mot de passe associé
     * @return true si insertion réussie, false sinon
     */
    public boolean creerUtilisateur(String username, String password) {
        // Requête d'insertion sécurisée avec paramètres
        String sql = "INSERT INTO utilisateurs (username, password) VALUES (?, ?)";
        try (
                Connection conn = ConnexionBD.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // Remplit les paramètres
            stmt.setString(1, username);
            stmt.setString(2, password);

            // Exécute la mise à jour
            stmt.executeUpdate();

            // Retourne true si tout s'est bien passé
            return true;
        } catch (SQLException e) {
            // En cas d'erreur, affiche l'erreur et retourne false
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Vérifie si un utilisateur avec ce nom existe déjà dans la base.
     * @param username nom d'utilisateur à vérifier
     * @return true si utilisateur existe, false sinon
     */
    public boolean utilisateurExiste(String username) {
        // Requête pour compter les utilisateurs avec ce nom (évite doublons)
        String sql = "SELECT COUNT(*) FROM utilisateurs WHERE nom_utilisateur = ?";
        try (
                Connection conn = ConnexionBD.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Si le count est supérieur à 0, l'utilisateur existe
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Par défaut, retourne false si aucune donnée trouvée ou exception
        return false;
    }
}
