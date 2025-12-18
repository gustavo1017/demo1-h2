package com.example.demo.service;

import com.example.demo.client.AuditClient;
import com.example.demo.exception.PetAlreadyExistsException;
import com.example.demo.exception.PetServiceException;
import com.example.demo.model.Pet;
import com.example.demo.repository.PetRepository;
import com.example.demo.util.AuditRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@Slf4j
@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final AuditClient auditClient;

    @Override
    public Mono<Pet> savePet(Pet pet) {
        log.info("name: {}", pet.getName());
        return Mono.fromCallable(() -> {
                    if (petRepository.existsByNameAndAge(
                            pet.getName(), pet.getAge())) {
                        throw new PetAlreadyExistsException(
                                "Pet with same name and age already exists"
                        );
                    }
                    return petRepository.save(pet);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(savedPet -> {

                    AuditRequestDto dto = new AuditRequestDto(
                            "system",
                            "pets-service"
                    );
                    return auditClient.sendAudit(dto)
                            .doOnSuccess(x -> log.info("success"))
                            .onErrorResume(ex -> {
                                log.warn("Audit service failed", ex);
                                return Mono.empty();
                            })
                            .thenReturn(savedPet);
                });
    }

    @Override
    public Flux<Pet> getPets() {
        return Flux.defer(() -> Flux.fromIterable(petRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(ex ->
                        new PetServiceException("Error retrieving pets")
                )
                .map(pet -> {
                    if (pet.getBreed() == null || pet.getBreed().isBlank()) {
                        pet.setBreed("Falta detallar");
                    }
                    return pet;
                });
    }
}
