package be.yildizgames.common.file;

import be.yildizgames.common.exception.technical.TechnicalException;

public class ZipException extends TechnicalException {

    ZipException(String message, Exception cause) {
        super(message, cause);
    }

    ZipException(Exception cause) {
        super(cause);
    }

    ZipException(String s) {
        super(s);
    }
}
