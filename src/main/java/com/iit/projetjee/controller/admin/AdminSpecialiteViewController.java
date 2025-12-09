package com.iit.projetjee.controller.admin;

import com.iit.projetjee.entity.Specialite;
import com.iit.projetjee.service.SpecialiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/specialites")
public class AdminSpecialiteViewController {

    private final SpecialiteService specialiteService;

    @Autowired
    public AdminSpecialiteViewController(SpecialiteService specialiteService) {
        this.specialiteService = specialiteService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("specialites", specialiteService.findAll());
        model.addAttribute("specialite", new Specialite());
        return "admin/specialites/list";
    }

    @PostMapping
    public String create(@ModelAttribute Specialite specialite, RedirectAttributes ra) {
        try {
            specialiteService.create(specialite);
            ra.addFlashAttribute("success", "Spécialité créée");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/specialites";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Specialite specialite, RedirectAttributes ra) {
        try {
            specialiteService.update(id, specialite);
            ra.addFlashAttribute("success", "Spécialité mise à jour");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/specialites";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            specialiteService.delete(id);
            ra.addFlashAttribute("success", "Spécialité supprimée");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/specialites";
    }
}

