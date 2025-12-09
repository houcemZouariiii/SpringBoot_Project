package com.iit.projetjee.repository;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Formateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CoursRepository extends JpaRepository<Cours, Long> {

    // Recherche par titre
    List<Cours> findByTitreContainingIgnoreCase(String titre);

    // Recherche par formateur
    List<Cours> findByFormateur(Formateur formateur);

    // Recherche par formateur ID
    List<Cours> findByFormateurId(Long formateurId);

    // Recherche par niveau
    List<Cours> findByNiveau(Cours.NiveauCours niveau);

    // Recherche par date de début
    List<Cours> findByDateDebut(LocalDate dateDebut);

    // Recherche par date de début après une date donnée
    List<Cours> findByDateDebutAfter(LocalDate date);

    // Recherche par nombre d'heures
    List<Cours> findByNombreHeures(Integer nombreHeures);

    // Recherche par nombre d'heures entre deux valeurs
    List<Cours> findByNombreHeuresBetween(Integer min, Integer max);

    // Recherche par titre ou description
    @Query("SELECT c FROM Cours c WHERE LOWER(c.titre) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Cours> findByTitreOrDescriptionContaining(@Param("search") String search);
}

