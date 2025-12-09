package com.iit.projetjee.repository;

import com.iit.projetjee.entity.Formateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormateurRepository extends JpaRepository<Formateur, Long> {

    // Recherche par email
    Optional<Formateur> findByEmail(String email);

    // Recherche par nom
    List<Formateur> findByNomContainingIgnoreCase(String nom);

    // Recherche par spécialité
    List<Formateur> findBySpecialiteContainingIgnoreCase(String specialite);

    // Recherche par nom ou prénom
    @Query("SELECT f FROM Formateur f WHERE LOWER(f.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(f.prenom) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Formateur> findByNomOrPrenomContaining(@Param("search") String search);

    // Vérifier si l'email existe
    boolean existsByEmail(String email);

    // Recherche par username
    Optional<Formateur> findByUsername(String username);

    // Vérifier si le username existe
    boolean existsByUsername(String username);
}

