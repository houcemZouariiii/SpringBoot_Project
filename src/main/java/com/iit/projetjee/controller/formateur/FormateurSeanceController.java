package com.iit.projetjee.controller.formateur;

import com.iit.projetjee.entity.Seance;
import com.iit.projetjee.entity.Formateur;
import com.iit.projetjee.service.IFormateurService;
import com.iit.projetjee.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/formateur/seances")
public class FormateurSeanceController {

    private final SeanceService seanceService;
    private final IFormateurService formateurService;

    @Autowired
    public FormateurSeanceController(SeanceService seanceService, IFormateurService formateurService) {
        this.seanceService = seanceService;
        this.formateurService = formateurService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN')")
    public String list(Model model, Authentication authentication) {
        String username = authentication.getName();
        Formateur formateur = formateurService.getFormateurByUsername(username);
        List<Seance> seances = seanceService.findByFormateur(formateur.getId());
        model.addAttribute("seances", seances);
        return "formateur/seances/list";
    }
}

