package com.iit.projetjee.service;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Inscription;
import com.iit.projetjee.entity.Note;
import com.iit.projetjee.repository.CoursRepository;
import com.iit.projetjee.repository.InscriptionRepository;
import com.iit.projetjee.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatistiqueService {

    private final NoteRepository noteRepository;
    private final InscriptionRepository inscriptionRepository;
    private final CoursRepository coursRepository;

    @Autowired
    public StatistiqueService(NoteRepository noteRepository,
                             InscriptionRepository inscriptionRepository,
                             CoursRepository coursRepository) {
        this.noteRepository = noteRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.coursRepository = coursRepository;
    }

    // Calculer la moyenne d'un étudiant pour un cours
    public Double calculerMoyenneEtudiantCours(Long etudiantId, Long coursId) {
        Double moyenne = noteRepository.calculateMoyenneByEtudiantAndCours(etudiantId, coursId);
        return moyenne != null ? moyenne : 0.0;
    }

    // Calculer le taux de réussite d'un cours (notes >= 10)
    public Double calculerTauxReussiteCours(Long coursId) {
        List<Note> notes = noteRepository.findByCoursId(coursId);
        if (notes.isEmpty()) {
            return 0.0;
        }

        long notesReussies = notes.stream()
            .filter(note -> note.getValeur() != null && note.getValeur() >= 10.0)
            .count();

        return (double) notesReussies / notes.size() * 100;
    }

    // Obtenir les statistiques d'un cours
    public Map<String, Object> getStatistiquesCours(Long coursId) {
        Map<String, Object> stats = new HashMap<>();
        
        Cours cours = coursRepository.findById(coursId).orElse(null);
        if (cours == null) {
            return stats;
        }

        List<Note> notes = noteRepository.findByCoursId(coursId);
        List<Inscription> inscriptions = inscriptionRepository.findByCoursId(coursId);

        stats.put("coursId", coursId);
        stats.put("coursTitre", cours.getTitre());
        stats.put("nombreInscriptions", inscriptions.size());
        stats.put("nombreInscriptionsValidees", 
            inscriptions.stream()
                .filter(i -> i.getStatut() == Inscription.StatutInscription.VALIDEE)
                .count());
        stats.put("nombreNotes", notes.size());
        
        if (!notes.isEmpty()) {
            Double moyenne = noteRepository.calculateMoyenneByCours(coursId);
            stats.put("moyenneCours", moyenne != null ? moyenne : 0.0);
            stats.put("noteMin", notes.stream().mapToDouble(Note::getValeur).min().orElse(0.0));
            stats.put("noteMax", notes.stream().mapToDouble(Note::getValeur).max().orElse(0.0));
            stats.put("tauxReussite", calculerTauxReussiteCours(coursId));
        } else {
            stats.put("moyenneCours", 0.0);
            stats.put("noteMin", 0.0);
            stats.put("noteMax", 0.0);
            stats.put("tauxReussite", 0.0);
        }

        return stats;
    }

    // Obtenir les statistiques globales
    public Map<String, Object> getStatistiquesGlobales() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalEtudiants = inscriptionRepository.findAll().stream()
            .map(i -> i.getEtudiant().getId())
            .distinct()
            .count();
        
        long totalCours = coursRepository.count();
        long totalInscriptions = inscriptionRepository.count();
        long totalNotes = noteRepository.count();

        stats.put("totalEtudiants", totalEtudiants);
        stats.put("totalCours", totalCours);
        stats.put("totalInscriptions", totalInscriptions);
        stats.put("totalNotes", totalNotes);

        if (totalNotes > 0) {
            List<Note> toutesLesNotes = noteRepository.findAll();
            double moyenneGlobale = toutesLesNotes.stream()
                .mapToDouble(Note::getValeur)
                .average()
                .orElse(0.0);
            stats.put("moyenneGlobale", moyenneGlobale);
        } else {
            stats.put("moyenneGlobale", 0.0);
        }

        return stats;
    }
}

