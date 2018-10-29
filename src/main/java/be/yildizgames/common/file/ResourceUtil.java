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

import be.yildizgames.common.exception.technical.ResourceCorruptedException;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provide streams with a platform independent encoding.
 *
 * @author Grégory Van den Borre
 */
public final class ResourceUtil {

    /**
     * Encoding to use.
     */
    private static final Charset ENCODING = Charset.forName("UTF-8");

    /**
     * Private constructor to prevent use.
     */
    private ResourceUtil() {
        super();
    }

    /**
     * Get a byte array from a string.
     *
     * @param string String to use.
     * @return A byte array built from the string.
     */
    public static byte[] getByteArray(final String string) {
        return string.getBytes(ResourceUtil.ENCODING);
    }

    /**
     * Get a file reader.
     *
     * @param path Path of the file to read.
     * @return An input stream reader to read the file.
     * @throws IOException If the file is not found.
     */
    public static Reader getFileReader(final Path path) throws IOException {
        return Files.newBufferedReader(path, ResourceUtil.ENCODING);
    }


    /**
     * Get a file writer.
     *
     * @param path Path of the file to write.
     * @return An output stream writer to write the file.
     * @throws IOException If the file is not found.
     */
    public static Writer getFileWriter(final Path path) throws IOException {
        return Files.newBufferedWriter(path, ResourceUtil.ENCODING);
    }

    public static BufferedInputStream getInputStream(Path file) throws IOException {
        return new BufferedInputStream(Files.newInputStream(file));
    }

    /**
     * Get a string from a byte array.
     *
     * @param array Byte array.
     * @return A string build from the byte array.
     */
    public static String getString(final byte[] array) {
        return new String(array, ResourceUtil.ENCODING);
    }

    /**
     * Get a string from a ByteArrayOutputStream.
     *
     * @param bos Byte array.
     * @return A string build from the byte array.
     */
    public static String getString(final ByteArrayOutputStream bos) {
        return ResourceUtil.getString(bos.toByteArray());
    }

    public static void createDirectoryTree(final Path path) {
        createDirectoryTree(path.toAbsolutePath().toString());
    }

    public static void createDirectoryTree(final String path) {
        assert path!= null : "Path should not be null.";
        File file = new File(path);
        if(file.exists() && !file.isDirectory()) {
            throw FileCreationException.directoryErrorFileAlreadyExists(path);
        }
        if(!file.exists() && !file.mkdirs()) {
            throw new FileCreationException("Directories were not created successfully for " + path);
        }
    }

    public static void createDirectory(final Path path) {
        createDirectoryTree(path.toAbsolutePath().toString());
    }

    public static void createDirectory(final String path) {
        assert path!= null : "Path should not be null.";
        File file = new File(path);
        if(file.exists() && !file.isDirectory()) {
            throw FileCreationException.directoryErrorFileAlreadyExists(path);
        }
        if(!file.exists() && !file.mkdir()) {
            throw new FileCreationException("Directory was not created successfully for " + path);
        }
    }

    /**
     * Delete a folder and its content.
     *
     * @param folder Folder to delete.
     */
    @Deprecated(since = "1.0.2", forRemoval = true)
    public static void deleteDirectoryTree(final File folder) {
        for (File f : ResourceUtil.listFile(folder)) {
            if (f.isDirectory()) {
                ResourceUtil.deleteDirectoryTree(f);
            } else {
                if(!f.delete()) {
                    throw new FileDeletionException(f.getAbsolutePath() + "has not been deleted properly.");
                }
            }
        }
        if(!folder.delete()) {
            throw new FileDeletionException(folder.getAbsolutePath() + "has not been deleted properly.");
        }
    }

    /**
     * @deprecated Use Files.walk
     * @param file
     * @return
     */
    @Deprecated(since = "1.0.2", forRemoval = true)
    public static List<File> listFile(final File file) {
        File[] files = file.listFiles();
        if(files == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(files);
    }

    public static String decode(String string) {
        //FIXME remove .name() once in java 10+
        try {
            return URLDecoder.decode(string, ResourceUtil.ENCODING.name());
        } catch (UnsupportedEncodingException e) {
            throw new ResourceCorruptedException(e);
        }
    }

    /**
     * Get a file reader.
     * @deprecated Use getFileReader(final Path path) instead.
     * @param path Path of the file to read.
     * @return An input stream reader to read the file.
     */
    @Deprecated(since = "1.0.2", forRemoval = true)
    public static Reader getFileReader(final File path) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(path), ResourceUtil.ENCODING));
    }

    /**
     * Get a file writer.
     * @deprecated Use getFileWriter(final Path path) instead.
     *
     * @param path Path of the file to write.
     * @return An output stream writer to write the file.
     * @throws FileNotFoundException If the file is not found.
     */
    @Deprecated(since = "1.0.2", forRemoval = true)
    public static Writer getFileWriter(final File path) throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), ResourceUtil.ENCODING));
    }
}
