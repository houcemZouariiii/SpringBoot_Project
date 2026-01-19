-- Script SQL pour créer la base de données projetjee
-- Ce script est optionnel car l'application créera automatiquement la base
-- Utilisez-le si vous voulez créer la base manuellement via phpMyAdmin

-- Créer la base de données
CREATE DATABASE IF NOT EXISTS springiit 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Utiliser la base de données
USE springiit;

-- Note: Les tables seront créées automatiquement par Hibernate/JPA
-- lors du premier démarrage de l'application grâce à:
-- spring.jpa.hibernate.ddl-auto=update

-- Si vous voulez voir les tables après le démarrage de l'application:
-- SHOW TABLES;

-- Tables qui seront créées automatiquement:
-- - etudiants
-- - formateurs
-- - cours
-- - inscriptions
-- - notes

