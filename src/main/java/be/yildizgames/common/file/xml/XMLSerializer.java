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

package be.yildizgames.common.file.xml;


import be.yildizgames.common.exception.technical.ResourceCorruptedException;
import be.yildizgames.common.exception.technical.ResourceMissingException;
import be.yildizgames.common.file.FileCreationException;
import be.yildizgames.common.file.Serializer;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.MissingResourceException;

/**
 * Use XML to serialize and deserialize objects.
 *
 * @param <T> Object type to use.
 * @author Grégory Van Den Borre
 */
public final class XMLSerializer<T> implements Serializer<T> {

    /**
     * File to use to read/write object.
     */
    private final File file;

    /**
     * Full constructor.
     *
     * @param xmlFile Initialize the file to use.
     */
    public XMLSerializer(final File xmlFile) {
        super();
        this.file = xmlFile;
    }

    /**
     * Throw an exception with file not found.
     */
    private void fileNotFound() {
        throw new ResourceMissingException(this.file + "was not found");
    }

    /**
     * Read a file and deserialize object from it.
     *
     * @return The created object.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T readFromFile() {
        InputStream fis = null;
        try {
            if (!this.file.exists()) {
                this.fileNotFound();
            }
            fis = new FileInputStream(this.file);
        } catch (FileNotFoundException e) {
            this.fileNotFound();
        }
        try (XMLDecoder decode = new XMLDecoder(fis)) {
            decode.setExceptionListener(e -> {
                throw new ResourceCorruptedException("XML configuration file corrupted.");
            });
            return (T) decode.readObject();
        }
    }

    /**
     * Write an object into a XML file.
     *
     * @param o Object to persist.
     */
    @Override
    public void writeToFile(final T o) {
        try (OutputStream fos = new FileOutputStream(this.file)) {
            try (XMLEncoder encoder = new XMLEncoder(fos)) {
                encoder.setExceptionListener(FileCreationException::new);
                encoder.writeObject(o);
            }
        } catch (IOException e) {
            throw new FileCreationException(e);
        }
    }

}
