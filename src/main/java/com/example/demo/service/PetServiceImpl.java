package com.example.demo.service;

import com.example.demo.model.Pet;
import com.example.demo.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService{

    private final PetRepository petRepository;

    @Override
    public Mono<Pet> savePet(Pet pet) {
        return Mono.just(petRepository.save(pet));
    }
}
