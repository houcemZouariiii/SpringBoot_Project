package com.iit.projetjee.service;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Formateur;
import com.iit.projetjee.entity.Groupe;
import com.iit.projetjee.entity.SessionAcademique;
import com.iit.projetjee.entity.Specialite;
import com.iit.projetjee.exception.ResourceNotFoundException;
import com.iit.projetjee.repository.CoursRepository;
import com.iit.projetjee.repository.GroupeRepository;
import com.iit.projetjee.repository.SessionAcademiqueRepository;
import com.iit.projetjee.repository.SpecialiteRepository;
import com.iit.projetjee.repository.FormateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class CoursService {

    private final CoursRepository coursRepository;
    private final FormateurRepository formateurRepository;
    private final SessionAcademiqueRepository sessionAcademiqueRepository;
    private final SpecialiteRepository specialiteRepository;
    private final GroupeRepository groupeRepository;

    @Autowired
    public CoursService(CoursRepository coursRepository,
                        FormateurRepository formateurRepository,
                        SessionAcademiqueRepository sessionAcademiqueRepository,
                        SpecialiteRepository specialiteRepository,
                        GroupeRepository groupeRepository) {
        this.coursRepository = coursRepository;
        this.formateurRepository = formateurRepository;
        this.sessionAcademiqueRepository = sessionAcademiqueRepository;
        this.specialiteRepository = specialiteRepository;
        this.groupeRepository = groupeRepository;
    }

    // Créer un cours
    public Cours createCours(Cours cours) {
        // Vérifier que le formateur existe
        if (cours.getFormateur() != null && cours.getFormateur().getId() != null) {
            Formateur formateur = formateurRepository.findById(cours.getFormateur().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Formateur non trouvé"));
            cours.setFormateur(formateur);
        }
        attachReferences(cours);
        return coursRepository.save(cours);
    }

    // Obtenir tous les cours
    public List<Cours> getAllCours() {
        return coursRepository.findAll();
    }

    // Obtenir un cours par ID
    public Cours getCoursById(Long id) {
        return coursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé avec l'ID : " + id));
    }

    // Mettre à jour un cours
    public Cours updateCours(Long id, Cours coursDetails) {
        Cours cours = getCoursById(id);
        
        cours.setTitre(coursDetails.getTitre());
        cours.setDescription(coursDetails.getDescription());
        cours.setNombreHeures(coursDetails.getNombreHeures());
        cours.setDateDebut(coursDetails.getDateDebut());
        cours.setDateFin(coursDetails.getDateFin());
        cours.setPrix(coursDetails.getPrix());
        cours.setNiveau(coursDetails.getNiveau());
        attachReferences(coursDetails);
        cours.setSession(coursDetails.getSession());
        cours.setSpecialite(coursDetails.getSpecialite());
        cours.setGroupes(coursDetails.getGroupes());
        
        // Mettre à jour le formateur si fourni
        if (coursDetails.getFormateur() != null && coursDetails.getFormateur().getId() != null) {
            Formateur formateur = formateurRepository.findById(coursDetails.getFormateur().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Formateur non trouvé"));
            cours.setFormateur(formateur);
        }
        
        return coursRepository.save(cours);
    }

    // Supprimer un cours
    public void deleteCours(Long id) {
        Cours cours = getCoursById(id);
        coursRepository.delete(cours);
    }

    // Assigner un formateur à un cours
    public Cours assignerFormateur(Long coursId, Long formateurId) {
        Cours cours = getCoursById(coursId);
        Formateur formateur = formateurRepository.findById(formateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Formateur non trouvé avec l'ID : " + formateurId));
        
        cours.setFormateur(formateur);
        return coursRepository.save(cours);
    }

    // Vérifier les conflits d'horaires pour un formateur
    public boolean hasConflitHoraires(Long formateurId, LocalDate dateDebut, LocalDate dateFin, Long coursIdExclu) {
        List<Cours> coursDuFormateur = coursRepository.findByFormateurId(formateurId);
        
        for (Cours cours : coursDuFormateur) {
            if (coursIdExclu != null && cours.getId().equals(coursIdExclu)) {
                continue; // Ignorer le cours en cours de modification
            }
            
            if (cours.getDateDebut() != null && cours.getDateFin() != null &&
                dateDebut != null && dateFin != null) {
                
                // Vérifier si les périodes se chevauchent
                boolean chevauchement = !(dateFin.isBefore(cours.getDateDebut()) || 
                                          dateDebut.isAfter(cours.getDateFin()));
                
                if (chevauchement) {
                    return true; // Conflit détecté
                }
            }
        }
        
        return false; // Pas de conflit
    }

    // Rechercher des cours
    public List<Cours> searchCours(String search) {
        return coursRepository.findByTitreOrDescriptionContaining(search);
    }

    // Obtenir les cours d'un formateur
    public List<Cours> getCoursByFormateur(Long formateurId) {
        return coursRepository.findByFormateurId(formateurId);
    }

    private void attachReferences(Cours cours) {
        if (cours.getSession() != null && cours.getSession().getId() != null) {
            SessionAcademique session = sessionAcademiqueRepository.findById(cours.getSession().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Session académique non trouvée"));
            cours.setSession(session);
        }
        if (cours.getSpecialite() != null && cours.getSpecialite().getId() != null) {
            Specialite specialite = specialiteRepository.findById(cours.getSpecialite().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Spécialité non trouvée"));
            cours.setSpecialite(specialite);
        }
        if (cours.getGroupes() != null && !cours.getGroupes().isEmpty()) {
            List<Long> ids = cours.getGroupes().stream()
                    .map(Groupe::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            cours.setGroupes(groupeRepository.findAllById(ids));
        }
    }
}

