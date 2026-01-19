package com.iit.projetjee.controller;

import com.iit.projetjee.entity.Seance;
import com.iit.projetjee.service.ICoursService;
import com.iit.projetjee.service.IFormateurService;
import com.iit.projetjee.service.GroupeService;
import com.iit.projetjee.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/seances")
public class SeanceController {

    private final SeanceService seanceService;
    private final ICoursService coursService;
    private final IFormateurService formateurService;
    private final GroupeService groupeService;

    @Autowired
    public SeanceController(SeanceService seanceService,
                           ICoursService coursService,
                           IFormateurService formateurService,
                           GroupeService groupeService) {
        this.seanceService = seanceService;
        this.coursService = coursService;
        this.formateurService = formateurService;
        this.groupeService = groupeService;
    }

    // Liste de toutes les séances
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listSeances(Model model) {
        model.addAttribute("seances", seanceService.findAll());
        model.addAttribute("cours", coursService.getAllCours());
        model.addAttribute("formateurs", formateurService.getAllFormateurs());
        model.addAttribute("groupes", groupeService.findAll());
        return "admin/seances/list";
    }

    // Afficher le formulaire de création
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("seance", new Seance());
        model.addAttribute("cours", coursService.getAllCours());
        model.addAttribute("formateurs", formateurService.getAllFormateurs());
        model.addAttribute("groupes", groupeService.findAll());
        model.addAttribute("groupeIds", new ArrayList<Long>());
        return "admin/seances/form";
    }

    // Afficher le formulaire d'édition
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Seance seance = seanceService.findById(id);
        model.addAttribute("seance", seance);
        model.addAttribute("cours", coursService.getAllCours());
        model.addAttribute("formateurs", formateurService.getAllFormateurs());
        model.addAttribute("groupes", groupeService.findAll());
        
        // Créer une liste des IDs des groupes déjà associés à la séance
        List<Long> groupeIds = seance.getGroupes() != null 
            ? seance.getGroupes().stream().map(g -> g.getId()).collect(Collectors.toList())
            : new ArrayList<>();
        model.addAttribute("groupeIds", groupeIds);
        
        return "admin/seances/form";
    }

    // Créer une séance
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createSeance(@RequestParam Long coursId,
                               @RequestParam Long formateurId,
                               @RequestParam String dateDebut,
                               @RequestParam String dateFin,
                               @RequestParam(required = false) String salle,
                               @RequestParam(required = false) List<Long> groupeIds,
                               RedirectAttributes redirectAttributes) {
        try {
            Seance seance = new Seance();
            
            // Créer des objets proxy pour les relations
            com.iit.projetjee.entity.Cours cours = new com.iit.projetjee.entity.Cours();
            cours.setId(coursId);
            seance.setCours(cours);
            
            com.iit.projetjee.entity.Formateur formateur = new com.iit.projetjee.entity.Formateur();
            formateur.setId(formateurId);
            seance.setFormateur(formateur);
            
            // Convertir les dates depuis le format datetime-local
            // Le format peut être yyyy-MM-ddTHH:mm ou yyyy-MM-ddTHH:mm:ss
            LocalDateTime debut;
            LocalDateTime fin;
            try {
                // Essayer d'abord avec le format avec secondes
                DateTimeFormatter formatterWithSeconds = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                debut = LocalDateTime.parse(dateDebut, formatterWithSeconds);
                fin = LocalDateTime.parse(dateFin, formatterWithSeconds);
            } catch (Exception e) {
                // Si ça échoue, utiliser le format sans secondes
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                debut = LocalDateTime.parse(dateDebut, formatter);
                fin = LocalDateTime.parse(dateFin, formatter);
            }
            
            // Vérifier que la date de fin est après ou égale à la date de début
            if (fin.isBefore(debut)) {
                redirectAttributes.addFlashAttribute("error", "La date de fin doit être après ou égale à la date de début");
                return "redirect:/seances";
            }
            
            seance.setDateDebut(debut);
            seance.setDateFin(fin);
            seance.setSalle(salle);
            
            if (groupeIds != null && !groupeIds.isEmpty()) {
                List<com.iit.projetjee.entity.Groupe> groupes = new ArrayList<>();
                for (Long groupeId : groupeIds) {
                    com.iit.projetjee.entity.Groupe groupe = new com.iit.projetjee.entity.Groupe();
                    groupe.setId(groupeId);
                    groupes.add(groupe);
                }
                seance.setGroupes(groupes);
            }
            
            seanceService.create(seance);
            redirectAttributes.addFlashAttribute("success", "Séance planifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seances";
    }

    // Mettre à jour une séance
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateSeance(@PathVariable Long id,
                               @RequestParam Long coursId,
                               @RequestParam Long formateurId,
                               @RequestParam String dateDebut,
                               @RequestParam String dateFin,
                               @RequestParam(required = false) String salle,
                               @RequestParam(required = false) List<Long> groupeIds,
                               RedirectAttributes redirectAttributes) {
        try {
            Seance seance = seanceService.findById(id);
            
            // Créer des objets proxy pour les relations
            com.iit.projetjee.entity.Cours cours = new com.iit.projetjee.entity.Cours();
            cours.setId(coursId);
            seance.setCours(cours);
            
            com.iit.projetjee.entity.Formateur formateur = new com.iit.projetjee.entity.Formateur();
            formateur.setId(formateurId);
            seance.setFormateur(formateur);
            
            // Convertir les dates depuis le format datetime-local
            LocalDateTime debut;
            LocalDateTime fin;
            try {
                DateTimeFormatter formatterWithSeconds = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                debut = LocalDateTime.parse(dateDebut, formatterWithSeconds);
                fin = LocalDateTime.parse(dateFin, formatterWithSeconds);
            } catch (Exception e) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                debut = LocalDateTime.parse(dateDebut, formatter);
                fin = LocalDateTime.parse(dateFin, formatter);
            }
            
            // Vérifier que la date de fin est après ou égale à la date de début
            if (fin.isBefore(debut)) {
                redirectAttributes.addFlashAttribute("error", "La date de fin doit être après ou égale à la date de début");
                return "redirect:/seances/" + id + "/edit";
            }
            
            seance.setDateDebut(debut);
            seance.setDateFin(fin);
            seance.setSalle(salle);
            
            if (groupeIds != null && !groupeIds.isEmpty()) {
                List<com.iit.projetjee.entity.Groupe> groupes = new ArrayList<>();
                for (Long groupeId : groupeIds) {
                    com.iit.projetjee.entity.Groupe groupe = new com.iit.projetjee.entity.Groupe();
                    groupe.setId(groupeId);
                    groupes.add(groupe);
                }
                seance.setGroupes(groupes);
            } else {
                seance.setGroupes(new ArrayList<>());
            }
            
            seanceService.update(id, seance);
            redirectAttributes.addFlashAttribute("success", "Séance mise à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/seances/" + id + "/edit";
        }
        return "redirect:/seances";
    }

    // Supprimer une séance
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteSeance(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            seanceService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Séance supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seances";
    }
}
