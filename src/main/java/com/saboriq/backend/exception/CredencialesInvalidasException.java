
package com.saboriq.backend.exception;

public class CredencialesInvalidasException
        extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Correo o contraseña incorrectos");
    }
}
