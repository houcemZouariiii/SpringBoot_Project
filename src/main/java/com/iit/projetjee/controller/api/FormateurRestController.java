package com.iit.projetjee.controller.api;

import com.iit.projetjee.entity.Formateur;
import com.iit.projetjee.service.FormateurService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formateurs")
@CrossOrigin(origins = "*")
public class FormateurRestController {

    private final FormateurService formateurService;

    @Autowired
    public FormateurRestController(FormateurService formateurService) {
        this.formateurService = formateurService;
    }

    // GET /api/formateurs - Liste tous les formateurs
    @GetMapping
    public ResponseEntity<List<Formateur>> getAllFormateurs() {
        List<Formateur> formateurs = formateurService.getAllFormateurs();
        return ResponseEntity.ok(formateurs);
    }

    // GET /api/formateurs/{id} - Obtenir un formateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<Formateur> getFormateurById(@PathVariable Long id) {
        Formateur formateur = formateurService.getFormateurById(id);
        return ResponseEntity.ok(formateur);
    }

    // POST /api/formateurs - Créer un nouveau formateur
    @PostMapping
    public ResponseEntity<Formateur> createFormateur(@Valid @RequestBody Formateur formateur) {
        Formateur createdFormateur = formateurService.createFormateur(formateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFormateur);
    }

    // PUT /api/formateurs/{id} - Mettre à jour un formateur
    @PutMapping("/{id}")
    public ResponseEntity<Formateur> updateFormateur(@PathVariable Long id, 
                                                     @Valid @RequestBody Formateur formateur) {
        Formateur updatedFormateur = formateurService.updateFormateur(id, formateur);
        return ResponseEntity.ok(updatedFormateur);
    }

    // DELETE /api/formateurs/{id} - Supprimer un formateur
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormateur(@PathVariable Long id) {
        formateurService.deleteFormateur(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/formateurs/search?q=... - Rechercher des formateurs
    @GetMapping("/search")
    public ResponseEntity<List<Formateur>> searchFormateurs(@RequestParam String q) {
        List<Formateur> formateurs = formateurService.searchFormateurs(q);
        return ResponseEntity.ok(formateurs);
    }
}

