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


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Grégory Van den Borre
 */
class ResourceUtilTest {

    @Nested
    class CreateDirectoryTree {

        @Test
        void happyFlow() throws IOException {
            Path folder = Files.createTempDirectory("test");
            String path = folder.toFile().getAbsolutePath() + "/";
            ResourceUtil.createDirectoryTree(path + "test/dir");
            assertTrue(new File(path + "test/dir").exists());
        }

        @Test
        void withSpace() throws IOException {
            Path folder = Files.createTempDirectory("test");
            String path = folder.toFile().getAbsolutePath() + "/";
            ResourceUtil.createDirectoryTree(path + "tes t/dir");
            assertTrue(new File(path + "tes t/dir").exists());
        }

        @Test
        void withDot() throws IOException {
            Path folder = Files.createTempDirectory("test");
            String path = folder.toFile().getAbsolutePath() + "/";
            ResourceUtil.createDirectoryTree(path + "te.st/dir");
            assertTrue(new File(path + "te.st/dir").exists());
        }

        @Test
        void alreadyExisting() throws IOException {
            Path folder = Files.createTempDirectory("test");
            String path = folder.toFile().getAbsolutePath() + "/";
            ResourceUtil.createDirectoryTree(path + "test/exist");
            ResourceUtil.createDirectoryTree(path + "test/exist");
            assertTrue(new File(path + "test/exist").exists());
        }

        @Test
        void withNull() {
            assertThrows(AssertionError.class, () -> ResourceUtil.createDirectoryTree(null));
        }
    }

    @Nested
    class CreateDirectory {

        @Test
        void happyFlow() throws IOException {
            Path folder = Files.createTempDirectory("test");
            String path = folder.toFile().getAbsolutePath() + "/";
            ResourceUtil.createDirectory(path + "test");
            assertTrue(new File(path + "test").exists());
        }

        @Test
        void withSpace() throws IOException {
            Path folder = Files.createTempDirectory("test");
            String path = folder.toFile().getAbsolutePath() + "/";
            ResourceUtil.createDirectory(path + "tes t");
            assertTrue(new File(path + "tes t").exists());
        }

        @Test
        void withDot() throws IOException {
            Path folder = Files.createTempDirectory("test");
            String path = folder.toFile().getAbsolutePath() + "/";
            ResourceUtil.createDirectory(path + "te.st");
            assertTrue(new File(path + "te.st").exists());
        }

        @Test
        void alreadyExisting() throws IOException {
            Path folder = Files.createTempDirectory("test");
            String path = folder.toFile().getAbsolutePath() + "/";
            ResourceUtil.createDirectory(path + "exist");
            ResourceUtil.createDirectory(path + "exist");
            assertTrue(new File(path + "exist").exists());
        }

        @Test
        void withNull() {
            assertThrows(AssertionError.class, () -> ResourceUtil.createDirectory(null));
        }
    }

    private static File getFile(String name) {
        return new File(ResourceUtil.class.getClassLoader().getResource(name).getFile()).getAbsoluteFile();
    }
}
