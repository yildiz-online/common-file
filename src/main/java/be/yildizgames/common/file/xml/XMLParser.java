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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * Helper class to get Document from an XML file.
 *
 * @author Grégory Van den Borre
 */
public final class XMLParser {

    /**
     * Factory to create Document builders.
     */
    private static DocumentBuilderFactory factory;

    /**
     * Factory to create Documents.
     */
    private static DocumentBuilder documentFactory;

    static {
        XMLParser.factory = DocumentBuilderFactory.newInstance();
        try {
            XMLParser.documentFactory = XMLParser.factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ResourceCorruptedException(e);
        }
    }

    /**
     * Private constructor, to prevent use.
     */
    private XMLParser() {
        super();
    }

    /**
     * Create an usable Document from a XML file.
     *
     * @param file XML file.
     * @return The generated Document to parse.
     */
    public static Document getDocument(final File file) {
        try {
            return XMLParser.documentFactory.parse(file);
        } catch (SAXException | IOException e) {
            throw new ResourceCorruptedException(e);
        }
    }

    /**
     * Create an usable Document from a XML file and check it with a schema.
     *
     * @param file       XML file.
     * @param schemaFile File to use for validation.
     * @return The generated Document to parse.
     */
    public static Document getDocument(final File file, final File schemaFile) {
        try {
            Document doc = XMLParser.documentFactory.parse(file);
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(new DOMSource(doc));
            return doc;
        } catch (SAXException | IOException e) {
            throw new ResourceCorruptedException(e);
        }
    }

    /**
     * Create an usable Document from a String.
     *
     * @param doc String to use to create the Document.
     * @return The generated Document to parse.
     */
    public static Document getDocument(final String doc) {
        try {
            return XMLParser.documentFactory.parse(new InputSource(new StringReader(doc)));
        } catch (SAXException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Retrieve an element from an XML document.
     *
     * @param doc Document.
     * @param tag Element tag.
     * @return The element value.
     */
    public static String getElement(final Document doc, final String tag) {
        NodeList l = doc.getElementsByTagName(tag);
        if (l.getLength() > 0) {
            return l.item(0).getNodeValue();
        }
        return "";
    }

}
