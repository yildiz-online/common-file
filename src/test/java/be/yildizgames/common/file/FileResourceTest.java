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

import be.yildizgames.common.exception.technical.ResourceMissingException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for FileResource.
 *
 * @author Grégory Van den Borre
 */
final class  FileResourceTest {

    private static File getFile(String name) {
        return new File(FileResourceTest.class.getClassLoader().getResource(name).getFile()).getAbsoluteFile();
    }

    @Nested
    class FindResource {

        @Test
        void happyFlow() {
            FileResource.findResource(getFile("file with space.txt").getAbsolutePath());
        }

        @Test
        void notExisting() {
            assertThrows(ResourceMissingException.class, () -> FileResource.findResource("azerty"));
        }

        @Test
        void fromNull() {
            assertThrows(AssertionError.class, () -> FileResource.findResource(null));
        }
    }

    @Nested
    class createFile {

        @Test
        void happyFlow() throws IOException {
            Path folder = Files.createTempDirectory("test");
            String file = folder.toFile().getAbsolutePath() + "/test.txt";
            assertFalse(new File(file).exists());
            FileResource.createFile(file);
            assertTrue(new File(file).exists());
            assertTrue(new File(file).isFile());
        }

        @Test
        void fromNull() {
            assertThrows(AssertionError.class, () -> FileResource.createFile((Path)null));
        }

        @Test
        void alreadyExisting() {
            String file = getFile("test-resource.txt").getAbsolutePath();
            assertTrue(new File(file).exists());
            long size = new File(file).length();
            FileResource f = FileResource.createFile(file);
            assertTrue(new File(file).exists());
            assertTrue(new File(file).isFile());
            assertEquals(size, f.getSize());
        }
    }

    @Nested
    class createDirectory {

        @Test
        void happyFlow() throws IOException {
            Path folder = Files.createTempDirectory("test");
            String file = folder.toFile().getAbsolutePath() + "/test";
            assertFalse(new File(file).exists());
            FileResource.createDirectory(file);
            assertTrue(new File(file).exists());
            assertTrue(new File(file).isDirectory());
        }

        @Test
        void fromNull() {
            assertThrows(AssertionError.class, () -> FileResource.createDirectory((Path)null));
        }

        @Test
        void alreadyExisting() throws IOException {
            Path folder = Files.createTempDirectory("test");
            String file = folder.toFile().getAbsolutePath() + "/";
            assertTrue(new File(file).exists());
            FileResource.createDirectory(file);
            assertTrue(new File(file).exists());
            assertTrue(new File(file).isDirectory());
        }
    }

    @Nested
    class createResource {

        @Test
        void file() {
            String file = getFile("test-resource.txt").getAbsolutePath();
            assertTrue(new File(file).exists());
            long size = new File(file).length();
            FileResource f = FileResource.createFileResource(file, FileResource.FileType.FILE);
            assertTrue(new File(file).exists());
            assertTrue(new File(file).isFile());
            assertEquals(size, f.getSize());
        }

        @Test
        void directory() throws IOException {
            Path folder = Files.createTempDirectory("test");
            String path = folder.toFile().getAbsolutePath() + "/";
            String file = path + "/test";
            assertFalse(new File(file).exists());
            FileResource.createFileResource(file, FileResource.FileType.DIRECTORY);
            assertTrue(new File(file).exists());
            assertTrue(new File(file).isDirectory());
        }

        @Test
        void fromNullName() {
            assertThrows(AssertionError.class, () -> FileResource.createFileResource((Path)null, FileResource.FileType.FILE));
        }

        @Test
        void fromNullType() {
            assertThrows(AssertionError.class, () -> FileResource.createFileResource("ok", null));
        }
    }

    @Nested
    class ListFile {

        @Test
        void happyFlow() throws IOException{
            String file = getFile("fileresource-listfiles").getAbsolutePath();
            FileResource f = FileResource.findResource(file);
            List<FileResource> result = f.listFile();
            assertEquals(2, result.size());
            List<String> names = result.stream()
                    .map(FileResource::getName)
                    .collect(Collectors.toList());
            assertTrue(names.contains(file + File.separator + "file1.txt")
                    && names.contains(file + File.separator + "file2.txt"));
        }

        @Test
        void withIgnoredFile() throws IOException {
            String file = getFile("fileresource-listfiles").getAbsolutePath();
            FileResource f = FileResource.findResource(file);
            List<FileResource> result = f.listFile( "file1.txt");
            assertEquals(1, result.size());
            assertEquals(file + File.separator + "file2.txt", result.get(0).getName());
        }
    }
}
