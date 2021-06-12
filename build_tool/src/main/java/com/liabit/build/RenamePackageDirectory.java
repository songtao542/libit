package com.liabit.build;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class RenamePackageDirectory {

    static void renameDir(File file, String oldName, String newName) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    renameDir(f, oldName, newName);
                }
            }
            if (file.getName().equals(oldName)) {
                String[] dirs = newName.split("\\.");
                if (dirs.length > 1) {
                    File newDir = new File(file.getParentFile(), dirs[0]);
                    boolean success = newDir.mkdirs();
                    if (!success) {
                        throw new RuntimeException("创建文件夹失败：" + newDir.getAbsolutePath());
                    }
                    File renameDir = new File(file.getParentFile(), dirs[1]);
                    //noinspection ResultOfMethodCallIgnored
                    file.renameTo(renameDir);
                    FileUtils.moveToDirectory(renameDir, newDir, true);
                } else {
                    //noinspection ResultOfMethodCallIgnored
                    file.renameTo(new File(file.getParentFile(), newName));
                }
            }
        }
    }

}
