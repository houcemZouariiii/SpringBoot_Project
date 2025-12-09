package com.iit.projetjee.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cours")
public class Cours extends BaseEntity {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 100, message = "Le titre doit contenir entre 3 et 100 caractères")
    @Column(name = "titre", nullable = false, length = 100)
    private String titre;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "Le nombre d'heures est obligatoire")
    @Min(value = 1, message = "Le nombre d'heures doit être au moins 1")
    @Max(value = 500, message = "Le nombre d'heures ne peut pas dépasser 500")
    @Column(name = "nombre_heures", nullable = false)
    private Integer nombreHeures;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "prix")
    private Double prix;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau", length = 20)
    private NiveauCours niveau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private SessionAcademique session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialite_id")
    private Specialite specialite;

    @ManyToMany
    @JoinTable(
            name = "cours_groupes",
            joinColumns = @JoinColumn(name = "cours_id"),
            inverseJoinColumns = @JoinColumn(name = "groupe_id")
    )
    private List<Groupe> groupes = new ArrayList<>();

    // Relation Many-to-One avec Formateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formateur_id", nullable = false)
    @NotNull(message = "Le formateur est obligatoire")
    private Formateur formateur;

    // Relation One-to-Many avec Inscription
    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inscription> inscriptions = new ArrayList<>();

    // Relation One-to-Many avec Note
    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes = new ArrayList<>();

    // Enum pour le niveau du cours
    public enum NiveauCours {
        DEBUTANT,
        INTERMEDIAIRE,
        AVANCE,
        EXPERT
    }

    // Constructeurs
    public Cours() {
    }

    public Cours(String titre, Integer nombreHeures, Formateur formateur) {
        this.titre = titre;
        this.nombreHeures = nombreHeures;
        this.formateur = formateur;
    }

    // Getters and Setters
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNombreHeures() {
        return nombreHeures;
    }

    public void setNombreHeures(Integer nombreHeures) {
        this.nombreHeures = nombreHeures;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public NiveauCours getNiveau() {
        return niveau;
    }

    public void setNiveau(NiveauCours niveau) {
        this.niveau = niveau;
    }

    public SessionAcademique getSession() {
        return session;
    }

    public void setSession(SessionAcademique session) {
        this.session = session;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    public List<Groupe> getGroupes() {
        return groupes;
    }

    public void setGroupes(List<Groupe> groupes) {
        this.groupes = groupes;
    }

    public Formateur getFormateur() {
        return formateur;
    }

    public void setFormateur(Formateur formateur) {
        this.formateur = formateur;
    }

    public List<Inscription> getInscriptions() {
        return inscriptions;
    }

    public void setInscriptions(List<Inscription> inscriptions) {
        this.inscriptions = inscriptions;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Cours{" +
                "id=" + getId() +
                ", titre='" + titre + '\'' +
                ", nombreHeures=" + nombreHeures +
                ", formateur=" + (formateur != null ? formateur.getNomComplet() : "null") +
                '}';
    }
}

