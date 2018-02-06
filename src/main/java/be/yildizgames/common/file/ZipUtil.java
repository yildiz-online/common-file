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

import java.io.*;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Utility class to use zip files.
 *
 * @author Grégory Van den Borre
 */
public final class ZipUtil {

    /**
     * Size of the buffer to use.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * Simple constructor, private to prevent use.
     */
    private ZipUtil() {
        super();
    }

    /**
     * Logic to extract the file.
     *
     * @param in  Zip input stream.
     * @param out File output stream.
     * @throws IOException If Exception occurs during the copy.
     */
    private static void extractFile(final InputStream in, final OutputStream out) throws IOException {
        byte[] buf = new byte[ZipUtil.BUFFER_SIZE];
        int l;
        while ((l = in.read(buf)) >= 0) {
            out.write(buf, 0, l);
        }
        in.close();
        out.close();
    }

    /**
     * Extract a directory and all its content from a zip file.
     *
     * @param zipFile     Zip file to extract the data from.
     * @param destination Path where the directory will be extracted.
     * @param keepRootDir Keep the root directory or extract all its content.
     * @throws ZipException If the zip file does not exists.
     */
    public static void extractFiles(final File zipFile, final String destination, final boolean keepRootDir) {
        try (ZipFile file = new ZipFile(URLDecoder.decode(zipFile.getAbsolutePath(), "UTF-8"))) {
            String rootDir = "";
            ResourceUtil.createDirectoryTree(destination);
            Enumeration<? extends ZipEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipentry = entries.nextElement();
                if (zipentry.isDirectory()) {
                    if (!keepRootDir && rootDir.isEmpty()) {
                        rootDir = zipentry.getName();
                    } else {
                        ResourceUtil.createDirectoryTree(destination + File.separator + zipentry.getName().replace(rootDir, ""));
                    }
                } else {
                    File current;
                    if (keepRootDir) {
                        current = new File(destination + File.separator + zipentry.getName());
                    } else {
                        current = new File(destination + File.separator + zipentry.getName().replace(rootDir, ""));
                    }
                    try(InputStream in = file.getInputStream(zipentry); OutputStream out = new FileOutputStream(current)) {
                        ZipUtil.extractFile(in, out);
                    }
                }
            }
        } catch (IOException ioe) {
            throw new ZipException(ioe);
        }
    }

    /**
     * Extract a directory and all its content from a zip file.
     *
     * @param zipFile     Zip file to extract the data from.
     * @param directory   Directory to extract.
     * @param destination Path where the directory will be extracted.
     * @throws ZipException If the zip file does not exists.
     */
    public static void extractFilesFromDirectory(final File zipFile, final String directory, final String destination) {
        try (ZipFile file = new ZipFile(URLDecoder.decode(zipFile.getAbsolutePath(), "UTF-8"))) {
            ResourceUtil.createDirectoryTree(destination + File.separator + directory);
            Enumeration<? extends ZipEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipentry = entries.nextElement();
                if (zipentry.getName().replace("/", File.separator).startsWith(directory + File.separator)) {
                    if (zipentry.isDirectory()) {
                        ResourceUtil.createDirectory(zipentry.getName());
                    } else {
                        File current = new File(destination + File.separator + zipentry.getName());
                        try(InputStream in = file.getInputStream(zipentry); OutputStream out = new FileOutputStream(current)) {
                            ZipUtil.extractFile(in, out);
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            throw new ZipException(ioe);
        }
    }
}
