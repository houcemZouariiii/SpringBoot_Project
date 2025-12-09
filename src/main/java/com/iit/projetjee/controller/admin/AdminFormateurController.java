package com.iit.projetjee.controller.admin;

import com.iit.projetjee.entity.Formateur;
import com.iit.projetjee.service.FormateurService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/formateurs")
public class AdminFormateurController {

    private final FormateurService formateurService;

    @Autowired
    public AdminFormateurController(FormateurService formateurService) {
        this.formateurService = formateurService;
    }

    @GetMapping
    public String listFormateurs(Model model, @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("formateurs", formateurService.searchFormateurs(search));
        } else {
            model.addAttribute("formateurs", formateurService.getAllFormateurs());
        }
        return "admin/formateurs/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("formateur", new Formateur());
        return "admin/formateurs/form";
    }

    @PostMapping
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
        return "redirect:/admin/formateurs";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Formateur formateur = formateurService.getFormateurById(id);
        model.addAttribute("formateur", formateur);
        return "admin/formateurs/form";
    }

    @PostMapping("/{id}")
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
        return "redirect:/admin/formateurs";
    }

    @PostMapping("/{id}/delete")
    public String deleteFormateur(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            formateurService.deleteFormateur(id);
            redirectAttributes.addFlashAttribute("success", "Formateur supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/formateurs";
    }
}

