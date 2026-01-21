package com.iit.projetjee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EtudiantDTO {
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("nom")
    private String nom;
    
    @JsonProperty("prenom")
    private String prenom;
    
    @JsonProperty("nomComplet")
    private String nomComplet;

    public EtudiantDTO() {
    }

    public EtudiantDTO(Long id, String nom, String prenom) {
        this.id = id;
        this.nom = nom != null ? nom : "";
        this.prenom = prenom != null ? prenom : "";
        this.nomComplet = (this.prenom + " " + this.nom).trim();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }
}

