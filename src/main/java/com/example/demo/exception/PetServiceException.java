package com.example.demo.exception;

public class PetServiceException extends RuntimeException{
    public PetServiceException(String message) {
        super(message);
    }
}
