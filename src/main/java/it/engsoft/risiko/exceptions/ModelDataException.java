package it.engsoft.risiko.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Eccezione che viene sollevata solo in caso di gravi errori logici non previsti interni al server.
 */
@ResponseStatus (value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ModelDataException extends RuntimeException {
    public ModelDataException(String message) { super(message); }
}
