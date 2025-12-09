package com.iit.projetjee.controller.admin;

import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.service.EtudiantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/etudiants")
public class AdminEtudiantController {

    private final EtudiantService etudiantService;

    @Autowired
    public AdminEtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    @GetMapping
    public String listEtudiants(Model model, @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("etudiants", etudiantService.searchEtudiants(search));
        } else {
            model.addAttribute("etudiants", etudiantService.getAllEtudiants());
        }
        return "admin/etudiants/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("etudiant", new Etudiant());
        return "admin/etudiants/form";
    }

    @PostMapping
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
        return "redirect:/admin/etudiants";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Etudiant etudiant = etudiantService.getEtudiantById(id);
        model.addAttribute("etudiant", etudiant);
        return "admin/etudiants/form";
    }

    @PostMapping("/{id}")
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
        return "redirect:/admin/etudiants";
    }

    @PostMapping("/{id}/delete")
    public String deleteEtudiant(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            etudiantService.deleteEtudiant(id);
            redirectAttributes.addFlashAttribute("success", "Étudiant supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/etudiants";
    }
}

