package com.salesanalyzer.controller;

import com.salesanalyzer.dao.UtilisateurDAO;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {

    /**
     * Affiche la page d'accueil avec logo, titre et deux boutons :
     * "Se connecter" et "Créer un compte".
     * @param primaryStage la fenêtre principale JavaFX
     */
    public void afficherLogin(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20px;");

        // Ajout du logo
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/img_1.png")));
        logo.setFitWidth(100);
        logo.setPreserveRatio(true);

        // Titre "Bienvenue"
        Label lblTitre = new Label("Bienvenue");
        lblTitre.getStyleClass().add("label-titre");

        // Boutons pour accéder aux formulaires de connexion et inscription
        Button btnConnexion = new Button("Se connecter");
        Button btnInscription = new Button("Créer un compte");

        // Gestion des clics pour afficher les formulaires correspondants
        btnConnexion.setOnAction(e -> afficherFormulaireConnexion(primaryStage));
        btnInscription.setOnAction(e -> afficherFormulaireInscription(primaryStage));

        root.getChildren().addAll(logo, lblTitre, btnConnexion, btnInscription);

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());

        // Animation d'apparition progressive (fade-in)
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Configuration et affichage de la scène
        primaryStage.setScene(scene);
        primaryStage.setTitle("Page d'accueil");
        primaryStage.show();
    }

    /**
     * Affiche le formulaire de connexion avec champs utilisateur, mot de passe,
     * option pour afficher/cacher le mot de passe et bouton de connexion.
     * @param primaryStage la fenêtre principale JavaFX
     */
    private void afficherFormulaireConnexion(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20px;");

        // Bouton pour revenir à la page d'accueil
        Button btnRetour = new Button("🔙 Retour");
        btnRetour.getStyleClass().add("button-retour");
        btnRetour.setOnAction(e -> afficherLogin(primaryStage));

        // Création de la grille contenant les champs du formulaire
        GridPane grille = creerGrilleLogin();

        // Titre "Connexion"
        Label titre = new Label("Connexion");
        titre.getStyleClass().add("label-titre");
        grille.add(titre, 0, 0, 2, 1);

        // Champs utilisateur et mot de passe
        TextField tfUtilisateur = new TextField();
        PasswordField pfMotDePasse = new PasswordField();
        TextField tfMotDePasseVisible = new TextField(); // champ texte pour mot de passe visible
        tfMotDePasseVisible.setManaged(false);
        tfMotDePasseVisible.setVisible(false);

        // Styles CSS
        tfUtilisateur.getStyleClass().add("text-field");
        pfMotDePasse.getStyleClass().add("password-field");
        tfMotDePasseVisible.getStyleClass().add("text-field");

        // Liaison bidirectionnelle entre mot de passe masqué et visible
        pfMotDePasse.textProperty().bindBidirectional(tfMotDePasseVisible.textProperty());

        // Ajout des éléments dans la grille
        grille.add(new Label("Nom d'utilisateur :"), 0, 1);
        grille.add(tfUtilisateur, 1, 1);
        grille.add(new Label("Mot de passe :"), 0, 2);
        grille.add(pfMotDePasse, 1, 2);
        grille.add(tfMotDePasseVisible, 1, 2);

        // Checkbox pour afficher/cacher le mot de passe
        CheckBox cbAfficherMotDePasse = new CheckBox("Afficher le mot de passe");
        grille.add(cbAfficherMotDePasse, 1, 3);

        // Bouton de connexion
        Button btnConnexion = new Button("Se connecter");
        btnConnexion.getStyleClass().add("button");
        grille.add(btnConnexion, 1, 4);

        // Gestion de l'affichage du mot de passe selon la checkbox
        cbAfficherMotDePasse.setOnAction(e -> {
            boolean isSelected = cbAfficherMotDePasse.isSelected();
            tfMotDePasseVisible.setManaged(isSelected);
            tfMotDePasseVisible.setVisible(isSelected);
            pfMotDePasse.setManaged(!isSelected);
            pfMotDePasse.setVisible(!isSelected);
        });

        // Action au clic sur le bouton connexion
        btnConnexion.setOnAction(e -> {
            UtilisateurDAO dao = new UtilisateurDAO();
            // Récupération du mot de passe selon affichage
            String motDePasse = cbAfficherMotDePasse.isSelected() ? tfMotDePasseVisible.getText() : pfMotDePasse.getText();
            // Vérification des identifiants en base
            boolean verifie = dao.verifierConnexion(tfUtilisateur.getText(), motDePasse);

            if (verifie) {
                // Si succès, création de l'interface principale
                MainController mainController = new MainController();
                Scene scenePrincipale = new Scene(mainController.creerInterfacePrincipale(), 800, 600);
                scenePrincipale.getStylesheets().add(getClass().getResource("/css/style1.css").toExternalForm());
                primaryStage.setScene(scenePrincipale);
                primaryStage.setTitle("Analyse des ventes");
            } else {
                // Sinon affichage d'une alerte d'erreur
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Nom d'utilisateur ou mot de passe incorrect !");
            }
        });

        layout.getChildren().addAll(btnRetour, grille);

        Scene scene = new Scene(layout, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());

        // Animation fade-in à l'ouverture du formulaire
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), layout);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connexion");
    }

    /**
     * Affiche le formulaire d'inscription avec champs utilisateur, mot de passe, confirmation,
     * option d'affichage des mots de passe, et bouton d'inscription.
     * @param primaryStage la fenêtre principale JavaFX
     */
    private void afficherFormulaireInscription(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20px;");

        // Bouton retour vers la page d'accueil
        Button btnRetour = new Button("🔙 Retour");
        btnRetour.getStyleClass().add("button-retour");
        btnRetour.setOnAction(e -> afficherLogin(primaryStage));

        // Création de la grille pour les champs du formulaire
        GridPane grille = creerGrilleLogin();

        // Titre "Créer un compte"
        Label titre = new Label("Créer un compte");
        titre.getStyleClass().add("label-titre");
        grille.add(titre, 0, 0, 2, 1);

        // Champ nom d'utilisateur
        TextField tfUtilisateur = new TextField();
        tfUtilisateur.getStyleClass().add("text-field");

        // Champs mot de passe et confirmation (avec champs visibles associés)
        PasswordField pfMotDePasse = new PasswordField();
        PasswordField pfMotDePasseConfirm = new PasswordField();
        TextField tfMotDePasseVisible = new TextField();
        TextField tfMotDePasseConfirmVisible = new TextField();

        tfMotDePasseVisible.setManaged(false); tfMotDePasseVisible.setVisible(false);
        tfMotDePasseConfirmVisible.setManaged(false); tfMotDePasseConfirmVisible.setVisible(false);

        // Liaison bidirectionnelle des mots de passe visibles avec les masqués
        tfMotDePasseVisible.textProperty().bindBidirectional(pfMotDePasse.textProperty());
        tfMotDePasseConfirmVisible.textProperty().bindBidirectional(pfMotDePasseConfirm.textProperty());

        // Application des styles CSS
        pfMotDePasse.getStyleClass().add("password-field");
        pfMotDePasseConfirm.getStyleClass().add("password-field");
        tfMotDePasseVisible.getStyleClass().add("text-field");
        tfMotDePasseConfirmVisible.getStyleClass().add("text-field");

        // Ajout des labels et champs dans la grille
        grille.add(new Label("Nom d'utilisateur :"), 0, 1);
        grille.add(tfUtilisateur, 1, 1);
        grille.add(new Label("Mot de passe :"), 0, 2);
        grille.add(pfMotDePasse, 1, 2);
        grille.add(tfMotDePasseVisible, 1, 2);
        grille.add(new Label("Confirmer le mot de passe :"), 0, 3);
        grille.add(pfMotDePasseConfirm, 1, 3);
        grille.add(tfMotDePasseConfirmVisible, 1, 3);

        // Checkbox pour afficher ou masquer les mots de passe
        CheckBox cbAfficher = new CheckBox("Afficher les mots de passe");
        cbAfficher.setOnAction(e -> {
            boolean show = cbAfficher.isSelected();

            tfMotDePasseVisible.setManaged(show);
            tfMotDePasseVisible.setVisible(show);
            pfMotDePasse.setManaged(!show);
            pfMotDePasse.setVisible(!show);

            tfMotDePasseConfirmVisible.setManaged(show);
            tfMotDePasseConfirmVisible.setVisible(show);
            pfMotDePasseConfirm.setManaged(!show);
            pfMotDePasseConfirm.setVisible(!show);
        });
        grille.add(cbAfficher, 1, 4);

        // Bouton inscription
        Button btnInscription = new Button("S'inscrire");
        btnInscription.getStyleClass().add("button");
        grille.add(btnInscription, 1, 5);

        // Action lors du clic sur "S'inscrire"
        btnInscription.setOnAction(e -> {
            String username = tfUtilisateur.getText().trim();
            String motDePasse = pfMotDePasse.getText().trim();
            String confirm = pfMotDePasseConfirm.getText().trim();

            UtilisateurDAO dao = new UtilisateurDAO();

            // Validation des champs
            if (username.isEmpty() || motDePasse.isEmpty() || confirm.isEmpty()) {
                afficherAlerte(Alert.AlertType.WARNING, "Champs requis", "Tous les champs doivent être remplis !");
                return;
            }

            // Vérification que les mots de passe correspondent
            if (!motDePasse.equals(confirm)) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Les mots de passe ne correspondent pas !");
                return;
            }

            // Vérification que le nom d'utilisateur n'existe pas déjà
            if (dao.utilisateurExiste(username)) {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Nom d'utilisateur déjà pris !");
                return;
            }

            // Création du nouvel utilisateur en base
            boolean cree = dao.creerUtilisateur(username, motDePasse);
            if (cree) {
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Compte créé avec succès !");
                afficherFormulaireConnexion(primaryStage); // Retour au formulaire connexion
            } else {
                afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création du compte.");
            }
        });

        layout.getChildren().addAll(btnRetour, grille);

        Scene scene = new Scene(layout, 600, 450);
        scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());

        // Animation fade-in à l'ouverture
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), layout);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Créer un compte");
    }


    /**
     * Crée une grille (GridPane) avec espacement et styles utilisés dans les formulaires.
     * @return une instance de GridPane configurée
     */
    private GridPane creerGrilleLogin() {
        GridPane grille = new GridPane();
        grille.setHgap(10);
        grille.setVgap(10);
        grille.setAlignment(Pos.CENTER);
        grille.setStyle("-fx-padding: 20px;");
        grille.getStyleClass().add("grid-pane");
        return grille;
    }

    /**
     * Affiche une boîte de dialogue d'alerte avec un titre, un type et un message.
     * @param type type d'alerte (ERROR, INFORMATION, WARNING...)
     * @param titre titre de la fenêtre d'alerte
     * @param message message à afficher dans le contenu
     */
    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}
