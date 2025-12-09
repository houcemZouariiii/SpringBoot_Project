package com.iit.projetjee.service;

import com.iit.projetjee.entity.Groupe;
import com.iit.projetjee.entity.SessionAcademique;
import com.iit.projetjee.entity.Specialite;
import com.iit.projetjee.exception.ResourceNotFoundException;
import com.iit.projetjee.repository.GroupeRepository;
import com.iit.projetjee.repository.SessionAcademiqueRepository;
import com.iit.projetjee.repository.SpecialiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GroupeService {

    private final GroupeRepository groupeRepository;
    private final SessionAcademiqueRepository sessionAcademiqueRepository;
    private final SpecialiteRepository specialiteRepository;

    @Autowired
    public GroupeService(GroupeRepository groupeRepository,
                        SessionAcademiqueRepository sessionAcademiqueRepository,
                        SpecialiteRepository specialiteRepository) {
        this.groupeRepository = groupeRepository;
        this.sessionAcademiqueRepository = sessionAcademiqueRepository;
        this.specialiteRepository = specialiteRepository;
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
        return groupeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Groupe non trouvé avec l'ID : " + id));
    }

    public List<Groupe> findAll() {
        return groupeRepository.findAll();
    }

    public void delete(Long id) {
        groupeRepository.delete(findById(id));
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

