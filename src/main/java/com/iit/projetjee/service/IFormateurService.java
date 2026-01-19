package com.iit.projetjee.service;

import com.iit.projetjee.entity.Formateur;

import java.util.List;

public interface IFormateurService {
    Formateur createFormateur(Formateur formateur);
    List<Formateur> getAllFormateurs();
    Formateur getFormateurById(Long id);
    Formateur getFormateurByUsername(String username);
    Formateur updateFormateur(Long id, Formateur formateurDetails);
    void deleteFormateur(Long id);
    List<Formateur> searchFormateurs(String search);
}
