/*
 * This file is part of the Yildiz-Engine project, licenced under the MIT License  (MIT)
 *
 *  Copyright (c) 2019 Grégory Van den Borre
 *
 *  More infos available: https://engine.yildiz-games.be
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


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Grégory Van den Borre
 */
public class ResourceUtilTest {

    @Nested
    public class CreateDirectoryTree {

        @Test
        public void happyFlow() throws IOException {
            Path folder = Files.createTempDirectory("test");
            Path folderToTest = folder.resolve("test").resolve("dir");
            ResourceUtil.createDirectoryTree(folderToTest);
            assertTrue(Files.exists(folderToTest));
        }

        @Test
        public void withSpace() throws IOException {
            Path folder = Files.createTempDirectory("test");
            Path folderToTest = folder.resolve("tes t").resolve("dir");
            ResourceUtil.createDirectoryTree(folderToTest);
            assertTrue(Files.exists(folderToTest));
        }

        @Test
        public void withDot() throws IOException {
            Path folder = Files.createTempDirectory("test");
            Path folderToTest = folder.resolve("te.st").resolve("dir");
            ResourceUtil.createDirectoryTree(folderToTest);
            assertTrue(Files.exists(folderToTest));
        }

        @Test
        public void alreadyExisting() throws IOException {
            Path folder = Files.createTempDirectory("test");
            Path folderToTest = folder.resolve("test").resolve("exist");
            ResourceUtil.createDirectoryTree(folderToTest);
            ResourceUtil.createDirectoryTree(folderToTest);
            assertTrue(Files.exists(folderToTest));
        }

        @Test
        public void withNull() {
            assertThrows(NullPointerException.class, () -> ResourceUtil.createDirectoryTree((Path)null));
        }
    }

    @Nested
    public class CreateDirectory {

        @Test
        public void happyFlow() throws IOException {
            Path folder = Files.createTempDirectory("test");
            Path folderToTest = folder.resolve("test");
            ResourceUtil.createDirectory(folderToTest);
            assertTrue(Files.exists(folderToTest));
        }

        @Test
        public void withSpace() throws IOException {
            Path folder = Files.createTempDirectory("test");
            Path folderToTest = folder.resolve("tes t");
            ResourceUtil.createDirectory(folderToTest);
            assertTrue(Files.exists(folderToTest));
        }

        @Test
        public void withDot() throws IOException {
            Path folder = Files.createTempDirectory("test");
            Path folderToTest = folder.resolve("te.st");
            ResourceUtil.createDirectory(folderToTest);
            assertTrue(Files.exists(folderToTest));
        }

        @Test
        public void alreadyExisting() throws IOException {
            Path folder = Files.createTempDirectory("test");
            Path folderToTest = folder.resolve("exists");
            ResourceUtil.createDirectory(folderToTest);
            ResourceUtil.createDirectory(folderToTest);
            assertTrue(Files.exists(folderToTest));
        }

        @Test
        public void withNull() {
            assertThrows(NullPointerException.class, () -> ResourceUtil.createDirectory(null));
        }
    }

}

