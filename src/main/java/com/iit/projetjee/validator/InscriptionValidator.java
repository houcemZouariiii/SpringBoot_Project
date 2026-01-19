package com.iit.projetjee.validator;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.entity.Inscription;
import com.iit.projetjee.exception.ResourceNotFoundException;
import com.iit.projetjee.repository.CoursRepository;
import com.iit.projetjee.repository.EtudiantRepository;
import com.iit.projetjee.repository.InscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InscriptionValidator {

    private final InscriptionRepository inscriptionRepository;
    private final EtudiantRepository etudiantRepository;
    private final CoursRepository coursRepository;

    @Autowired
    public InscriptionValidator(InscriptionRepository inscriptionRepository,
                               EtudiantRepository etudiantRepository,
                               CoursRepository coursRepository) {
        this.inscriptionRepository = inscriptionRepository;
        this.etudiantRepository = etudiantRepository;
        this.coursRepository = coursRepository;
    }

    /**
     * Valide une inscription avant sa création
     * @param inscription L'inscription à valider
     * @return Liste des erreurs de validation (vide si aucune erreur)
     */
    public List<String> validateInscription(Inscription inscription) {
        List<String> errors = new ArrayList<>();

        // Vérifier que l'étudiant existe
        if (inscription.getEtudiant() == null || inscription.getEtudiant().getId() == null) {
            errors.add("L'étudiant est obligatoire");
        } else {
            Etudiant etudiant = etudiantRepository.findById(inscription.getEtudiant().getId())
                    .orElse(null);
            if (etudiant == null) {
                errors.add("Étudiant non trouvé");
            } else {
                inscription.setEtudiant(etudiant);
            }
        }

        // Vérifier que le cours existe
        if (inscription.getCours() == null || inscription.getCours().getId() == null) {
            errors.add("Le cours est obligatoire");
        } else {
            Cours cours = coursRepository.findById(inscription.getCours().getId())
                    .orElse(null);
            if (cours == null) {
                errors.add("Cours non trouvé");
            } else {
                inscription.setCours(cours);
            }
        }

        // Vérifier si l'étudiant n'est pas déjà inscrit
        if (inscription.getEtudiant() != null && inscription.getCours() != null) {
            if (inscriptionRepository.existsByEtudiantAndCours(
                    inscription.getEtudiant(), inscription.getCours())) {
                errors.add("L'étudiant est déjà inscrit à ce cours");
            }
        }

        return errors;
    }

    /**
     * Vérifie les conflits d'horaires pour un étudiant
     * @param etudiantId ID de l'étudiant
     * @param coursId ID du cours
     * @return true si conflit détecté, false sinon
     */
    public boolean hasConflitHoraires(Long etudiantId, Long coursId) {
        Cours nouveauCours = coursRepository.findById(coursId)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));

        if (nouveauCours.getDateDebut() == null || nouveauCours.getDateFin() == null) {
            return false; // Pas de dates, pas de conflit possible
        }

        List<Inscription> inscriptions = inscriptionRepository.findByEtudiantId(etudiantId);

        for (Inscription inscription : inscriptions) {
            if (inscription.getStatut() != Inscription.StatutInscription.VALIDEE) {
                continue; // Ne considérer que les inscriptions validées
            }

            Cours coursExistant = inscription.getCours();
            if (coursExistant.getDateDebut() != null && coursExistant.getDateFin() != null) {
                // Vérifier si les périodes se chevauchent
                boolean chevauchement = !(nouveauCours.getDateFin().isBefore(coursExistant.getDateDebut()) ||
                        nouveauCours.getDateDebut().isAfter(coursExistant.getDateFin()));

                if (chevauchement) {
                    return true; // Conflit détecté
                }
            }
        }

        return false; // Pas de conflit
    }
}
