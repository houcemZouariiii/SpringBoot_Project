package com.iit.projetjee.controller.admin;

import com.iit.projetjee.service.StatistiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/admin/statistiques")
public class AdminStatistiqueController {

    private final StatistiqueService statistiqueService;

    @Autowired
    public AdminStatistiqueController(StatistiqueService statistiqueService) {
        this.statistiqueService = statistiqueService;
    }

    @GetMapping
    public String statistiquesGlobales(Model model) {
        Map<String, Object> stats = statistiqueService.getStatistiquesGlobales();
        model.addAttribute("stats", stats);
        return "admin/statistiques/globales";
    }

    @GetMapping("/cours/{coursId}")
    public String statistiquesCours(@PathVariable Long coursId, Model model) {
        Map<String, Object> stats = statistiqueService.getStatistiquesCours(coursId);
        model.addAttribute("stats", stats);
        return "admin/statistiques/cours";
    }
}

