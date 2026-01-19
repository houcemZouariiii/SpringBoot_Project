package com.iit.projetjee.controller;

import com.iit.projetjee.entity.Specialite;
import com.iit.projetjee.service.SpecialiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/specialites")
public class SpecialiteController {

    private final SpecialiteService specialiteService;

    @Autowired
    public SpecialiteController(SpecialiteService specialiteService) {
        this.specialiteService = specialiteService;
    }

    // Liste de toutes les spécialités
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listSpecialites(Model model) {
        model.addAttribute("specialites", specialiteService.findAll());
        return "admin/specialites/list";
    }

    // Afficher le formulaire de création
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("specialite", new Specialite());
        return "admin/specialites/form";
    }

    // Afficher le formulaire d'édition
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("specialite", specialiteService.findById(id));
        return "admin/specialites/form";
    }

    // Créer une spécialité
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createSpecialite(@RequestParam String libelle,
                                   @RequestParam(required = false) String description,
                                   RedirectAttributes redirectAttributes) {
        try {
            Specialite specialite = new Specialite();
            specialite.setLibelle(libelle);
            specialite.setDescription(description);
            
            specialiteService.create(specialite);
            redirectAttributes.addFlashAttribute("success", "Spécialité créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/specialites";
    }

    // Mettre à jour une spécialité
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateSpecialite(@PathVariable Long id,
                                    @RequestParam String libelle,
                                    @RequestParam(required = false) String description,
                                    RedirectAttributes redirectAttributes) {
        try {
            Specialite specialite = specialiteService.findById(id);
            specialite.setLibelle(libelle);
            specialite.setDescription(description);
            
            specialiteService.update(id, specialite);
            redirectAttributes.addFlashAttribute("success", "Spécialité mise à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/specialites";
    }

    // Supprimer une spécialité
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteSpecialite(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            specialiteService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Spécialité supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/specialites";
    }
}
