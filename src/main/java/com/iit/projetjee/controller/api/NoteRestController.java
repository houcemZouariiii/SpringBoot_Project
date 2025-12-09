package com.iit.projetjee.controller.api;

import com.iit.projetjee.entity.Note;
import com.iit.projetjee.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteRestController {

    private final NoteService noteService;

    @Autowired
    public NoteRestController(NoteService noteService) {
        this.noteService = noteService;
    }

    // GET /api/notes - Liste toutes les notes
    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        List<Note> notes = noteService.getAllNotes();
        return ResponseEntity.ok(notes);
    }

    // GET /api/notes/{id} - Obtenir une note par ID
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        Note note = noteService.getNoteById(id);
        return ResponseEntity.ok(note);
    }

    // POST /api/notes - Créer une nouvelle note
    @PostMapping
    public ResponseEntity<Note> createNote(@Valid @RequestBody Note note) {
        Note createdNote = noteService.createNote(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
    }

    // POST /api/notes/ajouter - Ajouter une note
    @PostMapping("/ajouter")
    public ResponseEntity<Note> ajouterNote(@RequestBody Map<String, Object> request) {
        Long etudiantId = Long.valueOf(request.get("etudiantId").toString());
        Long coursId = Long.valueOf(request.get("coursId").toString());
        Double valeur = Double.valueOf(request.get("valeur").toString());
        String typeEvaluation = (String) request.get("typeEvaluation");
        
        Note note = noteService.ajouterNote(etudiantId, coursId, valeur, typeEvaluation);
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    // PUT /api/notes/{id} - Mettre à jour une note
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, 
                                          @Valid @RequestBody Note note) {
        Note updatedNote = noteService.updateNote(id, note);
        return ResponseEntity.ok(updatedNote);
    }

    // DELETE /api/notes/{id} - Supprimer une note
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/notes/etudiant/{etudiantId} - Obtenir les notes d'un étudiant
    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<Note>> getNotesByEtudiant(@PathVariable Long etudiantId) {
        List<Note> notes = noteService.getNotesByEtudiant(etudiantId);
        return ResponseEntity.ok(notes);
    }

    // GET /api/notes/cours/{coursId} - Obtenir les notes d'un cours
    @GetMapping("/cours/{coursId}")
    public ResponseEntity<List<Note>> getNotesByCours(@PathVariable Long coursId) {
        List<Note> notes = noteService.getNotesByCours(coursId);
        return ResponseEntity.ok(notes);
    }

    // GET /api/notes/etudiant/{etudiantId}/cours/{coursId} - Obtenir les notes d'un étudiant pour un cours
    @GetMapping("/etudiant/{etudiantId}/cours/{coursId}")
    public ResponseEntity<List<Note>> getNotesByEtudiantAndCours(@PathVariable Long etudiantId, 
                                                                  @PathVariable Long coursId) {
        List<Note> notes = noteService.getNotesByEtudiantAndCours(etudiantId, coursId);
        return ResponseEntity.ok(notes);
    }

    // GET /api/notes/etudiant/{etudiantId}/cours/{coursId}/moyenne - Calculer la moyenne
    @GetMapping("/etudiant/{etudiantId}/cours/{coursId}/moyenne")
    public ResponseEntity<Map<String, Double>> calculerMoyenne(@PathVariable Long etudiantId, 
                                                               @PathVariable Long coursId) {
        Double moyenne = noteService.calculerMoyenneEtudiantCours(etudiantId, coursId);
        return ResponseEntity.ok(Map.of("moyenne", moyenne));
    }

    // GET /api/notes/cours/{coursId}/moyenne - Calculer la moyenne d'un cours
    @GetMapping("/cours/{coursId}/moyenne")
    public ResponseEntity<Map<String, Double>> calculerMoyenneCours(@PathVariable Long coursId) {
        Double moyenne = noteService.calculerMoyenneCours(coursId);
        return ResponseEntity.ok(Map.of("moyenne", moyenne));
    }
}

