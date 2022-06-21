package com.codecool.spellcool.data;

import com.codecool.spellcool.testmodels.Wizard;

import java.util.List;

import static com.codecool.spellcool.data.TestApprentice.*;

public interface TestWizard {
    Wizard SEVERUS = new Wizard(null, "Serverus Snape", List.of(PETER, THOMAS));
    Wizard MINERVA = new Wizard(null, "Minerva McGonagall", List.of(RON, JULIE));
    Wizard ALBUS = new Wizard(null, "Albus Dumbledore", List.of(RON, JULIE, HARRY, THOMAS, PETER));
}
