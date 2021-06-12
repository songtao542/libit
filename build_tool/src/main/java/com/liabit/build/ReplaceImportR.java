package com.liabit.build;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ReplaceImportR {
    public static void replace(File file, String replace, String replacement) throws IOException {
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
                String importR = replacement + (isJava ? ";" : "");
                String lineSeparator = System.getProperty("line.separator");
                while ((line = br.readLine()) != null) {
                    boolean isPackageRow = line.contains("package ");
                    if (isPackageRow) {
                        // 如果是 package 所在行，则在该行之后添加 import R 语句
                        caw.write(line);
                        caw.append(lineSeparator).append(lineSeparator);
                        caw.write(importR);
                    } else {
                        // 如果是 import R 的行，则直接跳过
                        if (line.matches(replace)) {
                            continue;
                        }
                        //"import com\\.liabit\\..*\\.R"
                        if (line.contains("import") && line.contains("com.zhihu.matisse.R")) {
                            continue;
                        } else if (line.contains("com.zhihu.matisse.R")) {
                            String rp = replacement.replace("import ", "");
                            line = line.replace("com.zhihu.matisse.R", rp);
                        }
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
