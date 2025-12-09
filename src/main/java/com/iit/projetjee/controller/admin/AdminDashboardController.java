package com.iit.projetjee.controller.admin;

import com.iit.projetjee.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final EtudiantService etudiantService;
    private final FormateurService formateurService;
    private final CoursService coursService;
    private final InscriptionService inscriptionService;
    private final NoteService noteService;

    @Autowired
    public AdminDashboardController(EtudiantService etudiantService,
                                   FormateurService formateurService,
                                   CoursService coursService,
                                   InscriptionService inscriptionService,
                                   NoteService noteService) {
        this.etudiantService = etudiantService;
        this.formateurService = formateurService;
        this.coursService = coursService;
        this.inscriptionService = inscriptionService;
        this.noteService = noteService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("nombreEtudiants", etudiantService.getAllEtudiants().size());
        model.addAttribute("nombreFormateurs", formateurService.getAllFormateurs().size());
        model.addAttribute("nombreCours", coursService.getAllCours().size());
        model.addAttribute("nombreInscriptions", inscriptionService.getAllInscriptions().size());
        model.addAttribute("nombreNotes", noteService.getAllNotes().size());
        return "admin/dashboard";
    }
}

