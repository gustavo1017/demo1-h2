package com.example.demo.service;

import com.example.demo.model.Pet;
import reactor.core.publisher.Mono;

public interface PetService {

    Mono<Pet> savePet(Pet pet);
}
