package com.moskalenko.application.exceptions;

public class DecreaseBalanceException extends RuntimeException{
    public DecreaseBalanceException(String message) {
        super(message);
    }
}
