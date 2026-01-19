package com.iit.projetjee.controller;

import com.iit.projetjee.entity.SessionAcademique;
import com.iit.projetjee.service.SessionAcademiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sessions")
public class SessionController {

    private final SessionAcademiqueService sessionAcademiqueService;

    @Autowired
    public SessionController(SessionAcademiqueService sessionAcademiqueService) {
        this.sessionAcademiqueService = sessionAcademiqueService;
    }

    // Liste de toutes les sessions académiques
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listSessions(Model model) {
        model.addAttribute("sessions", sessionAcademiqueService.findAll());
        model.addAttribute("semestres", SessionAcademique.Semestre.values());
        return "admin/sessions/list";
    }

    // Afficher le formulaire de création
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("session", new SessionAcademique());
        model.addAttribute("semestres", SessionAcademique.Semestre.values());
        return "admin/sessions/form";
    }

    // Afficher le formulaire d'édition
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("session", sessionAcademiqueService.findById(id));
        model.addAttribute("semestres", SessionAcademique.Semestre.values());
        return "admin/sessions/form";
    }

    // Créer une session académique
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createSession(@RequestParam String libelle,
                               @RequestParam Integer annee,
                               @RequestParam SessionAcademique.Semestre semestre,
                               @RequestParam(required = false) Boolean actif,
                               RedirectAttributes redirectAttributes) {
        try {
            SessionAcademique session = new SessionAcademique();
            session.setLibelle(libelle);
            session.setAnnee(annee);
            session.setSemestre(semestre);
            session.setActif(actif != null ? actif : true);
            
            sessionAcademiqueService.create(session);
            redirectAttributes.addFlashAttribute("success", "Session académique créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/sessions";
    }

    // Mettre à jour une session académique
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateSession(@PathVariable Long id,
                               @RequestParam String libelle,
                               @RequestParam Integer annee,
                               @RequestParam SessionAcademique.Semestre semestre,
                               @RequestParam(required = false) Boolean actif,
                               RedirectAttributes redirectAttributes) {
        try {
            SessionAcademique session = sessionAcademiqueService.findById(id);
            session.setLibelle(libelle);
            session.setAnnee(annee);
            session.setSemestre(semestre);
            session.setActif(actif != null ? actif : false);
            
            sessionAcademiqueService.update(id, session);
            redirectAttributes.addFlashAttribute("success", "Session académique mise à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/sessions";
    }

    // Supprimer une session académique
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteSession(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            sessionAcademiqueService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Session académique supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/sessions";
    }
}
