package com.iit.projetjee.service;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.entity.Note;
import com.iit.projetjee.exception.ResourceNotFoundException;
import com.iit.projetjee.repository.CoursRepository;
import com.iit.projetjee.repository.EtudiantRepository;
import com.iit.projetjee.repository.InscriptionRepository;
import com.iit.projetjee.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class NoteService implements INoteService {

    private final NoteRepository noteRepository;
    private final EtudiantRepository etudiantRepository;
    private final CoursRepository coursRepository;
    private final InscriptionRepository inscriptionRepository;
    private final INotificationEmailService emailService;

    @Autowired
    public NoteService(NoteRepository noteRepository,
                      EtudiantRepository etudiantRepository,
                      CoursRepository coursRepository,
                      InscriptionRepository inscriptionRepository,
                      INotificationEmailService emailService) {
        this.noteRepository = noteRepository;
        this.etudiantRepository = etudiantRepository;
        this.coursRepository = coursRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.emailService = emailService;
    }

    // Créer une note
    public Note createNote(Note note) {
        // Vérifier que l'étudiant existe
        Etudiant etudiant = null;
        if (note.getEtudiant() != null && note.getEtudiant().getId() != null) {
            etudiant = etudiantRepository.findById(note.getEtudiant().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));
            note.setEtudiant(etudiant);
        }
        
        // Vérifier que le cours existe
        Cours cours = null;
        if (note.getCours() != null && note.getCours().getId() != null) {
            cours = coursRepository.findById(note.getCours().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));
            note.setCours(cours);
        }
        
        // Vérifier que l'étudiant est inscrit au cours
        if (!isEtudiantInscrit(note.getEtudiant().getId(), note.getCours().getId())) {
            throw new IllegalStateException("L'étudiant n'est pas inscrit à ce cours");
        }
        
        if (note.getDateEvaluation() == null) {
            note.setDateEvaluation(LocalDate.now());
        }
        
        Note savedNote = noteRepository.save(note);
        
        // Envoyer un email de notification à l'étudiant
        if (etudiant != null && cours != null && savedNote.getValeur() != null) {
            try {
                emailService.sendNoteNotification(
                    etudiant.getEmail(),
                    etudiant.getNomComplet(),
                    cours.getTitre(),
                    savedNote.getValeur(),
                    savedNote.getTypeEvaluation(),
                    savedNote.getCommentaire(),
                    false // Nouvelle note
                );
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi de l'email de notification de note : " + e.getMessage());
            }
        }
        
        return savedNote;
    }

    // Ajouter une note
    public Note ajouterNote(Long etudiantId, Long coursId, Double valeur, String typeEvaluation) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'ID : " + etudiantId));
        
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé avec l'ID : " + coursId));
        
        // Vérifier que l'étudiant est inscrit au cours
        if (!isEtudiantInscrit(etudiantId, coursId)) {
            throw new IllegalStateException("L'étudiant n'est pas inscrit à ce cours");
        }
        
        Note note = new Note(valeur, etudiant, cours);
        note.setTypeEvaluation(typeEvaluation);
        
        Note savedNote = noteRepository.save(note);
        
        // Envoyer un email de notification
        try {
            emailService.sendNoteNotification(
                etudiant.getEmail(),
                etudiant.getNomComplet(),
                cours.getTitre(),
                valeur,
                savedNote.getTypeEvaluation(),
                savedNote.getCommentaire(),
                false // Nouvelle note
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
        
        return savedNote;
    }

    // Obtenir toutes les notes
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    // Obtenir une note par ID
    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note non trouvée avec l'ID : " + id));
    }

    // Mettre à jour une note
    public Note updateNote(Long id, Note noteDetails) {
        Note note = getNoteById(id);
        
        // Sauvegarder les anciennes valeurs pour comparer
        Double ancienneValeur = note.getValeur();
        
        note.setValeur(noteDetails.getValeur());
        note.setTypeEvaluation(noteDetails.getTypeEvaluation());
        note.setCommentaire(noteDetails.getCommentaire());
        note.setDateEvaluation(noteDetails.getDateEvaluation());
        
        Note savedNote = noteRepository.save(note);
        
        // Envoyer un email de notification si la note a changé
        if (savedNote.getEtudiant() != null && savedNote.getCours() != null && 
            savedNote.getValeur() != null && 
            (ancienneValeur == null || !ancienneValeur.equals(savedNote.getValeur()))) {
            try {
                emailService.sendNoteNotification(
                    savedNote.getEtudiant().getEmail(),
                    savedNote.getEtudiant().getNomComplet(),
                    savedNote.getCours().getTitre(),
                    savedNote.getValeur(),
                    savedNote.getTypeEvaluation(),
                    savedNote.getCommentaire(),
                    true // Mise à jour
                );
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi de l'email de notification de note : " + e.getMessage());
            }
        }
        
        return savedNote;
    }

    // Supprimer une note
    public void deleteNote(Long id) {
        Note note = getNoteById(id);
        noteRepository.delete(note);
    }

    // Obtenir les notes d'un étudiant
    public List<Note> getNotesByEtudiant(Long etudiantId) {
        return noteRepository.findByEtudiantId(etudiantId);
    }

    // Obtenir les notes d'un cours
    public List<Note> getNotesByCours(Long coursId) {
        return noteRepository.findByCoursId(coursId);
    }

    // Obtenir les notes d'un étudiant pour un cours
    public List<Note> getNotesByEtudiantAndCours(Long etudiantId, Long coursId) {
        return noteRepository.findByEtudiantId(etudiantId).stream()
                .filter(note -> note.getCours().getId().equals(coursId))
                .toList();
    }

    // Calculer la moyenne d'un étudiant pour un cours
    public Double calculerMoyenneEtudiantCours(Long etudiantId, Long coursId) {
        Double moyenne = noteRepository.calculateMoyenneByEtudiantAndCours(etudiantId, coursId);
        return moyenne != null ? moyenne : 0.0;
    }

    // Calculer la moyenne d'un cours
    public Double calculerMoyenneCours(Long coursId) {
        Double moyenne = noteRepository.calculateMoyenneByCours(coursId);
        return moyenne != null ? moyenne : 0.0;
    }

    // Moyenne générale d'un étudiant (tous cours confondus)
    public double calculMoyenneGeneraleEtudiant(Long etudiantId) {
        List<Note> notes = noteRepository.findByEtudiantId(etudiantId);
        if (notes == null || notes.isEmpty()) {
            return 0d;
        }
        return notes.stream()
                .filter(n -> n.getValeur() != null)
                .mapToDouble(Note::getValeur)
                .average()
                .orElse(0d);
    }

    // Taux de réussite d'un cours (notes >= 10 / total)
    public double calculTauxReussiteCours(Long coursId) {
        List<Note> notes = noteRepository.findByCoursId(coursId);
        if (notes == null || notes.isEmpty()) {
            return 0d;
        }
        long reussites = notes.stream()
                .filter(n -> n.getValeur() != null && n.getValeur() >= 10d)
                .count();
        return (reussites * 100.0) / notes.size();
    }

    // Top cours les plus suivis (par nb d'inscriptions)
    public List<com.iit.projetjee.dto.CoursStatDTO> topCoursParInscriptions(int limit) {
        List<Cours> coursList = coursRepository.findAll();
        return coursList.stream()
                .map(c -> new com.iit.projetjee.dto.CoursStatDTO(
                        c.getId(),
                        c.getTitre(),
                        inscriptionRepository.countByCoursId(c.getId())))
                .sorted((a, b) -> Long.compare(b.getInscriptions(), a.getInscriptions()))
                .limit(limit)
                .toList();
    }

    // Vérifier si un étudiant est inscrit à un cours
    private boolean isEtudiantInscrit(Long etudiantId, Long coursId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));
        
        return inscriptionRepository.existsByEtudiantAndCours(etudiant, cours);
    }
}

