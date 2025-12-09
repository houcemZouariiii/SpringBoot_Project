package com.iit.projetjee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "sessions_academiques")
public class SessionAcademique extends BaseEntity {

    public enum Semestre {
        S1, S2
    }

    @NotBlank(message = "Le libellé de la session est obligatoire")
    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @NotNull(message = "L'année académique est obligatoire")
    @Min(value = 2000, message = "Année invalide")
    @Max(value = 2100, message = "Année invalide")
    @Column(name = "annee", nullable = false)
    private Integer annee;

    @NotNull(message = "Le semestre est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "semestre", nullable = false, length = 10)
    private Semestre semestre;

    @Column(name = "actif")
    private boolean actif = true;

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public void setSemestre(Semestre semestre) {
        this.semestre = semestre;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
}

