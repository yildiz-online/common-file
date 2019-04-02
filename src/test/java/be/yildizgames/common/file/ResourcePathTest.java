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

import be.yildizgames.common.exception.implementation.ImplementationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Grégory Van den Borre
 */
public class ResourcePathTest {

    @Nested
    public class Vfs {

        @Test
        public void happyFlow() {
            ResourcePath p = ResourcePath.vfs("test", "azerty");
            assertEquals(FileResource.FileType.VFS, p.getType());
            assertEquals("azerty", p.getPath());
            assertEquals("test", p.getName());
        }

        @Test
        public void fromNull() {
            assertThrows(ImplementationException.class, () -> ResourcePath.vfs("test", null));
        }

        @Test
        public void fromNameNull() {
            assertThrows(ImplementationException.class, () -> ResourcePath.vfs(null, "azerty"));
        }
    }

    @Nested
    public class Directory {

        @Test
        public void happyFlow() {
            ResourcePath p = ResourcePath.directory("test", "azerty");
            assertEquals(FileResource.FileType.DIRECTORY, p.getType());
            assertEquals("azerty", p.getPath());
            assertEquals("test", p.getName());
        }

        @Test
        public void fromNull() {
            assertThrows(ImplementationException.class, () -> ResourcePath.directory("test", null));
        }

        @Test
        public void fromNameNull() {
            assertThrows(ImplementationException.class, () -> ResourcePath.directory(null, "azerty"));
        }
    }

    @Nested
    public class Exists {

        @Test
        public void exists() {
            ResourcePath p = ResourcePath.directory("test", getFile("test.properties").getParentFile().getAbsolutePath());
            assertTrue(p.exists("test.properties"));
        }

        @Test
        public void existsVfs() {
            ResourcePath p = ResourcePath.vfs("test", "any");
            assertTrue(p.exists("any"));
        }

        @Test
        public void doesNotExist() {
            ResourcePath p = ResourcePath.directory("test", getFile("test.properties").getParentFile().getAbsolutePath());
            assertFalse(p.exists("ttest.properties"));
        }

        @Test
        public void withNull() {
            ResourcePath p = ResourcePath.directory("test", "azerty");
            assertThrows(AssertionError.class, () -> p.exists(null));
        }
    }

    private static File getFile(String name) {
        return new File(ResourcePath.class.getClassLoader().getResource(name).getFile()).getAbsoluteFile();
    }
}

