package com.iit.projetjee.repository;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.entity.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    // Recherche par étudiant
    List<Inscription> findByEtudiant(Etudiant etudiant);

    // Recherche par étudiant ID
    List<Inscription> findByEtudiantId(Long etudiantId);

    // Recherche par cours
    List<Inscription> findByCours(Cours cours);

    // Recherche par cours ID
    List<Inscription> findByCoursId(Long coursId);
    
    @Query("SELECT i FROM Inscription i JOIN FETCH i.etudiant WHERE i.cours.id = :coursId")
    List<Inscription> findByCoursIdWithEtudiant(@Param("coursId") Long coursId);

    // Recherche par statut
    List<Inscription> findByStatut(Inscription.StatutInscription statut);

    // Recherche par date d'inscription
    List<Inscription> findByDateInscription(LocalDate dateInscription);

    // Recherche par date d'inscription entre deux dates
    List<Inscription> findByDateInscriptionBetween(LocalDate dateDebut, LocalDate dateFin);

    // Vérifier si un étudiant est inscrit à un cours
    boolean existsByEtudiantAndCours(Etudiant etudiant, Cours cours);

    // Trouver une inscription spécifique étudiant-cours
    Optional<Inscription> findByEtudiantAndCours(Etudiant etudiant, Cours cours);

    // Compter les inscriptions par cours
    @Query("SELECT COUNT(i) FROM Inscription i WHERE i.cours.id = :coursId")
    Long countByCoursId(@Param("coursId") Long coursId);

    // Compter les inscriptions par étudiant
    @Query("SELECT COUNT(i) FROM Inscription i WHERE i.etudiant.id = :etudiantId")
    Long countByEtudiantId(@Param("etudiantId") Long etudiantId);
}

