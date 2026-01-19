package com.iit.projetjee.controller;

import com.iit.projetjee.entity.Inscription;
import com.iit.projetjee.service.ICoursService;
import com.iit.projetjee.service.IEtudiantService;
import com.iit.projetjee.service.IInscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inscriptions")
public class InscriptionController {

    private final IInscriptionService inscriptionService;
    private final IEtudiantService etudiantService;
    private final ICoursService coursService;

    @Autowired
    public InscriptionController(IInscriptionService inscriptionService,
                                IEtudiantService etudiantService,
                                ICoursService coursService) {
        this.inscriptionService = inscriptionService;
        this.etudiantService = etudiantService;
        this.coursService = coursService;
    }

    // Liste de toutes les inscriptions
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listInscriptions(Model model) {
        model.addAttribute("inscriptions", inscriptionService.getAllInscriptions());
        return "admin/inscriptions/list";
    }

    // Formulaire de création
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("inscription", new Inscription());
        model.addAttribute("etudiants", etudiantService.getAllEtudiants());
        model.addAttribute("cours", coursService.getAllCours());
        model.addAttribute("statuts", Inscription.StatutInscription.values());
        return "admin/inscriptions/form";
    }

    // Créer une inscription
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createInscription(@ModelAttribute Inscription inscription,
                                   RedirectAttributes redirectAttributes) {
        try {
            inscriptionService.createInscription(inscription);
            redirectAttributes.addFlashAttribute("success", "Inscription créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/inscriptions/new";
        }
        return "redirect:/inscriptions";
    }

    // Valider une inscription
    @PostMapping("/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public String validerInscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inscriptionService.validerInscription(id);
            redirectAttributes.addFlashAttribute("success", "Inscription validée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inscriptions";
    }

    // Refuser une inscription
    @PostMapping("/{id}/refuser")
    @PreAuthorize("hasRole('ADMIN')")
    public String refuserInscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inscriptionService.refuserInscription(id);
            redirectAttributes.addFlashAttribute("success", "Inscription refusée");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inscriptions";
    }

    // Supprimer une inscription
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteInscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inscriptionService.deleteInscription(id);
            redirectAttributes.addFlashAttribute("success", "Inscription supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inscriptions";
    }
}
