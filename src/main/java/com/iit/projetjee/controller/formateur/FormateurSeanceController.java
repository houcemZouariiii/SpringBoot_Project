package com.iit.projetjee.controller.formateur;

import com.iit.projetjee.entity.Seance;
import com.iit.projetjee.entity.Formateur;
import com.iit.projetjee.service.FormateurService;
import com.iit.projetjee.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final FormateurService formateurService;

    @Autowired
    public FormateurSeanceController(SeanceService seanceService, FormateurService formateurService) {
        this.seanceService = seanceService;
        this.formateurService = formateurService;
    }

    @GetMapping
    public String list(Model model, Authentication authentication) {
        String username = authentication.getName();
        Formateur formateur = formateurService.getFormateurByUsername(username);
        List<Seance> seances = seanceService.findByFormateur(formateur.getId());
        model.addAttribute("seances", seances);
        return "formateur/seances/list";
    }
}

