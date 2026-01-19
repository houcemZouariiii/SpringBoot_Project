package com.iit.projetjee.controller;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Groupe;
import com.iit.projetjee.entity.SessionAcademique;
import com.iit.projetjee.entity.Specialite;
import com.iit.projetjee.service.ICoursService;
import com.iit.projetjee.service.IFormateurService;
import com.iit.projetjee.service.GroupeService;
import com.iit.projetjee.service.SessionAcademiqueService;
import com.iit.projetjee.service.SpecialiteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cours")
public class CoursController {

    private final ICoursService coursService;
    private final IFormateurService formateurService;
    private final SessionAcademiqueService sessionAcademiqueService;
    private final SpecialiteService specialiteService;
    private final GroupeService groupeService;

    @Autowired
    public CoursController(ICoursService coursService,
                          IFormateurService formateurService,
                          SessionAcademiqueService sessionAcademiqueService,
                          SpecialiteService specialiteService,
                          GroupeService groupeService) {
        this.coursService = coursService;
        this.formateurService = formateurService;
        this.sessionAcademiqueService = sessionAcademiqueService;
        this.specialiteService = specialiteService;
        this.groupeService = groupeService;
    }

    // Liste de tous les cours
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listCours(Model model, @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("cours", coursService.searchCours(search));
        } else {
            model.addAttribute("cours", coursService.getAllCours());
        }
        return "admin/cours/list";
    }

    // Formulaire de création
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("cours", new Cours());
        model.addAttribute("formateurs", formateurService.getAllFormateurs());
        model.addAttribute("niveaux", Cours.NiveauCours.values());
        model.addAttribute("sessions", sessionAcademiqueService.findAll());
        model.addAttribute("specialites", specialiteService.findAll());
        model.addAttribute("groupes", groupeService.findAll());
        return "admin/cours/form";
    }

    // Créer un cours
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createCours(@Valid @ModelAttribute Cours cours,
                             BindingResult result,
                             @RequestParam(required = false) Long sessionId,
                             @RequestParam(required = false) Long specialiteId,
                             @RequestParam(required = false) List<Long> groupeIds,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("formateurs", formateurService.getAllFormateurs());
            model.addAttribute("niveaux", Cours.NiveauCours.values());
            model.addAttribute("sessions", sessionAcademiqueService.findAll());
            model.addAttribute("specialites", specialiteService.findAll());
            model.addAttribute("groupes", groupeService.findAll());
            return "admin/cours/form";
        }
        try {
            if (sessionId != null) {
                SessionAcademique s = new SessionAcademique();
                s.setId(sessionId);
                cours.setSession(s);
            }
            if (specialiteId != null) {
                Specialite sp = new Specialite();
                sp.setId(specialiteId);
                cours.setSpecialite(sp);
            }
            if (groupeIds != null && !groupeIds.isEmpty()) {
                List<Groupe> gs = groupeIds.stream().map(id -> {
                    Groupe g = new Groupe();
                    g.setId(id);
                    return g;
                }).toList();
                cours.setGroupes(gs);
            }
            coursService.createCours(cours);
            redirectAttributes.addFlashAttribute("success", "Cours créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            model.addAttribute("formateurs", formateurService.getAllFormateurs());
            model.addAttribute("niveaux", Cours.NiveauCours.values());
            model.addAttribute("sessions", sessionAcademiqueService.findAll());
            model.addAttribute("specialites", specialiteService.findAll());
            model.addAttribute("groupes", groupeService.findAll());
            return "admin/cours/form";
        }
        return "redirect:/cours";
    }

    // Formulaire d'édition
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Cours cours = coursService.getCoursById(id);
        model.addAttribute("cours", cours);
        model.addAttribute("formateurs", formateurService.getAllFormateurs());
        model.addAttribute("niveaux", Cours.NiveauCours.values());
        model.addAttribute("sessions", sessionAcademiqueService.findAll());
        model.addAttribute("specialites", specialiteService.findAll());
        model.addAttribute("groupes", groupeService.findAll());
        return "admin/cours/form";
    }

    // Mettre à jour un cours
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateCours(@PathVariable Long id,
                             @Valid @ModelAttribute Cours cours,
                             BindingResult result,
                             @RequestParam(required = false) Long sessionId,
                             @RequestParam(required = false) Long specialiteId,
                             @RequestParam(required = false) List<Long> groupeIds,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("formateurs", formateurService.getAllFormateurs());
            model.addAttribute("niveaux", Cours.NiveauCours.values());
            model.addAttribute("sessions", sessionAcademiqueService.findAll());
            model.addAttribute("specialites", specialiteService.findAll());
            model.addAttribute("groupes", groupeService.findAll());
            return "admin/cours/form";
        }
        try {
            if (sessionId != null) {
                SessionAcademique s = new SessionAcademique();
                s.setId(sessionId);
                cours.setSession(s);
            } else {
                cours.setSession(null);
            }
            if (specialiteId != null) {
                Specialite sp = new Specialite();
                sp.setId(specialiteId);
                cours.setSpecialite(sp);
            } else {
                cours.setSpecialite(null);
            }
            if (groupeIds != null && !groupeIds.isEmpty()) {
                List<Groupe> gs = groupeIds.stream().map(gid -> {
                    Groupe g = new Groupe();
                    g.setId(gid);
                    return g;
                }).toList();
                cours.setGroupes(gs);
            } else {
                cours.setGroupes(List.of());
            }
            coursService.updateCours(id, cours);
            redirectAttributes.addFlashAttribute("success", "Cours mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            model.addAttribute("formateurs", formateurService.getAllFormateurs());
            model.addAttribute("niveaux", Cours.NiveauCours.values());
            model.addAttribute("sessions", sessionAcademiqueService.findAll());
            model.addAttribute("specialites", specialiteService.findAll());
            model.addAttribute("groupes", groupeService.findAll());
            return "admin/cours/form";
        }
        return "redirect:/cours";
    }

    // Supprimer un cours
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCours(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            coursService.deleteCours(id);
            redirectAttributes.addFlashAttribute("success", "Cours supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cours";
    }

    // Assigner un formateur à un cours
    @PostMapping("/{id}/assigner-formateur")
    @PreAuthorize("hasRole('ADMIN')")
    public String assignerFormateur(@PathVariable Long id, 
                                    @RequestParam Long formateurId,
                                    RedirectAttributes redirectAttributes) {
        try {
            coursService.assignerFormateur(id, formateurId);
            redirectAttributes.addFlashAttribute("success", "Formateur assigné avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cours";
    }
}
