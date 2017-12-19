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

import be.yildizgames.common.exception.technical.ResourceMissingException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public class FileProperties {

    /**
     * Get a property object from a file, and override the values retrieved with the one from args parameter.
     * This is typically to be used with the main method.
     *
     * @param file Physical file containing the properties.
     * @param args Array of key=values to override the content retrieved from the file.
     * @return The properties from the file.
     * @throws ResourceMissingException if the file does not exists.
     */
    //@Requires ("file != null")
    //@Ensures ("result != null")
    static Properties getPropertiesFromFile(final File file, final String... args) {
        final Properties properties = new Properties();
        try (Reader reader = ResourceUtil.getFileReader(file)) {
            properties.load(reader);
        } catch (IOException ioe) {
            throw new ResourceMissingException("Error while reading property file: " + file.getAbsolutePath(), ioe);
        }
        if (args == null) {
            return properties;
        }
        for (String pair : args) {
            if (pair != null && pair.contains("=")) {
                String[] values = pair.split("=");
                if (properties.containsKey(values[0])) {
                    properties.setProperty(values[0], values[1]);
                }
            }
        }
        return properties;
    }

    /**
     * Save the content of a properties in a file.
     *
     * @param p Properties to save.
     * @param f File to use.
     * @throws FileCreationException If the file cannot be written.
     */
    //@Requires ("p != null")
    //@Requires ("f != null")
    //@Requires ("f.exists() == true")
    static void save(final Properties p, final File f) {
        try {
            if (f.getParentFile() != null && !f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            if (!f.exists() && !f.createNewFile()) {
                throw new FileCreationException("Cannot create property file:" + f.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new FileCreationException("Cannot create property file:" + f.getAbsolutePath(), e);
        }
        try (Writer fileWriter = ResourceUtil.getFileWriter(f)) {
            p.store(fileWriter, "");
        } catch (IOException e) {
            throw new FileCreationException("Configuration could not be saved in file " + f.getAbsolutePath(), e);
        }
    }
}
