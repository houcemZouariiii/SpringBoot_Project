package com.iit.projetjee.controller;

import com.iit.projetjee.service.IEtudiantService;
import com.iit.projetjee.service.IFormateurService;
import com.iit.projetjee.service.ICoursService;
import com.iit.projetjee.service.IInscriptionService;
import com.iit.projetjee.service.INoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final IEtudiantService etudiantService;
    private final IFormateurService formateurService;
    private final ICoursService coursService;
    private final IInscriptionService inscriptionService;
    private final INoteService noteService;

    @Autowired
    public AdminDashboardController(IEtudiantService etudiantService,
                                   IFormateurService formateurService,
                                   ICoursService coursService,
                                   IInscriptionService inscriptionService,
                                   INoteService noteService) {
        this.etudiantService = etudiantService;
        this.formateurService = formateurService;
        this.coursService = coursService;
        this.inscriptionService = inscriptionService;
        this.noteService = noteService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String dashboard(Model model) {
        model.addAttribute("nombreEtudiants", etudiantService.getAllEtudiants().size());
        model.addAttribute("nombreFormateurs", formateurService.getAllFormateurs().size());
        model.addAttribute("nombreCours", coursService.getAllCours().size());
        model.addAttribute("nombreInscriptions", inscriptionService.getAllInscriptions().size());
        model.addAttribute("nombreNotes", noteService.getAllNotes().size());
        return "admin/dashboard";
    }
}
