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

import be.yildizgames.common.exception.implementation.ImplementationException;
import be.yildizgames.common.util.StringUtil;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Grégory Van den Borre
 */
public class ResourcePath {

    private final String name;

    private final String path;

    private final FileResource.FileType type;

    private ResourcePath(String name, String path, FileResource.FileType type) {
        super();
        ImplementationException.throwForNull(name);
        ImplementationException.throwForNull(path);
        ImplementationException.throwForNull(type);
        this.name = name;
        this.path = path;
        this.type = type;
    }

    public static ResourcePath vfs(String name, String path) {
        return new ResourcePath(name, path, FileResource.FileType.VFS);
    }

    public static ResourcePath directory(String name, String path) {
        return new ResourcePath(name, path, FileResource.FileType.DIRECTORY);
    }

    public static ResourcePath currentDirectory(String name) {
        return new ResourcePath(name, Paths.get("").toAbsolutePath().toString(), FileResource.FileType.DIRECTORY);
    }

    public static ResourcePath currentDirectory() {
        return currentDirectory(StringUtil.buildRandomString("resourcePath"));
    }

    public final String getName() {
        return this.name;
    }

    public final String getPath() {
        return this.path;
    }

    public final FileResource.FileType getType() {
        return this.type;
    }

    public boolean exists() {
        return Files.exists(Paths.get(this.path));
    }

    public boolean exists(String file) {
        assert file != null;
        return this.type == FileResource.FileType.VFS || Files.exists(Paths.get(this.path, file));
    }
}
