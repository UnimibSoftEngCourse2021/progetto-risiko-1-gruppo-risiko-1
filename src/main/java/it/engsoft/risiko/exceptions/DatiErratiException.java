package it.engsoft.risiko.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DatiErratiException extends RuntimeException {
    public DatiErratiException(String message) {
        super(message);
    }
}
