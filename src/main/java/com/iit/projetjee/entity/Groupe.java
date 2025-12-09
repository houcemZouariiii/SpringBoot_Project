package com.iit.projetjee.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groupes")
public class Groupe extends BaseEntity {

    public enum TypeGroupe {
        TP, TD, CM
    }

    @NotBlank(message = "Le code du groupe est obligatoire")
    @Size(min = 2, max = 30, message = "Le code doit contenir entre 2 et 30 caractères")
    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code;

    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(name = "nom", length = 100)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_groupe", length = 10)
    private TypeGroupe typeGroupe = TypeGroupe.TP;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private SessionAcademique session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialite_id")
    private Specialite specialite;

    @ManyToMany
    @JoinTable(
            name = "groupe_etudiants",
            joinColumns = @JoinColumn(name = "groupe_id"),
            inverseJoinColumns = @JoinColumn(name = "etudiant_id")
    )
    private List<Etudiant> etudiants = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public TypeGroupe getTypeGroupe() {
        return typeGroupe;
    }

    public void setTypeGroupe(TypeGroupe typeGroupe) {
        this.typeGroupe = typeGroupe;
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

    public List<Etudiant> getEtudiants() {
        return etudiants;
    }

    public void setEtudiants(List<Etudiant> etudiants) {
        this.etudiants = etudiants;
    }
}

