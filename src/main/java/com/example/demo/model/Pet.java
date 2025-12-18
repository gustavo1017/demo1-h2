package com.example.demo.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public record Pet (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id,

        String name,
        Integer age,
        String breed
){}
