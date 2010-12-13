package se.vgregion.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Ogiltligt argument")
public class IllegalWebArgumentException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public IllegalWebArgumentException() {
        super();
    }

    public IllegalWebArgumentException(String errorMsg) {
        super(errorMsg);
    }

    public IllegalWebArgumentException(Throwable cause) {
        super(cause);
    }

    public IllegalWebArgumentException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
    }
}