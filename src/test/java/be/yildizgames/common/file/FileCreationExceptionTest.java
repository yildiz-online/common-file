/*
 * This file is part of the Yildiz-Engine project, licenced under the MIT License  (MIT)
 *
 *  Copyright (c) 2018 Grégory Van den Borre
 *
 *  More infos available: https://www.yildiz-games.be
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *  of the Software, and to permit persons to whom the Software is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 *  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 *  OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  SOFTWARE.
 *
 */
package be.yildizgames.common.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * @author Grégory Van den Borre
 */
class FileCreationExceptionTest {

    private static final String EXPECTED_NO_MESSAGE = "";

    private static final Throwable EXPECTED_NO_CAUSE = null;

    @Nested
    class Constructor {

        @Test
        void withMessage() {
            FileCreationException e = new FileCreationException("myTest");
            Assertions.assertEquals("myTest", e.message);
            Assertions.assertEquals(EXPECTED_NO_CAUSE, e.getCause());
        }

        @Test
        void withCause() {
            Exception root = new RuntimeException("cause");
            FileCreationException e = new FileCreationException(root);
            Assertions.assertEquals(root, e.getCause());
            Assertions.assertEquals(EXPECTED_NO_MESSAGE, e.message);
        }

        @Test
        void withMessageAndCause() {
            Exception root = new RuntimeException("cause");
            FileCreationException e = new FileCreationException("myTest", root);
            Assertions.assertEquals(root, e.getCause());
            Assertions.assertEquals("myTest", e.message);
        }

    }

}
