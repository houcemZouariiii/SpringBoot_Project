package com.iit.projetjee.dto;

public class CoursStatDTO {
    private Long coursId;
    private String titre;
    private Long inscriptions;

    public CoursStatDTO(Long coursId, String titre, Long inscriptions) {
        this.coursId = coursId;
        this.titre = titre;
        this.inscriptions = inscriptions;
    }

    public Long getCoursId() {
        return coursId;
    }

    public String getTitre() {
        return titre;
    }

    public Long getInscriptions() {
        return inscriptions;
    }
}

