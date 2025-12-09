package com.iit.projetjee.controller.admin;

import com.iit.projetjee.entity.Inscription;
import com.iit.projetjee.service.CoursService;
import com.iit.projetjee.service.EtudiantService;
import com.iit.projetjee.service.InscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/inscriptions")
public class AdminInscriptionController {

    private final InscriptionService inscriptionService;
    private final EtudiantService etudiantService;
    private final CoursService coursService;

    @Autowired
    public AdminInscriptionController(InscriptionService inscriptionService,
                                     EtudiantService etudiantService,
                                     CoursService coursService) {
        this.inscriptionService = inscriptionService;
        this.etudiantService = etudiantService;
        this.coursService = coursService;
    }

    @GetMapping
    public String listInscriptions(Model model) {
        model.addAttribute("inscriptions", inscriptionService.getAllInscriptions());
        return "admin/inscriptions/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("inscription", new Inscription());
        model.addAttribute("etudiants", etudiantService.getAllEtudiants());
        model.addAttribute("cours", coursService.getAllCours());
        model.addAttribute("statuts", Inscription.StatutInscription.values());
        return "admin/inscriptions/form";
    }

    @PostMapping
    public String createInscription(@ModelAttribute Inscription inscription,
                                   RedirectAttributes redirectAttributes) {
        try {
            inscriptionService.createInscription(inscription);
            redirectAttributes.addFlashAttribute("success", "Inscription créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/inscriptions/new";
        }
        return "redirect:/admin/inscriptions";
    }

    @PostMapping("/{id}/valider")
    public String validerInscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inscriptionService.validerInscription(id);
            redirectAttributes.addFlashAttribute("success", "Inscription validée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/inscriptions";
    }

    @PostMapping("/{id}/refuser")
    public String refuserInscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inscriptionService.refuserInscription(id);
            redirectAttributes.addFlashAttribute("success", "Inscription refusée");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/inscriptions";
    }

    @PostMapping("/{id}/delete")
    public String deleteInscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inscriptionService.deleteInscription(id);
            redirectAttributes.addFlashAttribute("success", "Inscription supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/inscriptions";
    }
}

