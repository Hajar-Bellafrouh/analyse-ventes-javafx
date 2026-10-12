package com.salesanalyzer.controller;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.fx.ChartViewer;

import javafx.embed.swing.SwingNode;
import org.jfree.chart.ChartFactory;

import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;




import com.salesanalyzer.dao.VenteDAO;
import com.salesanalyzer.model.Vente;
import com.salesanalyzer.service.AnalyseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;


import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.imageio.ImageIO;

//import javax.swing.text.html.ImageView;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.List;

//import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;








public class MainController {

    private TableView<Vente> tableVentes;
    private ObservableList<Vente> listeVentes;

    public VBox creerInterfacePrincipale() {
        Label titre = new Label("Analyse des données de ventes");
        titre.getStyleClass().add("titre-centre");
        titre.setMaxWidth(Double.MAX_VALUE);              // Permet au Label de prendre toute la largeur disponible
        titre.setAlignment(Pos.CENTER);                  // Centre le contenu dans le Label



        Button btnChargerCSV = new Button("Charger fichier CSV");
        Button btnChargerBD = new Button("Charger depuis la base de données");
        Button btnEnregistrerBD = new Button("Enregistrer dans la base de données");

        btnChargerCSV.getStyleClass().add("btn-csv");
        btnChargerBD.getStyleClass().add("btn-bd");

        btnEnregistrerBD.getStyleClass().add("btn-save");



        for (Button b : List.of(btnChargerCSV, btnChargerBD, btnEnregistrerBD)) {
            b.setPrefWidth(200); // Même largeur
        }

        btnChargerCSV.setOnAction(e -> chargerFichierCSV());
        btnChargerBD.setOnAction(e -> chargerDepuisBaseDeDonnees());
        btnEnregistrerBD.setOnAction(e -> enregistrerDansBaseDeDonnees());

        HBox hboxChargement = new HBox(15, btnChargerCSV, btnChargerBD, btnEnregistrerBD);
        hboxChargement.setStyle("-fx-alignment: center;");

        Label texteRecherche = new Label("🔎 Recherche par produit ou catégorie :");
        texteRecherche.setStyle("-fx-font-size: 15px; -fx-text-fill: #555555;");

        TextField champRecherche = new TextField();
        champRecherche.setPromptText("Saisir un mot-clé...");
        champRecherche.setPrefWidth(250);

        Button btnRechercher = new Button("Rechercher");
        btnRechercher.getStyleClass().add("bouton-rech");
        btnRechercher.setOnAction(e -> rechercherVentes(champRecherche.getText()));

        HBox hboxRecherche = new HBox(10, champRecherche, btnRechercher);
        hboxRecherche.setStyle("-fx-alignment: center;");

        tableVentes = creerTableView();

        Button btnAjouterVente = new Button("Ajouter une vente");
        btnAjouterVente.getStyleClass().add("bouton-ajouter");
        btnAjouterVente.setOnAction(e -> afficherFormulaireAjoutVente());

        Button btnViderTableau = new Button("Vider le tableau");
        btnViderTableau.getStyleClass().add("bouton-vider");
        btnViderTableau.setOnAction(e -> viderTableView());

        Button btnAnalyserDonnees = new Button("Analyser les données");
        btnAnalyserDonnees.getStyleClass().add("bouton-analyser");
        btnAnalyserDonnees.setOnAction(e -> ouvrirInterfaceAnalyse());

        HBox hboxBoutons = new HBox(10, btnAjouterVente, btnViderTableau, btnAnalyserDonnees);
        hboxBoutons.setStyle("-fx-alignment: center-right; -fx-padding: 10px;");

        VBox layout = new VBox(20);
        layout.setStyle("-fx-padding: 20;");
        layout.getChildren().addAll(
                titre,
                texteRecherche,
                hboxRecherche,
                hboxChargement,
                tableVentes,
                hboxBoutons
        );

        return layout;
    }





    private void chargerFichierCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        File fichier = fileChooser.showOpenDialog(null);

        if (fichier != null) {
            VenteDAO dao = new VenteDAO();
            List<Vente> ventes = dao.chargerDepuisCSV(fichier.getAbsolutePath());

            listeVentes = FXCollections.observableArrayList(ventes);
            tableVentes.setItems(listeVentes);
        }
    }



    private void chargerDepuisBaseDeDonnees() {
        try {
            VenteDAO dao = new VenteDAO();
            List<Vente> ventes = dao.recupererToutesLesVentes(); // À implémenter dans VenteDAO

            // 🔍 Vérifie si la TableView contient déjà des données
            if (listeVentes != null && !listeVentes.isEmpty()) {
                // 🛑 Demande confirmation avant de remplacer les données
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirmation");
                confirmation.setHeaderText(null);
                confirmation.setContentText("Des données existent déjà dans le tableau. Les remplacer par celles de la base de données ?");

                Optional<ButtonType> resultat = confirmation.showAndWait();

                if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
                    // ✅ Utilisateur confirme : on remplace les données
                    listeVentes = FXCollections.observableArrayList(ventes);
                    tableVentes.setItems(listeVentes);

                    if (ventes.isEmpty()) {
                        afficherAlerte(Alert.AlertType.INFORMATION, "Aucune donnée", "La base de données est vide !");
                    } else {
                        afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Données chargées depuis la base !");
                    }
                } else {
                    // ❌ Utilisateur annule : on ne fait rien
                    afficherAlerte(Alert.AlertType.INFORMATION, "Annulé", "Chargement des données abandonné.");
                }
            } else {
                // 📥 Aucune donnée existante : on charge directement
                listeVentes = FXCollections.observableArrayList(ventes);
                tableVentes.setItems(listeVentes);

                if (ventes.isEmpty()) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Aucune donnée", "La base de données est vide !");
                } else {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Données chargées depuis la base !");
                }
            }
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les données !");
            e.printStackTrace();
        }
    }







    private void enregistrerDansBaseDeDonnees() {
        if (listeVentes != null && !listeVentes.isEmpty()) {
            VenteDAO dao = new VenteDAO();
            try {
                dao.insererVentes(listeVentes);
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Les ventes ont été enregistrées avec succès !");
            } catch (Exception e) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement des ventes !");
                e.printStackTrace();
            }
        } else {
            afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Aucune vente à enregistrer !");
        }
    }


    private void rechercherVentes(String motCle) {
        if (listeVentes != null && !listeVentes.isEmpty()) {
            ObservableList<Vente> resultats = FXCollections.observableArrayList();

            for (Vente v : listeVentes) {
                if (v.getProduit().toLowerCase().contains(motCle.toLowerCase()) ||
                        v.getCategorie().toLowerCase().contains(motCle.toLowerCase())) {
                    resultats.add(v);
                }
            }

            if (resultats.isEmpty()) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Aucun résultat", "Aucune vente trouvée pour : " + motCle);
            }
            tableVentes.setItems(resultats);
        }
    }



    private void viderTableView() {
        if (listeVentes != null && !listeVentes.isEmpty()) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Êtes-vous sûr de vouloir vider toutes les ventes du tableau ?");

            if (confirmation.showAndWait().get() == ButtonType.OK) {
                listeVentes.clear();
            }
        } else {
            afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Le tableau est déjà vide !");
        }
    }


    private void afficherFormulaireAjoutVente() {
        Stage fenetre = new Stage();
        fenetre.initModality(Modality.APPLICATION_MODAL);
        fenetre.setTitle("Ajouter une Vente");

        GridPane grille = new GridPane();
        grille.setHgap(10);
        grille.setVgap(10);
        grille.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        TextField tfId = new TextField();
        tfId.setDisable(true); // L'utilisateur ne pourra pas modifier l'ID

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField tfProduit = new TextField();
        TextField tfCategorie = new TextField();
        TextField tfQuantite = new TextField();
        TextField tfPrix = new TextField();

        // Définir l'ID automatique
        int nouvelId = genererNouvelId();
        tfId.setText(String.valueOf(nouvelId));

        grille.add(new Label("Identifiant :"), 0, 0);
        grille.add(tfId, 1, 0);
        grille.add(new Label("Date de la vente :"), 0, 1);
        grille.add(datePicker, 1, 1);
        grille.add(new Label("Nom du produit :"), 0, 2);
        grille.add(tfProduit, 1, 2);
        grille.add(new Label("Catégorie :"), 0, 3);
        grille.add(tfCategorie, 1, 3);
        grille.add(new Label("Quantité vendue :"), 0, 4);
        grille.add(tfQuantite, 1, 4);
        grille.add(new Label("Prix unitaire (DH) :"), 0, 5);
        grille.add(tfPrix, 1, 5);

        Button btnAjouter = new Button("Ajouter");
        btnAjouter.setOnAction(e -> {
            try {
                if (tfProduit.getText().isEmpty() || tfCategorie.getText().isEmpty() ||
                        tfQuantite.getText().isEmpty() || tfPrix.getText().isEmpty()) {
                    afficherAlerte(Alert.AlertType.WARNING, "Champs obligatoires", "Veuillez remplir tous les champs !");
                    return;
                }

                int id = Integer.parseInt(tfId.getText());
                LocalDate date = datePicker.getValue();
                String produit = tfProduit.getText();
                String categorie = tfCategorie.getText();
                int quantite = Integer.parseInt(tfQuantite.getText());
                double prixUnitaire = Double.parseDouble(tfPrix.getText());

                Vente vente = new Vente(id, date, produit, categorie, quantite, prixUnitaire);
                if (listeVentes == null) {
                    listeVentes = FXCollections.observableArrayList();
                }
                listeVentes.add(vente);
                tableVentes.setItems(listeVentes);

                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "La vente a été ajoutée avec succès !");
                fenetre.close();
            } catch (NumberFormatException ex) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur de format", "Veuillez entrer des nombres valides pour la quantité et le prix !");
            } catch (Exception ex) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de la vente !");
                ex.printStackTrace();
            }
        });

        grille.add(btnAjouter, 1, 6);

        Scene scene = new Scene(grille, 400, 350);

        scene.getStylesheets().add(getClass().getResource("/css/form_vente.css").toExternalForm());
        grille.getStyleClass().add("grid-pane");
        tfProduit.getStyleClass().add("text-field");
        tfCategorie.getStyleClass().add("text-field");
        tfQuantite.getStyleClass().add("text-field");
        tfPrix.getStyleClass().add("text-field");
        tfId.getStyleClass().add("text-field");
        datePicker.getStyleClass().add("date-picker");
        btnAjouter.getStyleClass().add("button");



        fenetre.setScene(scene);
        fenetre.showAndWait();

    }



    private int genererNouvelId() {
        if (listeVentes == null || listeVentes.isEmpty()) {
            return 1;
        } else {
            return listeVentes.stream()
                    .mapToInt(Vente::getId)
                    .max()
                    .orElse(0) + 1;
        }
    }



    private TableView<Vente> creerTableView() {
        TableView<Vente> table = new TableView<>();

        double largeurColonne = 140;

        TableColumn<Vente, Number> colId = new TableColumn<>("Identifiant de la vente");
        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colId.setPrefWidth(largeurColonne);

        TableColumn<Vente, String> colDate = new TableColumn<>("Date de la vente");
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDateVente().toString()
        ));
        colDate.setPrefWidth(largeurColonne);

        TableColumn<Vente, String> colProduit = new TableColumn<>("Nom du produit");
        colProduit.setCellValueFactory(data -> data.getValue().produitProperty());
        colProduit.setPrefWidth(largeurColonne);

        TableColumn<Vente, String> colCategorie = new TableColumn<>("Catégorie du produit");
        colCategorie.setCellValueFactory(data -> data.getValue().categorieProperty());
        colCategorie.setPrefWidth(largeurColonne);

        TableColumn<Vente, Number> colQuantite = new TableColumn<>("Quantité vendue");
        colQuantite.setCellValueFactory(data -> data.getValue().quantiteProperty());
        colQuantite.setPrefWidth(largeurColonne);

        TableColumn<Vente, Number> colPrix = new TableColumn<>("Prix unitaire (DH)");
        colPrix.setCellValueFactory(data -> data.getValue().prixUnitaireProperty());
        colPrix.setPrefWidth(largeurColonne);

        TableColumn<Vente, Number> colTotal = new TableColumn<>("Total de la vente (DH)");
        colTotal.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(
                data.getValue().getTotal()
        ));
        colTotal.setPrefWidth(largeurColonne);

        table.getColumns().addAll(colId, colDate, colProduit, colCategorie, colQuantite, colPrix, colTotal);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        return table;
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);

        alerte.initOwner(tableVentes.getScene().getWindow());
        alerte.initModality(Modality.APPLICATION_MODAL);

        // Réduire la taille du texte du message
        DialogPane dialogPane = alerte.getDialogPane();
        dialogPane.getStyleClass().add("custom-alert"); // Optionnel : pour appliquer un style CSS

        // Méthode 1 : Modifier directement la police du message
        Label contentLabel = (Label) dialogPane.lookup(".content-label");
        if (contentLabel != null) {
            contentLabel.setStyle("-fx-font-size: 12px;"); // Ajustez la taille ici
        }

        // Méthode 2 (alternative) : Appliquer un style CSS global via une feuille de style
        // dialogPane.getStylesheets().add(getClass().getResource("alert-style.css").toExternalForm());

        // Animation (optionnel)
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), dialogPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        alerte.show();
    }


    private void ouvrirInterfaceAnalyse() {
        Stage stageAnalyse = new Stage();
        stageAnalyse.setTitle("Analyse et Visualisation des Données");

        BorderPane root = new BorderPane();

        // 🔼 En-tête avec image
        try {
            javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("/images/img.png"));
            ImageView imageTop = new ImageView(image);
            imageTop.setFitHeight(100);
            imageTop.setPreserveRatio(true);
            HBox header = new HBox(imageTop);
            header.setStyle("-fx-alignment: center; -fx-padding: 10;");
            root.setTop(header);
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement image d’en-tête : " + e.getMessage());
        }

        // 📋 Menu gauche
        VBox menuGauche = new VBox(10);
        menuGauche.setStyle("-fx-padding: 10; -fx-background-color: #f4f4f4;");
        menuGauche.setPrefWidth(250);

        Button btnCategorie = new Button("Ventes par catégorie");
        Button btnMensuelle = new Button("Ventes mensuelles");
        Button btnTopProduits = new Button("Top produits");
        Button btnHistogramme = new Button("Histogramme");
        Button btnCourbe = new Button("Courbe mensuelle");
        Button btnPrixExtremes = new Button("Produits les plus/moins chers");

        for (Button b : List.of(btnCategorie, btnMensuelle, btnTopProduits, btnHistogramme, btnCourbe, btnPrixExtremes)) {
            b.getStyleClass().add("menu-button");
            b.setMaxWidth(Double.MAX_VALUE);
        }

        menuGauche.getChildren().addAll(
                btnCategorie, btnMensuelle, btnTopProduits,
                btnHistogramme, btnCourbe, btnPrixExtremes
        );

        VBox contenuDroite = new VBox();
        contenuDroite.setStyle("-fx-padding: 20;");
        root.setLeft(menuGauche);
        root.setCenter(contenuDroite);

        // ⬇ Pied de page
        Button btnRetourner = new Button("⬅ Retourner");
        btnRetourner.setOnAction(e -> stageAnalyse.close());

        Button btnExporterPDF = new Button("📄 Exporter tout en PDF");
        btnExporterPDF.setOnAction(e -> exporterAnalysePDF());

        btnRetourner.getStyleClass().add("button-violet");
        btnExporterPDF.getStyleClass().add("button-violet");

        HBox hboxBas = new HBox(10, btnRetourner, new Label(), btnExporterPDF);
        hboxBas.setStyle("-fx-padding: 10; -fx-alignment: center-right;");
        HBox.setHgrow(hboxBas.getChildren().get(1), javafx.scene.layout.Priority.ALWAYS);
        root.setBottom(hboxBas);

        // 👉 Actions des boutons
        btnCategorie.setOnAction(e -> afficherResultatCategorieDansPane(contenuDroite));
        btnMensuelle.setOnAction(e -> afficherResultatMensuelDansPane(contenuDroite));
        btnTopProduits.setOnAction(e -> afficherResultatTopProduitsDansPane(contenuDroite));
        btnHistogramme.setOnAction(e -> afficherHistogrammeDansPane(contenuDroite));
        btnCourbe.setOnAction(e -> afficherCourbeDansPane(contenuDroite));
        btnPrixExtremes.setOnAction(e -> afficherProduitsPrixExtremesDansPane(contenuDroite));

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/css/styleAnalyse.css").toExternalForm());
        stageAnalyse.setScene(scene);
        stageAnalyse.show();
    }

    private void afficherProduitsPrixExtremesDansPane(VBox contenu) {
        contenu.getChildren().clear();
        if (listeVentes == null || listeVentes.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Données manquantes", "Aucune vente chargée.");
            return;
        }

        Vente produitMax = listeVentes.stream().max(Comparator.comparingDouble(Vente::getPrixUnitaire)).orElse(null);
        Vente produitMin = listeVentes.stream().min(Comparator.comparingDouble(Vente::getPrixUnitaire)).orElse(null);

        TextArea area = new TextArea();
        area.setEditable(false);
        area.appendText("🔺 Produit le plus cher :\n");
        area.appendText(produitMax.getProduit() + " - " + produitMax.getPrixUnitaire() + " DH\n\n");
        area.appendText("🔻 Produit le moins cher :\n");
        area.appendText(produitMin.getProduit() + " - " + produitMin.getPrixUnitaire() + " DH");
        contenu.getChildren().add(area);
    }




    private void exporterAnalysePDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter l'analyse");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
            File fichier = fileChooser.showSaveDialog(null);
            if (fichier == null) return;

            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(fichier));
            doc.open();

            AnalyseService service = new AnalyseService();

            // ➤ Résultat par catégorie
            doc.add(new Paragraph("Ventes par catégorie :"));
            Map<String, Double> cat = service.calculerVentesParCategorie(listeVentes);
            for (String c : cat.keySet()) {
                doc.add(new Paragraph(c + " : " + String.format("%.2f DH", cat.get(c))));
            }

            doc.add(new Paragraph("\nAnalyse mensuelle :"));
            Map<Month, Double> mois = service.analyserVentesMensuelles(listeVentes);
            for (Month m : mois.keySet()) {
                doc.add(new Paragraph(m.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.FRENCH)
                        + " : " + String.format("%.2f DH", mois.get(m))));
            }

            doc.add(new Paragraph("\nTop Produits :"));
            List<String> top = service.detecterProduitsLesPlusVendus(listeVentes, 5);
            for (String produit : top) {
                doc.add(new Paragraph("• " + produit));
            }

            // ➤ Produits extrêmes
            doc.add(new Paragraph("\nProduits extrêmes :"));
            Vente plusCher = listeVentes.stream().max(Comparator.comparingDouble(Vente::getPrixUnitaire)).orElse(null);
            Vente moinsCher = listeVentes.stream().min(Comparator.comparingDouble(Vente::getPrixUnitaire)).orElse(null);
            if (plusCher != null && moinsCher != null) {
                doc.add(new Paragraph("Produit le plus cher : " + plusCher.getProduit() + " - " + plusCher.getPrixUnitaire() + " DH"));
                doc.add(new Paragraph("Produit le moins cher : " + moinsCher.getProduit() + " - " + moinsCher.getPrixUnitaire() + " DH"));
            }

            // ➤ Graphique histogramme
            doc.add(new Paragraph("\nHistogramme des ventes par catégorie :"));
            JFreeChart histoChart = creerHistogrammeChart(cat);
            BufferedImage histoImage = histoChart.createBufferedImage(500, 300);
            File tempHisto = File.createTempFile("histo", ".png");
            ImageIO.write(histoImage, "png", tempHisto);
            doc.add(Image.getInstance(tempHisto.getAbsolutePath()));

            // ➤ Graphique courbe
            doc.add(new Paragraph("\nCourbe d’évolution mensuelle :"));
            JFreeChart courbeChart = creerCourbeChart(mois);
            BufferedImage courbeImage = courbeChart.createBufferedImage(500, 300);
            File tempCourbe = File.createTempFile("courbe", ".png");
            ImageIO.write(courbeImage, "png", tempCourbe);
            doc.add(Image.getInstance(tempCourbe.getAbsolutePath()));

            // ➤ Tableau récapitulatif des ventes
            doc.add(new Paragraph("\nTableau récapitulatif des ventes :\n"));
            PdfPTable table = new PdfPTable(5); // 5 colonnes : Date, Produit, Catégorie, Quantité, Total
            table.setWidthPercentage(100);

            String[] entetes = {"Date", "Produit", "Catégorie", "Quantité", "Total (DH)"};
            for (String col : entetes) {
                PdfPCell cell = new PdfPCell(new Paragraph(col));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            for (Vente v : listeVentes) {
                table.addCell(v.getDateVente().toString());
                table.addCell(v.getProduit());
                table.addCell(v.getCategorie());
                table.addCell(String.valueOf(v.getQuantite()));
                table.addCell(String.format("%.2f", v.getTotal()));
            }

            doc.add(table);



            doc.close();
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Analyse exportée avec succès en PDF !");
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'export PDF : " + e.getMessage());
            e.printStackTrace();
        }
    }




    private JFreeChart creerHistogrammeChart(Map<String, Double> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((cat, total) -> dataset.addValue(total, "Ventes", cat));
        return ChartFactory.createBarChart(
                "Ventes par catégorie", "Catégorie", "Total (DH)", dataset
        );
    }

    private JFreeChart creerCourbeChart(Map<Month, Double> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((mois, total) -> {
            String moisFr = mois.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.FRENCH);
            dataset.addValue(total, "Ventes", moisFr);
        });
        return ChartFactory.createLineChart(
                "Évolution mensuelle", "Mois", "Total (DH)", dataset
        );
    }





    private void afficherHistogrammeDansPane(VBox contenu) {
        contenu.getChildren().clear();

        if (listeVentes == null || listeVentes.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Pas de données", "Veuillez charger les ventes d'abord.");
            return;
        }

        AnalyseService service = new AnalyseService();
        Map<String, Double> ventes = service.calculerVentesParCategorie(listeVentes);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        ventes.forEach((categorie, total) -> dataset.addValue(total, "Ventes", categorie));

        JFreeChart chart = ChartFactory.createBarChart(
                "Ventes par catégorie", "Catégorie", "Total (DH)", dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);

        SwingNode swingNode = new SwingNode();
        javafx.application.Platform.runLater(() -> swingNode.setContent(chartPanel)); // ⚠️ important

        ScrollPane scrollPane = new ScrollPane(swingNode); // Permet d'afficher même si le graphe est grand
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        contenu.getChildren().add(scrollPane);
        System.out.println("✅ Histogramme affiché !");
    }



    private void afficherCourbeDansPane(VBox contenu) {
        contenu.getChildren().clear();

        if (listeVentes == null || listeVentes.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Pas de données", "Veuillez charger les ventes d'abord.");
            return;
        }

        AnalyseService service = new AnalyseService();
        Map<Month, Double> ventes = service.analyserVentesMensuelles(listeVentes);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        ventes.forEach((mois, total) -> {
            String moisFr = mois.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.FRENCH);
            dataset.addValue(total, "Ventes", moisFr);
        });

        JFreeChart chart = ChartFactory.createLineChart(
                "Évolution mensuelle des ventes", "Mois", "Ventes (DH)", dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);

        SwingNode swingNode = new SwingNode();
        javafx.application.Platform.runLater(() -> swingNode.setContent(chartPanel));

        ScrollPane scrollPane = new ScrollPane(swingNode);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        contenu.getChildren().add(scrollPane);
        System.out.println("✅ Courbe affichée !");
    }









    private void afficherResultatCategorieDansPane(VBox contenu) {
        contenu.getChildren().clear();
        AnalyseService service = new AnalyseService();
        Map<String, Double> ventes = service.calculerVentesParCategorie(listeVentes);

        TextArea zone = new TextArea();
        ventes.forEach((cat, total) -> zone.appendText(cat + " : " + String.format("%.2f DH", total) + "\n"));
        zone.setEditable(false);
        contenu.getChildren().add(zone);
    }

    private void afficherResultatMensuelDansPane(VBox contenu) {
        contenu.getChildren().clear();
        AnalyseService service = new AnalyseService();
        Map<Month, Double> ventes = service.analyserVentesMensuelles(listeVentes);

        TextArea zone = new TextArea();
        ventes.forEach((mois, total) -> {
            String m = mois.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.FRENCH);
            zone.appendText(m + " : " + String.format("%.2f DH", total) + "\n");
        });
        zone.setEditable(false);
        contenu.getChildren().add(zone);
    }

    private void afficherResultatTopProduitsDansPane(VBox contenu) {
        contenu.getChildren().clear();
        AnalyseService service = new AnalyseService();
        List<String> top = service.detecterProduitsLesPlusVendus(listeVentes, 5);

        TextArea zone = new TextArea();
        top.forEach(p -> zone.appendText(p + "\n"));
        zone.setEditable(false);
        contenu.getChildren().add(zone);
    }





    private void analyserVentesParCategorie() {
        AnalyseService service = new AnalyseService();
        Map<String, Double> resultats = service.calculerVentesParCategorie(listeVentes);
        resultats.forEach((categorie, total) ->
                System.out.println(categorie + " : " + total + " DH")
        );
    }

    private void analyserVentesMensuelles() {
        if (listeVentes == null || listeVentes.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Aucune vente disponible pour analyser !");
            return;
        }

        AnalyseService service = new AnalyseService();
        TreeMap<Month, Double> resultats = service.analyserVentesMensuelles(listeVentes);

        System.out.println("Analyse des ventes mensuelles :");
        resultats.forEach((mois, total) -> {
            String moisFrancais = mois.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.FRENCH);
            System.out.printf("%-15s : %.2f DH\n", moisFrancais, total);
        });
    }


    private void analyserTopProduits() {
        AnalyseService service = new AnalyseService();
        List<String> produits = service.detecterProduitsLesPlusVendus(listeVentes, 5);
        produits.forEach(System.out::println);
    }


    private void afficherResultatCategorie() {
        if (listeVentes == null || listeVentes.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Aucune vente disponible pour analyser !");
            return;
        }

        AnalyseService service = new AnalyseService();
        Map<String, Double> ventes = service.calculerVentesParCategorie(listeVentes);

        StringBuilder sb = new StringBuilder();
        ventes.forEach((cat, total) -> sb.append(cat).append(" : ").append(String.format("%.2f DH", total)).append("\n"));

        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        VBox vbox = new VBox(textArea);
        Scene scene = new Scene(vbox, 400, 300);

        Stage resultStage = new Stage();
        resultStage.setTitle("Calcul des ventes par catégorie");
        resultStage.setScene(scene);
        resultStage.show();
    }


    private void afficherResultatMensuel() {
        if (listeVentes == null || listeVentes.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Aucune vente disponible pour analyser !");
            return;
        }

        AnalyseService service = new AnalyseService();
        Map<Month, Double> ventes = service.analyserVentesMensuelles(listeVentes);

        StringBuilder sb = new StringBuilder();
        ventes.forEach((mois, total) -> sb.append(mois).append(" : ").append(String.format("%.2f DH", total)).append("\n"));

        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        VBox vbox = new VBox(textArea);
        Scene scene = new Scene(vbox, 400, 300);

        Stage resultStage = new Stage();
        resultStage.setTitle("Analyse mensuelle des ventes");
        resultStage.setScene(scene);
        resultStage.show();
    }


    private void afficherResultatTopProduits() {
        if (listeVentes == null || listeVentes.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Avertissement", "Aucune vente disponible pour analyser !");
            return;
        }

        AnalyseService service = new AnalyseService();
        List<String> top = service.detecterProduitsLesPlusVendus(listeVentes, 5);

        StringBuilder sb = new StringBuilder();
        for (String produit : top) {
            sb.append(produit).append("\n");
        }

        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        VBox vbox = new VBox(textArea);
        Scene scene = new Scene(vbox, 400, 300);

        Stage resultStage = new Stage();
        resultStage.setTitle("Produits les plus vendus");
        resultStage.setScene(scene);
        resultStage.show();
    }




}
