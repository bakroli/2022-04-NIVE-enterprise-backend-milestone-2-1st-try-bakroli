package com.codecool.spellcool.integration;

import com.codecool.spellcool.testmodels.Apprentice;
import com.codecool.spellcool.testmodels.Wizard;
import com.codecool.spellcool.testmodels.StudentHouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static com.codecool.spellcool.data.TestApprentice.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ApprenticeIntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String entityUrl;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port;
        entityUrl = baseUrl + "/apprentice";
    }

    @Test
    void emptyDatabase_getAll_shouldReturnEmptyList() {
        assertEquals(Collections.emptyList(), List.of(restTemplate.getForObject(entityUrl, Apprentice[].class)));
    }

    @Test
    void emptyDatabase_addOne_shouldReturnAddedApprentice() {
        Apprentice result = restTemplate.postForObject(entityUrl, HARRY, Apprentice.class);
        assertEquals(HARRY.getName(), result.getName());
    }

    @Test
    void someApprenticesStored_getAll_shouldReturnAll() {
        List<Apprentice> testData = List.of(HARRY, RON, THOMAS);
        testData.forEach(ingredient -> restTemplate.postForObject(entityUrl, ingredient, Apprentice.class));

        Apprentice[] result = restTemplate.getForObject(entityUrl, Apprentice[].class);
        assertEquals(testData.size(), result.length);

        Set<String> apprenticeNames = testData.stream().map(Apprentice::getName).collect(Collectors.toSet());
        assertTrue(Arrays.stream(result).map(Apprentice::getName).allMatch(apprenticeNames::contains));
    }

    @Test
    void oneApprenticeStored_getOneById_shouldReturnCorrectApprentice() {
        Long id = restTemplate.postForObject(entityUrl, HARRY, Apprentice.class).getId();
        Apprentice result = restTemplate.getForObject(entityUrl + "/" + id, Apprentice.class);
        assertEquals(HARRY.getName(), result.getName());
        assertEquals(id, result.getId());
    }

    @Test
    void someApprenticesStored_deleteOne_getAllShouldReturnRemaining() {
        List<Apprentice> testData = new ArrayList<>(List.of(HARRY, JULIE, THOMAS));
        testData = new ArrayList<>(testData.stream()
                .map(p -> restTemplate.postForObject(entityUrl, p, Apprentice.class))
                .toList());

        restTemplate.delete(entityUrl + "/" + testData.get(0).getId());
        Set<String> expectedApprenticeNames = testData.stream().skip(1L).map(Apprentice::getName).collect(Collectors.toSet());

        Apprentice[] response = restTemplate.getForObject(entityUrl, Apprentice[].class);

        assertEquals(expectedApprenticeNames.size(), response.length);
        for (Apprentice p : response) {
            assertTrue(expectedApprenticeNames.contains(p.getName()));
        }
    }

    @Test
    void oneApprenticeStored_deleteById_getAllShouldReturnEmptyList() {
        Apprentice testApprentice = restTemplate.postForObject(entityUrl, THOMAS, Apprentice.class);
        assertNotNull(testApprentice.getId());
        restTemplate.delete(entityUrl + "/" + testApprentice.getId());
        Apprentice[] result = restTemplate.getForObject(entityUrl, Apprentice[].class);
        assertEquals(0, result.length);
    }

    @Test
    void oneApprenticeStoredUsedInWizard_deleteById_ApprenticeShouldNotBeDeleted() {
        Apprentice testApprentice = restTemplate.postForObject(entityUrl, RON, Apprentice.class);
        Wizard testWizard = restTemplate.postForObject(
                "http://localhost:" + port + "/wizard",
                new Wizard(null, "toy car", List.of(testApprentice)),
                Wizard.class
        );
        restTemplate.delete(entityUrl + "/" + testApprentice.getId());
        Apprentice result = restTemplate.getForObject(entityUrl + "/" + testApprentice.getId(), Apprentice.class);
        assertEquals(testApprentice.getName(), result.getName());
    }

    @Test
    void oneApprenticeStored_updateIt_ApprenticeShouldBeUpdated() {
        Apprentice testApprentice = restTemplate.postForObject(entityUrl, HARRY, Apprentice.class);

        testApprentice.setName(testApprentice.getName() + "update");
        String url = entityUrl + "/" + testApprentice.getId();
        restTemplate.put(url, testApprentice);

        Apprentice result = restTemplate.getForObject(url, Apprentice.class);
        assertEquals(testApprentice.getName(), result.getName());
    }

    @Test
    void oneApprenticeStored_updateWithWrongId_ApprenticeShouldBeUnchanged() {
        Apprentice testApprentice = restTemplate.postForObject(entityUrl, JULIE, Apprentice.class);

        String originalName = testApprentice.getName();
        assertNotNull(originalName);
        Long originalId = testApprentice.getId();

        testApprentice.setName(originalName + "update");
        testApprentice.setId(42L);
        String url = entityUrl + "/" + originalId;
//         restTemplate.put(url, testApprentice, Object.class);
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(testApprentice, null), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

        Apprentice result = restTemplate.getForObject(url, Apprentice.class);
        assertEquals(originalName, result.getName());
    }

    @Test
    void getOneByWrongId_shouldRespond404() {
        ResponseEntity<String> response = restTemplate.getForEntity(entityUrl + "/12345", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void postInvalidApprenticeWithNull_shouldRespond400() {
        ResponseEntity<String> response = restTemplate.postForEntity(entityUrl, new Apprentice(null, null, StudentHouse.GRIFFINCLAW), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void postInvalidApprenticeWithBlankString_shouldRespond400() {
        ResponseEntity<String> response = restTemplate.postForEntity(entityUrl, new Apprentice(null, "", StudentHouse.GRIFFINCLAW), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void putInvalidApprenticeWithBlankString_shouldRespond400() {
        Apprentice testApprentice = restTemplate.postForObject(entityUrl, THOMAS, Apprentice.class);
        String url = entityUrl + "/" + testApprentice.getId();

        testApprentice.setName("");
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(testApprentice, null), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }
}
