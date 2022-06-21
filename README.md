# SpellCool - Varázslóiskola 🧙 

A feladatod egy varázslóiskola backendjének kidolgozása.
A varázslókat és a személyes tanítványaikat kell relációs adatbázisban tárolni.

Az automata, integrációs teszteseteket készen kapod.
Ezek futtatásához először létre kell hoznod a modelleket (**fontos**, hogy a megadott package-ben hozd őket létre!),
majd implementálnod kell a meghatározott API végpontokat.

A feladat megoldását a **Clean Code** elvek betartásával, rétegekre bontva kell biztosítanod.
**Használj 3 rétegű architektúrát** (*controller, service, dao/repository* rétegek)!

A megoldáshoz csatolj konténerizációhoz szükséges fájlokat is (`Dockerfile`, futtatáshoz szükséges parancsok)!

## Modellek

- package: `com.codecool.spellcool.model`

### StudentHouse

- Enum, a következő opciókkal: `GRIFFINCLAW, RAVENDOR, HUFFLETHERIN, SLYTHERPUFF`

### Apprentice

- id: `Long` (automatikusan generált)
- name: `String`
- house: `StudentHouse`

### Wizard

- id: `Long` (automatikusan generált)
- name: `String`
- apprentices: `Apprentice` objektumokat tartalmazó `List`

A vizsgafeladatban elég, ha a két entitás közt **1 to many** egy irányú kapcsolat van.

## Validálás

A `POST` illetve `PUT` végpontokra érkező entitásokat ellenőrizd, megfelelnek-e az alábbiaknak,
ellenkező esetben `400`-as hibával térnek vissza:

- A `Apprentice` osztály `name` mezője nem lehet üres
- Az `Wizard` osztály `name` mezője nem lehet üres
- Az `Wizard` osztály `apprentices` mezője szintén nem lehet üres, egy varázslónak legalább egy tanítványa kell hogy legyen. 

## API Végpontok
* Minden végpont JSON formátumban kommunikál.
* Minden olyan végpont, amely tömbbel válaszol, az adatbázishoz való hozzáadás sorrendjében adja vissza az adatokat,
  találat híján pedig üres tömbbel tér vissza

| HTTP kérés | erőforrás          | leírás                                                                                                                       | válasz kód |
|------------|--------------------|------------------------------------------------------------------------------------------------------------------------------|------------|
| `GET`      | `/apprentice`      | visszaadja az adatbázisban tárolt összes tanítványt                                                                          | 200        |
| `GET`      | `/apprentice/{id}` | visszatér a megadott id-val rendelkező tanítvánnyal, ha az létezik, 404-as hiba, ha nem                                      | 200, 404   |
| `POST`     | `/apprentice`      | a _request body_-ban kapott JSON formátumú `Apprentice`-ot menti le az adatbázisba, (visszatér a lementett entitással)       | 200, 400   |
| `PUT`      | `/apprentice/{id}` | a _request body_-ban kapott `Apprentice`-ot felülírja az adatbázisban. (ha rossz `{id}`-ra küldték: 400-as hiba)             | 200, 400   |
| `DELETE`   | `/apprentice/{id}` | törli az adatbázisból a megadott azonosítójú tanítványt, feltéve, hogy az nem szerepel egy `Wizard` tanítványai közt         | 200        |
| `GET`      | `/wizard`          | visszaadja az adatbázisban tárolt összes varázslót                                                                           | 200        |
| `GET`      | `/wizard/{id}`     | visszaadja a megadott id-val rendelkező varázslót. Ha nincs ilyen, _404-as HTTP kóddal_ válaszol (pl. `RuntimeException`).   | 200, 404   |
| `POST`     | `/wizard`          | _request body_-jában kapott JSON formátumú új varázslót, `Wizard`-t menti az adatbázisba, (visszatér a lementett entitással) | 200, 400   |
| `DELETE`   | `/wizard/{id}`     | törli az adatbázisból a megadott id-jú varázslót (az tanítványokat nem!).                                                    | 200        |
| `GET`      | `/wizard/diverse`  | visszaadja az adatbázisban tárolt összes olyan varázslót, amelynek mindegyik házból van legalább 1 tanítványa                | 200        |

## Docker

- Állítsd össze az alkalmazásodhoz egy `Dockerfile`-t!
- Mellékeld a `docker_build.sh` és a `docker_build.bat` parancsokban az alkalmazás docker képpé fordításához szükséges parancsokat. Az `image` név legyen `spellcool-api`.
- Mellékeld a `docker_run.sh` és a `docker_run.bat` parancsokban a docker image futtatásához szükséges parancsot.

## Kiegészítő UML osztály diagram - model réteg

 ```mermaid
 classDiagram
 direction LR
 class Wizard{
    Long id
    String name
    List~Apprentice~ apprentices
  }
  class Apprentice{
    Long id
    String name
    StudentHouse house  
  }
  Wizard "1" --> "1..*" Apprentice

  class StudentHouse{
    <<enumeration>>
    GRIFFINCLAW
    RAVENDOR
    HUFFLETHERIN
    SLYTHERPUFF
  }
  
  Apprentice --> StudentHouse: has-a
```

## Pontozás

Egy feladatra 0 pontot ér, ha:

- nem fordul le
- lefordul, de egy teszteset sem fut le sikeresen.
- ha a forráskód olvashatatlan, nem felel meg a konvencióknak, nem követi a clean code alapelveket.

0 pont adandó továbbá, ha:

- kielégíti a teszteseteket, de a szöveges követelményeknek nem felel meg

Pontokat a további működési funkciók megfelelősségének arányában kell adni a vizsgafeladatra:

- 5 pont: az adott projekt lefordul, néhány teszteset sikeresen lefut, és ezek funkcionálisan is helyesek. Azonban több
  teszteset nem fut le, és a kód is olvashatatlan.
- 10 pont: a projekt lefordul, a tesztesetek legtöbbje lefut, ezek funkcionálisan is helyesek, és a clean code elvek
  nagyrészt betartásra kerültek.
- 20 pont: ha a projekt lefordul, a tesztesetek lefutnak, funkcionálisan helyesek, és csak apróbb funkcionális vagy
  clean code hibák szerepelnek a megoldásban.

Konténerizálás feladatrész összesen 3 pont:

- 1 pont: ha megvan a Dockerfile
- +1 pont: ha megvannak az indító scriptek
- +1 pont: Docker file betartja a bevált gyakorlat elveit (best practice).

Gyakorlati pontozás a project feladatokhoz:

- Alap pontszám egy feladatra(max 20): lefutó egység tesztek száma / összes egység tesztek száma * 20, feltéve, hogy a
  megoldás a szövegben megfogalmazott feladatot valósítja meg
- Clean kód, programozási elvek, bevett gyakorlat, kód formázás megsértéséért - pontlevonás jár. Szintén pontlevonás
  jár, ha valaki a feladatot nem a leghatékonyabb módszerrel oldja meg - amennyiben ez értelmezhető.
