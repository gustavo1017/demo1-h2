package com.example.demo.service;

import com.example.demo.client.AuditClient;
import com.example.demo.exception.PetAlreadyExistsException;
import com.example.demo.exception.PetServiceException;
import com.example.demo.model.Pet;
import com.example.demo.repository.PetRepository;
import com.example.demo.util.AuditRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceImplTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private AuditClient auditClient;

    @InjectMocks
    private PetServiceImpl petService;

    @Test
    void givenNewPet_whenSavePet_thenPetIsSavedAndAuditIsSent() {
        Pet pet = new Pet();
        pet.setName("Firulais");
        pet.setAge(3);

        Pet savedPet = new Pet();
        savedPet.setId(1L);
        savedPet.setName("Firulais");
        savedPet.setAge(3);

        when(petRepository.existsByNameAndAge("Firulais", 3))
                .thenReturn(false);
        when(petRepository.save(pet))
                .thenReturn(savedPet);
        when(auditClient.sendAudit(any(AuditRequestDto.class)))
                .thenReturn(Mono.empty());

        Mono<Pet> result = petService.savePet(pet);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(1L, response.getId());
                    assertEquals("Firulais", response.getName());
                    assertEquals(3, response.getAge());
                })
                .verifyComplete();

        verify(petRepository).existsByNameAndAge("Firulais", 3);
        verify(petRepository).save(pet);
        verify(auditClient).sendAudit(any(AuditRequestDto.class));
    }

    @Test
    void givenExistingPet_whenSavePet_thenThrowPetAlreadyExistsException() {
        Pet pet = new Pet();
        pet.setName("Firulais");
        pet.setAge(3);

        when(petRepository.existsByNameAndAge("Firulais", 3))
                .thenReturn(true);

        Mono<Pet> result = petService.savePet(pet);

        StepVerifier.create(result)
                .expectError(PetAlreadyExistsException.class)
                .verify();

        verify(petRepository).existsByNameAndAge("Firulais", 3);
        verify(petRepository, never()).save(any());
        verify(auditClient, never()).sendAudit(any());
    }

    @Test
    void givenPetsWithAndWithoutBreed_whenGetPets_thenBreedIsDefaultedIfMissing() {
        Pet petWithBreed = new Pet();
        petWithBreed.setName("Firulais");
        petWithBreed.setBreed("Labrador");

        Pet petWithoutBreed = new Pet();
        petWithoutBreed.setName("Bobby");
        petWithoutBreed.setBreed(null);

        Pet petWithBlankBreed = new Pet();
        petWithBlankBreed.setName("Max");
        petWithBlankBreed.setBreed("   ");

        when(petRepository.findAll())
                .thenReturn(List.of(
                        petWithBreed,
                        petWithoutBreed,
                        petWithBlankBreed
                ));

        Flux<Pet> result = petService.getPets();

        StepVerifier.create(result)
                .assertNext(pet -> {
                    assertEquals("Labrador", pet.getBreed());
                })
                .assertNext(pet -> {
                    assertEquals("Falta detallar", pet.getBreed());
                })
                .assertNext(pet -> {
                    assertEquals("Falta detallar", pet.getBreed());
                })
                .verifyComplete();

        verify(petRepository).findAll();
    }

    @Test
    void givenRepositoryThrowsException_whenGetPets_thenPetServiceExceptionIsThrown() {
        when(petRepository.findAll())
                .thenThrow(new RuntimeException("DB error"));

        Flux<Pet> result = petService.getPets();

        StepVerifier.create(result)
                .expectErrorSatisfies(ex -> {
                    assertTrue(ex instanceof PetServiceException);
                    assertEquals("Error retrieving pets", ex.getMessage());
                })
                .verify();

        verify(petRepository).findAll();
    }

    @Test
    void givenEmptyPetList_whenGetPets_thenReturnEmptyFlux() {
        when(petRepository.findAll())
                .thenReturn(Collections.emptyList());

        Flux<Pet> result = petService.getPets();

        StepVerifier.create(result)
                .verifyComplete();

        verify(petRepository).findAll();
    }
}