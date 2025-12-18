package com.example.demo.controller;

import com.example.demo.model.Pet;
import com.example.demo.service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Pet> save(@RequestBody Pet pet) {
        return petService.savePet(pet);
    }


}
