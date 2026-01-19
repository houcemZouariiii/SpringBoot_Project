package com.iit.projetjee.controller;

import com.iit.projetjee.entity.Groupe;
import com.iit.projetjee.service.IEtudiantService;
import com.iit.projetjee.service.GroupeService;
import com.iit.projetjee.service.SessionAcademiqueService;
import com.iit.projetjee.service.SpecialiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/groupes")
public class GroupeController {

    private final GroupeService groupeService;
    private final SessionAcademiqueService sessionAcademiqueService;
    private final SpecialiteService specialiteService;
    private final IEtudiantService etudiantService;

    @Autowired
    public GroupeController(GroupeService groupeService,
                            SessionAcademiqueService sessionAcademiqueService,
                            SpecialiteService specialiteService,
                            IEtudiantService etudiantService) {
        this.groupeService = groupeService;
        this.sessionAcademiqueService = sessionAcademiqueService;
        this.specialiteService = specialiteService;
        this.etudiantService = etudiantService;
    }

    // Liste de tous les groupes
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listGroupes(Model model) {
        model.addAttribute("groupes", groupeService.findAll());
        model.addAttribute("sessions", sessionAcademiqueService.findAll());
        model.addAttribute("specialites", specialiteService.findAll());
        model.addAttribute("types", Groupe.TypeGroupe.values());
        model.addAttribute("etudiants", etudiantService.getAllEtudiants());
        return "admin/groupes/list";
    }

    // Afficher le formulaire de création
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("groupe", new Groupe());
        model.addAttribute("sessions", sessionAcademiqueService.findAll());
        model.addAttribute("specialites", specialiteService.findAll());
        model.addAttribute("types", Groupe.TypeGroupe.values());
        return "admin/groupes/form";
    }

    // Créer un groupe
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createGroupe(@RequestParam String code,
                               @RequestParam(required = false) String nom,
                               @RequestParam(required = false) Groupe.TypeGroupe typeGroupe,
                               @RequestParam(required = false) Long sessionId,
                               @RequestParam(required = false) Long specialiteId,
                               RedirectAttributes redirectAttributes) {
        try {
            Groupe groupe = new Groupe();
            groupe.setCode(code);
            groupe.setNom(nom);
            groupe.setTypeGroupe(typeGroupe != null ? typeGroupe : Groupe.TypeGroupe.TP);
            
            if (sessionId != null) {
                com.iit.projetjee.entity.SessionAcademique session = new com.iit.projetjee.entity.SessionAcademique();
                session.setId(sessionId);
                groupe.setSession(session);
            }
            
            if (specialiteId != null) {
                com.iit.projetjee.entity.Specialite specialite = new com.iit.projetjee.entity.Specialite();
                specialite.setId(specialiteId);
                groupe.setSpecialite(specialite);
            }
            
            groupeService.create(groupe);
            redirectAttributes.addFlashAttribute("success", "Groupe créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/groupes";
    }

    // Afficher le formulaire d'édition
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("groupe", groupeService.findById(id));
        model.addAttribute("sessions", sessionAcademiqueService.findAll());
        model.addAttribute("specialites", specialiteService.findAll());
        model.addAttribute("types", Groupe.TypeGroupe.values());
        return "admin/groupes/form";
    }

    // Mettre à jour un groupe
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateGroupe(@PathVariable Long id,
                                @RequestParam String code,
                                @RequestParam(required = false) String nom,
                                @RequestParam(required = false) Groupe.TypeGroupe typeGroupe,
                                @RequestParam(required = false) Long sessionId,
                                @RequestParam(required = false) Long specialiteId,
                                RedirectAttributes redirectAttributes) {
        try {
            Groupe groupe = groupeService.findById(id);
            groupe.setCode(code);
            groupe.setNom(nom);
            groupe.setTypeGroupe(typeGroupe != null ? typeGroupe : Groupe.TypeGroupe.TP);
            
            if (sessionId != null) {
                com.iit.projetjee.entity.SessionAcademique session = new com.iit.projetjee.entity.SessionAcademique();
                session.setId(sessionId);
                groupe.setSession(session);
            } else {
                groupe.setSession(null);
            }
            
            if (specialiteId != null) {
                com.iit.projetjee.entity.Specialite specialite = new com.iit.projetjee.entity.Specialite();
                specialite.setId(specialiteId);
                groupe.setSpecialite(specialite);
            } else {
                groupe.setSpecialite(null);
            }
            
            groupeService.update(id, groupe);
            redirectAttributes.addFlashAttribute("success", "Groupe mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/groupes";
    }

    // Supprimer un groupe
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteGroupe(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            groupeService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Groupe supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/groupes";
    }

    // Afficher le formulaire d'affectation d'étudiants
    @GetMapping("/{id}/etudiants")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAffecterEtudiants(@PathVariable Long id, Model model) {
        Groupe groupe = groupeService.findById(id);
        model.addAttribute("groupe", groupe);
        model.addAttribute("etudiants", etudiantService.getAllEtudiants());
        
        // Créer une liste des IDs des étudiants déjà dans le groupe pour faciliter la sélection
        List<Long> etudiantIds = groupe.getEtudiants() != null 
            ? groupe.getEtudiants().stream().map(e -> e.getId()).collect(Collectors.toList())
            : new ArrayList<>();
        model.addAttribute("etudiantIds", etudiantIds);
        
        return "admin/groupes/etudiants";
    }

    // Affecter des étudiants à un groupe
    @PostMapping("/{id}/etudiants")
    @PreAuthorize("hasRole('ADMIN')")
    public String affecterEtudiants(@PathVariable Long id,
                                    @RequestParam(required = false) List<Long> etudiantIds,
                                    RedirectAttributes redirectAttributes) {
        try {
            groupeService.affecterEtudiants(id, etudiantIds);
            redirectAttributes.addFlashAttribute("success", "Étudiants affectés au groupe avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/groupes/" + id + "/etudiants";
    }

    // Ajouter un étudiant à un groupe
    @PostMapping("/{id}/etudiants/ajouter")
    @PreAuthorize("hasRole('ADMIN')")
    public String ajouterEtudiant(@PathVariable Long id,
                                  @RequestParam Long etudiantId,
                                  RedirectAttributes redirectAttributes) {
        try {
            groupeService.ajouterEtudiant(id, etudiantId);
            redirectAttributes.addFlashAttribute("success", "Étudiant ajouté au groupe avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/groupes/" + id + "/etudiants";
    }

    // Retirer un étudiant d'un groupe
    @PostMapping("/{id}/etudiants/retirer")
    @PreAuthorize("hasRole('ADMIN')")
    public String retirerEtudiant(@PathVariable Long id,
                                  @RequestParam Long etudiantId,
                                  RedirectAttributes redirectAttributes) {
        try {
            groupeService.retirerEtudiant(id, etudiantId);
            redirectAttributes.addFlashAttribute("success", "Étudiant retiré du groupe avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/groupes/" + id + "/etudiants";
    }
}
