package com.iit.projetjee.controller.formateur;

import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.entity.Formateur;
import com.iit.projetjee.entity.Inscription;
import com.iit.projetjee.service.EmailService;
import com.iit.projetjee.service.EtudiantService;
import com.iit.projetjee.service.FormateurService;
import com.iit.projetjee.service.InscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/formateur/emails")
public class FormateurEmailController {

    private final EmailService emailService;
    private final EtudiantService etudiantService;
    private final FormateurService formateurService;
    private final InscriptionService inscriptionService;

    @Autowired
    public FormateurEmailController(EmailService emailService, 
                                   EtudiantService etudiantService,
                                   FormateurService formateurService,
                                   InscriptionService inscriptionService) {
        this.emailService = emailService;
        this.etudiantService = etudiantService;
        this.formateurService = formateurService;
        this.inscriptionService = inscriptionService;
    }

    @GetMapping("/send")
    public String showEmailForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        
        try {
            // Récupérer le formateur par username
            Formateur formateur = formateurService.getFormateurByUsername(username);
            model.addAttribute("formateur", formateur);
            
            // Récupérer les étudiants inscrits aux cours du formateur
            List<Inscription> inscriptions = inscriptionService.getAllInscriptions();
            List<Etudiant> etudiants = inscriptions.stream()
                    .filter(ins -> ins.getCours() != null && 
                                 ins.getCours().getFormateur() != null &&
                                 ins.getCours().getFormateur().getId().equals(formateur.getId()))
                    .map(Inscription::getEtudiant)
                    .distinct()
                    .collect(Collectors.toList());
            
            model.addAttribute("etudiants", etudiants);
        } catch (Exception e) {
            // Fallback si le formateur n'est pas trouvé
            model.addAttribute("etudiants", etudiantService.getAllEtudiants());
        }
        
        return "formateur/emails/send";
    }

    @PostMapping("/send")
    public String sendEmail(@RequestParam(required = false) List<Long> etudiantIds,
                           @RequestParam(required = false) String emailTo,
                           @RequestParam String subject,
                           @RequestParam String message,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            // Récupérer le formateur par username
            String username = authentication.getName();
            Formateur formateur = formateurService.getFormateurByUsername(username);

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

                emailService.sendBulkEmailFromFormateur(
                    recipients, recipientNames, subject, message,
                    formateur.getNomComplet(), formateur.getEmail(), formateur.getSpecialite()
                );
                redirectAttributes.addFlashAttribute("success", 
                    "Email envoyé avec succès à " + recipients.size() + " étudiant(s)");
            } else if (emailTo != null && !emailTo.isEmpty()) {
                // Envoyer à un email spécifique
                emailService.sendEmailFromFormateur(
                    emailTo, "Cher(e) Étudiant(e)", subject, message,
                    formateur.getNomComplet(), formateur.getEmail(), formateur.getSpecialite()
                );
                redirectAttributes.addFlashAttribute("success", "Email envoyé avec succès à " + emailTo);
            } else {
                redirectAttributes.addFlashAttribute("error", "Veuillez sélectionner au moins un destinataire");
                return "redirect:/formateur/emails/send";
            }
        } catch (Exception e) {
            String errorMessage = "Erreur lors de l'envoi de l'email: " + e.getMessage();
            if (e.getCause() != null) {
                errorMessage += " (" + e.getCause().getMessage() + ")";
            }
            redirectAttributes.addFlashAttribute("error", errorMessage);
        }
        return "redirect:/formateur/emails/send";
    }
}

