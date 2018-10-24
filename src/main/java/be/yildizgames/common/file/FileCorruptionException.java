/*
 * This file is part of the Yildiz-Engine project, licenced under the MIT License  (MIT)
 *
 * Copyright (c) 2018 Grégory Van den Borre
 *
 * More infos available: https://www.yildiz-games.be
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  SOFTWARE.
 */

package be.yildizgames.common.file;

import be.yildizgames.common.exception.technical.TechnicalException;

/**
 * This exception is thrown when a file is corrupted.
 */
public class FileCorruptionException extends TechnicalException {

    /**
     * Build a new corrupted exception with a message and an exception cause.
     * @param message Message for this exception.
     * @param cause Cause of this exception.
     */
    public FileCorruptionException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Build a new corrupted exception with an exception cause.
     * @param cause Cause of this exception.
     */
    public FileCorruptionException(Exception cause) {
        super(cause);
    }

    /**
     * Build a new corrupted exception with a message.
     * @param message Message for this exception.
     */
    public FileCorruptionException(String message) {
        super(message);
    }
}
