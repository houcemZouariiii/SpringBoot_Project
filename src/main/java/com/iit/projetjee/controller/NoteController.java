package com.iit.projetjee.controller;

import com.iit.projetjee.entity.Note;
import com.iit.projetjee.service.ICoursService;
import com.iit.projetjee.service.IEtudiantService;
import com.iit.projetjee.service.INoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private final INoteService noteService;
    private final IEtudiantService etudiantService;
    private final ICoursService coursService;

    @Autowired
    public NoteController(INoteService noteService,
                         IEtudiantService etudiantService,
                         ICoursService coursService) {
        this.noteService = noteService;
        this.etudiantService = etudiantService;
        this.coursService = coursService;
    }

    // Liste de toutes les notes
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listNotes(Model model) {
        model.addAttribute("notes", noteService.getAllNotes());
        return "admin/notes/list";
    }

    // Formulaire de création
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public String showCreateForm(Model model) {
        model.addAttribute("note", new Note());
        model.addAttribute("etudiants", etudiantService.getAllEtudiants());
        model.addAttribute("cours", coursService.getAllCours());
        return "admin/notes/form";
    }

    // Créer une note
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public String createNote(@ModelAttribute Note note,
                            RedirectAttributes redirectAttributes) {
        try {
            noteService.createNote(note);
            redirectAttributes.addFlashAttribute("success", "Note ajoutée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/notes/new";
        }
        return "redirect:/notes";
    }

    // Formulaire d'édition
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Note note = noteService.getNoteById(id);
        model.addAttribute("note", note);
        model.addAttribute("etudiants", etudiantService.getAllEtudiants());
        model.addAttribute("cours", coursService.getAllCours());
        return "admin/notes/form";
    }

    // Mettre à jour une note
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public String updateNote(@PathVariable Long id,
                            @ModelAttribute Note note,
                            RedirectAttributes redirectAttributes) {
        try {
            noteService.updateNote(id, note);
            redirectAttributes.addFlashAttribute("success", "Note mise à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/notes/" + id + "/edit";
        }
        return "redirect:/notes";
    }

    // Supprimer une note
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteNote(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            noteService.deleteNote(id);
            redirectAttributes.addFlashAttribute("success", "Note supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/notes";
    }
}
