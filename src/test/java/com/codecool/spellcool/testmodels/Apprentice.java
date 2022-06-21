package com.codecool.spellcool.testmodels;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Apprentice {
    private Long id;
    private String name;
    private StudentHouse house;

    public Apprentice(Long id, String name, StudentHouse house) {
        this.id = id;
        this.name = name;
        this.house = house;
    }
}
