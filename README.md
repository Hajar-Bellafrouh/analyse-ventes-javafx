# Analyse des ventes – Application JavaFX

Application de bureau (JavaFX) d'analyse de ventes : authentification, import CSV, stockage MySQL, statistiques, graphiques (JFreeChart) et export PDF (iText).

## Structure

```
projet_analyse_ventes/
├── pom.xml
├── db.properties.example      # modèle de configuration MySQL
├── database/schema.sql        # création de la base ventesdb
├── data/ventes.csv            # exemple de données à importer
└── src/main/
    ├── java/com/salesanalyzer/
    │   ├── App.java
    │   ├── controller/        # LoginController, MainController
    │   ├── dao/               # VenteDAO, UtilisateurDAO
    │   ├── model/             # Vente
    │   ├── service/           # AnalyseService
    │   └── util/              # ConnexionBD
    └── resources/             # css/ et images/
```

## Prérequis

- JDK 11+ et Maven
- MySQL (port 3306)

## Installation

1. Créer la base : exécuter `database/schema.sql` dans MySQL.
2. Copier `db.properties.example` en `db.properties` (ce fichier est ignoré par git) et renseigner l'utilisateur et le mot de passe MySQL.
   Alternative : variables d'environnement `DB_URL`, `DB_USER`, `DB_PASSWORD`.
3. Lancer depuis la racine du projet :

```bash
mvn javafx:run
```

4. Dans l'application, importer `data/ventes.csv` via le sélecteur de fichier.


##  Auteurs

- Hajar Bellafrouh 
- IID1
