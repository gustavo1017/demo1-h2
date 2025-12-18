package com.example.demo.controller;

import com.example.demo.model.Pet;
import com.example.demo.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Pet> save(@RequestBody Pet pet) {
        return petService.savePet(pet);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<Pet> getPets() {
        return petService.getPets();
    }


}
