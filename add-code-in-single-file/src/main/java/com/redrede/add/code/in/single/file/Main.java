/*
 * Copyright 2017 RedRede.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redrede.add.code.in.single.file;

import com.google.common.hash.Hashing;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

/**
 * Class responsible for aggregating code in a single file
 *
 *
 * @author RedRede
 */
public class Main {

    private static final List<Path> FILES = new ArrayList();

    public static void main(String[] args) throws IOException {

        if (args.length > 2) {
            System.out.println("outputFile:" + args[0]);
            System.out.println("projectDir:" + args[1]);
            System.out.println("fileExtension:" + args[2]);
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(args[0], Boolean.TRUE), "utf-8"))) {
                listFiles(Paths.get(args[1]), args[2]);
                for (Path file : FILES) {
                    writer.write("------------------------" + file.toFile().getName() + "------------------------\n");
                    String r = readFile(file);
                    writer.write(r);
                }
            }
            System.out.println("hashAlgorithm: SHA-512");
            System.out.println("hash:" + hash(args[0]));
        } else {
            System.out.println("java -jar add-code-in-single-file.jar outputFile projectDir fileExtension");
        }
    }

    public static String hash(String file) throws IOException {
        return Hashing.sha512().hashString(readFile(Paths.get(file)), StandardCharsets.UTF_8).toString();
    }

    public static String readFile(Path filePath) throws FileNotFoundException, IOException {
        try (FileInputStream inputStream = new FileInputStream(filePath.toFile())) {
            return IOUtils.toString(inputStream, "utf-8");
        }
    }

    public static void listFiles(Path path, String fileExtension) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    listFiles(entry, fileExtension);
                }
                if (fileExtension == null || fileExtension.isEmpty() || FilenameUtils.getExtension(entry.toString()).equals(fileExtension)) {
                    FILES.add(entry);
                }
            }
        }
    }
}
