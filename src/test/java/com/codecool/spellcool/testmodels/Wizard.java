package com.codecool.spellcool.testmodels;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Wizard {
    private Long id;
    private String name;
    private List<Apprentice> apprentices;

    public Wizard(Long id, String name, List<Apprentice> apprentices) {

        this.id = id;
        this.name = name;
        this.apprentices = apprentices == null ? new ArrayList<>() : new ArrayList<>(apprentices);
    }
}
