package com.moskalenko.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice("com.moskalenko.application.controller")
public class GlobalDefaultExceptionHandler {


    @ExceptionHandler({Exception.class})
    @ResponseBody
    public String commonError() {
        return "Error";
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<String> serviceError(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
