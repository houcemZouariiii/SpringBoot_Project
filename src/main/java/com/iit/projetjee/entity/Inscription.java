package com.iit.projetjee.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "inscriptions", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"etudiant_id", "cours_id"}))
public class Inscription extends BaseEntity {

    @NotNull(message = "La date d'inscription est obligatoire")
    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutInscription statut = StatutInscription.EN_ATTENTE;

    @Column(name = "commentaire", length = 500)
    private String commentaire;

    // Relation Many-to-One avec Étudiant
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    @NotNull(message = "L'étudiant est obligatoire")
    private Etudiant etudiant;

    // Relation Many-to-One avec Cours
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_id", nullable = false)
    @NotNull(message = "Le cours est obligatoire")
    private Cours cours;

    // Enum pour le statut de l'inscription
    public enum StatutInscription {
        EN_ATTENTE,
        VALIDEE,
        REFUSEE,
        ANNULEE
    }

    // Constructeurs
    public Inscription() {
        this.dateInscription = LocalDate.now();
    }

    public Inscription(Etudiant etudiant, Cours cours) {
        this.etudiant = etudiant;
        this.cours = cours;
        this.dateInscription = LocalDate.now();
        this.statut = StatutInscription.EN_ATTENTE;
    }

    // Getters and Setters
    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public StatutInscription getStatut() {
        return statut;
    }

    public void setStatut(StatutInscription statut) {
        this.statut = statut;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    @Override
    public String toString() {
        return "Inscription{" +
                "id=" + getId() +
                ", dateInscription=" + dateInscription +
                ", statut=" + statut +
                ", etudiant=" + (etudiant != null ? etudiant.getNomComplet() : "null") +
                ", cours=" + (cours != null ? cours.getTitre() : "null") +
                '}';
    }
}

