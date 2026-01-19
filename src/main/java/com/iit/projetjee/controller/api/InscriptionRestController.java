package com.iit.projetjee.controller.api;

import com.iit.projetjee.entity.Inscription;
import com.iit.projetjee.service.IInscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inscriptions")
@CrossOrigin(origins = "*")
public class InscriptionRestController {

    private final IInscriptionService inscriptionService;

    @Autowired
    public InscriptionRestController(IInscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    // GET /api/inscriptions - Liste toutes les inscriptions
    @GetMapping
    public ResponseEntity<List<Inscription>> getAllInscriptions() {
        List<Inscription> inscriptions = inscriptionService.getAllInscriptions();
        return ResponseEntity.ok(inscriptions);
    }

    // GET /api/inscriptions/{id} - Obtenir une inscription par ID
    @GetMapping("/{id}")
    public ResponseEntity<Inscription> getInscriptionById(@PathVariable Long id) {
        Inscription inscription = inscriptionService.getInscriptionById(id);
        return ResponseEntity.ok(inscription);
    }

    // POST /api/inscriptions - Créer une nouvelle inscription
    @PostMapping
    public ResponseEntity<Inscription> createInscription(@Valid @RequestBody Inscription inscription) {
        Inscription createdInscription = inscriptionService.createInscription(inscription);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInscription);
    }

    // POST /api/inscriptions/s-inscrire - S'inscrire à un cours
    @PostMapping("/s-inscrire")
    public ResponseEntity<Inscription> sInscrireACours(@RequestBody Map<String, Long> request) {
        Long etudiantId = request.get("etudiantId");
        Long coursId = request.get("coursId");
        Inscription inscription = inscriptionService.sInscrireACours(etudiantId, coursId);
        return ResponseEntity.status(HttpStatus.CREATED).body(inscription);
    }

    // PUT /api/inscriptions/{id} - Mettre à jour une inscription
    @PutMapping("/{id}")
    public ResponseEntity<Inscription> updateInscription(@PathVariable Long id, 
                                                         @Valid @RequestBody Inscription inscription) {
        Inscription updatedInscription = inscriptionService.updateInscription(id, inscription);
        return ResponseEntity.ok(updatedInscription);
    }

    // DELETE /api/inscriptions/{id} - Supprimer une inscription
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInscription(@PathVariable Long id) {
        inscriptionService.deleteInscription(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/inscriptions/etudiant/{etudiantId} - Obtenir les inscriptions d'un étudiant
    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<Inscription>> getInscriptionsByEtudiant(@PathVariable Long etudiantId) {
        List<Inscription> inscriptions = inscriptionService.getInscriptionsByEtudiant(etudiantId);
        return ResponseEntity.ok(inscriptions);
    }

    // GET /api/inscriptions/cours/{coursId} - Obtenir les inscriptions d'un cours
    @GetMapping("/cours/{coursId}")
    public ResponseEntity<List<Inscription>> getInscriptionsByCours(@PathVariable Long coursId) {
        List<Inscription> inscriptions = inscriptionService.getInscriptionsByCours(coursId);
        return ResponseEntity.ok(inscriptions);
    }

    // POST /api/inscriptions/{id}/valider - Valider une inscription
    @PostMapping("/{id}/valider")
    public ResponseEntity<Inscription> validerInscription(@PathVariable Long id) {
        Inscription inscription = inscriptionService.validerInscription(id);
        return ResponseEntity.ok(inscription);
    }

    // POST /api/inscriptions/{id}/refuser - Refuser une inscription
    @PostMapping("/{id}/refuser")
    public ResponseEntity<Inscription> refuserInscription(@PathVariable Long id) {
        Inscription inscription = inscriptionService.refuserInscription(id);
        return ResponseEntity.ok(inscription);
    }
}

