package com.iit.projetjee.controller.admin;

import com.iit.projetjee.entity.SessionAcademique;
import com.iit.projetjee.service.SessionAcademiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/sessions")
public class AdminSessionViewController {

    private final SessionAcademiqueService sessionAcademiqueService;

    @Autowired
    public AdminSessionViewController(SessionAcademiqueService sessionAcademiqueService) {
        this.sessionAcademiqueService = sessionAcademiqueService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("sessions", sessionAcademiqueService.findAll());
        model.addAttribute("session", new SessionAcademique());
        model.addAttribute("semestres", SessionAcademique.Semestre.values());
        return "admin/sessions/list";
    }

    @PostMapping
    public String create(@ModelAttribute SessionAcademique session, RedirectAttributes ra) {
        try {
            sessionAcademiqueService.create(session);
            ra.addFlashAttribute("success", "Session créée");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/sessions";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute SessionAcademique session, RedirectAttributes ra) {
        try {
            sessionAcademiqueService.update(id, session);
            ra.addFlashAttribute("success", "Session mise à jour");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/sessions";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            sessionAcademiqueService.delete(id);
            ra.addFlashAttribute("success", "Session supprimée");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/sessions";
    }
}

