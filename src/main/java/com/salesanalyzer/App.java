package com.salesanalyzer;

import com.salesanalyzer.controller.LoginController;
import com.salesanalyzer.controller.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override


    public void start(Stage primaryStage) {
        LoginController login = new LoginController();         // Création du contrôleur de la fenêtre de connexion
        login.afficherLogin(primaryStage);                     // Affiche l'interface de connexion dans la fenêtre principale
    }


    public static void main(String[] args) {
        launch(args); // Lance l'application JavaFX (appelera ensuite start())
    }









    /*public void start(Stage primaryStage) {
        MainController controller = new MainController();

        Scene scene = new Scene(controller.creerInterfacePrincipale(), 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Analyse des Ventes");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }*/
}
