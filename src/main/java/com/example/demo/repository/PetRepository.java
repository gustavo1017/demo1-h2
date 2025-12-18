package com.example.demo.repository;

import com.example.demo.model.Pet;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PetRepository extends ReactiveCrudRepository<Pet, Long> {
}
