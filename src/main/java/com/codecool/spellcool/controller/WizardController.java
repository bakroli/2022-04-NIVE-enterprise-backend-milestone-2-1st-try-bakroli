package com.codecool.spellcool.controller;

import com.codecool.spellcool.model.Wizard;
import com.codecool.spellcool.service.WizardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/wizard")
public class WizardController {

    private WizardService wizardService;

    @Autowired
    public WizardController(WizardService wizardService) {
        this.wizardService = wizardService;
    }

    @GetMapping
    public List<Wizard> getAllWizards() {
        return wizardService.getAllWizards();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wizard> getWizardById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(wizardService.getWizardById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Wizard> saveWizard(@Valid @RequestBody Wizard wizard, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (wizard.getApprentices().size()==0) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(wizardService.saveWizard(wizard));

    }

    @DeleteMapping("/{id}")
    public void deleteWizardById(@PathVariable("id") Long id) {
        wizardService.deleteWizardById(id);
    }

    @GetMapping("/diverse")
    public List<Wizard> getWizardsByDiverse() {
        return wizardService.getWizardsByDiverse();
    }

}
