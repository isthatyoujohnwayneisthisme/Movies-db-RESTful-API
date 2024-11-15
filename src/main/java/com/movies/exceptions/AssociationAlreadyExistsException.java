package com.movies.exceptions;

public class AssociationAlreadyExistsException extends RuntimeException {
    public AssociationAlreadyExistsException(String message) {
        super(message);
    }
}