package com.iit.projetjee.controller.api;

import com.iit.projetjee.entity.Cours;
import com.iit.projetjee.service.ICoursService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cours")
@CrossOrigin(origins = "*")
public class CoursRestController {

    private final ICoursService coursService;

    @Autowired
    public CoursRestController(ICoursService coursService) {
        this.coursService = coursService;
    }

    // GET /api/cours - Liste tous les cours
    @GetMapping
    public ResponseEntity<List<Cours>> getAllCours() {
        List<Cours> cours = coursService.getAllCours();
        return ResponseEntity.ok(cours);
    }

    // GET /api/cours/{id} - Obtenir un cours par ID
    @GetMapping("/{id}")
    public ResponseEntity<Cours> getCoursById(@PathVariable Long id) {
        Cours cours = coursService.getCoursById(id);
        return ResponseEntity.ok(cours);
    }

    // POST /api/cours - Créer un nouveau cours
    @PostMapping
    public ResponseEntity<Cours> createCours(@Valid @RequestBody Cours cours) {
        Cours createdCours = coursService.createCours(cours);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCours);
    }

    // PUT /api/cours/{id} - Mettre à jour un cours
    @PutMapping("/{id}")
    public ResponseEntity<Cours> updateCours(@PathVariable Long id, 
                                             @Valid @RequestBody Cours cours) {
        Cours updatedCours = coursService.updateCours(id, cours);
        return ResponseEntity.ok(updatedCours);
    }

    // DELETE /api/cours/{id} - Supprimer un cours
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCours(@PathVariable Long id) {
        coursService.deleteCours(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/cours/{id}/assigner-formateur - Assigner un formateur à un cours
    @PostMapping("/{id}/assigner-formateur")
    public ResponseEntity<Cours> assignerFormateur(@PathVariable Long id, 
                                                   @RequestBody Map<String, Long> request) {
        Long formateurId = request.get("formateurId");
        Cours cours = coursService.assignerFormateur(id, formateurId);
        return ResponseEntity.ok(cours);
    }

    // GET /api/cours/search?q=... - Rechercher des cours
    @GetMapping("/search")
    public ResponseEntity<List<Cours>> searchCours(@RequestParam String q) {
        List<Cours> cours = coursService.searchCours(q);
        return ResponseEntity.ok(cours);
    }

    // GET /api/cours/formateur/{formateurId} - Obtenir les cours d'un formateur
    @GetMapping("/formateur/{formateurId}")
    public ResponseEntity<List<Cours>> getCoursByFormateur(@PathVariable Long formateurId) {
        List<Cours> cours = coursService.getCoursByFormateur(formateurId);
        return ResponseEntity.ok(cours);
    }
}

