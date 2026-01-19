package com.iit.projetjee.service;

import com.iit.projetjee.entity.Cours;

import java.time.LocalDate;
import java.util.List;

public interface ICoursService {
    Cours createCours(Cours cours);
    List<Cours> getAllCours();
    Cours getCoursById(Long id);
    Cours updateCours(Long id, Cours coursDetails);
    void deleteCours(Long id);
    Cours assignerFormateur(Long coursId, Long formateurId);
    boolean hasConflitHoraires(Long formateurId, LocalDate dateDebut, LocalDate dateFin, Long coursIdExclu);
    List<Cours> searchCours(String search);
    List<Cours> getCoursByFormateur(Long formateurId);
}
