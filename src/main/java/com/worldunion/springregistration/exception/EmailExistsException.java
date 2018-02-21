package com.worldunion.springregistration.exception;

public class EmailExistsException extends Exception {
    public EmailExistsException(String s) {
        super(s);
    }
}
