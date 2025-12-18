package com.example.demo.repository;

import com.example.demo.model.Pet;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends ReactiveCrudRepository<Pet, Long> {
}
