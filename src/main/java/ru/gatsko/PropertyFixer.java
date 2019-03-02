package main.java.ru.gatsko;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PropertyFixer {
    //There is no sense to save non-unique keys, so I replaced List with Set
    private static Set<String> keys = new HashSet<>();

    //translates keys to lowercase with dot-separator
    private static String fixKey(String k) {
        return k.toLowerCase().replaceAll("_", ".");
    }

    private static String replaceKey(String content, String key) {
        Pattern p = Pattern.compile(key + "\\b");
        Matcher m = p.matcher(content);
        content = m.replaceAll(fixKey(key));
        return content;
    }

    //reads property files, search and save keys and replace them by calling fixKey method
    //I removed replacing keys in property file to allow the programm to be reused.
    //If replace keys in original file when adding new .java file to this utility with non-replaced keys it won't work
    //because property file after replacing may not content key which is necessary for programm logic to replace key in java-files/
    //Another way if it is necessary to save keys on disk is to write them in separate file, but I believe there is no necessity
    private static void fixProperty(String path) {
        Path propertyPath = Paths.get(path);

        try (BufferedReader reader = Files.newBufferedReader(propertyPath)) {
//            String content = new String(Files.readAllBytes(propertyPath));
            Stream<String> lines = reader.lines();
            lines
                    .parallel()
                    .map(line -> line.split("=")[0].trim())
                    .forEach(line -> keys.add(line));
//            for (String line; (line = reader.readLine()) != null;) {
//                String key = line.split("=")[0].trim();
//                keys.add(key);
//                content = replaceKey(content, key);
//            }

//            Files.write(Paths.get("src/main/resources/keys"), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //reads directory path, search for .java files, search and replace keys in files
    private static void fixFiles(String dir) {
        Path path1 = Paths.get(dir);

        try (Stream<Path> entries = Files.find(path1, Integer.MAX_VALUE, (path, attrs) -> path.toString().endsWith(".java"))) {
            entries.parallel().forEach(path -> {
                try {
                    String content = new String(Files.readAllBytes(path));

                    for (String key : keys) {
                        content = replaceKey(content, key);
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
    public static void fix(String propertyPath, String...filesDirPath) {
        fixProperty(propertyPath);
        for(String path : filesDirPath) {
            fixFiles(path);
        }
    }

    public static void main(String[] args) {
        fix("src/main/resources/properties", "src");
    }
}
