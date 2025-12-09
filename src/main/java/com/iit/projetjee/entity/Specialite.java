package com.iit.projetjee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "specialites")
public class Specialite extends BaseEntity {

    @NotBlank(message = "Le libellé de la spécialité est obligatoire")
    @Size(min = 2, max = 100, message = "Le libellé doit contenir entre 2 et 100 caractères")
    @Column(name = "libelle", nullable = false, unique = true, length = 100)
    private String libelle;

    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
    @Column(name = "description", length = 255)
    private String description;

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

