package com.iit.projetjee.repository;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Recherche par étudiant
    List<Note> findByEtudiant(Etudiant etudiant);

    // Recherche par étudiant ID
    List<Note> findByEtudiantId(Long etudiantId);

    // Recherche par cours
    List<Note> findByCours(Cours cours);

    // Recherche par cours ID
    List<Note> findByCoursId(Long coursId);

    // Recherche par type d'évaluation
    List<Note> findByTypeEvaluation(String typeEvaluation);

    // Recherche par date d'évaluation
    List<Note> findByDateEvaluation(LocalDate dateEvaluation);

    // Recherche par valeur de note (supérieure ou égale)
    List<Note> findByValeurGreaterThanEqual(Double valeur);

    // Recherche par valeur de note (inférieure ou égale)
    List<Note> findByValeurLessThanEqual(Double valeur);

    // Recherche par valeur de note entre deux valeurs
    List<Note> findByValeurBetween(Double min, Double max);

    // Trouver une note spécifique étudiant-cours-type
    Optional<Note> findByEtudiantAndCoursAndTypeEvaluation(Etudiant etudiant, Cours cours, String typeEvaluation);

    // Calculer la moyenne d'un étudiant pour un cours
    @Query("SELECT AVG(n.valeur) FROM Note n WHERE n.etudiant.id = :etudiantId AND n.cours.id = :coursId")
    Double calculateMoyenneByEtudiantAndCours(@Param("etudiantId") Long etudiantId, @Param("coursId") Long coursId);

    // Calculer la moyenne d'un cours
    @Query("SELECT AVG(n.valeur) FROM Note n WHERE n.cours.id = :coursId")
    Double calculateMoyenneByCours(@Param("coursId") Long coursId);

    // Compter les notes par cours
    @Query("SELECT COUNT(n) FROM Note n WHERE n.cours.id = :coursId")
    Long countByCoursId(@Param("coursId") Long coursId);
}

