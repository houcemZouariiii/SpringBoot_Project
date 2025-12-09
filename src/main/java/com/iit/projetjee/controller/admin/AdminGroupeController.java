package com.iit.projetjee.controller.admin;

import com.iit.projetjee.entity.Groupe;
import com.iit.projetjee.service.GroupeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/groupes")
public class AdminGroupeController {

    private final GroupeService groupeService;

    @Autowired
    public AdminGroupeController(GroupeService groupeService) {
        this.groupeService = groupeService;
    }

    @GetMapping
    public List<Groupe> list() {
        return groupeService.findAll();
    }

    @PostMapping
    public ResponseEntity<Groupe> create(@RequestBody Groupe groupe) {
        return ResponseEntity.ok(groupeService.create(groupe));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Groupe> update(@PathVariable Long id, @RequestBody Groupe groupe) {
        return ResponseEntity.ok(groupeService.update(id, groupe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        groupeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

