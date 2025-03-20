package com.example.expensetracker.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailDuplicateException extends RuntimeException {

    public EmailDuplicateException(String message) {
        super(message);
    }
}
