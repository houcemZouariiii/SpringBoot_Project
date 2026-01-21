package com.iit.projetjee.controller.formateur;

import com.iit.projetjee.dto.EtudiantDTO;
import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.entity.Formateur;
import com.iit.projetjee.entity.Inscription;
import com.iit.projetjee.entity.Note;
import com.iit.projetjee.service.ICoursService;
import com.iit.projetjee.service.IFormateurService;
import com.iit.projetjee.service.IInscriptionService;
import com.iit.projetjee.service.INoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
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
    public ResponseEntity<List<EtudiantDTO>> getEtudiantsByCours(@PathVariable Long coursId, Authentication authentication) {
        List<EtudiantDTO> result = new ArrayList<>();
        
        try {
            System.out.println("========== DEBUT getEtudiantsByCours ==========");
            System.out.println("coursId: " + coursId);
            
            // Étape 1: Vérification de l'authentification
            if (authentication == null) {
                System.err.println("ERREUR: authentication est null");
                return ResponseEntity.ok(result);
            }
            System.out.println("✓ Authentication OK");
            
            // Étape 2: Récupération du username
            String username = authentication.getName();
            System.out.println("Username: " + username);
            
            // Étape 3: Vérification du rôle
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            System.out.println("IsAdmin: " + isAdmin);
            
            // Étape 4: Vérification du formateur (sauf pour admin)
            if (!isAdmin) {
                System.out.println("Vérification du formateur...");
                try {
                    Formateur formateur = formateurService.getFormateurByUsername(username);
                    System.out.println("✓ Formateur trouvé: " + formateur.getId());
                    
                    Cours cours = coursService.getCoursById(coursId);
                    System.out.println("✓ Cours trouvé: " + cours.getTitre());
                    
                    if (cours.getFormateur() == null) {
                        System.err.println("ERREUR: Le cours n'a pas de formateur");
                        return ResponseEntity.ok(result);
                    }
                    
                    if (!cours.getFormateur().getId().equals(formateur.getId())) {
                        System.err.println("ERREUR: Le cours n'appartient pas au formateur");
                        return ResponseEntity.ok(result);
                    }
                    System.out.println("✓ Vérification d'accès OK");
                } catch (Exception e) {
                    System.err.println("ERREUR lors de la vérification: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.ok(result);
                }
            } else {
                System.out.println("✓ Admin - pas de vérification nécessaire");
            }
            
            // Étape 5: Récupération des inscriptions
            System.out.println("Récupération des inscriptions pour coursId: " + coursId);
            List<Inscription> inscriptions;
            try {
                inscriptions = inscriptionService.getInscriptionsByCoursWithEtudiant(coursId);
                System.out.println("✓ Inscriptions récupérées: " + (inscriptions != null ? inscriptions.size() : 0));
            } catch (Exception e) {
                System.err.println("ERREUR lors de la récupération des inscriptions: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.ok(result);
            }
            
            // Étape 6: Transformation en DTOs
            if (inscriptions != null && !inscriptions.isEmpty()) {
                System.out.println("Transformation des inscriptions en DTOs...");
                for (Inscription inscription : inscriptions) {
                    try {
                        Etudiant etudiant = inscription.getEtudiant();
                        if (etudiant != null && etudiant.getId() != null) {
                            String nom = etudiant.getNom() != null ? etudiant.getNom() : "";
                            String prenom = etudiant.getPrenom() != null ? etudiant.getPrenom() : "";
                            
                            EtudiantDTO dto = new EtudiantDTO(etudiant.getId(), nom, prenom);
                            result.add(dto);
                            System.out.println("  → Étudiant ajouté: ID=" + dto.getId() + ", Nom=" + dto.getNom() + ", Prénom=" + dto.getPrenom());
                        } else {
                            System.err.println("  ⚠ Inscription sans étudiant valide: " + inscription.getId());
                        }
                    } catch (Exception e) {
                        System.err.println("  ⚠ Erreur lors de la transformation: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("Aucune inscription trouvée");
            }
            
            System.out.println("========== FIN getEtudiantsByCours - Résultat: " + result.size() + " étudiant(s) ==========");
            
            // Tester la sérialisation avant de retourner
            try {
                System.out.println("Test de sérialisation JSON...");
                // Forcer la sérialisation en accédant aux propriétés
                for (EtudiantDTO dto : result) {
                    dto.getId();
                    dto.getNom();
                    dto.getPrenom();
                    dto.getNomComplet();
                }
                System.out.println("✓ Sérialisation OK");
            } catch (Exception e) {
                System.err.println("ERREUR lors du test de sérialisation: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Retourner la réponse avec Content-Type explicite
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(result);
            
        } catch (org.springframework.http.converter.HttpMessageNotWritableException e) {
            System.err.println("========== ERREUR DE SÉRIALISATION JSON ==========");
            System.err.println("Message: " + e.getMessage());
            System.err.println("Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "null"));
            e.printStackTrace();
            System.err.println("==================================================");
            // Retourner une liste vide en cas d'erreur de sérialisation
            return ResponseEntity.ok(new ArrayList<>());
        } catch (Exception e) {
            System.err.println("========== ERREUR GLOBALE ==========");
            System.err.println("Type: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            System.err.println("Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "null"));
            e.printStackTrace();
            System.err.println("====================================");
            return ResponseEntity.ok(result);
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

