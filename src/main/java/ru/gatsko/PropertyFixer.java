package main.java.ru.gatsko;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PropertyFixer {
    private static List<String> keys = new ArrayList<>();

    //translates keys to lowercase with dot-separator
    private static String fixKey(String k) {
        return k.toLowerCase().replaceAll("_", ".");
    }

    //reads property files, search and save keys and replace them by calling fixKey method
    private static void fixProperty(String path) {
        Path propertyPath = Paths.get(path);

        try (BufferedReader reader = Files.newBufferedReader(propertyPath)) {
            String content = new String(Files.readAllBytes(propertyPath));

            for (String line; (line = reader.readLine()) != null;) {
                String key = line.split("=")[0].trim();
                keys.add(key);
                String token = fixKey(key);
                content = content.replaceAll(key, token);
            }

            Files.write(propertyPath, content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //reads directory path, search for .java files, search and replace keys in files
    private static void fixFiles(String dir) {
        Path path1 = Paths.get(dir);

        try (Stream<Path> entries = Files.find(path1, Integer.MAX_VALUE, (path, attrs) -> path.toString().endsWith(".java"))) {
            entries.forEach(path -> {
                try {
                    String content = new String(Files.readAllBytes(path));

                    for (String key : keys) {
                        content = content.replaceAll(key, fixKey(key));
                    }

                    Files.write(path, content.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //one-click fix property file and java-files
    public static void fix(String propertyPath, String filesDirPath) {
        fixProperty(propertyPath);

        fixFiles(filesDirPath);
    }

    public static void main(String[] args) {
        fix("src/main/resources/properties", "src");
    }
}
