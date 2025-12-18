package com.example.demo.service;

import com.example.demo.exception.PetAlreadyExistsException;
import com.example.demo.model.Pet;
import com.example.demo.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService{

    private final PetRepository petRepository;

    @Override
    public Mono<Pet> savePet(Pet pet) {
        return Mono.fromCallable(() -> {
                    boolean exists =
                            petRepository.existsByNameAndAge(pet.getName(), pet.getAge());

                    if (exists) {
                        throw new PetAlreadyExistsException(
                                "Pet with same name and age already exists"
                        );
                    }

                    return petRepository.save(pet);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Pet> getPets() {
        return Flux.fromIterable(petRepository.findAll());
    }
}
