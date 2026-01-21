package com.iit.projetjee.service;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.entity.Inscription;
import com.iit.projetjee.exception.ResourceNotFoundException;
import com.iit.projetjee.repository.CoursRepository;
import com.iit.projetjee.repository.EtudiantRepository;
import com.iit.projetjee.repository.InscriptionRepository;
import com.iit.projetjee.validator.InscriptionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Service
@Transactional
public class InscriptionService implements IInscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final EtudiantRepository etudiantRepository;
    private final CoursRepository coursRepository;
    private final ICoursService coursService;
    private final IInscriptionEmailService emailService;
    private final InscriptionValidator validator;

    @Autowired
    public InscriptionService(InscriptionRepository inscriptionRepository,
                             EtudiantRepository etudiantRepository,
                             CoursRepository coursRepository,
                             ICoursService coursService,
                             IInscriptionEmailService emailService,
                             InscriptionValidator validator) {
        this.inscriptionRepository = inscriptionRepository;
        this.etudiantRepository = etudiantRepository;
        this.coursRepository = coursRepository;
        this.coursService = coursService;
        this.emailService = emailService;
        this.validator = validator;
    }

    // Créer une inscription
    public Inscription createInscription(Inscription inscription) {
        // Valider l'inscription
        List<String> validationErrors = validator.validateInscription(inscription);
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", validationErrors));
        }
        
        // Vérifier les conflits d'horaires
        if (validator.hasConflitHoraires(inscription.getEtudiant().getId(), inscription.getCours().getId())) {
            throw new IllegalStateException("Conflit d'horaires : l'étudiant a déjà un cours à cette période");
        }
        
        if (inscription.getDateInscription() == null) {
            inscription.setDateInscription(LocalDate.now());
        }
        
        Inscription saved = inscriptionRepository.save(inscription);

        // Notifications email
        try {
            emailService.sendInscriptionConfirmation(
                    inscription.getEtudiant().getEmail(),
                    inscription.getEtudiant().getNomComplet(),
                    inscription.getCours().getTitre()
            );
            if (inscription.getCours().getFormateur() != null && inscription.getCours().getFormateur().getEmail() != null) {
                emailService.sendInscriptionNotificationFormateur(
                        inscription.getCours().getFormateur().getEmail(),
                        inscription.getEtudiant().getNomComplet(),
                        inscription.getCours().getTitre(),
                        false);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }

        return saved;
    }

    // S'inscrire à un cours
    public Inscription sInscrireACours(Long etudiantId, Long coursId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'ID : " + etudiantId));
        
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé avec l'ID : " + coursId));
        
        // Vérifier si déjà inscrit
        if (inscriptionRepository.existsByEtudiantAndCours(etudiant, cours)) {
            throw new IllegalArgumentException("L'étudiant est déjà inscrit à ce cours");
        }
        
        // Vérifier les conflits d'horaires
        if (validator.hasConflitHoraires(etudiantId, coursId)) {
            throw new IllegalStateException("Conflit d'horaires : l'étudiant a déjà un cours à cette période");
        }
        
        Inscription inscription = new Inscription(etudiant, cours);
        inscription.setStatut(Inscription.StatutInscription.EN_ATTENTE);
        
        Inscription savedInscription = inscriptionRepository.save(inscription);
        
        // Envoyer un email de confirmation
        try {
            emailService.sendInscriptionConfirmation(
                etudiant.getEmail(), 
                etudiant.getNomComplet(), 
                cours.getTitre()
            );
            if (cours.getFormateur() != null && cours.getFormateur().getEmail() != null) {
                emailService.sendInscriptionNotificationFormateur(
                        cours.getFormateur().getEmail(),
                        etudiant.getNomComplet(),
                        cours.getTitre(),
                        false);
            }
        } catch (Exception e) {
            // Logger l'erreur mais ne pas faire échouer l'inscription
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
        
        return savedInscription;
    }

    // Obtenir toutes les inscriptions
    public List<Inscription> getAllInscriptions() {
        return inscriptionRepository.findAll();
    }

    // Obtenir une inscription par ID
    public Inscription getInscriptionById(Long id) {
        return inscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription non trouvée avec l'ID : " + id));
    }

    // Mettre à jour une inscription
    public Inscription updateInscription(Long id, Inscription inscriptionDetails) {
        Inscription inscription = getInscriptionById(id);
        
        inscription.setStatut(inscriptionDetails.getStatut());
        inscription.setCommentaire(inscriptionDetails.getCommentaire());
        
        return inscriptionRepository.save(inscription);
    }

    // Supprimer une inscription
    public void deleteInscription(Long id) {
        Inscription inscription = getInscriptionById(id);
        String etudiantNom = inscription.getEtudiant().getNomComplet();
        String coursTitre = inscription.getCours().getTitre();
        String formateurEmail = inscription.getCours().getFormateur() != null ? inscription.getCours().getFormateur().getEmail() : null;
        inscriptionRepository.delete(inscription);
        try {
            if (formateurEmail != null) {
                emailService.sendInscriptionNotificationFormateur(
                        formateurEmail,
                        etudiantNom,
                        coursTitre,
                        true);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email de désinscription : " + e.getMessage());
        }
    }

    // Obtenir les inscriptions d'un étudiant
    public List<Inscription> getInscriptionsByEtudiant(Long etudiantId) {
        return inscriptionRepository.findByEtudiantId(etudiantId);
    }

    // Obtenir les inscriptions d'un cours
    @Transactional(readOnly = true)
    public List<Inscription> getInscriptionsByCours(Long coursId) {
        List<Inscription> inscriptions = inscriptionRepository.findByCoursId(coursId);
        // Forcer le chargement des étudiants dans la transaction
        if (inscriptions != null) {
            inscriptions.forEach(inscription -> {
                if (inscription.getEtudiant() != null) {
                    // Forcer le chargement en accédant aux propriétés
                    inscription.getEtudiant().getId();
                    inscription.getEtudiant().getNom();
                    inscription.getEtudiant().getPrenom();
                }
            });
        }
        return inscriptions;
    }
    
    // Obtenir les inscriptions d'un cours avec les étudiants chargés
    @Transactional(readOnly = true)
    public List<Inscription> getInscriptionsByCoursWithEtudiant(Long coursId) {
        try {
            List<Inscription> inscriptions = inscriptionRepository.findByCoursIdWithEtudiant(coursId);
            // Forcer le chargement des étudiants en accédant à leurs propriétés
            if (inscriptions != null) {
                inscriptions.forEach(inscription -> {
                    if (inscription.getEtudiant() != null) {
                        // Forcer le chargement en accédant aux propriétés
                        inscription.getEtudiant().getId();
                        inscription.getEtudiant().getNom();
                        inscription.getEtudiant().getPrenom();
                    }
                });
            }
            return inscriptions;
        } catch (Exception e) {
            System.err.println("Erreur dans getInscriptionsByCoursWithEtudiant: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Valider une inscription
    public Inscription validerInscription(Long id) {
        Inscription inscription = getInscriptionById(id);
        inscription.setStatut(Inscription.StatutInscription.VALIDEE);
        Inscription savedInscription = inscriptionRepository.save(inscription);
        
        // Envoyer un email de validation
        try {
            emailService.sendInscriptionValidation(
                inscription.getEtudiant().getEmail(),
                inscription.getEtudiant().getNomComplet(),
                inscription.getCours().getTitre()
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
        
        return savedInscription;
    }

    // Refuser une inscription
    public Inscription refuserInscription(Long id) {
        Inscription inscription = getInscriptionById(id);
        inscription.setStatut(Inscription.StatutInscription.REFUSEE);
        return inscriptionRepository.save(inscription);
    }

}

