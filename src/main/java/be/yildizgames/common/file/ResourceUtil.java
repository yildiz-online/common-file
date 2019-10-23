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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provide streams with a platform independent encoding.
 *
 * @author Grégory Van den Borre
 */
public final class ResourceUtil {

    private static final System.Logger LOGGER = System.getLogger(ResourceUtil.class.getName());

    /**
     * Encoding to use.
     */
    private static final Charset ENCODING = StandardCharsets.UTF_8;

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
        Objects.requireNonNull(path);
        Path file = Paths.get(path);
        if (Files.exists(file) && !Files.isDirectory(file)) {
            throw new IllegalStateException("The file " + file + " already exists in this directory");
        }
        if (Files.notExists(file)) {
            try {
                Files.createDirectories(file);
            } catch (IOException e) {
                throw new IllegalStateException("Directories were not created successfully for " + path, e);
            }
        }
    }

    public static void createDirectory(final Path path) {
        createDirectoryTree(path.toAbsolutePath().toString());
    }

    private static void deleteDirectoryTree(final File folder) throws IOException {
        //TODO cleanup, use only NIO api
        try(Stream<Path> path = Files.walk(Paths.get(folder.toURI()))) {
            List<File> files = path.map(Path::toFile).collect(Collectors.toList());
            for (File f : files) {
                if (f.isDirectory()) {
                    ResourceUtil.deleteDirectoryTree(f);
                } else {
                    if (!f.delete()) {
                        throw new IllegalStateException("Cannot delete " + f.toPath());
                    }
                }
            }
            if (!folder.delete()) {
                throw new IllegalStateException("Cannot delete " + folder.toPath());
            }
        }
    }

    /**
     * Delete a folder and its content.
     *
     * @param folder Folder to delete.
     */
    public static void deleteDirectoryTree(final Path folder) {
        try {
            deleteDirectoryTree(folder.toFile());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot delete " + folder);
        }
    }

    public static String decode(String string) {
        return URLDecoder.decode(string, ResourceUtil.ENCODING);
    }

    public static Path getFileFromClassPath(Class clazz, String name) throws URISyntaxException {
        return Paths.get(clazz.getClassLoader().getResource(name).toURI()).toAbsolutePath();
    }

    public static Stream<Path> getFilesInDirectory(Path directory) {
        if(Files.exists(directory)) {
            try {
                return Files.walk(directory);
            } catch (IOException e) {
                LOGGER.log(System.Logger.Level.ERROR, e);
            }
        } else {
            LOGGER.log(System.Logger.Level.WARNING, "Directory {0} does not exists.", directory);
        }
        return Stream.<Path>builder().build();
    }

    public static List<String> readAllLines(Path file) {
        if(Files.notExists(file)) {
            LOGGER.log(System.Logger.Level.WARNING, "File {0} does not exists", file);
            return List.of();
        }
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Error reading file", e);
            return List.of();
        }
    }

    public static void writeAllLines(Path path, List<String> lines) {
        if(Files.notExists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                LOGGER.log(System.Logger.Level.ERROR, e);
            }
        }
        try {
            if(Files.notExists(path)) {
                Files.createFile(path);
            }
            Files.writeString(path, lines.stream().collect(Collectors.joining("\n", "", "")), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        }
    }

    public static void addLine(Path file, String line) {
        try {
            Files.writeString(file, line, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        }
    }
}

