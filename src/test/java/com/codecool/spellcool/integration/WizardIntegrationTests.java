package com.codecool.spellcool.integration;

import com.codecool.spellcool.testmodels.Apprentice;
import com.codecool.spellcool.testmodels.Wizard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codecool.spellcool.data.TestWizard.*;
import static com.codecool.spellcool.data.TestApprentice.THOMAS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class WizardIntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;
    private String entityUrl;
    private String baseUrl;

    private Wizard postWizard(Wizard wizard) {

        List<Apprentice> postedApprentices = wizard.getApprentices().stream()
                .map(p -> restTemplate.postForObject(baseUrl + "/apprentice", p, Apprentice.class))
                .toList();
        Wizard wizardToPost = new Wizard(wizard.getId(), wizard.getName(), postedApprentices);
        return restTemplate.postForObject(entityUrl, wizardToPost, Wizard.class);
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        entityUrl = baseUrl + "/wizard";
    }

    @RepeatedTest(2)
    void emptyDatabase_getAll_shouldReturnEmptyList() {
        assertEquals(Collections.emptyList(), List.of(restTemplate.getForObject(entityUrl, Wizard[].class)));
    }

    @Test
    void emptyDatabase_addOne_shouldReturnAdded() {
        SEVERUS.getApprentices().forEach(ing -> System.out.println(ing.getId()));
        Wizard result = postWizard(SEVERUS);
        assertEquals(SEVERUS.getName(), result.getName());
    }

    @Test
    void someStored_getAll_shouldReturnAll() {
        List<Wizard> testData = List.of(SEVERUS, MINERVA);
        Set<String> expectedNames = testData.stream().map(Wizard::getName).collect(Collectors.toSet());
        testData.forEach(this::postWizard);

        Wizard[] response = restTemplate.getForObject(entityUrl, Wizard[].class);

        assertEquals(testData.size(), response.length);
        for (Wizard a : response) {
            assertTrue(expectedNames.contains(a.getName()));
        }
    }

    @Test
    void oneStored_getOneById_shouldReturnCorrect() {
        Long id = postWizard(SEVERUS).getId();
        Wizard response = restTemplate.getForObject(entityUrl + "/" + id, Wizard.class);
        assertEquals(SEVERUS.getName(), response.getName());
        assertEquals(SEVERUS.getApprentices().size(), response.getApprentices().size());

        List<String> responseApprenticeNames = response.getApprentices().stream().map(Apprentice::getName).toList();
        List<String> expectedApprenticeNames = SEVERUS.getApprentices().stream().map(Apprentice::getName).toList();
        assertThat(responseApprenticeNames).hasSameElementsAs(expectedApprenticeNames);
    }

    @Test
    void getOneByWrongId_shouldRespond404() {
        ResponseEntity<String> response = restTemplate.getForEntity(entityUrl + "/12345", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void someWizardsStored_deleteOne_getAllShouldReturnRemaining() {
        List<Wizard> testData = new ArrayList<>(List.of(SEVERUS, MINERVA));
        testData.replaceAll(this::postWizard);

        String url = entityUrl + "/" + testData.get(0).getId();
        restTemplate.delete(url);
        testData.remove(testData.get(0));

        Wizard[] result = restTemplate.getForObject(entityUrl, Wizard[].class);

        assertEquals(testData.size(), result.length);
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(testData.get(i).getName(), result[i].getName());
        }
    }

    @Test
    void oneWizardStored_deleteById_getAllShouldReturnEmptyList() {
        Wizard testData = postWizard(SEVERUS);

        restTemplate.delete(entityUrl + "/" + testData.getId());

        Wizard[] result = restTemplate.getForObject(entityUrl, Wizard[].class);

        assertEquals(0, result.length);
    }

    @Test
    void postInvalidApprenticeWithNull_shouldRespond400() {
        var data = new Wizard(null, null, List.of(THOMAS));
        ResponseEntity<String> response = restTemplate.postForEntity(entityUrl, data, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void postInvalidApprenticeWithBlankString_shouldRespond400() {
        var data = new Wizard(null, "", List.of(THOMAS));
        ResponseEntity<String> response = restTemplate.postForEntity(entityUrl, data, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void postInvalidApprenticeWithBlankCollection_shouldRespond400() {
        var data = new Wizard(null, "ABC", null);
        ResponseEntity<String> response = restTemplate.postForEntity(entityUrl, data, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void postWizards_whenRequestingDiverse_ReturnOnlyTheDiverseOnes() {
        postWizard(SEVERUS);
        postWizard(MINERVA);
        postWizard(ALBUS);

        Wizard[] response = restTemplate.getForObject(entityUrl + "/diverse", Wizard[].class);
        Set<String> responseNames = Arrays.stream(response).map(Wizard::getName).collect(Collectors.toSet());

        var expectedNames = Stream.of(ALBUS).map(Wizard::getName).collect(Collectors.toSet());

        assertThat(responseNames).hasSameElementsAs(expectedNames);
    }
}
