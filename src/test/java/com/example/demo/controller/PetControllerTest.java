package com.example.demo.controller;

import com.example.demo.model.Pet;
import com.example.demo.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetControllerTest {

    @Mock
    private PetService petService;

    @InjectMocks
    private PetController petController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient
                .bindToController(petController)
                .build();
    }

    @Test
    void givenValidPetAndHeader_whenSavePet_thenPetIsCreated() {
        Pet pet = new Pet();
        pet.setName("Firulais");
        pet.setAge(3);

        when(petService.savePet(any(Pet.class)))
                .thenReturn(Mono.just(pet));

        webTestClient.post()
                .uri("/pets")
                .header("X-Cliente", "cliente-test")
                .bodyValue(pet)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Pet.class)
                .value(response -> {
                    assertEquals("Firulais", response.getName());
                    assertEquals(3, response.getAge());
                });

        verify(petService).savePet(any(Pet.class));
    }

    @Test
    void givenExistingPets_whenGetPets_thenReturnListOfPets() {
        Pet pet1 = new Pet();
        pet1.setName("Firulais");

        Pet pet2 = new Pet();
        pet2.setName("Bobby");

        when(petService.getPets())
                .thenReturn(Flux.just(pet1, pet2));

        webTestClient.get()
                .uri("/pets")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Pet.class)
                .hasSize(2)
                .value(pets -> {
                    assertEquals("Firulais", pets.get(0).getName());
                    assertEquals("Bobby", pets.get(1).getName());
                });

        verify(petService).getPets();
    }
}