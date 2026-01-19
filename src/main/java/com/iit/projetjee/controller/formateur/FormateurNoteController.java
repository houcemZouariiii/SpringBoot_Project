package com.iit.projetjee.controller.formateur;

import com.iit.projetjee.dto.EtudiantDTO;
import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.entity.Formateur;
import com.iit.projetjee.entity.Inscription;
import com.iit.projetjee.entity.Note;
import com.iit.projetjee.service.ICoursService;
import com.iit.projetjee.service.IEtudiantService;
import com.iit.projetjee.service.IFormateurService;
import com.iit.projetjee.service.IInscriptionService;
import com.iit.projetjee.service.INoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/formateur/notes")
public class FormateurNoteController {

    private final INoteService noteService;
    private final ICoursService coursService;
    private final IFormateurService formateurService;
    private final IInscriptionService inscriptionService;

    @Autowired
    public FormateurNoteController(INoteService noteService,
                                   ICoursService coursService,
                                   IFormateurService formateurService,
                                   IInscriptionService inscriptionService) {
        this.noteService = noteService;
        this.coursService = coursService;
        this.formateurService = formateurService;
        this.inscriptionService = inscriptionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN')")
    public String listNotes(Model model, Authentication authentication) {
        String username = authentication.getName();
        Formateur formateur = formateurService.getFormateurByUsername(username);
        
        // Récupérer tous les cours du formateur
        List<Cours> mesCours = coursService.getCoursByFormateur(formateur.getId());
        
        // Récupérer toutes les notes pour ces cours
        List<Note> notes = mesCours.stream()
                .flatMap(cours -> noteService.getNotesByCours(cours.getId()).stream())
                .collect(Collectors.toList());
        
        model.addAttribute("notes", notes);
        return "formateur/notes/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN')")
    public String showCreateForm(Model model, Authentication authentication,
                                @RequestParam(required = false) Long coursId) {
        String username = authentication.getName();
        Formateur formateur = formateurService.getFormateurByUsername(username);
        
        // Récupérer uniquement les cours du formateur
        List<Cours> mesCours = coursService.getCoursByFormateur(formateur.getId());
        
        model.addAttribute("note", new Note());
        model.addAttribute("cours", mesCours);
        
        // Si un coursId est fourni, pré-sélectionner ce cours
        if (coursId != null) {
            model.addAttribute("selectedCoursId", coursId);
        }
        
        return "formateur/notes/form";
    }

    @GetMapping("/cours/{coursId}/etudiants")
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN')")
    @ResponseBody
    public List<EtudiantDTO> getEtudiantsByCours(@PathVariable Long coursId, Authentication authentication) {
        try {
            String username = authentication.getName();
            Formateur formateur = formateurService.getFormateurByUsername(username);
            
            Cours cours = coursService.getCoursById(coursId);
            
            // Vérifier que le cours appartient au formateur
            if (cours.getFormateur() == null || !cours.getFormateur().getId().equals(formateur.getId())) {
                System.err.println("Cours n'appartient pas au formateur ou formateur null");
                return List.of();
            }
            
            // Récupérer les étudiants inscrits à ce cours (avec les étudiants chargés)
            List<Inscription> inscriptions = inscriptionService.getInscriptionsByCoursWithEtudiant(coursId);
            
            if (inscriptions == null || inscriptions.isEmpty()) {
                System.out.println("Aucune inscription trouvée pour le cours " + coursId);
                return List.of();
            }
            
            List<EtudiantDTO> etudiants = inscriptions.stream()
                    .filter(inscription -> inscription.getEtudiant() != null)
                    .map(Inscription::getEtudiant)
                    .map(etudiant -> new EtudiantDTO(etudiant.getId(), etudiant.getNom(), etudiant.getPrenom()))
                    .collect(Collectors.toList());
            
            System.out.println("Nombre d'étudiants trouvés: " + etudiants.size());
            return etudiants;
        } catch (Exception e) {
            // Log l'erreur pour le débogage
            System.err.println("Erreur lors du chargement des étudiants: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN')")
    public String createNote(@ModelAttribute Note note,
                            @RequestParam Long coursId,
                            @RequestParam Long etudiantId,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Formateur formateur = formateurService.getFormateurByUsername(username);
        
        try {
            Cours cours = coursService.getCoursById(coursId);
            
            // Vérifier que le cours appartient au formateur
            if (!cours.getFormateur().getId().equals(formateur.getId())) {
                redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas autorisé à ajouter une note pour ce cours");
                return "redirect:/formateur/notes/new";
            }
            
            // Récupérer l'étudiant
            List<Inscription> inscriptions = inscriptionService.getInscriptionsByCours(coursId);
            Etudiant etudiant = inscriptions.stream()
                    .map(Inscription::getEtudiant)
                    .filter(e -> e.getId().equals(etudiantId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("L'étudiant n'est pas inscrit à ce cours"));
            
            note.setEtudiant(etudiant);
            note.setCours(cours);
            
            noteService.createNote(note);
            redirectAttributes.addFlashAttribute("success", "Note ajoutée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/formateur/notes/new?coursId=" + coursId;
        }
        return "redirect:/formateur/notes";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        String username = authentication.getName();
        Formateur formateur = formateurService.getFormateurByUsername(username);
        
        Note note = noteService.getNoteById(id);
        
        // Vérifier que la note appartient à un cours du formateur
        if (!note.getCours().getFormateur().getId().equals(formateur.getId())) {
            return "redirect:/formateur/notes?error=unauthorized";
        }
        
        // Récupérer uniquement les cours du formateur
        List<Cours> mesCours = coursService.getCoursByFormateur(formateur.getId());
        
        model.addAttribute("note", note);
        model.addAttribute("cours", mesCours);
        return "formateur/notes/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN')")
    public String updateNote(@PathVariable Long id,
                            @ModelAttribute Note noteDetails,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Formateur formateur = formateurService.getFormateurByUsername(username);
        
        Note note = noteService.getNoteById(id);
        
        // Vérifier que la note appartient à un cours du formateur
        if (!note.getCours().getFormateur().getId().equals(formateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas autorisé à modifier cette note");
            return "redirect:/formateur/notes";
        }
        
        try {
            noteService.updateNote(id, noteDetails);
            redirectAttributes.addFlashAttribute("success", "Note mise à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/formateur/notes/" + id + "/edit";
        }
        return "redirect:/formateur/notes";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN')")
    public String deleteNote(@PathVariable Long id, 
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Formateur formateur = formateurService.getFormateurByUsername(username);
        
        Note note = noteService.getNoteById(id);
        
        // Vérifier que la note appartient à un cours du formateur
        if (!note.getCours().getFormateur().getId().equals(formateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas autorisé à supprimer cette note");
            return "redirect:/formateur/notes";
        }
        
        try {
            noteService.deleteNote(id);
            redirectAttributes.addFlashAttribute("success", "Note supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/formateur/notes";
    }
}

