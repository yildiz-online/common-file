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

import java.security.InvalidParameterException;

/**
 * @author Grégory Van den Borre
 */
public final class XMLValueTag extends XMLTag {

    /**
     * Value stored in this tag.
     */
    private final String value;

    /**
     * Build a new value tag from a long.
     *
     * @param name  Tag name.
     * @param value Long value.
     */
    public XMLValueTag(final String name, final long value) {
        this(name, String.valueOf(value));
    }

    /**
     * Build a new value tag from an object.
     *
     * @param name  Tag name.
     * @param value Object value.
     */
    public XMLValueTag(final String name, final Object value) {
        this(name, value.toString());
    }

    /**
     * Build a new value tag.
     *
     * @param name  Tag name.
     * @param value Value.
     */
    public XMLValueTag(final String name, final String value) {
        super(name);
        if (value.contains("<") || value.contains(">") || value.contains("&") || value.contains("\"") || value.contains("'")) {
            throw new InvalidParameterException(value + "contains XML forbidden character(<,>,&,\",'");
        }
        this.value = value;
    }

    @Override
    public String generate(final StringBuilder builder) {
        builder.append("<");
        builder.append(this.getName());
        builder.append(">");
        builder.append(this.value);
        builder.append("</");
        builder.append(this.getName());
        builder.append(">");
        return builder.toString();
    }

}
