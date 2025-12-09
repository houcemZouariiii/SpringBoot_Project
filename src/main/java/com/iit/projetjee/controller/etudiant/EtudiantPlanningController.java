package com.iit.projetjee.controller.etudiant;

import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.entity.Groupe;
import com.iit.projetjee.entity.Seance;
import com.iit.projetjee.service.EtudiantService;
import com.iit.projetjee.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/etudiant/planning")
public class EtudiantPlanningController {

    private final EtudiantService etudiantService;
    private final SeanceService seanceService;

    @Autowired
    public EtudiantPlanningController(EtudiantService etudiantService, SeanceService seanceService) {
        this.etudiantService = etudiantService;
        this.seanceService = seanceService;
    }

    @GetMapping
    public String planning(Model model, Authentication authentication) {
        String username = authentication.getName();
        Etudiant etudiant = etudiantService.getEtudiantByUsername(username);
        List<Groupe> groupes = etudiant.getGroupes();
        List<Long> groupeIds = groupes.stream().map(Groupe::getId).collect(Collectors.toList());
        List<Seance> seances = seanceService.findByGroupeIds(groupeIds);
        model.addAttribute("seances", seances);
        model.addAttribute("groupes", groupes);
        return "etudiant/planning";
    }
}

