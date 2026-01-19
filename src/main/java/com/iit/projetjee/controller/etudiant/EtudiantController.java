package com.iit.projetjee.controller.etudiant;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.entity.Inscription;
import com.iit.projetjee.entity.Note;
import com.iit.projetjee.service.ICoursService;
import com.iit.projetjee.service.IEtudiantService;
import com.iit.projetjee.service.IInscriptionService;
import com.iit.projetjee.service.INoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/etudiants")
public class EtudiantController {

    private final IInscriptionService inscriptionService;
    private final IEtudiantService etudiantService;
    private final INoteService noteService;
    private final ICoursService coursService;

    @Autowired
    public EtudiantController(IInscriptionService inscriptionService, 
                             IEtudiantService etudiantService,
                             INoteService noteService,
                             ICoursService coursService) {
        this.inscriptionService = inscriptionService;
        this.etudiantService = etudiantService;
        this.noteService = noteService;
        this.coursService = coursService;
    }

    // Dashboard pour l'étudiant connecté
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ADMIN')")
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

    // Liste de tous les étudiants (pour admin)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listEtudiants(Model model, @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("etudiants", etudiantService.searchEtudiants(search));
        } else {
            model.addAttribute("etudiants", etudiantService.getAllEtudiants());
        }
        return "admin/etudiants/list";
    }

    // Formulaire de création
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("etudiant", new Etudiant());
        return "admin/etudiants/form";
    }

    // Créer un étudiant
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createEtudiant(@Valid @ModelAttribute Etudiant etudiant, 
                               BindingResult result, 
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/etudiants/form";
        }
        try {
            etudiantService.createEtudiant(etudiant);
            redirectAttributes.addFlashAttribute("success", "Étudiant créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "admin/etudiants/form";
        }
        return "redirect:/etudiants";
    }

    // Formulaire d'édition
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Etudiant etudiant = etudiantService.getEtudiantById(id);
        model.addAttribute("etudiant", etudiant);
        return "admin/etudiants/form";
    }

    // Mettre à jour un étudiant
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateEtudiant(@PathVariable Long id, 
                                 @Valid @ModelAttribute Etudiant etudiant,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/etudiants/form";
        }
        try {
            etudiantService.updateEtudiant(id, etudiant);
            redirectAttributes.addFlashAttribute("success", "Étudiant mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "admin/etudiants/form";
        }
        return "redirect:/etudiants";
    }

    // Supprimer un étudiant
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteEtudiant(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            etudiantService.deleteEtudiant(id);
            redirectAttributes.addFlashAttribute("success", "Étudiant supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/etudiants";
    }

    // Afficher les cours disponibles pour s'inscrire
    @GetMapping("/cours")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ADMIN')")
    public String listCoursDisponibles(Model model, Authentication authentication) {
        String username = authentication.getName();
        try {
            Etudiant etudiant = etudiantService.getEtudiantByUsername(username);
            model.addAttribute("etudiant", etudiant);
            
            // Récupérer tous les cours
            List<Cours> tousLesCours = coursService.getAllCours();
            
            // Récupérer les inscriptions de l'étudiant
            List<Inscription> inscriptions = inscriptionService.getInscriptionsByEtudiant(etudiant.getId());
            
            // Créer une liste des IDs des cours où l'étudiant est déjà inscrit
            List<Long> coursInscritsIds = inscriptions.stream()
                    .map(ins -> ins.getCours().getId())
                    .collect(Collectors.toList());
            
            model.addAttribute("cours", tousLesCours);
            model.addAttribute("coursInscritsIds", coursInscritsIds);
            model.addAttribute("inscriptions", inscriptions);
            
        } catch (Exception e) {
            model.addAttribute("error", "Impossible de charger les cours : " + e.getMessage());
            model.addAttribute("cours", List.of());
            model.addAttribute("coursInscritsIds", List.of());
        }
        
        return "etudiant/cours";
    }

    // S'inscrire à un cours
    @PostMapping("/cours/{coursId}/inscrire")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ADMIN')")
    public String sInscrireACours(@PathVariable Long coursId,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            Etudiant etudiant = etudiantService.getEtudiantByUsername(username);
            
            inscriptionService.sInscrireACours(etudiant.getId(), coursId);
            redirectAttributes.addFlashAttribute("success", "Inscription au cours effectuée avec succès. En attente de validation.");
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'inscription : " + e.getMessage());
        }
        
        return "redirect:/etudiants/cours";
    }
}

