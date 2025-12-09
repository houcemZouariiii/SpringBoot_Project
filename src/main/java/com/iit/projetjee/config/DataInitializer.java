package com.iit.projetjee.config;

import com.iit.projetjee.entity.*;
import com.iit.projetjee.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final EtudiantService etudiantService;
    private final FormateurService formateurService;
    private final CoursService coursService;
    private final InscriptionService inscriptionService;
    private final NoteService noteService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(EtudiantService etudiantService,
                          FormateurService formateurService,
                          CoursService coursService,
                          InscriptionService inscriptionService,
                          NoteService noteService,
                          org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.etudiantService = etudiantService;
        this.formateurService = formateurService;
        this.coursService = coursService;
        this.inscriptionService = inscriptionService;
        this.noteService = noteService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Vérifier si des données existent déjà
        if (!etudiantService.getAllEtudiants().isEmpty()) {
            logger.info("La base de données contient déjà des données. Initialisation ignorée.");
            return;
        }

        logger.info("Début de l'initialisation de la base de données...");

        // 1. Créer des Formateurs avec credentials
        logger.info("Création des formateurs avec credentials...");
        Formateur formateur1 = createFormateur("Dupont", "Jean", "jean.dupont@iit.tn", 
                "+33 6 12 34 56 78", "Java & Spring Boot", 10, "jean.dupont", "formateur123");
        Formateur formateur2 = createFormateur("Martin", "Sophie", "sophie.martin@iit.tn", 
                "+33 6 23 45 67 89", "Web Development", 8, "sophie.martin", "formateur123");
        Formateur formateur3 = createFormateur("Bernard", "Pierre", "pierre.bernard@iit.tn", 
                "+33 6 34 56 78 90", "Database & SQL", 12, "pierre.bernard", "formateur123");
        Formateur formateur4 = createFormateur("Dubois", "Marie", "marie.dubois@iit.tn", 
                "+33 6 45 67 89 01", "DevOps & Cloud", 7, "marie.dubois", "formateur123");

        List<Formateur> formateurs = Arrays.asList(formateur1, formateur2, formateur3, formateur4);

        // 2. Créer des Étudiants avec sections et credentials
        logger.info("Création des étudiants avec sections...");
        Etudiant etudiant1 = createEtudiant("Zouari", "Houcem", "houcem.zouari@student.iit.tn", 
                "+33 6 11 22 33 44", "123 Rue de la République, Tunis",
                Etudiant.Section.GENIE_INFO, "houcem.zouari", "etudiant123");
        Etudiant etudiant2 = createEtudiant("Ben Ali", "Amira", "amira.benali@student.iit.tn", 
                "+33 6 22 33 44 55", "456 Avenue Habib Bourguiba, Tunis",
                Etudiant.Section.GENIE_INFO, "amira.benali", "etudiant123");
        Etudiant etudiant3 = createEtudiant("Trabelsi", "Mehdi", "mehdi.trabelsi@student.iit.tn", 
                "+33 6 33 44 55 66", "789 Boulevard Mohamed V, Sfax",
                Etudiant.Section.GENIE_INDUSTRIEL, "mehdi.trabelsi", "etudiant123");
        Etudiant etudiant4 = createEtudiant("Khelifi", "Sara", "sara.khelifi@student.iit.tn", 
                "+33 6 44 55 66 77", "321 Rue de la Liberté, Sousse",
                Etudiant.Section.GENIE_MECANIQUE, "sara.khelifi", "etudiant123");
        Etudiant etudiant5 = createEtudiant("Hamdi", "Youssef", "youssef.hamdi@student.iit.tn", 
                "+33 6 55 66 77 88", "654 Place de l'Indépendance, Bizerte",
                Etudiant.Section.GENIE_ELECTRIQUE, "youssef.hamdi", "etudiant123");
        Etudiant etudiant6 = createEtudiant("Jemai", "Leila", "leila.jemai@student.iit.tn", 
                "+33 6 66 77 88 99", "987 Rue Ibn Khaldoun, Gabès",
                Etudiant.Section.GENIE_CIVIL, "leila.jemai", "etudiant123");

        List<Etudiant> etudiants = Arrays.asList(etudiant1, etudiant2, etudiant3, etudiant4, etudiant5, etudiant6);

        // 3. Créer des Cours (avec dates non chevauchantes pour éviter les conflits)
        logger.info("Création des cours...");
        Cours cours1 = createCours("Spring Boot Avancé", 
                "Maîtrisez Spring Boot avec des concepts avancés : sécurité, JPA, REST API, microservices",
                40, formateur1, Cours.NiveauCours.AVANCE, 450.0,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(50));
        
        Cours cours2 = createCours("Développement Web Full Stack", 
                "Apprenez à développer des applications web complètes avec HTML, CSS, JavaScript et frameworks modernes",
                60, formateur2, Cours.NiveauCours.INTERMEDIAIRE, 600.0,
                LocalDate.now().plusDays(60), LocalDate.now().plusDays(120));
        
        Cours cours3 = createCours("Base de données et SQL", 
                "Conception et gestion de bases de données relationnelles avec MySQL et PostgreSQL",
                35, formateur3, Cours.NiveauCours.DEBUTANT, 350.0,
                LocalDate.now().plusDays(130), LocalDate.now().plusDays(165));
        
        Cours cours4 = createCours("Java pour Débutants", 
                "Introduction complète au langage Java : syntaxe, POO, collections, exceptions",
                50, formateur1, Cours.NiveauCours.DEBUTANT, 400.0,
                LocalDate.now().plusDays(170), LocalDate.now().plusDays(220));
        
        Cours cours5 = createCours("DevOps et Docker", 
                "Découvrez Docker, Kubernetes et les pratiques DevOps pour déployer vos applications",
                30, formateur4, Cours.NiveauCours.INTERMEDIAIRE, 500.0,
                LocalDate.now().plusDays(230), LocalDate.now().plusDays(260));
        
        Cours cours6 = createCours("Architecture Microservices", 
                "Concevez et développez des architectures microservices avec Spring Cloud",
                45, formateur1, Cours.NiveauCours.EXPERT, 750.0,
                LocalDate.now().plusDays(270), LocalDate.now().plusDays(315));

        List<Cours> cours = Arrays.asList(cours1, cours2, cours3, cours4, cours5, cours6);

        // 4. Créer des Inscriptions (en évitant les conflits d'horaires pour les inscriptions validées)
        logger.info("Création des inscriptions...");
        // Étudiant 1 s'inscrit à plusieurs cours (dates non chevauchantes)
        createInscription(etudiant1, cours1, Inscription.StatutInscription.VALIDEE);
        createInscription(etudiant1, cours2, Inscription.StatutInscription.VALIDEE);
        createInscription(etudiant1, cours4, Inscription.StatutInscription.EN_ATTENTE);
        
        // Étudiant 2 (dates non chevauchantes)
        createInscription(etudiant2, cours2, Inscription.StatutInscription.VALIDEE);
        createInscription(etudiant2, cours3, Inscription.StatutInscription.VALIDEE);
        createInscription(etudiant2, cours5, Inscription.StatutInscription.EN_ATTENTE);
        
        // Étudiant 3
        createInscription(etudiant3, cours1, Inscription.StatutInscription.VALIDEE);
        createInscription(etudiant3, cours6, Inscription.StatutInscription.EN_ATTENTE);
        
        // Étudiant 4 (dates non chevauchantes)
        createInscription(etudiant4, cours3, Inscription.StatutInscription.VALIDEE);
        createInscription(etudiant4, cours4, Inscription.StatutInscription.VALIDEE);
        createInscription(etudiant4, cours2, Inscription.StatutInscription.EN_ATTENTE);
        
        // Étudiant 5 (dates non chevauchantes)
        createInscription(etudiant5, cours5, Inscription.StatutInscription.VALIDEE);
        createInscription(etudiant5, cours6, Inscription.StatutInscription.EN_ATTENTE);
        
        // Étudiant 6 (dates non chevauchantes)
        createInscription(etudiant6, cours4, Inscription.StatutInscription.VALIDEE);
        createInscription(etudiant6, cours3, Inscription.StatutInscription.VALIDEE);

        // 5. Créer des Notes
        logger.info("Création des notes...");
        // Notes pour cours1 (Spring Boot Avancé)
        createNote(etudiant1, cours1, 16.5, "Examen Final", "Excellent travail, très bonne compréhension des concepts");
        createNote(etudiant1, cours1, 15.0, "Projet", "Projet bien réalisé");
        createNote(etudiant3, cours1, 14.0, "Examen Final", "Bon niveau");
        createNote(etudiant3, cours1, 13.5, "Projet", "Projet correct");
        
        // Notes pour cours2 (Développement Web)
        createNote(etudiant1, cours2, 17.0, "Examen Final", "Très bon travail");
        createNote(etudiant1, cours2, 16.0, "TP", "TP bien fait");
        createNote(etudiant2, cours2, 15.5, "Examen Final", "Bon niveau");
        createNote(etudiant4, cours2, 12.0, "Examen Final", "Passable");
        
        // Notes pour cours3 (Base de données)
        createNote(etudiant2, cours3, 18.0, "Examen Final", "Excellent");
        createNote(etudiant2, cours3, 17.5, "TP", "Très bon TP");
        createNote(etudiant4, cours3, 16.0, "Examen Final", "Très bien");
        createNote(etudiant6, cours3, 15.0, "Examen Final", "Bien");
        
        // Notes pour cours4 (Java pour Débutants)
        createNote(etudiant1, cours4, 19.0, "Examen Final", "Excellent travail");
        createNote(etudiant4, cours4, 16.5, "Examen Final", "Très bien");
        createNote(etudiant6, cours4, 14.5, "Examen Final", "Bien");
        
        // Notes pour cours5 (DevOps)
        createNote(etudiant2, cours5, 15.0, "Projet", "Projet bien réalisé");
        createNote(etudiant5, cours5, 13.0, "Examen Final", "Passable");

        logger.info("Initialisation de la base de données terminée avec succès!");
        logger.info("Données créées: {} formateurs, {} étudiants, {} cours, inscriptions et notes", 
                formateurs.size(), etudiants.size(), cours.size());
    }

    private Formateur createFormateur(String nom, String prenom, String email, 
                                      String telephone, String specialite, Integer anneesExperience,
                                      String username, String password) {
        Formateur formateur = new Formateur();
        formateur.setNom(nom);
        formateur.setPrenom(prenom);
        formateur.setEmail(email);
        formateur.setTelephone(telephone);
        formateur.setSpecialite(specialite);
        formateur.setAnneesExperience(anneesExperience);
        formateur.setUsername(username);
        formateur.setPassword(password); // Sera encodé par le service
        return formateurService.createFormateur(formateur);
    }

    private Etudiant createEtudiant(String nom, String prenom, String email, 
                                    String telephone, String adresse,
                                    Etudiant.Section section, String username, String password) {
        Etudiant etudiant = new Etudiant();
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        etudiant.setEmail(email);
        etudiant.setTelephone(telephone);
        etudiant.setAdresse(adresse);
        etudiant.setSection(section);
        etudiant.setUsername(username);
        etudiant.setPassword(password); // Sera encodé par le service
        return etudiantService.createEtudiant(etudiant);
    }

    private Cours createCours(String titre, String description, Integer nombreHeures, 
                             Formateur formateur, Cours.NiveauCours niveau, Double prix,
                             LocalDate dateDebut, LocalDate dateFin) {
        Cours cours = new Cours();
        cours.setTitre(titre);
        cours.setDescription(description);
        cours.setNombreHeures(nombreHeures);
        cours.setFormateur(formateur);
        cours.setNiveau(niveau);
        cours.setPrix(prix);
        cours.setDateDebut(dateDebut);
        cours.setDateFin(dateFin);
        return coursService.createCours(cours);
    }

    private Inscription createInscription(Etudiant etudiant, Cours cours, 
                                          Inscription.StatutInscription statut) {
        Inscription inscription = new Inscription();
        inscription.setEtudiant(etudiant);
        inscription.setCours(cours);
        inscription.setStatut(statut);
        inscription.setDateInscription(LocalDate.now().minusDays((int)(Math.random() * 30)));
        if (statut == Inscription.StatutInscription.VALIDEE) {
            inscription.setCommentaire("Inscription validée par l'administrateur");
        } else if (statut == Inscription.StatutInscription.EN_ATTENTE) {
            inscription.setCommentaire("En attente de validation");
        }
        return inscriptionService.createInscription(inscription);
    }

    private Note createNote(Etudiant etudiant, Cours cours, Double valeur, 
                            String typeEvaluation, String commentaire) {
        Note note = new Note();
        note.setEtudiant(etudiant);
        note.setCours(cours);
        note.setValeur(valeur);
        note.setTypeEvaluation(typeEvaluation);
        note.setCommentaire(commentaire);
        note.setDateEvaluation(LocalDate.now().minusDays((int)(Math.random() * 60)));
        return noteService.createNote(note);
    }
}

