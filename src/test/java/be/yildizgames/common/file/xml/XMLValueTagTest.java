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

package be.yildizgames.common.file.xml;


import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Grégory Van den Borre
 */
final class XMLValueTagTest {

    @Test
    void testGenerate() {
        XMLTag tag = new XMLValueTag("a name", "a value");
        String result = "<a name>a value</a name>";
        assertEquals(result, tag.generate(new StringBuilder()));
    }

    @Test
    void testXMLValueTagName() {
        XMLTag tag = new XMLValueTag("name", "value");
        assertEquals("name", tag.getName());
    }

    @Test
    void testXMLValueTagName1() {
        assertThrows(InvalidParameterException.class, () -> new XMLValueTag("a<a", "value"));
    }

    @Test
    void testXMLValueTagName2() {
        assertThrows(InvalidParameterException.class, () -> new XMLValueTag("a>a", "value"));
    }

    @Test
    void testXMLValueTagName3() {
        assertThrows(InvalidParameterException.class, () -> new XMLValueTag("a&a", "value"));
    }

    @Test
    void testXMLValueTagName4() {
        assertThrows(InvalidParameterException.class, () -> new XMLValueTag("a'a", "value"));
    }

    @Test
    void testXMLValueTagName5() {
        assertThrows(InvalidParameterException.class, () -> new XMLValueTag("a\"a", "value"));
    }

    @Test
    void testXMLValueTagNameNull() {
        assertThrows(NullPointerException.class, () -> new XMLValueTag(null, "value"));
    }

    @Test
    void testXMLValueTagValue() {
        assertThrows(NullPointerException.class, () -> new XMLValueTag("name", null));
    }

    @Test
    void testXMLValueTagValue1() {
        assertThrows(InvalidParameterException.class, () -> new XMLValueTag("name", "a<a"));
    }

    @Test
    void testXMLValueTagValue2() {
        assertThrows(InvalidParameterException.class, () -> new XMLValueTag("name", "a>a"));
    }

    @Test
    void testXMLValueTagValue3() {
        assertThrows(InvalidParameterException.class, () -> new XMLValueTag("name", "a&a"));
    }

    @Test
    void testXMLValueTagValue4() {
        assertThrows(InvalidParameterException.class, () -> new XMLValueTag("name", "a'a"));
    }

    @Test
    void testXMLValueTagValue5() {
        assertThrows(InvalidParameterException.class, () -> new XMLValueTag("name", "a\"a"));
    }

}
