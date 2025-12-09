package com.iit.projetjee.controller.admin;

import com.iit.projetjee.entity.Seance;
import com.iit.projetjee.service.CoursService;
import com.iit.projetjee.service.FormateurService;
import com.iit.projetjee.service.GroupeService;
import com.iit.projetjee.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/seances")
public class AdminSeanceViewController {

    private final SeanceService seanceService;
    private final CoursService coursService;
    private final FormateurService formateurService;
    private final GroupeService groupeService;

    @Autowired
    public AdminSeanceViewController(SeanceService seanceService,
                                     CoursService coursService,
                                     FormateurService formateurService,
                                     GroupeService groupeService) {
        this.seanceService = seanceService;
        this.coursService = coursService;
        this.formateurService = formateurService;
        this.groupeService = groupeService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("seances", seanceService.findAll());
        model.addAttribute("cours", coursService.getAllCours());
        model.addAttribute("formateurs", formateurService.getAllFormateurs());
        model.addAttribute("groupes", groupeService.findAll());
        model.addAttribute("seance", new Seance());
        return "admin/seances/list";
    }

    @PostMapping
    public String create(@RequestParam Long coursId,
                         @RequestParam Long formateurId,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
                         @RequestParam(required = false) String salle,
                         @RequestParam(required = false) List<Long> groupeIds,
                         RedirectAttributes ra) {
        try {
            Seance seance = new Seance();
            seance.setSalle(salle);
            seance.setDateDebut(dateDebut);
            seance.setDateFin(dateFin);

            seance.setCours(new com.iit.projetjee.entity.Cours());
            seance.getCours().setId(coursId);
            seance.setFormateur(new com.iit.projetjee.entity.Formateur());
            seance.getFormateur().setId(formateurId);

            if (groupeIds != null) {
                var gs = groupeIds.stream().map(id -> {
                    var g = new com.iit.projetjee.entity.Groupe();
                    g.setId(id);
                    return g;
                }).toList();
                seance.setGroupes(gs);
            }
            seanceService.create(seance);
            ra.addFlashAttribute("success", "Séance créée");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/seances";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam Long coursId,
                         @RequestParam Long formateurId,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
                         @RequestParam(required = false) String salle,
                         @RequestParam(required = false) List<Long> groupeIds,
                         RedirectAttributes ra) {
        try {
            Seance data = new Seance();
            data.setSalle(salle);
            data.setDateDebut(dateDebut);
            data.setDateFin(dateFin);
            data.setCours(new com.iit.projetjee.entity.Cours());
            data.getCours().setId(coursId);
            data.setFormateur(new com.iit.projetjee.entity.Formateur());
            data.getFormateur().setId(formateurId);
            if (groupeIds != null) {
                var gs = groupeIds.stream().map(idg -> {
                    var g = new com.iit.projetjee.entity.Groupe();
                    g.setId(idg);
                    return g;
                }).toList();
                data.setGroupes(gs);
            }
            seanceService.update(id, data);
            ra.addFlashAttribute("success", "Séance mise à jour");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/seances";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            seanceService.delete(id);
            ra.addFlashAttribute("success", "Séance supprimée");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/seances";
    }
}

