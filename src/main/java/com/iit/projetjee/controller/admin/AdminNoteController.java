package com.iit.projetjee.controller.admin;

import com.iit.projetjee.entity.Note;
import com.iit.projetjee.service.CoursService;
import com.iit.projetjee.service.EtudiantService;
import com.iit.projetjee.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/notes")
public class AdminNoteController {

    private final NoteService noteService;
    private final EtudiantService etudiantService;
    private final CoursService coursService;

    @Autowired
    public AdminNoteController(NoteService noteService,
                              EtudiantService etudiantService,
                              CoursService coursService) {
        this.noteService = noteService;
        this.etudiantService = etudiantService;
        this.coursService = coursService;
    }

    @GetMapping
    public String listNotes(Model model) {
        model.addAttribute("notes", noteService.getAllNotes());
        return "admin/notes/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("note", new Note());
        model.addAttribute("etudiants", etudiantService.getAllEtudiants());
        model.addAttribute("cours", coursService.getAllCours());
        return "admin/notes/form";
    }

    @PostMapping
    public String createNote(@ModelAttribute Note note,
                            RedirectAttributes redirectAttributes) {
        try {
            noteService.createNote(note);
            redirectAttributes.addFlashAttribute("success", "Note ajoutée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/notes/new";
        }
        return "redirect:/admin/notes";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Note note = noteService.getNoteById(id);
        model.addAttribute("note", note);
        model.addAttribute("etudiants", etudiantService.getAllEtudiants());
        model.addAttribute("cours", coursService.getAllCours());
        return "admin/notes/form";
    }

    @PostMapping("/{id}")
    public String updateNote(@PathVariable Long id,
                            @ModelAttribute Note note,
                            RedirectAttributes redirectAttributes) {
        try {
            noteService.updateNote(id, note);
            redirectAttributes.addFlashAttribute("success", "Note mise à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/notes/" + id + "/edit";
        }
        return "redirect:/admin/notes";
    }

    @PostMapping("/{id}/delete")
    public String deleteNote(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            noteService.deleteNote(id);
            redirectAttributes.addFlashAttribute("success", "Note supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/notes";
    }
}

