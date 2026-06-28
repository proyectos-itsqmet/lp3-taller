package com.lp3_taller.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String recurso, String campo, Object valor) {
        super("Ya existe un " + recurso + " con " + campo + " '" + valor + "'");
    }
}
