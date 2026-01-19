package com.iit.projetjee.controller.api;

import com.iit.projetjee.entity.Etudiant;
import com.iit.projetjee.service.IEtudiantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
@CrossOrigin(origins = "*")
public class EtudiantRestController {

    private final IEtudiantService etudiantService;

    @Autowired
    public EtudiantRestController(IEtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    // GET /api/etudiants - Liste tous les étudiants
    @GetMapping
    public ResponseEntity<List<Etudiant>> getAllEtudiants() {
        List<Etudiant> etudiants = etudiantService.getAllEtudiants();
        return ResponseEntity.ok(etudiants);
    }

    // GET /api/etudiants/{id} - Obtenir un étudiant par ID
    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getEtudiantById(@PathVariable Long id) {
        Etudiant etudiant = etudiantService.getEtudiantById(id);
        return ResponseEntity.ok(etudiant);
    }

    // POST /api/etudiants - Créer un nouvel étudiant
    @PostMapping
    public ResponseEntity<Etudiant> createEtudiant(@Valid @RequestBody Etudiant etudiant) {
        Etudiant createdEtudiant = etudiantService.createEtudiant(etudiant);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEtudiant);
    }

    // PUT /api/etudiants/{id} - Mettre à jour un étudiant
    @PutMapping("/{id}")
    public ResponseEntity<Etudiant> updateEtudiant(@PathVariable Long id, 
                                                    @Valid @RequestBody Etudiant etudiant) {
        Etudiant updatedEtudiant = etudiantService.updateEtudiant(id, etudiant);
        return ResponseEntity.ok(updatedEtudiant);
    }

    // DELETE /api/etudiants/{id} - Supprimer un étudiant
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable Long id) {
        etudiantService.deleteEtudiant(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/etudiants/search?q=... - Rechercher des étudiants
    @GetMapping("/search")
    public ResponseEntity<List<Etudiant>> searchEtudiants(@RequestParam String q) {
        List<Etudiant> etudiants = etudiantService.searchEtudiants(q);
        return ResponseEntity.ok(etudiants);
    }
}

