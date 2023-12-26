package com.arnold.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
){}
