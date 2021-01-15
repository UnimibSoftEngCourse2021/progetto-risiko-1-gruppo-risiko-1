package it.engsoft.risiko.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class MossaIllegaleException extends RuntimeException {
    public MossaIllegaleException(String message) {
        super(message);
    }

    public MossaIllegaleException() {
    }
}
