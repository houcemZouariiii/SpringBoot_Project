package com.iit.projetjee.service;

import com.iit.projetjee.entity.Etudiant;

import java.util.List;

public interface IEtudiantService {
    Etudiant createEtudiant(Etudiant etudiant);
    List<Etudiant> getAllEtudiants();
    Etudiant getEtudiantByUsername(String username);
    Etudiant getEtudiantById(Long id);
    Etudiant updateEtudiant(Long id, Etudiant etudiantDetails);
    void deleteEtudiant(Long id);
    List<Etudiant> searchEtudiants(String search);
    boolean emailExists(String email);
}
