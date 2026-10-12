package com.salesanalyzer.service;

import com.salesanalyzer.model.Vente;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyseService {

    // 🔹 Calcul des ventes par catégorie
    public Map<String, Double> calculerVentesParCategorie(List<Vente> ventes) {
        return ventes.stream()
                .collect(Collectors.groupingBy(
                        Vente::getCategorie, // Groupe les ventes par catégorie
                        Collectors.summingDouble(Vente::getTotal) // Somme des totaux de chaque groupe
                ));
    }


    // 🔹 Analyse mensuelle des ventes
    public TreeMap<Month, Double> analyserVentesMensuelles(List<Vente> ventes) {
        if (ventes == null || ventes.isEmpty()) {
            return new TreeMap<>(); // Retourne un TreeMap vide si pas de ventes
        }

        return ventes.stream()
                .collect(Collectors.groupingBy(
                        vente -> vente.getDateVente().getMonth(), // Grouper par mois de la date de vente
                        TreeMap::new, // Utiliser TreeMap pour garder l'ordre naturel des mois
                        Collectors.summingDouble(Vente::getTotal) // Additionner les totaux de ventes
                ));
    }


    // 🔹 Détection des produits les plus vendus
    public List<String> detecterProduitsLesPlusVendus(List<Vente> ventes, int topN) {
        Map<String, Integer> ventesParProduit = ventes.stream()
                .collect(Collectors.groupingBy(
                        Vente::getProduit, // Groupe par nom de produit
                        Collectors.summingInt(Vente::getQuantite) // Somme des quantités vendues
                ));

        return ventesParProduit.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) // Tri par quantité décroissante
                .limit(topN) // Ne garder que les top N produits
                .map(Map.Entry::getKey) // Récupérer le nom du produit
                .collect(Collectors.toList());
    }

}
