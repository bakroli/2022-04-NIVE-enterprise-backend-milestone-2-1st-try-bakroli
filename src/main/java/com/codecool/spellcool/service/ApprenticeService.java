package com.codecool.spellcool.service;

import com.codecool.spellcool.model.Apprentice;
import com.codecool.spellcool.repository.ApprenticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprenticeService {

    private ApprenticeRepository apprenticeRepository;

    @Autowired
    public ApprenticeService(ApprenticeRepository apprenticeRepository) {
        this.apprenticeRepository = apprenticeRepository;
    }

    public List<Apprentice> getAllApprentices() {
        return apprenticeRepository.findAll();
    }

    public Apprentice getApprenticeById(long id) {
        return apprenticeRepository.findById(id).orElseThrow();
    }

    public Apprentice saveApprentice(Apprentice apprentice) {
        apprenticeRepository.save(apprentice);
        return apprentice;
    }

    public Apprentice updateApprentice(Apprentice apprentice) {
        Apprentice apprenticeOld = apprenticeRepository.findById(apprentice.getId()).orElseThrow();
        if (apprentice.getHouse() == null) {
            apprentice.setHouse(apprenticeOld.getHouse());
        }
        apprenticeRepository.save(apprentice);
        return apprentice;
    }

    public void deleteApprenticeById(long id) {
        apprenticeRepository.deleteById(id);
    }

}
