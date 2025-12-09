package com.iit.projetjee.repository;

import com.iit.projetjee.entity.SessionAcademique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionAcademiqueRepository extends JpaRepository<SessionAcademique, Long> {
    Optional<SessionAcademique> findByLibelleIgnoreCase(String libelle);
}

