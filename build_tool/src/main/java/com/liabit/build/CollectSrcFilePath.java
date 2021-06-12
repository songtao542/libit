package com.liabit.build;

import java.io.File;
import java.util.HashMap;

public class CollectSrcFilePath {
    static void collect(String packagePath, File file, HashMap<String, String> resultMap) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    collect(packagePath, f, resultMap);
                }
            }
        } else {
            boolean isJava = file.getAbsolutePath().endsWith(".java");
            boolean isKt = file.getAbsolutePath().endsWith(".kt");
            if (isJava || isKt) {
                String absPath = file.getAbsolutePath();
                int index = absPath.indexOf(packagePath);
                if (index >= 0) {
                    String path = absPath.substring(index)
                            .replace(File.separator, ".")
                            .replace(".kt", "")
                            .replace(".java", "");
                    String srcName = file.getName()
                            .replace(".kt", "")
                            .replace(".java", "");
                    System.out.println("file path: " + srcName + " -> " + path);
                    resultMap.put(srcName, path);
                }
            }
        }
    }
}
