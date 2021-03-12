/*
 * Copyright (c) 2019, 2020, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.javafx.scenebuilder.kit.library.util;

import com.oracle.javafx.scenebuilder.kit.library.util.JarReportEntry.Status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FolderExplorer extends ExplorerBase {

    private final Path rootFolderPath;

    public FolderExplorer(Path folderPath) {
        assert folderPath != null;
        assert folderPath.isAbsolute();

        this.rootFolderPath = folderPath;
    }

    public JarReport explore(ClassLoader classLoader) throws IOException {
        final JarReport result = new JarReport(rootFolderPath);

        try (Stream<Path> stream = Files.walk(rootFolderPath).filter(p -> !p.toFile().isDirectory())) {
            stream.forEach(p -> {
                JarReportEntry explored = exploreEntry(rootFolderPath, p, classLoader);
                if (explored.getStatus() != Status.IGNORED)
                    result.getEntries().add(explored);
            });
        };

        return result;
    }

    /*
     * Private
     */

    private JarReportEntry exploreEntry(Path rootpath, Path path, ClassLoader classLoader) {
        File file = path.toFile();

        if (file.isDirectory()) {
            return new JarReportEntry(file.getName(), Status.IGNORED, null, null, null);
        } else {
            Path relativepath = rootpath.relativize(path);

            String className = makeClassName(relativepath.toString(), File.separator);
            return super.exploreEntry(file.getName(), classLoader, className);
        }
    }

}