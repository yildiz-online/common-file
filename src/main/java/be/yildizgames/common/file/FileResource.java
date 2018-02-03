/*
 * This file is part of the Yildiz-Engine project, licenced under the MIT License  (MIT)
 *
 * Copyright (c) 2017 Grégory Van den Borre
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

import be.yildizgames.common.exception.technical.ResourceCorruptedException;
import be.yildizgames.common.exception.technical.ResourceMissingException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.CRC32;

/**
 * @author Grégory Van den Borre
 */
public final class FileResource {

    /**
     * Associated File object.
     */
    private File file;
    /**
     * Path and name of the file.
     */
    private String name;

    /**
     * CRC32 value.
     */
    private long crc32;

    /**
     * File size.
     */
    private long size;

    private FileResource() {
        super();
    }

    public static FileResource createFile(String name) {
        return createFileResource(name, FileType.FILE);
    }

    public static FileResource createDirectory(String name) {
        return createFileResource(name, FileType.DIRECTORY);
    }

    public static FileResource createFileResource(final String name, final FileType type) {
        if(type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        FileResource resource = new FileResource();
        String sanitizedName = null;
        try {
            sanitizedName = URLDecoder.decode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ResourceCorruptedException(e);
        }
        resource.name = sanitizedName;
        resource.file = new File(sanitizedName);
        resource.size = resource.file.length();
        if (resource.exists()) {
            return resource;
        }
        try {
            resource.file.getParentFile().mkdirs();
            if (type == FileType.DIRECTORY && !resource.file.mkdir()) {
                throw new ResourceMissingException("File " + sanitizedName + " could not be created");
            } else if (type == FileType.FILE && !resource.file.createNewFile()) {
                throw new ResourceMissingException("Directory " + sanitizedName + " could not be created");
            }
        } catch (IOException | SecurityException e) {
            throw new ResourceMissingException("The file " + resource.file.getAbsolutePath() + " could not be created.", e);
        }
        return resource;
    }

    public static FileResource findResource(final String name) {
        FileResource resource = new FileResource();
        String sanitizedName = null;
        try {
            sanitizedName = URLDecoder.decode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        resource.file = new File(sanitizedName);
        resource.name = sanitizedName;
        if (!resource.exists()) {
            throw new ResourceMissingException("The file " + resource.file.getAbsolutePath() + " does not exist.");
        }
        resource.size = resource.file.length();
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
            throw new ResourceMissingException("File does not exists");
        } else if (this.size != expectedSize) {
            throw new ResourceCorruptedException("Size does not match");
        } else if (this.crc32 != expectedCrc) {
            throw new ResourceCorruptedException("Crc32 does not match");
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
    public void deleteFile() {
        if (this.file.delete()) {
            this.name = "";
            this.crc32 = 0;
            this.size = 0;
        }
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
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
        return this.size == other.size;
    }

    /**
     * Check if the file is present on the hard disk.
     *
     * @return True if the file exists.
     */
    public boolean exists() {
        return this.file.exists();
    }

    /**
     * Get the file absolute path.
     *
     * @return The file absolute path.
     */
    public String getAbsolutePath() {
        return this.file.getAbsolutePath();
    }

    /**
     * Build a byte[] from the file wrapped in this object.
     *
     * @return the byte[] if not problem occurred, null otherwise.
     */
    public byte[] getBytesFromFile() {
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(this.file))) {

            if (this.size > Integer.MAX_VALUE) {
                throw new ResourceCorruptedException("File too large");
            }

            byte[] bytes = new byte[(int) this.size];
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
                throw new IOException("Could not completely read file " + this.file.getName());
            }
            return bytes;
        } catch (IOException e) {
            throw new ResourceCorruptedException(e);
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
     * List all files contained in this folder.
     *
     * @param files    List to fill with result.
     * @param toIgnore If the file name contains this value, it will be ignored.
     * @throws IOException If an exception occurs during the search.
     */
    public void listFile(final List<FileResource> files, final String... toIgnore) throws IOException {
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

    /**
     * Rename the file or move it if the path is changed. This only work if the
     * new name is in the same physical drive.
     *
     * @param newName New name and path of the file.
     * @return True if completed successfully.
     */
    public boolean rename(final String newName) {
        File f = new File(newName);
        return !(f.getParentFile() != null && !f.getParentFile().mkdirs()) && this.file.renameTo(f);
    }

    @Override
    public String toString() {
        return this.name + "_" + this.crc32 + "_" + this.size;
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
