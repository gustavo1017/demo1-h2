package com.example.demo.service;

import com.example.demo.exception.PetAlreadyExistsException;
import com.example.demo.model.Pet;
import com.example.demo.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@Slf4j
@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService{

    private static final Logger log = LoggerFactory.getLogger(PetServiceImpl.class);
    private final PetRepository petRepository;

    @Override
    public Mono<Pet> savePet(Pet pet) {
        log.info("name: {}", pet.getName());
        return Mono.fromCallable(() -> {
                    boolean exists =
                            petRepository.existsByNameAndAge(pet.getName(), pet.getAge());

                    if (exists) {
                        throw new PetAlreadyExistsException(
                                "Pet with same name and age already exists"
                        );
                    }
                    log.info("pet new: {}", pet.getName());
                    return petRepository.save(pet);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Pet> getPets() {
        return Flux.defer(() -> Flux.fromIterable(petRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(pet -> {
                    if (pet.getBreed() == null || pet.getBreed().isBlank()) {
                        pet.setBreed("Falta detallar");
                    }
                    return pet;
                });
    }
}
