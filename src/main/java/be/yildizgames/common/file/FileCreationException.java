package be.yildizgames.common.file;

import be.yildizgames.common.exception.technical.TechnicalException;

public class FileCreationException extends TechnicalException {

    public FileCreationException(String message, Exception cause) {
        super(message, cause);
    }

    public FileCreationException(Exception cause) {
        super(cause);
    }

    public FileCreationException(String s) {
        super(s);
    }
}
