package com.worldunion.springregistration.error;

public class UserAlreadyExistException extends RuntimeException {
    UserAlreadyExistException(String msg)
    {
        super(msg);
    }
}
