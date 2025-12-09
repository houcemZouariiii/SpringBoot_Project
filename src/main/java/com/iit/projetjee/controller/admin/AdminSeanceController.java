package com.iit.projetjee.controller.admin;

import com.iit.projetjee.entity.Seance;
import com.iit.projetjee.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/seances")
public class AdminSeanceController {

    private final SeanceService seanceService;

    @Autowired
    public AdminSeanceController(SeanceService seanceService) {
        this.seanceService = seanceService;
    }

    @GetMapping
    public List<Seance> list() {
        return seanceService.findAll();
    }

    @PostMapping
    public ResponseEntity<Seance> create(@RequestBody Seance seance) {
        return ResponseEntity.ok(seanceService.create(seance));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Seance> update(@PathVariable Long id, @RequestBody Seance seance) {
        return ResponseEntity.ok(seanceService.update(id, seance));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        seanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

