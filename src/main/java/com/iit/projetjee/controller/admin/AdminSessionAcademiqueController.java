package com.iit.projetjee.controller.admin;

import com.iit.projetjee.entity.SessionAcademique;
import com.iit.projetjee.service.SessionAcademiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/sessions")
public class AdminSessionAcademiqueController {

    private final SessionAcademiqueService sessionAcademiqueService;

    @Autowired
    public AdminSessionAcademiqueController(SessionAcademiqueService sessionAcademiqueService) {
        this.sessionAcademiqueService = sessionAcademiqueService;
    }

    @GetMapping
    public List<SessionAcademique> list() {
        return sessionAcademiqueService.findAll();
    }

    @PostMapping
    public ResponseEntity<SessionAcademique> create(@RequestBody SessionAcademique session) {
        return ResponseEntity.ok(sessionAcademiqueService.create(session));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionAcademique> update(@PathVariable Long id, @RequestBody SessionAcademique session) {
        return ResponseEntity.ok(sessionAcademiqueService.update(id, session));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sessionAcademiqueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

