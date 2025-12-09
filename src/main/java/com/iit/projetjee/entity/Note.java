package com.iit.projetjee.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.time.LocalDate;

@Entity
@Table(name = "notes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"etudiant_id", "cours_id", "type_evaluation"}))
public class Note extends BaseEntity {

    @NotNull(message = "La note est obligatoire")
    @Min(value = 0, message = "La note ne peut pas être inférieure à 0")
    @Max(value = 20, message = "La note ne peut pas être supérieure à 20")
    @Column(name = "valeur", nullable = false)
    private Double valeur;

    @Column(name = "date_evaluation")
    private LocalDate dateEvaluation;

    @Column(name = "type_evaluation", length = 50)
    private String typeEvaluation; // Ex: "Examen", "Contrôle", "TP", "Projet"

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

    // Constructeurs
    public Note() {
        this.dateEvaluation = LocalDate.now();
    }

    public Note(Double valeur, Etudiant etudiant, Cours cours) {
        this.valeur = valeur;
        this.etudiant = etudiant;
        this.cours = cours;
        this.dateEvaluation = LocalDate.now();
    }

    // Getters and Setters
    public Double getValeur() {
        return valeur;
    }

    public void setValeur(Double valeur) {
        this.valeur = valeur;
    }

    public LocalDate getDateEvaluation() {
        return dateEvaluation;
    }

    public void setDateEvaluation(LocalDate dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }

    public String getTypeEvaluation() {
        return typeEvaluation;
    }

    public void setTypeEvaluation(String typeEvaluation) {
        this.typeEvaluation = typeEvaluation;
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

    // Méthodes utilitaires
    public String getAppreciation() {
        if (valeur == null) return "Non noté";
        if (valeur >= 16) return "Très bien";
        if (valeur >= 14) return "Bien";
        if (valeur >= 12) return "Assez bien";
        if (valeur >= 10) return "Passable";
        return "Insuffisant";
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + getId() +
                ", valeur=" + valeur +
                ", typeEvaluation='" + typeEvaluation + '\'' +
                ", etudiant=" + (etudiant != null ? etudiant.getNomComplet() : "null") +
                ", cours=" + (cours != null ? cours.getTitre() : "null") +
                '}';
    }
}

