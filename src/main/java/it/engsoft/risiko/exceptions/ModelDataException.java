package it.engsoft.risiko.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ModelDataException extends RuntimeException {
    public ModelDataException(String message) { super(message); }
}
