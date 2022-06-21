package com.codecool.spellcool.service;

import com.codecool.spellcool.model.Apprentice;
import com.codecool.spellcool.model.StudentHouse;
import com.codecool.spellcool.model.Wizard;
import com.codecool.spellcool.repository.WizardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WizardService {

    private WizardRepository wizardRepository;

    @Autowired
    public WizardService(WizardRepository wizardRepository) {
        this.wizardRepository = wizardRepository;
    }

    public List<Wizard> getAllWizards() {
        return wizardRepository.findAll();
    }

    public Wizard getWizardById(long id) {
        return wizardRepository.findById(id).orElseThrow();
    }

    public Wizard saveWizard(Wizard wizard) {
        wizardRepository.save(wizard);
        return wizard;
    }

    public void deleteWizardById(long id) {
        wizardRepository.deleteById(id);
    }

    public List<Wizard> getWizardsByDiverse() {
        List<Wizard> wizards = wizardRepository.findAll();
        List<Wizard> diverseWizards = new ArrayList<>();
        for (Wizard wizard : wizards) {
            boolean griffinclaw =false;
            boolean ravendor =false;
            boolean huffletherin = false;
            boolean slytherpuff = false;
            for (Apprentice apprentice : wizard.getApprentices()) {
                if (apprentice.getHouse() == StudentHouse.GRIFFINCLAW) {
                    griffinclaw = true;
                }
                if (apprentice.getHouse() == StudentHouse.RAVENDOR) {
                    ravendor = true;
                }
                if (apprentice.getHouse() == StudentHouse.HUFFLETHERIN) {
                    huffletherin = true;
                }
                if (apprentice.getHouse() == StudentHouse.SLYTHERPUFF) {
                    slytherpuff = true;
                }
            }
            if (griffinclaw && ravendor && huffletherin && slytherpuff) {
                diverseWizards.add(wizard);
            }
        }
        return diverseWizards;
    }

}
