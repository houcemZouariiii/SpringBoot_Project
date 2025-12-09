package com.iit.projetjee.controller.etudiant;

import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.entity.Inscription;
import com.iit.projetjee.entity.Note;
import com.iit.projetjee.service.EtudiantService;
import com.iit.projetjee.service.InscriptionService;
import com.iit.projetjee.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/etudiant")
public class EtudiantController {

    private final InscriptionService inscriptionService;
    private final EtudiantService etudiantService;
    private final NoteService noteService;

    @Autowired
    public EtudiantController(InscriptionService inscriptionService, 
                             EtudiantService etudiantService,
                             NoteService noteService) {
        this.inscriptionService = inscriptionService;
        this.etudiantService = etudiantService;
        this.noteService = noteService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        
        try {
            // Récupérer l'étudiant par username
            Etudiant etudiant = etudiantService.getEtudiantByUsername(username);
            model.addAttribute("etudiant", etudiant);
            
            // Récupérer les inscriptions de l'étudiant
            List<Inscription> toutesInscriptions = inscriptionService.getAllInscriptions();
            List<Inscription> inscriptions = toutesInscriptions.stream()
                    .filter(ins -> ins.getEtudiant() != null && ins.getEtudiant().getId().equals(etudiant.getId()))
                    .collect(Collectors.toList());
            model.addAttribute("inscriptions", inscriptions);
            
            // Récupérer les notes de l'étudiant
            List<Note> toutesNotes = noteService.getAllNotes();
            List<Note> notes = toutesNotes.stream()
                    .filter(note -> note.getEtudiant() != null && note.getEtudiant().getId().equals(etudiant.getId()))
                    .collect(Collectors.toList());
            model.addAttribute("notes", notes);
            
            // Calculer les statistiques
            long nombreCours = inscriptions.stream()
                    .filter(ins -> ins.getStatut().toString().equals("VALIDEE"))
                    .count();
            long nombreInscriptionsEnAttente = inscriptions.stream()
                    .filter(ins -> ins.getStatut().toString().equals("EN_ATTENTE"))
                    .count();
            
            double moyenneGenerale = notes.stream()
                    .mapToDouble(Note::getValeur)
                    .average()
                    .orElse(0.0);
            
            model.addAttribute("nombreCours", nombreCours);
            model.addAttribute("nombreInscriptionsEnAttente", nombreInscriptionsEnAttente);
            model.addAttribute("moyenneGenerale", moyenneGenerale);
            
        } catch (Exception e) {
            // Si l'étudiant n'est pas trouvé, afficher un message d'erreur
            model.addAttribute("error", "Impossible de charger les données de l'étudiant: " + e.getMessage());
            model.addAttribute("inscriptions", List.of());
            model.addAttribute("notes", List.of());
            model.addAttribute("nombreCours", 0);
            model.addAttribute("nombreInscriptionsEnAttente", 0);
            model.addAttribute("moyenneGenerale", 0.0);
        }
        
        return "etudiant/dashboard";
    }
}

