package it.engsoft.risiko.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Eccezione che viene sollevata se i dati forniti tramite request http non sono sintatticamente validi.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DatiErratiException extends RuntimeException {
    public DatiErratiException(String message) {
        super(message);
    }
}
