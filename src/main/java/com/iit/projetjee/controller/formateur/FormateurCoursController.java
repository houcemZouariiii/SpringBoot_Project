package com.iit.projetjee.controller.formateur;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Formateur;
import com.iit.projetjee.service.CoursService;
import com.iit.projetjee.service.FormateurService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/formateur/cours")
public class FormateurCoursController {

    private final CoursService coursService;
    private final FormateurService formateurService;

    @Autowired
    public FormateurCoursController(CoursService coursService, FormateurService formateurService) {
        this.coursService = coursService;
        this.formateurService = formateurService;
    }

    @GetMapping
    public String listCours(Model model, Authentication authentication) {
        String username = authentication.getName();
        Formateur formateur = formateurService.getFormateurByUsername(username);
        model.addAttribute("cours", coursService.getCoursByFormateur(formateur.getId()));
        return "formateur/cours/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        Formateur formateur = formateurService.getFormateurByUsername(username);
        
        Cours cours = new Cours();
        cours.setFormateur(formateur); // Pré-remplir avec le formateur connecté
        
        model.addAttribute("cours", cours);
        model.addAttribute("niveaux", Cours.NiveauCours.values());
        return "formateur/cours/form";
    }

    @PostMapping
    public String createCours(@Valid @ModelAttribute Cours cours, 
                             BindingResult result,
                             Model model,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Formateur formateur = formateurService.getFormateurByUsername(username);
        
        // S'assurer que le cours est assigné au formateur connecté
        cours.setFormateur(formateur);
        
        if (result.hasErrors()) {
            model.addAttribute("niveaux", Cours.NiveauCours.values());
            return "formateur/cours/form";
        }
        try {
            coursService.createCours(cours);
            redirectAttributes.addFlashAttribute("success", "Cours créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            model.addAttribute("niveaux", Cours.NiveauCours.values());
            return "formateur/cours/form";
        }
        return "redirect:/formateur/cours";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        String username = authentication.getName();
        Formateur formateur = formateurService.getFormateurByUsername(username);
        
        Cours cours = coursService.getCoursById(id);
        
        // Vérifier que le cours appartient au formateur
        if (!cours.getFormateur().getId().equals(formateur.getId())) {
            return "redirect:/formateur/cours?error=unauthorized";
        }
        
        model.addAttribute("cours", cours);
        model.addAttribute("niveaux", Cours.NiveauCours.values());
        return "formateur/cours/form";
    }

    @PostMapping("/{id}")
    public String updateCours(@PathVariable Long id, 
                             @Valid @ModelAttribute Cours coursDetails,
                             BindingResult result,
                             Model model,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Formateur formateur = formateurService.getFormateurByUsername(username);
        
        Cours cours = coursService.getCoursById(id);
        
        // Vérifier que le cours appartient au formateur
        if (!cours.getFormateur().getId().equals(formateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas autorisé à modifier ce cours");
            return "redirect:/formateur/cours";
        }
        
        // S'assurer que le formateur reste le même
        coursDetails.setFormateur(formateur);
        
        if (result.hasErrors()) {
            model.addAttribute("niveaux", Cours.NiveauCours.values());
            return "formateur/cours/form";
        }
        try {
            coursService.updateCours(id, coursDetails);
            redirectAttributes.addFlashAttribute("success", "Cours mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            model.addAttribute("niveaux", Cours.NiveauCours.values());
            return "formateur/cours/form";
        }
        return "redirect:/formateur/cours";
    }

    @PostMapping("/{id}/delete")
    public String deleteCours(@PathVariable Long id, 
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Formateur formateur = formateurService.getFormateurByUsername(username);
        
        Cours cours = coursService.getCoursById(id);
        
        // Vérifier que le cours appartient au formateur
        if (!cours.getFormateur().getId().equals(formateur.getId())) {
            redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas autorisé à supprimer ce cours");
            return "redirect:/formateur/cours";
        }
        
        try {
            coursService.deleteCours(id);
            redirectAttributes.addFlashAttribute("success", "Cours supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/formateur/cours";
    }
}

