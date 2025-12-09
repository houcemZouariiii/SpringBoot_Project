package com.iit.projetjee.controller;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.entity.Formateur;
import com.iit.projetjee.service.CoursService;
import com.iit.projetjee.service.EtudiantService;
import com.iit.projetjee.service.FormateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/public")
public class PublicController {

    private final EtudiantService etudiantService;
    private final FormateurService formateurService;
    private final CoursService coursService;

    @Autowired
    public PublicController(EtudiantService etudiantService,
                           FormateurService formateurService,
                           CoursService coursService) {
        this.etudiantService = etudiantService;
        this.formateurService = formateurService;
        this.coursService = coursService;
    }

    @GetMapping("/etudiants")
    public String etudiants(@RequestParam(required = false) String search, Model model) {
        List<Etudiant> etudiants;
        if (search != null && !search.trim().isEmpty()) {
            etudiants = etudiantService.getAllEtudiants().stream()
                    .filter(e -> e.getNom().toLowerCase().contains(search.toLowerCase()) ||
                            e.getPrenom().toLowerCase().contains(search.toLowerCase()) ||
                            e.getEmail().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        } else {
            etudiants = etudiantService.getAllEtudiants();
        }
        model.addAttribute("etudiants", etudiants);
        model.addAttribute("search", search);
        return "public/etudiants";
    }

    @GetMapping("/formateurs")
    public String formateurs(@RequestParam(required = false) String search, Model model) {
        List<Formateur> formateurs;
        if (search != null && !search.trim().isEmpty()) {
            formateurs = formateurService.getAllFormateurs().stream()
                    .filter(f -> f.getNom().toLowerCase().contains(search.toLowerCase()) ||
                            f.getPrenom().toLowerCase().contains(search.toLowerCase()) ||
                            f.getEmail().toLowerCase().contains(search.toLowerCase()) ||
                            (f.getSpecialite() != null && f.getSpecialite().toLowerCase().contains(search.toLowerCase())))
                    .toList();
        } else {
            formateurs = formateurService.getAllFormateurs();
        }
        model.addAttribute("formateurs", formateurs);
        model.addAttribute("search", search);
        return "public/formateurs";
    }

    @GetMapping("/cours")
    public String cours(@RequestParam(required = false) String search, Model model) {
        List<Cours> cours;
        if (search != null && !search.trim().isEmpty()) {
            cours = coursService.getAllCours().stream()
                    .filter(c -> c.getTitre().toLowerCase().contains(search.toLowerCase()) ||
                            (c.getDescription() != null && c.getDescription().toLowerCase().contains(search.toLowerCase())) ||
                            (c.getFormateur() != null && c.getFormateur().getNomComplet().toLowerCase().contains(search.toLowerCase())))
                    .toList();
        } else {
            cours = coursService.getAllCours();
        }
        model.addAttribute("cours", cours);
        model.addAttribute("search", search);
        return "public/cours";
    }
}

