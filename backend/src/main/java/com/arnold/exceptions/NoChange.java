package com.arnold.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NoChange extends RuntimeException{
    public NoChange(String message) {
        super(message);
    }
}
