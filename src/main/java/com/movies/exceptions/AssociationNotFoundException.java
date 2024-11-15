package com.movies.exceptions;

public class AssociationNotFoundException extends RuntimeException {
    public AssociationNotFoundException(String message) {
        super(message);
    }
}
