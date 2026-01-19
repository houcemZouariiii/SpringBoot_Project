package com.iit.projetjee.service;

import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.entity.Groupe;
import com.iit.projetjee.entity.SessionAcademique;
import com.iit.projetjee.entity.Specialite;
import com.iit.projetjee.exception.ResourceNotFoundException;
import com.iit.projetjee.repository.EtudiantRepository;
import com.iit.projetjee.repository.GroupeRepository;
import com.iit.projetjee.repository.SessionAcademiqueRepository;
import com.iit.projetjee.repository.SpecialiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GroupeService {

    private final GroupeRepository groupeRepository;
    private final SessionAcademiqueRepository sessionAcademiqueRepository;
    private final SpecialiteRepository specialiteRepository;
    private final EtudiantRepository etudiantRepository;

    @Autowired
    public GroupeService(GroupeRepository groupeRepository,
                        SessionAcademiqueRepository sessionAcademiqueRepository,
                        SpecialiteRepository specialiteRepository,
                        EtudiantRepository etudiantRepository) {
        this.groupeRepository = groupeRepository;
        this.sessionAcademiqueRepository = sessionAcademiqueRepository;
        this.specialiteRepository = specialiteRepository;
        this.etudiantRepository = etudiantRepository;
    }

    public Groupe create(Groupe groupe) {
        if (groupeRepository.existsByCode(groupe.getCode())) {
            throw new IllegalArgumentException("Un groupe avec ce code existe déjà");
        }
        attachReferences(groupe);
        return groupeRepository.save(groupe);
    }

    public Groupe update(Long id, Groupe data) {
        Groupe existing = findById(id);
        if (!existing.getCode().equalsIgnoreCase(data.getCode()) && groupeRepository.existsByCode(data.getCode())) {
            throw new IllegalArgumentException("Un groupe avec ce code existe déjà");
        }
        existing.setCode(data.getCode());
        existing.setNom(data.getNom());
        existing.setTypeGroupe(data.getTypeGroupe());
        existing.setSpecialite(null);
        existing.setSession(null);
        attachReferences(data);
        existing.setSpecialite(data.getSpecialite());
        existing.setSession(data.getSession());
        return groupeRepository.save(existing);
    }

    public Groupe findById(Long id) {
        Groupe groupe = groupeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Groupe non trouvé avec l'ID : " + id));
        // Initialiser la collection des étudiants pour éviter LazyInitializationException
        if (groupe.getEtudiants() != null) {
            groupe.getEtudiants().size();
        }
        return groupe;
    }

    public List<Groupe> findAll() {
        List<Groupe> groupes = groupeRepository.findAll();
        // Initialiser les collections des étudiants pour éviter LazyInitializationException
        groupes.forEach(groupe -> {
            if (groupe.getEtudiants() != null) {
                groupe.getEtudiants().size();
            }
        });
        return groupes;
    }

    public void delete(Long id) {
        groupeRepository.delete(findById(id));
    }

    // Affecter des étudiants à un groupe
    public Groupe affecterEtudiants(Long groupeId, List<Long> etudiantIds) {
        Groupe groupe = findById(groupeId);
        
        if (etudiantIds == null || etudiantIds.isEmpty()) {
            groupe.setEtudiants(new ArrayList<>());
        } else {
            List<Etudiant> etudiants = etudiantRepository.findAllById(etudiantIds);
            if (etudiants.size() != etudiantIds.size()) {
                throw new ResourceNotFoundException("Certains étudiants n'ont pas été trouvés");
            }
            groupe.setEtudiants(etudiants);
        }
        
        return groupeRepository.save(groupe);
    }

    // Ajouter un étudiant à un groupe
    public Groupe ajouterEtudiant(Long groupeId, Long etudiantId) {
        Groupe groupe = findById(groupeId);
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'ID : " + etudiantId));
        
        if (!groupe.getEtudiants().contains(etudiant)) {
            groupe.getEtudiants().add(etudiant);
        }
        
        return groupeRepository.save(groupe);
    }

    // Retirer un étudiant d'un groupe
    public Groupe retirerEtudiant(Long groupeId, Long etudiantId) {
        Groupe groupe = findById(groupeId);
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'ID : " + etudiantId));
        
        groupe.getEtudiants().remove(etudiant);
        
        return groupeRepository.save(groupe);
    }

    private void attachReferences(Groupe groupe) {
        if (groupe.getSession() != null && groupe.getSession().getId() != null) {
            SessionAcademique session = sessionAcademiqueRepository.findById(groupe.getSession().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Session académique non trouvée"));
            groupe.setSession(session);
        }
        if (groupe.getSpecialite() != null && groupe.getSpecialite().getId() != null) {
            Specialite specialite = specialiteRepository.findById(groupe.getSpecialite().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Spécialité non trouvée"));
            groupe.setSpecialite(specialite);
        }
    }
}

