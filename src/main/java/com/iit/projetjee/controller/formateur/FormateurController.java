package com.iit.projetjee.controller.formateur;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Formateur;
import com.iit.projetjee.service.ICoursService;
import com.iit.projetjee.service.IFormateurService;
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

@Controller
@RequestMapping("/formateurs")
public class FormateurController {

    private final ICoursService coursService;
    private final IFormateurService formateurService;

    @Autowired
    public FormateurController(ICoursService coursService, IFormateurService formateurService) {
        this.coursService = coursService;
        this.formateurService = formateurService;
    }

    // Dashboard pour le formateur connecté (route /formateurs/dashboard)
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN')")
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        try {
            // Récupérer le formateur par username
            Formateur formateur = formateurService.getFormateurByUsername(username);
            
            // Récupérer uniquement les cours de ce formateur
            List<Cours> mesCours = coursService.getCoursByFormateur(formateur.getId());
            
            model.addAttribute("formateur", formateur);
            model.addAttribute("cours", mesCours);
            model.addAttribute("nombreCours", mesCours.size());
            
            // Count students enrolled in formateur's courses
            long nombreEtudiants = mesCours.stream()
                    .flatMap(c -> c.getInscriptions().stream())
                    .map(inscription -> inscription.getEtudiant().getId())
                    .distinct()
                    .count();
            model.addAttribute("nombreEtudiants", nombreEtudiants);
        } catch (Exception e) {
            // Si le formateur n'est pas trouvé, afficher tous les cours (fallback)
            List<Cours> cours = coursService.getAllCours();
            model.addAttribute("cours", cours);
            model.addAttribute("nombreCours", cours.size());
            model.addAttribute("nombreEtudiants", 0L);
        }
        return "formateur/dashboard";
    }

    // Liste de tous les formateurs (pour admin)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listFormateurs(Model model, @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("formateurs", formateurService.searchFormateurs(search));
        } else {
            model.addAttribute("formateurs", formateurService.getAllFormateurs());
        }
        return "admin/formateurs/list";
    }

    // Formulaire de création
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("formateur", new Formateur());
        return "admin/formateurs/form";
    }

    // Créer un formateur
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createFormateur(@Valid @ModelAttribute Formateur formateur, 
                                  BindingResult result, 
                                  RedirectAttributes redirectAttributes) {
        // Validation manuelle supplémentaire
        if (formateur.getUsername() == null || formateur.getUsername().trim().isEmpty()) {
            result.rejectValue("username", "error.formateur", "Le nom d'utilisateur est obligatoire");
        }
        if (formateur.getPassword() == null || formateur.getPassword().trim().isEmpty()) {
            result.rejectValue("password", "error.formateur", "Le mot de passe est obligatoire");
        }
        
        if (result.hasErrors()) {
            return "admin/formateurs/form";
        }
        try {
            formateurService.createFormateur(formateur);
            redirectAttributes.addFlashAttribute("success", "Formateur créé avec succès. Username: " + formateur.getUsername());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "admin/formateurs/form";
        }
        return "redirect:/formateurs";
    }

    // Formulaire d'édition
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Formateur formateur = formateurService.getFormateurById(id);
        model.addAttribute("formateur", formateur);
        return "admin/formateurs/form";
    }

    // Mettre à jour un formateur
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateFormateur(@PathVariable Long id, 
                                  @Valid @ModelAttribute Formateur formateur,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/formateurs/form";
        }
        try {
            formateurService.updateFormateur(id, formateur);
            redirectAttributes.addFlashAttribute("success", "Formateur mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "admin/formateurs/form";
        }
        return "redirect:/formateurs";
    }

    // Supprimer un formateur
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteFormateur(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            formateurService.deleteFormateur(id);
            redirectAttributes.addFlashAttribute("success", "Formateur supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/formateurs";
    }
}

