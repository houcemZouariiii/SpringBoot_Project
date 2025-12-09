# Projet JEE - Spring Boot Application

Mini-projet Spring Boot pour IIT S1 25-26

## Description

Application Spring Boot développée dans le cadre du mini-projet JEE.

## Prérequis

- Java 17 ou supérieur
- Maven 3.6 ou supérieur
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## Structure du Projet

```
projetjee/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/iit/projetjee/
│   │   │       ├── ProjetJeeApplication.java
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── WebConfig.java
│   │   │       ├── controller/
│   │   │       │   ├── BaseController.java
│   │   │       │   └── LoginController.java
│   │   │       ├── entity/
│   │   │       │   └── BaseEntity.java
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── exception/
│   │   │           ├── GlobalExceptionHandler.java
│   │   │           └── ResourceNotFoundException.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/
│   │           ├── index.html
│   │           └── login.html
│   └── test/
├── pom.xml
└── README.md
```

## Installation et Exécution

### Option 1 : Exécution locale (sans Docker)

#### 1. Cloner ou télécharger le projet

#### 2. Installer les dépendances

```bash
mvn clean install
```

#### 3. Lancer l'application

**Développement (H2 - Base en mémoire)** :
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**MySQL avec phpMyAdmin (Recommandé)** :
1. Installez MySQL et phpMyAdmin (voir `DATABASE_SETUP.md`)
2. Configurez le mot de passe MySQL dans `application-mysql.properties`
3. Lancez l'application :
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

**Production (MySQL)** :
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

Ou exécuter directement la classe `ProjetJeeApplication` depuis votre IDE.

#### 4. Accéder à l'application

- Application: http://localhost:8080
- H2 Console (dev): http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (vide)

### Option 2 : Exécution avec Docker (Recommandé)

#### Prérequis
- Docker installé
- Docker Compose installé

#### 1. Démarrer avec Docker Compose

**Production (MySQL + App)** :
```bash
docker-compose up -d
```

**Développement (H2)** :
```bash
docker-compose -f docker-compose.dev.yml up -d
```

#### 2. Accéder à l'application

- Application: http://localhost:8080
- MySQL: localhost:3306
  - Username: `projetjee`
  - Password: `projetjeepassword`
  - Database: `projetjee`

#### 3. Voir les logs

```bash
docker-compose logs -f app
```

#### 4. Arrêter les conteneurs

```bash
docker-compose down
```

Pour plus de détails, consultez [DOCKER.md](DOCKER.md)

## Technologies Utilisées

- **Spring Boot 3.2.0**
- **Spring Web**
- **Spring Data JPA**
- **Spring Security** (authentification et autorisation)
- **Thymeleaf** (moteur de template)
- **Validation**
- **DevTools**
- **Lombok**
- **H2 Database** (développement)
- **MySQL Driver** (production, optionnel)
- **Maven**

## Configuration

Les configurations sont définies dans `src/main/resources/application.properties`.

### Base de données H2 (par défaut)

La base de données H2 est utilisée en mémoire pour le développement. Les données sont perdues au redémarrage.

### Base de données MySQL (optionnel)

Pour utiliser MySQL, décommentez les lignes correspondantes dans `application.properties` et configurez vos paramètres de connexion.

## Fonctionnalités

### Authentification
- Page de connexion avec Spring Security
- Comptes de test :
  - **user** / **password** (rôle USER)
  - **admin** / **admin** (rôle ADMIN, USER)

### Interface Admin (Thymeleaf - SSR)
- `/admin/dashboard` - Tableau de bord avec statistiques
- `/admin/etudiants` - Gestion CRUD des étudiants
- `/admin/formateurs` - Gestion CRUD des formateurs
- `/admin/cours` - Gestion CRUD des cours
- `/admin/inscriptions` - Gestion des inscriptions (validation/refus)
- `/admin/notes` - Gestion des notes

### API REST (JSON - CSR)
- `/api/etudiants` - CRUD étudiants
- `/api/formateurs` - CRUD formateurs
- `/api/cours` - CRUD cours
- `/api/inscriptions` - Gestion inscriptions
- `/api/notes` - Gestion notes

### Pages Web
- `GET /` - Page d'accueil (Thymeleaf)
- `GET /login` - Page de connexion
- `GET /health` - API de vérification de l'état (JSON)

### Base de données

**H2 (Développement)** :
- Console H2 : http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (vide)

**MySQL avec phpMyAdmin** :
- phpMyAdmin : http://localhost/phpmyadmin
- Base de données : `projetjee`
- L'application créera automatiquement les tables au premier démarrage
- Les données de test seront chargées automatiquement via `DataInitializer`
- Voir `DATABASE_SETUP.md` pour les instructions détaillées

## Développement

### Ajouter une nouvelle entité

1. Créer une classe dans `com.iit.projetjee.entity` qui étend `BaseEntity`
2. Créer un repository dans `com.iit.projetjee.repository`
3. Créer un service dans `com.iit.projetjee.service`
4. Créer un controller dans `com.iit.projetjee.controller`

## Auteur

Étudiant IIT S1 25-26

## Licence

Ce projet est développé dans le cadre académique.

"# SpringBoot_Project" 
