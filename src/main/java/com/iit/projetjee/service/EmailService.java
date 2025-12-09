package com.iit.projetjee.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendInscriptionConfirmation(String to, String etudiantNom, String coursTitre) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Confirmation d'inscription au cours : " + coursTitre);
        message.setText("Bonjour " + etudiantNom + ",\n\n" +
                "Votre inscription au cours \"" + coursTitre + "\" a été enregistrée avec succès.\n\n" +
                "Vous recevrez une confirmation une fois votre inscription validée.\n\n" +
                "Cordialement,\nL'équipe pédagogique");
        mailSender.send(message);
    }

    public void sendInscriptionValidation(String to, String etudiantNom, String coursTitre) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Inscription validée : " + coursTitre);
        message.setText("Bonjour " + etudiantNom + ",\n\n" +
                "Votre inscription au cours \"" + coursTitre + "\" a été validée.\n\n" +
                "Nous vous attendons pour le début du cours.\n\n" +
                "Cordialement,\nL'équipe pédagogique");
        mailSender.send(message);
    }

    public void sendInscriptionNotificationFormateur(String to, String etudiantNom, String coursTitre, boolean desinscription) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject((desinscription ? "Désinscription" : "Nouvelle inscription") + " - " + coursTitre);
        message.setText("Bonjour,\n\n" +
                "L'étudiant " + etudiantNom + " " + (desinscription ? "s'est désinscrit" : "s'est inscrit") +
                " du cours \"" + coursTitre + "\".\n\n" +
                "Cordialement,\nL'équipe pédagogique");
        mailSender.send(message);
    }

    public void sendNoteNotification(String to, String etudiantNom, String coursTitre, Double note) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Nouvelle note pour le cours : " + coursTitre);
        message.setText("Bonjour " + etudiantNom + ",\n\n" +
                "Une nouvelle note a été ajoutée pour le cours \"" + coursTitre + "\".\n\n" +
                "Note : " + note + "/20\n\n" +
                "Cordialement,\nL'équipe pédagogique");
        mailSender.send(message);
    }

    /**
     * Envoyer un email depuis l'administration avec template HTML
     */
    public void sendEmailFromAdmin(String to, String recipientName, String subject, String message) {
        logger.info("Tentative d'envoi d'email admin à: {}", to);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            
            // Utiliser l'email configuré comme expéditeur (Gmail nécessite que from = username)
            String fromEmail = mailUsername != null ? mailUsername : "administration@iit.tn";
            try {
                helper.setFrom(new InternetAddress(fromEmail, "Administration IIT", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.warn("Erreur d'encodage, utilisation de l'email sans nom: {}", e.getMessage());
                helper.setFrom(fromEmail);
            }

            // Préparer le contexte Thymeleaf
            Context context = new Context();
            context.setVariable("recipientName", recipientName);
            context.setVariable("message", message);

            // Générer le contenu HTML depuis le template
            String htmlContent = templateEngine.process("emails/admin-email", context);
            helper.setText(htmlContent, true);

            logger.info("Envoi de l'email à {}...", to);
            mailSender.send(mimeMessage);
            logger.info("Email envoyé avec succès à {}", to);
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email à {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de l'envoi de l'email à {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage(), e);
        }
    }

    /**
     * Envoyer un email depuis un formateur avec template HTML
     */
    public void sendEmailFromFormateur(String to, String recipientName, String subject, String message,
                                      String formateurName, String formateurEmail, String formateurSpecialite) {
        logger.info("Tentative d'envoi d'email formateur à: {} depuis: {}", to, formateurEmail);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            
            // Utiliser l'email configuré comme expéditeur (Gmail nécessite que from = username)
            // Mais on peut utiliser Reply-To pour l'email du formateur
            String fromEmail = mailUsername != null ? mailUsername : "formateur@iit.tn";
            String fromName = formateurName != null && !formateurName.isEmpty() ? formateurName : "Formateur IIT";
            
            try {
                helper.setFrom(new InternetAddress(fromEmail, fromName, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.warn("Erreur d'encodage, utilisation de l'email sans nom: {}", e.getMessage());
                helper.setFrom(fromEmail);
            }
            
            // Ajouter Reply-To avec l'email du formateur si disponible
            if (formateurEmail != null && !formateurEmail.isEmpty()) {
                helper.setReplyTo(formateurEmail);
            }

            // Préparer le contexte Thymeleaf
            Context context = new Context();
            context.setVariable("recipientName", recipientName);
            context.setVariable("message", message);
            context.setVariable("formateurName", formateurName);
            context.setVariable("formateurEmail", formateurEmail);
            context.setVariable("formateurSpecialite", formateurSpecialite);

            // Générer le contenu HTML depuis le template
            String htmlContent = templateEngine.process("emails/formateur-email", context);
            helper.setText(htmlContent, true);

            logger.info("Envoi de l'email à {}...", to);
            mailSender.send(mimeMessage);
            logger.info("Email envoyé avec succès à {}", to);
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email à {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de l'envoi de l'email à {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage(), e);
        }
    }

    /**
     * Envoyer un email à plusieurs destinataires depuis l'administration
     */
    public void sendBulkEmailFromAdmin(List<String> recipients, Map<String, String> recipientNames, 
                                      String subject, String message) {
        for (String recipient : recipients) {
            String name = recipientNames.getOrDefault(recipient, "Cher(e) Étudiant(e)");
            sendEmailFromAdmin(recipient, name, subject, message);
        }
    }

    /**
     * Envoyer un email à plusieurs destinataires depuis un formateur
     */
    public void sendBulkEmailFromFormateur(List<String> recipients, Map<String, String> recipientNames,
                                          String subject, String message, String formateurName,
                                          String formateurEmail, String formateurSpecialite) {
        for (String recipient : recipients) {
            String name = recipientNames.getOrDefault(recipient, "Cher(e) Étudiant(e)");
            sendEmailFromFormateur(recipient, name, subject, message, formateurName, formateurEmail, formateurSpecialite);
        }
    }
}

