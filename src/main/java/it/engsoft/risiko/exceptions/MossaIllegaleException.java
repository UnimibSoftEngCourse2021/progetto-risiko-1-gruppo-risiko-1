package it.engsoft.risiko.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Eccezione che viene sollevata se un giocatore tenta di effettuare una mossa illegale (in senso stretto o perché al di
 * fuori del proprio turno o della corretta fase del turno).
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class MossaIllegaleException extends RuntimeException {
    public MossaIllegaleException(String message) {
        super(message);
    }
}
