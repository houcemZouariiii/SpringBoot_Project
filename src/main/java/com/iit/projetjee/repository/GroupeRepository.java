package com.iit.projetjee.repository;

import com.iit.projetjee.entity.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupeRepository extends JpaRepository<Groupe, Long> {
    Optional<Groupe> findByCode(String code);
    boolean existsByCode(String code);
    List<Groupe> findBySpecialite_Id(Long specialiteId);
    List<Groupe> findBySession_Id(Long sessionId);
}

