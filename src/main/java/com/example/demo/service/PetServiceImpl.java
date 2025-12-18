package com.example.demo.service;

import com.example.demo.model.Pet;
import com.example.demo.repository.PetRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PetServiceImpl implements PetService{

    private final PetRepository petRepository;

    public PetServiceImpl(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public Mono<Pet> savePet(Pet pet) {
        return Mono.just(petRepository.save(pet));
    }
}
