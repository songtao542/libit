package com.liabit.build;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ReplaceImportBuildConfig {
    static void replace(File file, String replace, String replacement) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    replace(f, replace, replacement);
                }
            }
        } else if (file.exists()) {
            boolean isJava = file.getAbsolutePath().endsWith(".java");
            boolean isKt = file.getAbsolutePath().endsWith(".kt");
            if (isJava || isKt) {
                System.out.println("file: " + file);
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                CharArrayWriter caw = new CharArrayWriter();
                String line;
                String importBuildConfig = replacement + (isJava ? ";" : "");
                String lineSeparator = System.getProperty("line.separator");
                while ((line = br.readLine()) != null) {
                    // 如果是 BuildConfig 的行
                    if (line.matches(replace) || line.matches("import com\\.zhihu\\..*\\.BuildConfig")) {
                        caw.write(importBuildConfig);
                    } else {
                        caw.write(line);
                    }
                    caw.append(lineSeparator);
                }
                br.close();
                FileWriter fw = new FileWriter(file);
                caw.writeTo(fw);
                fw.close();
            }

        }
    }
}
