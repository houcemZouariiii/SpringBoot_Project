package com.iit.projetjee.service;

import com.iit.projetjee.entity.Note;
import com.iit.projetjee.dto.CoursStatDTO;

import java.util.List;

public interface INoteService {
    Note createNote(Note note);
    Note ajouterNote(Long etudiantId, Long coursId, Double valeur, String typeEvaluation);
    List<Note> getAllNotes();
    Note getNoteById(Long id);
    Note updateNote(Long id, Note noteDetails);
    void deleteNote(Long id);
    List<Note> getNotesByEtudiant(Long etudiantId);
    List<Note> getNotesByCours(Long coursId);
    List<Note> getNotesByEtudiantAndCours(Long etudiantId, Long coursId);
    Double calculerMoyenneEtudiantCours(Long etudiantId, Long coursId);
    Double calculerMoyenneCours(Long coursId);
    double calculMoyenneGeneraleEtudiant(Long etudiantId);
    double calculTauxReussiteCours(Long coursId);
    List<CoursStatDTO> topCoursParInscriptions(int limit);
}
