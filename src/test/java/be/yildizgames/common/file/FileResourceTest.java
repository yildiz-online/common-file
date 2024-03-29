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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for FileResource.
 *
 * @author Grégory Van den Borre
 */
public final class  FileResourceTest {

    @Nested
    class FindResource {

        @Test
        void happyFlow() throws URISyntaxException {
            FileResource.findResource(ResourceUtil.getFileFromClassPath(this.getClass(), "file with space.txt").toString());
        }

        @Test
        void notExisting() {
            assertThrows(IllegalStateException.class, () -> FileResource.findResource("azerty"));
        }

        @Test
        void fromNull() {
            assertThrows(NullPointerException.class, () -> FileResource.findResource(null));
        }
    }

    @Nested
    class createFile {

        @Test
        void happyFlow() throws IOException {
            Path folder = Files.createTempDirectory("test");
            Path file = folder.resolve("test.txt");
            assertFalse(Files.exists(file));
            FileResource.createFile(file);
            assertTrue(Files.exists(file));
            assertTrue(Files.isRegularFile(file));
        }

        @Test
        void fromNull() {
            assertThrows(NullPointerException.class, () -> FileResource.createFile((Path) null));
        }

        @Test
        void alreadyExisting() throws IOException, URISyntaxException {
            Path file = ResourceUtil.getFileFromClassPath(this.getClass(), "test-resource.txt");
            assertTrue(Files.exists(file));
            long size = Files.size(file);
            FileResource f = FileResource.createFile(file);
            assertTrue(Files.exists(file));
            assertTrue(Files.isRegularFile(file));
            assertEquals(size, f.getSize());
        }
    }

    @Nested
    class createDirectory {

        @Test
        void happyFlow() throws IOException {
            Path folder = Files.createTempDirectory("test");
            Path file = folder.resolve("test");
            assertFalse(Files.exists(file));
            FileResource.createDirectory(file);
            assertTrue(Files.exists(file));
            assertTrue(Files.isDirectory(file));
        }

        @Test
        void fromNull() {
            assertThrows(NullPointerException.class, () -> FileResource.createDirectory((Path) null));
        }

        @Test
        void alreadyExisting() throws IOException {
            Path folder = Files.createTempDirectory("test");
            assertTrue(Files.exists(folder));
            FileResource.createDirectory(folder);
            assertTrue(Files.exists(folder));
            assertTrue(Files.isDirectory(folder));
        }
    }

    @Nested
    class createResource {

        @Test
        void file() throws IOException, URISyntaxException {
            Path file = ResourceUtil.getFileFromClassPath(this.getClass(), "test-resource.txt");
            assertTrue(Files.exists(file));
            long size = Files.size(file);
            FileResource f = FileResource.createFileResource(file, FileResource.FileType.FILE);
            assertTrue(Files.exists(file));
            assertTrue(Files.isRegularFile(file));
            assertEquals(size, f.getSize());
        }

        @Test
        void directory() throws IOException {
            Path folder = Files.createTempDirectory("test");
            Path file = folder.resolve("test");
            assertFalse(Files.exists(file));
            FileResource.createFileResource(file, FileResource.FileType.DIRECTORY);
            assertTrue(Files.exists(file));
            assertTrue(Files.isDirectory(file));
        }

        @Test
        void fromNullName() {
            assertThrows(NullPointerException.class, () -> FileResource.createFileResource((Path) null, FileResource.FileType.FILE));
        }

        @Test
        void fromNullType() {
            assertThrows(NullPointerException.class, () -> FileResource.createFileResource("ok", null));
        }
    }

    @Nested
    class ListFile {

        @Test
        void happyFlow() throws IOException, URISyntaxException {
            Path file = ResourceUtil.getFileFromClassPath(this.getClass(), "fileresource-listfiles");
            FileResource f = FileResource.findResource(file.toString());
            List<FileResource> result = f.listFile();
            assertEquals(2, result.size());
            List<String> names = result.stream()
                    .map(FileResource::getName)
                    .collect(Collectors.toList());
            assertTrue(names.contains(file + FileSystems.getDefault().getSeparator() + "file1.txt")
                    && names.contains(file + FileSystems.getDefault().getSeparator() + "file2.txt"));
        }

        @Test
        void withIgnoredFile() throws IOException, URISyntaxException {
            Path file = ResourceUtil.getFileFromClassPath(this.getClass(), "fileresource-listfiles");
            FileResource f = FileResource.findResource(file.toString());
            List<FileResource> result = f.listFile("file1.txt");
            assertEquals(1, result.size());
            assertEquals(file + FileSystems.getDefault().getSeparator() + "file2.txt", result.get(0).getName());
        }
    }

    @Nested
    class FileTypeEnum {

        @Test
        void happyFlow() {
            Assertions.assertEquals(0, FileResource.FileType.FILE.value);
            Assertions.assertEquals(1, FileResource.FileType.ZIP.value);
            Assertions.assertEquals(2, FileResource.FileType.VFS.value);
            Assertions.assertEquals(3, FileResource.FileType.DIRECTORY.value);
        }
    }

}
