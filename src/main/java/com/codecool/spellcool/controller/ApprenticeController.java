package com.codecool.spellcool.controller;

import com.codecool.spellcool.model.Apprentice;
import com.codecool.spellcool.service.ApprenticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/apprentice")
public class ApprenticeController {

    private ApprenticeService apprenticeService;

    @Autowired
    public ApprenticeController(ApprenticeService apprenticeService) {
        this.apprenticeService = apprenticeService;
    }

    @GetMapping
    public List<Apprentice> getAllApprentices() {
        return apprenticeService.getAllApprentices();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Apprentice> getApprenticeById(@PathVariable("id") long id) {
        try {
            return ResponseEntity.ok().body(apprenticeService.getApprenticeById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Apprentice> saveApprentice(@Valid @RequestBody Apprentice apprentice, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(apprenticeService.saveApprentice(apprentice));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Apprentice> updateApprentice(@PathVariable("id") long id, @Valid @RequestBody Apprentice apprentice, BindingResult bindingResult) {
        if (bindingResult.hasErrors() || (id != apprentice.getId())) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            return ResponseEntity.ok().body(apprenticeService.updateApprentice(apprentice));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteApprenticeById(@PathVariable("id") long id) {
        apprenticeService.deleteApprenticeById(id);
    }
}
