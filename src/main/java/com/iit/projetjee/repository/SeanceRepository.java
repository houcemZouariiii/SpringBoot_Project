package com.iit.projetjee.repository;

import com.iit.projetjee.entity.Seance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeanceRepository extends JpaRepository<Seance, Long> {

    @Query("""
        SELECT s FROM Seance s
        WHERE s.formateur.id = :formateurId
          AND s.dateDebut < :end
          AND s.dateFin > :start
          AND (:excludeId IS NULL OR s.id <> :excludeId)
    """)
    List<Seance> findConflitsFormateur(@Param("formateurId") Long formateurId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      @Param("excludeId") Long excludeId);

    @Query("""
        SELECT s FROM Seance s
        WHERE s.salle IS NOT NULL AND s.salle = :salle
          AND s.dateDebut < :end
          AND s.dateFin > :start
          AND (:excludeId IS NULL OR s.id <> :excludeId)
    """)
    List<Seance> findConflitsSalle(@Param("salle") String salle,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end,
                                  @Param("excludeId") Long excludeId);

    @Query("""
        SELECT DISTINCT s FROM Seance s
        JOIN s.groupes g
        WHERE g.id IN :groupeIds
          AND s.dateDebut < :end
          AND s.dateFin > :start
          AND (:excludeId IS NULL OR s.id <> :excludeId)
    """)
    List<Seance> findConflitsGroupes(@Param("groupeIds") List<Long> groupeIds,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end,
                                    @Param("excludeId") Long excludeId);

    List<Seance> findByFormateur_Id(Long formateurId);

    @Query("""
        SELECT DISTINCT s FROM Seance s
        JOIN s.groupes g
        WHERE g.id IN :groupeIds
    """)
    List<Seance> findByGroupeIds(@Param("groupeIds") List<Long> groupeIds);
}

