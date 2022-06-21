package com.codecool.spellcool.data;

import com.codecool.spellcool.testmodels.Apprentice;
import com.codecool.spellcool.testmodels.StudentHouse;

public interface TestApprentice {
    Apprentice HARRY = new Apprentice(null, "Harry", StudentHouse.GRIFFINCLAW);
    Apprentice RON = new Apprentice(null, "Ron", StudentHouse.RAVENDOR);
    Apprentice THOMAS = new Apprentice(null, "Thomas", StudentHouse.GRIFFINCLAW);
    Apprentice JULIE = new Apprentice(null, "Julie", StudentHouse.SLYTHERPUFF);
    Apprentice PETER = new Apprentice(null, "Peter", StudentHouse.HUFFLETHERIN);
}
