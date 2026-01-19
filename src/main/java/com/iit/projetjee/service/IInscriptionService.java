package com.iit.projetjee.service;

import com.iit.projetjee.entity.Inscription;

import java.util.List;

public interface IInscriptionService {
    Inscription createInscription(Inscription inscription);
    Inscription sInscrireACours(Long etudiantId, Long coursId);
    List<Inscription> getAllInscriptions();
    Inscription getInscriptionById(Long id);
    Inscription updateInscription(Long id, Inscription inscriptionDetails);
    void deleteInscription(Long id);
    List<Inscription> getInscriptionsByEtudiant(Long etudiantId);
    List<Inscription> getInscriptionsByCours(Long coursId);
    List<Inscription> getInscriptionsByCoursWithEtudiant(Long coursId);
    Inscription validerInscription(Long id);
    Inscription refuserInscription(Long id);
}
