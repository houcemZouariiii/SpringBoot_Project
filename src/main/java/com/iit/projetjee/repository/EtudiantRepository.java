package com.iit.projetjee.repository;

import com.iit.projetjee.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    // Recherche par email
    Optional<Etudiant> findByEmail(String email);

    // Recherche par username
    Optional<Etudiant> findByUsername(String username);

    // Recherche par nom
    List<Etudiant> findByNomContainingIgnoreCase(String nom);

    // Recherche par prénom
    List<Etudiant> findByPrenomContainingIgnoreCase(String prenom);

    // Recherche par nom ou prénom
    @Query("SELECT e FROM Etudiant e WHERE LOWER(e.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Etudiant> findByNomOrPrenomContaining(@Param("search") String search);

    // Vérifier si l'email existe
    boolean existsByEmail(String email);

    // Vérifier si le username existe
    boolean existsByUsername(String username);
}

