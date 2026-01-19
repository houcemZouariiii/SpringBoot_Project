package com.iit.projetjee.controller;

import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.service.IAdminEmailService;
import com.iit.projetjee.service.IEtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/emails")
public class AdminEmailController {

    private final IAdminEmailService emailService;
    private final IEtudiantService etudiantService;

    @Autowired
    public AdminEmailController(IAdminEmailService emailService, 
                               IEtudiantService etudiantService) {
        this.emailService = emailService;
        this.etudiantService = etudiantService;
    }

    @GetMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEmailForm(Model model) {
        // Récupérer tous les étudiants pour l'administration
        List<Etudiant> etudiants = etudiantService.getAllEtudiants();
        model.addAttribute("etudiants", etudiants);
        return "admin/emails/send";
    }

    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public String sendEmail(@RequestParam(required = false) List<Long> etudiantIds,
                           @RequestParam(required = false) String emailTo,
                           @RequestParam String subject,
                           @RequestParam String message,
                           RedirectAttributes redirectAttributes) {
        try {
            if (etudiantIds != null && !etudiantIds.isEmpty()) {
                // Envoyer à plusieurs étudiants
                List<String> recipients = etudiantIds.stream()
                        .map(id -> {
                            Etudiant etudiant = etudiantService.getEtudiantById(id);
                            return etudiant.getEmail();
                        })
                        .collect(Collectors.toList());

                Map<String, String> recipientNames = new HashMap<>();
                for (Long id : etudiantIds) {
                    Etudiant etudiant = etudiantService.getEtudiantById(id);
                    recipientNames.put(etudiant.getEmail(), etudiant.getNomComplet());
                }

                emailService.sendBulkEmailFromAdmin(recipients, recipientNames, subject, message);
                redirectAttributes.addFlashAttribute("success", 
                    "Email envoyé avec succès à " + recipients.size() + " étudiant(s)");
            } else if (emailTo != null && !emailTo.isEmpty()) {
                // Envoyer à un email spécifique
                emailService.sendEmailFromAdmin(emailTo, "Cher(e) Étudiant(e)", subject, message);
                redirectAttributes.addFlashAttribute("success", "Email envoyé avec succès à " + emailTo);
            } else {
                redirectAttributes.addFlashAttribute("error", "Veuillez sélectionner au moins un destinataire");
                return "redirect:/admin/emails/send";
            }
        } catch (Exception e) {
            String errorMessage = "Erreur lors de l'envoi de l'email: " + e.getMessage();
            if (e.getCause() != null) {
                errorMessage += " (" + e.getCause().getMessage() + ")";
            }
            redirectAttributes.addFlashAttribute("error", errorMessage);
        }
        return "redirect:/admin/emails/send";
    }
}
