package com.iit.projetjee.controller.formateur;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.entity.Formateur;
import com.iit.projetjee.service.ICoursService;
import com.iit.projetjee.service.IFormateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/formateur")
public class FormateurDashboardController {

    private final ICoursService coursService;
    private final IFormateurService formateurService;

    @Autowired
    public FormateurDashboardController(ICoursService coursService, IFormateurService formateurService) {
        this.coursService = coursService;
        this.formateurService = formateurService;
    }

    // Dashboard pour le formateur connecté (route /formateur/dashboard)
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN')")
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        try {
            // Récupérer le formateur par username
            Formateur formateur = formateurService.getFormateurByUsername(username);
            
            // Récupérer uniquement les cours de ce formateur
            List<Cours> mesCours = coursService.getCoursByFormateur(formateur.getId());
            
            model.addAttribute("formateur", formateur);
            model.addAttribute("cours", mesCours);
            model.addAttribute("nombreCours", mesCours.size());
            
            // Count students enrolled in formateur's courses
            long nombreEtudiants = mesCours.stream()
                    .flatMap(c -> c.getInscriptions().stream())
                    .map(inscription -> inscription.getEtudiant().getId())
                    .distinct()
                    .count();
            model.addAttribute("nombreEtudiants", nombreEtudiants);
        } catch (Exception e) {
            // Si le formateur n'est pas trouvé, afficher tous les cours (fallback)
            List<Cours> cours = coursService.getAllCours();
            model.addAttribute("cours", cours);
            model.addAttribute("nombreCours", cours.size());
            model.addAttribute("nombreEtudiants", 0L);
        }
        return "formateur/dashboard";
    }
}
