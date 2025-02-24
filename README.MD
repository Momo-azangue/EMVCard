# CarteEMV Application

Cette application permet de gérer les logs d'une carte EMV en utilisant une base de données sql. Voici les étapes pour configurer et lancer l'application.

## Prérequis

- Java Development Kit (JDK) installez (version 21  ou supérieure).
- XAMPP installez et executez.
- Maven installez pour gérer les dépendances.

## Configuration de la Base de Données

### Création de la Base de Données et de la Table

1. **Connectez-vous à MariaDB :**
    - Utilisez un client PHPMYADMIN pour vous connecter à votre serveur en tant qu'utilisateur administrateur (généralement `root`).

2. **Créer la Base de Données :**
   ```sql
   CREATE DATABASE log;
3. **Créer la Table logs :**
    ```sql
    USE log;
    
    CREATE TABLE logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(255) NOT NULL,
    response VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
4. **Créer la Procédure Stockée :**
    ```sql
    DELIMITER $$
    
    CREATE PROCEDURE InsertLog(IN action VARCHAR(255), IN response VARCHAR(255))
    BEGIN
    INSERT INTO logs (action, response) VALUES (action, response);
    END $$
    
    DELIMITER ;

## Configuration du Fichier `database.properties`

1. Créez un fichier `databases.properties` dans le répertoire `src/main/resources` de votre projet :
    ````java
   jdbc.url=jdbc:mysql://localhost:3306/log
   jdbc.user=your_user
   jdbc.password=your_password_here


