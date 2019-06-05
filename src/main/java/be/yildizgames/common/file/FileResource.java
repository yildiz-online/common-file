/*
 * This file is part of the Yildiz-Engine project, licenced under the MIT License  (MIT)
 *
 * Copyright (c) 2019 Grégory Van den Borre
 *
 * More infos available: https://engine.yildiz-games.be
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.CRC32;

/**
 * A file resource represent a file and provide functions to manipulate it.
 *
 * @author Grégory Van den Borre
 */
public final class FileResource {

    /**
     * Associated File object.
     */
    private Path file;

    /**
     * Path and name of the file.
     */
    private String name;

    /**
     * CRC32 value.
     */
    private long crc32;

    /**
     * Create a new empty object, private to prevent usage.
     */
    private FileResource() {
        super();
    }

    public static FileResource createFile(String name) {
        return createFileResource(name, FileType.FILE);
    }

    public static FileResource createFile(Path path) {
        return createFileResource(path, FileType.FILE);
    }

    public static FileResource createDirectory(String name) {
        return createFileResource(name, FileType.DIRECTORY);
    }

    public static FileResource createDirectory(Path directory) {
        return createFileResource(directory, FileType.DIRECTORY);
    }

    public static FileResource createFileResource(final Path path, final FileType type) {
        return createFileResource(path.toAbsolutePath().toString(), type);
    }

    public static FileResource createFileResource(final String name, final FileType type) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        FileResource resource = new FileResource();
        String sanitizedName = ResourceUtil.decode(name);
        resource.name = sanitizedName;
        resource.file = Paths.get(sanitizedName);
        if (resource.exists()) {
            return resource;
        }
        try {
            Files.createDirectories(resource.file.getParent());
            if (type == FileType.DIRECTORY) {
                Files.createDirectories(resource.file);
            } else if (type == FileType.FILE) {
                Files.createDirectories(resource.file.getParent());
                Files.createFile(resource.file);
            }
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException("The file " + resource.file.toAbsolutePath().toString() + " could not be created.", e);
        }
        return resource;
    }

    public static FileResource findResource(final String name) {
        Objects.requireNonNull(name);
        FileResource resource = new FileResource();
        String sanitizedName = ResourceUtil.decode(name);
        resource.file = Paths.get(sanitizedName);
        resource.name = sanitizedName;
        if (!resource.exists()) {
            throw new IllegalStateException("The file " + resource.file.toAbsolutePath().toString() + " does not exist.");
        }
        return resource;
    }

    /**
     * Check if the String received(the string must match the
     * {@link FileResource#toString()}) contains the same values as this object.
     * Otherwise Exceptions will be thrown.
     *
     * @param expected Validation string.
     */
    public void check(final String expected) {
        if (this.crc32 == 0) {
            this.crc32 = this.computeCrc();
        }
        String[] values = expected.split("_");
        long expectedCrc = Long.parseLong(values[1]);
        long expectedSize = Long.parseLong(values[2]);
        if (!this.exists()) {
            throw new IllegalStateException("File does not exists");
        } else if (this.getSize() != expectedSize) {
            throw new IllegalStateException("Size does not match");
        } else if (this.crc32 != expectedCrc) {
            throw new IllegalStateException("Crc32 does not match");
        }
    }

    /**
     * Compute the file CRC32.
     *
     * @return The computed value.
     */
    private long computeCrc() {
        CRC32 c = new CRC32();
        c.update(this.getBytesFromFile());
        return c.getValue();
    }

    /**
     * Delete the file on the hard disk and reset all attributes in this object.
     */
    public void deleteFile() throws IOException{
        Files.delete(this.file);
        this.name = "";
        this.crc32 = 0;
    }

    /**
     * @return This file name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The size of this file.
     */
    public long getSize() {
        try {
            return Files.size(this.file);
        } catch (IOException e) {
            return 0L;
        }
    }

    /**
     * Check if the file is present on the hard disk.
     *
     * @return True if the file exists.
     */
    public boolean exists() {
        return Files.exists(this.file);
    }

    /**
     * Get the file absolute path.
     *
     * @return The file absolute path.
     */
    public String getAbsolutePath() {
        return this.file.toAbsolutePath().toString();
    }

    /**
     * Build a byte[] from the file wrapped in this object.
     *
     * @return the byte[] if not problem occurred, null otherwise.
     */
    public byte[] getBytesFromFile() {
        try (BufferedInputStream is = ResourceUtil.getInputStream(this.file)) {
            if (this.getSize() > Integer.MAX_VALUE) {
                throw new IllegalStateException("File too large");
            }

            byte[] bytes = new byte[(int) this.getSize()];
            int offset = 0;
            int numRead;
            while (true) {
                numRead = is.read(bytes, offset, bytes.length - offset);
                if (offset < bytes.length && numRead >= 0) {
                    offset += numRead;
                } else {
                    break;
                }
            }

            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + this.file.toString());
            }
            return bytes;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @return The file crc32.
     */
    public long getCrc32() {
        if (this.crc32 == 0) {
            return this.computeCrc();
        }
        return this.crc32;
    }

    /**
     * List all files contained in this folder.
     *
     * @param toIgnore If the file name contains this value, it will be ignored.
     * @throws IOException If an exception occurs during the search.
     * @return The list of found files.
     */
    public List<FileResource> listFile(final String... toIgnore) throws IOException {
        List<FileResource> files = new ArrayList<>();
        this.listFile(files, toIgnore);
        return files;
    }

    /**
     * Rename the file or move it if the path is changed. This only work if the
     * new name is in the same physical drive.
     *
     * @param newName New name and path of the file.
     * @return True if completed successfully.
     */
    public boolean rename(final String newName) {
        try {
            Path newFile = Paths.get(newName).toAbsolutePath();
            Files.createDirectories(newFile.getParent());
            Files.move(this.file, newFile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result;
        int value = 0;
        if (this.name != null) {
            value = this.unifyName(this.name).hashCode();
        }
        result = prime * result + value;
        result = prime * result;
        return result;
    }

    /**
     * Check if an other object is equals to this file, first check is made on
     * memory address, then on object class, then on crc and finally on name
     * ignoring the '\' and '/' to get equality on different systems.
     *
     * @param obj Other object to test.
     * @return <code>true</code> If the two objects are considered equals,
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FileResource)) {
            return false;
        }
        FileResource other = (FileResource) obj;
        if (this.crc32 != other.crc32) {
            return false;
        }
        if (!this.name.equals(other.name)) {
            String s1 = this.unifyName(this.name);
            String s2 = this.unifyName(other.name);
            if (!s1.equals(s2)) {
                return false;
            }
        }
        return this.getSize() == other.getSize();
    }

    @Override
    public String toString() {
        return this.name + "_" + this.crc32 + "_" + this.getSize();
    }

    /**
     * Remove OS dependent path char from the file name.
     *
     * @param toUnify File name.
     * @return The name with all '\\' and '/' char removed.
     */
    private String unifyName(final String toUnify) {
        return toUnify.contains("\\") ? toUnify.replace("\\", "") : toUnify.replace("/", "");
    }

    /**
     * List all files contained in this folder.
     *
     * @param files    List to fill with result.
     * @param toIgnore If the file name contains this value, it will be ignored.
     * @throws IOException If an exception occurs during the search.
     */
    private void listFile(final List<FileResource> files, final String... toIgnore) throws IOException {
        Path folder = Paths.get(this.getName());
        DirectoryStream.Filter<Path> filter = entry -> {
            if(toIgnore == null) {
                return true;
            }
            for(String s : toIgnore) {
                if (entry.toString().contains(s)) {
                    return false;
                }
            }
            return true;
        };
        try (DirectoryStream<Path> directory = Files.newDirectoryStream(folder, filter)) {
            for (Path p : directory) {
                if (p.toFile().isDirectory()) {
                    FileResource.findResource(p.toString()).listFile(files, toIgnore);
                } else {
                    files.add(FileResource.findResource(p.toString()));
                }
            }
        }
    }

    public enum FileType {
        FILE(0), DIRECTORY(3), VFS(2), ZIP(1);

        /**
         * Associated value to avoid to depend on the natural order.
         */
        public final int value;

        /**
         * Constructor set the value.
         *
         * @param value Associated value.
         */
        FileType(final int value) {
            this.value = value;
        }
    }
}
