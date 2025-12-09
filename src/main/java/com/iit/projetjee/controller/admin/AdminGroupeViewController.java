package com.iit.projetjee.controller.admin;

import com.iit.projetjee.entity.Groupe;
import com.iit.projetjee.service.GroupeService;
import com.iit.projetjee.service.SessionAcademiqueService;
import com.iit.projetjee.service.SpecialiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/groupes")
public class AdminGroupeViewController {

    private final GroupeService groupeService;
    private final SessionAcademiqueService sessionService;
    private final SpecialiteService specialiteService;

    @Autowired
    public AdminGroupeViewController(GroupeService groupeService,
                                     SessionAcademiqueService sessionService,
                                     SpecialiteService specialiteService) {
        this.groupeService = groupeService;
        this.sessionService = sessionService;
        this.specialiteService = specialiteService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("groupes", groupeService.findAll());
        model.addAttribute("groupe", new Groupe());
        model.addAttribute("sessions", sessionService.findAll());
        model.addAttribute("specialites", specialiteService.findAll());
        model.addAttribute("types", Groupe.TypeGroupe.values());
        return "admin/groupes/list";
    }

    @PostMapping
    public String create(@ModelAttribute Groupe groupe, RedirectAttributes ra) {
        try {
            groupeService.create(groupe);
            ra.addFlashAttribute("success", "Groupe créé");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/groupes";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Groupe groupe, RedirectAttributes ra) {
        try {
            groupeService.update(id, groupe);
            ra.addFlashAttribute("success", "Groupe mis à jour");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/groupes";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            groupeService.delete(id);
            ra.addFlashAttribute("success", "Groupe supprimé");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/groupes";
    }
}

