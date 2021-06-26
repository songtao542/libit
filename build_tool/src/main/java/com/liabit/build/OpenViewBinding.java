package com.liabit.build;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class OpenViewBinding {

    public static void openViewBinding(File buildGradle, boolean open) {
        byte[] backup = null;
        try {
            backup = FileUtils.readFileToByteArray(buildGradle);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(buildGradle), StandardCharsets.UTF_8));
            CharArrayWriter caw = new CharArrayWriter();
            String line;
            String lineSeparator = System.getProperty("line.separator");
            String dataBinding = "        dataBinding " + (open ? "true" : "false");
            String viewBinding = "        viewBinding " + (open ? "true" : "false");
            while ((line = br.readLine()) != null) {
                if (line.contains("dataBinding")) {
                    caw.write(dataBinding);
                    caw.append(lineSeparator);
                } else if (line.contains("viewBinding")) {
                    caw.write(viewBinding);
                    caw.append(lineSeparator);
                } else {
                    caw.write(line);
                    caw.append(lineSeparator);
                }
            }
            br.close();
            FileWriter fw = new FileWriter(buildGradle);
            caw.writeTo(fw);
            fw.close();
        } catch (Throwable e) {
            e.printStackTrace();
            if (backup != null && backup.length > 0) {
                try {
                    FileUtils.writeByteArrayToFile(buildGradle, backup);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

}
