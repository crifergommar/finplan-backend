package com.finplan.shared.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }

    public ResourceNotFoundException(String entidad, Long id) {
        super(entidad + " con id " + id + " no encontrado");
    }
}