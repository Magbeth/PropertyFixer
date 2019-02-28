package test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test1 {
    static String str = "create.offlineuser.error";
    String str1 = "create.offlineuser.error_2";
    String str2 = "create.offlineuser.error_3";
    public static void main(String[] args) {
        try {
            Files.write(Paths.get("src/main/resources/properties"), "create.offlineuser.error = User ID, Password and Online Password can not be empty. While creating Offline User".getBytes());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
}
