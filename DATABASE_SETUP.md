# Configuration MySQL avec phpMyAdmin

## Prérequis

1. **MySQL Server** installé et démarré
2. **phpMyAdmin** installé et configuré

## Installation

### Windows

#### 1. Installer MySQL
- Téléchargez MySQL depuis : https://dev.mysql.com/downloads/installer/
- Installez MySQL Server
- Notez le mot de passe root que vous configurez

#### 2. Installer phpMyAdmin
**Option A : Via XAMPP (Recommandé)**
- Téléchargez XAMPP : https://www.apachefriends.org/
- Installez XAMPP (inclut MySQL et phpMyAdmin)
- Démarrez Apache et MySQL depuis le panneau de contrôle XAMPP
- Accédez à phpMyAdmin : http://localhost/phpmyadmin

**Option B : Via WAMP**
- Téléchargez WAMP : https://www.wampserver.com/
- Installez WAMP
- Démarrez les services
- Accédez à phpMyAdmin : http://localhost/phpmyadmin

**Option C : Installation standalone**
- Téléchargez phpMyAdmin : https://www.phpmyadmin.net/
- Placez-le dans votre serveur web (Apache/Nginx)
- Configurez config.inc.php

### Linux (Ubuntu/Debian)

```bash
# Installer MySQL
sudo apt update
sudo apt install mysql-server

# Installer phpMyAdmin
sudo apt install phpmyadmin

# Configurer Apache
sudo systemctl restart apache2
```

### macOS

```bash
# Via Homebrew
brew install mysql
brew install phpmyadmin

# Ou utiliser MAMP/XAMPP pour macOS
```

## Configuration de l'application

### 1. Modifier les paramètres de connexion

Éditez le fichier `src/main/resources/application-mysql.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/projetjee?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=VOTRE_MOT_DE_PASSE_MYSQL
```

### 2. Créer la base de données

**Option A : Automatique (recommandé)**
- L'application créera automatiquement la base `projetjee` au démarrage grâce à `createDatabaseIfNotExist=true`

**Option B : Manuel via phpMyAdmin**
1. Connectez-vous à phpMyAdmin : http://localhost/phpmyadmin
2. Cliquez sur "Nouvelle base de données"
3. Nom : `projetjee`
4. Interclassement : `utf8mb4_unicode_ci`
5. Cliquez sur "Créer"

**Option C : Via ligne de commande MySQL**
```sql
CREATE DATABASE projetjee CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Démarrer l'application

### Avec le profil MySQL
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

Ou modifiez `application.properties` pour activer le profil mysql par défaut.

### Vérifier la connexion

1. Démarrez l'application Spring Boot
2. L'application créera automatiquement les tables
3. Connectez-vous à phpMyAdmin
4. Sélectionnez la base `projetjee`
5. Vous devriez voir toutes les tables créées

## Tables créées

L'application créera automatiquement les tables suivantes :
- `etudiants`
- `formateurs`
- `cours`
- `inscriptions`
- `notes`

## Données de test

Le `DataInitializer` remplira automatiquement la base avec :
- 4 formateurs
- 6 étudiants
- 6 cours
- Inscriptions
- Notes

## Accès phpMyAdmin

- URL : http://localhost/phpmyadmin (ou selon votre configuration)
- Utilisateur : `root`
- Mot de passe : (celui que vous avez configuré pour MySQL)

## Dépannage

### Erreur : "Access denied for user 'root'@'localhost'"
- Vérifiez le mot de passe dans `application-mysql.properties`
- Réinitialisez le mot de passe MySQL si nécessaire

### Erreur : "Unknown database 'projetjee'"
- Vérifiez que `createDatabaseIfNotExist=true` est dans l'URL
- Ou créez manuellement la base via phpMyAdmin

### Erreur : "Communications link failure"
- Vérifiez que MySQL est démarré
- Vérifiez le port (par défaut 3306)
- Vérifiez les paramètres de connexion

### phpMyAdmin ne démarre pas
- Vérifiez que Apache est démarré
- Vérifiez les logs d'erreur Apache
- Vérifiez la configuration PHP

## Commandes utiles MySQL

```sql
-- Voir toutes les bases de données
SHOW DATABASES;

-- Utiliser la base projetjee
USE projetjee;

-- Voir toutes les tables
SHOW TABLES;

-- Voir la structure d'une table
DESCRIBE etudiants;

-- Voir les données
SELECT * FROM etudiants;
```

