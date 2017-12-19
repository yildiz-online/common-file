package be.yildizgames.common.file;

import be.yildizgames.common.exception.technical.TechnicalException;

public class FileDeletionException extends TechnicalException {

    FileDeletionException(String message, Exception cause) {
        super(message, cause);
    }

    FileDeletionException(Exception cause) {
        super(cause);
    }

    FileDeletionException(String s) {
        super(s);
    }
}
