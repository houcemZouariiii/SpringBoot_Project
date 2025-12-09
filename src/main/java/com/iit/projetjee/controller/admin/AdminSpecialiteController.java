package com.iit.projetjee.controller.admin;

import com.iit.projetjee.entity.Specialite;
import com.iit.projetjee.service.SpecialiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/specialites")
public class AdminSpecialiteController {

    private final SpecialiteService specialiteService;

    @Autowired
    public AdminSpecialiteController(SpecialiteService specialiteService) {
        this.specialiteService = specialiteService;
    }

    @GetMapping
    public List<Specialite> list() {
        return specialiteService.findAll();
    }

    @PostMapping
    public ResponseEntity<Specialite> create(@RequestBody Specialite specialite) {
        return ResponseEntity.ok(specialiteService.create(specialite));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Specialite> update(@PathVariable Long id, @RequestBody Specialite specialite) {
        return ResponseEntity.ok(specialiteService.update(id, specialite));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        specialiteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

