package com.salesanalyzer.dao;



import com.salesanalyzer.model.Vente;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.salesanalyzer.util.ConnexionBD;

import static com.salesanalyzer.util.ConnexionBD.getConnection;

public class VenteDAO {

    // Lire un fichier CSV et convertir chaque ligne en objet Vente
    public List<Vente> chargerDepuisCSV(String cheminFichier) {
        List<Vente> ventes = new ArrayList<>(); // Liste des ventes à retourner

        try (BufferedReader br = new BufferedReader(new FileReader(cheminFichier))) {
            String ligne;
            boolean premiereLigne = true; // Pour ignorer l'en-tête

            while ((ligne = br.readLine()) != null) {
                if (premiereLigne) {
                    premiereLigne = false; // On saute la première ligne (titres des colonnes)
                    continue;
                }

                // On découpe la ligne en utilisant la virgule comme séparateur
                String[] parties = ligne.split(",");

                // Conversion des données vers les bons types
                int id = Integer.parseInt(parties[0]);
                LocalDate date = LocalDate.parse(parties[1]);
                String produit = parties[2];
                String categorie = parties[3];
                int quantite = Integer.parseInt(parties[4]);
                double prixUnitaire = Double.parseDouble(parties[5]);

                // Création d’un objet Vente et ajout à la liste
                ventes.add(new Vente(id, date, produit, categorie, quantite, prixUnitaire));
            }

        } catch (IOException e) {
            e.printStackTrace(); // Affiche une erreur si le fichier est introuvable ou invalide
        }

        return ventes; // Retourne la liste des ventes lues
    }

//Insérer une liste d’objets Vente dans la base de données
    public void insererVentes(List<Vente> ventes) {
        // Requête SQL préparée pour insérer une vente
        String sql = "INSERT INTO ventes (id_vente, date_vente, produit, categorie, quantite, prix_unitaire, total) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); // Connexion à la base
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Vente v : ventes) {
                // On renseigne chaque paramètre de la requête
                stmt.setInt(1, v.getId());
                stmt.setDate(2, java.sql.Date.valueOf(v.getDateVente()));
                stmt.setString(3, v.getProduit());
                stmt.setString(4, v.getCategorie());
                stmt.setInt(5, v.getQuantite());
                stmt.setDouble(6, v.getPrixUnitaire());
                stmt.setDouble(7, v.getTotal());

                stmt.addBatch(); // Ajout à un "batch" pour exécution groupée (plus rapide)
            }

            stmt.executeBatch(); // Exécute toutes les requêtes préparées en une seule fois
            System.out.println("Données insérées avec succès dans la base de données !");

        } catch (SQLException e) {
            e.printStackTrace(); // En cas d'erreur SQL, affiche l'exception
        }
    }


//Lire toutes les ventes enregistrées dans la base et les retourner
    public List<Vente> recupererToutesLesVentes() throws SQLException {
        List<Vente> ventes = new ArrayList<>(); // Liste à remplir
        String query = "SELECT id_vente, date_vente, produit, categorie, quantite, prix_unitaire FROM ventes";

        try (Connection conn = getConnection(); // Connexion à la base
             Statement stmt = conn.createStatement(); // Création de la requête
             ResultSet rs = stmt.executeQuery(query)) { // Exécution et récupération du résultat

            while (rs.next()) {
                // Pour chaque ligne du résultat, on crée un objet Vente
                Vente vente = new Vente(
                        rs.getInt("id_vente"),
                        rs.getObject("date_vente", LocalDate.class),
                        rs.getString("produit"),
                        rs.getString("categorie"),
                        rs.getInt("quantite"),
                        rs.getDouble("prix_unitaire")
                );

                ventes.add(vente); // Ajout à la liste
            }
        }

        return ventes; // Retour de toutes les ventes extraites de la base
    }
}

