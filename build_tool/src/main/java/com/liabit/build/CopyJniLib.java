package com.liabit.build;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class CopyJniLib {
    static void copy(File jniLibsDir, File destJniLibsDir) {
        File[] files = jniLibsDir.listFiles();
        if (files == null) return;
        for (File file : files) {
            String fileName = file.getName();
            File destJniDir = new File(destJniLibsDir, fileName);
            if (!destJniDir.exists()) {
                boolean result = destJniDir.mkdirs();
                System.out.println("create jni dir: " + destJniDir + " : " + result);
            }
            File[] soFiles = file.listFiles();
            if (soFiles != null) {
                for (File so : soFiles) {
                    try {
                        System.out.println("copy " + so + " to " + destJniDir);
                        FileUtils.copyToDirectory(so, destJniDir);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
