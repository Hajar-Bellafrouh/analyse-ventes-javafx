package com.salesanalyzer.model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Vente {
    private IntegerProperty id;
    private ObjectProperty<LocalDate> dateVente;
    private StringProperty produit;
    private StringProperty categorie;
    private IntegerProperty quantite;
    private DoubleProperty prixUnitaire;

    public Vente(int id, LocalDate dateVente, String produit, String categorie, int quantite, double prixUnitaire) {
        this.id = new SimpleIntegerProperty(id);
        this.dateVente = new SimpleObjectProperty<>(dateVente);
        this.produit = new SimpleStringProperty(produit);
        this.categorie = new SimpleStringProperty(categorie);
        this.quantite = new SimpleIntegerProperty(quantite);
        this.prixUnitaire = new SimpleDoubleProperty(prixUnitaire);
    }

    // Accesseurs pour la liaison (binding) (pour lier les attributs à des composants JavaFX)
    public IntegerProperty idProperty() { return id; }
    public ObjectProperty<LocalDate> dateVenteProperty() { return dateVente; }
    public StringProperty produitProperty() { return produit; }
    public StringProperty categorieProperty() { return categorie; }
    public IntegerProperty quantiteProperty() { return quantite; }
    public DoubleProperty prixUnitaireProperty() { return prixUnitaire; }


    //Getters classiques
    public int getId() { return id.get(); }
    public LocalDate getDateVente() { return dateVente.get(); }
    public String getProduit() { return produit.get(); }
    public String getCategorie() { return categorie.get(); }
    public int getQuantite() { return quantite.get(); }
    public double getPrixUnitaire() { return prixUnitaire.get(); }
    public double getTotal() { return getQuantite() * getPrixUnitaire(); }
}
