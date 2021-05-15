package com.liabit.build;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 执行 main 方法将所有模块儿的源码拷贝的 libit 模块儿中，以方便编译出一个完整的 aar
 */
class BuildTool {

    static String[] mergeList = {
            /*"../wallpaper_cropper/src/main",
            "../wallpaper_cropper_lite/src/main",
            "../color_util/src/main",
            "../settings/src/main",
            "../gesture/src/main",
            "../swipeback/src/main",
            "../screenrecord/src/main",
            "../screencapture/src/main",
            "../location_picker/src/main",
            "../tablayout/src/main",
            "../util/src/main",
            "../shimmer/src/main",*/
            "../addsub/src/main",
            "../autoclear/src/main",
            "../photoview/src/main",
            "../picker_integrate/src/main",
            "../citypicker/src/main",
            "../picker/src/main",
            "../numberpicker/src/main",
            "../tagview/src/main",
            "../filterlayout/src/main",
            "../dialog/src/main",
            "../timerview/src/main",
            "../viewbinding/src/main",
            "../recyclerview/src/main",
            "../popup/src/main",
            "../widget/src/main",
            "../ext/src/main",
            "../livedata-ktx/src/main",
            "../injectable-viewmodel/src/main",
            "../base/src/main",
            "../third-auth/src/main",
    };


    public static void main(String[] args) throws DocumentException, IOException {
        String path = BuildTool.class.getResource("").getFile();
        int index = path.indexOf("build_tool");
        String rootPath = path.substring(0, index + "build_tool".length());
        File destDir = new File(rootPath, "../libit/src/main/");
        File resDir = new File(destDir, "res/values/");

        SAXReader saxReader = new SAXReader();
        File attrFile = new File(resDir, "attrs.xml");
        Document attrXml = DocumentHelper.createDocument();
        attrXml.addElement("resources");
        ArrayList<String> attrNames = new ArrayList<>();

        String packageName = "com.liabit.";
        String dirName = "liabit";

        if (!destDir.exists()) {
            boolean result = destDir.mkdirs();
            System.out.println("Create dest dir " + result);
        }
        FileUtils.cleanDirectory(destDir);

        File manifestFile = new File(destDir, "AndroidManifest.xml");

        Document manifestXml = DocumentHelper.createDocument();
        Element manifestRootElement = manifestXml.addElement("manifest");
        manifestRootElement.addAttribute("package", packageName.substring(0, packageName.length() - 1));
        manifestRootElement.addNamespace("android", "http://schemas.android.com/apk/res/android");
        Element manifestApplicationElement = manifestRootElement.addElement("application");
        ArrayList<String> manifestNames = new ArrayList<>();

        if (!manifestFile.exists()) {
            saveXml(manifestXml, manifestFile);
        }

        for (String merge : mergeList) {
            File source = new File(rootPath, merge);
            String name = merge.replace("../", "");
            String destName = name.substring(0, name.indexOf("/"));

            File javaSourceDir = new File(source.getCanonicalFile(), "java");
            File destJavaDir = new File(destDir, destName).getCanonicalFile();
            FileUtils.deleteDirectory(destJavaDir);

            System.out.println(javaSourceDir + " -> " + destJavaDir);
            FileUtils.copyDirectory(javaSourceDir, destJavaDir);

            addImportR(destJavaDir, "import com\\.liabit\\..*\\.R", "import com.liabit.R");
            replaceBuildConfig(destJavaDir, "import com\\.liabit\\..*\\.BuildConfig", "import com.liabit.BuildConfig");

            if (!"com.liabit.".equals(packageName)) {
                // 重命名
                renamePackage(destJavaDir, "com.liabit.", packageName);
            }
            if (!"liabit".equals(dirName)) {
                // 重命名文件夹
                renameDir(destJavaDir, "liabit", dirName);
            }

            File resSourceDir = new File(source.getCanonicalFile(), "res");
            if (resSourceDir.exists()) {
                File destResDir = new File(destDir, destName + "_res").getCanonicalFile();
                FileUtils.deleteDirectory(destResDir);
                System.out.println(resSourceDir + " -> " + destResDir);
                FileUtils.copyDirectory(resSourceDir, destResDir);
                mergeAttr(saxReader, attrXml, destResDir, attrNames);

                if (!"com.liabit.".equals(packageName)) {
                    // 重命名
                    renameXmlPackage(destResDir, "com.liabit.", packageName);
                }
            }
            File manifestSourceFile = new File(source.getCanonicalFile(), "AndroidManifest.xml");

            mergeManifest(saxReader, manifestRootElement, manifestApplicationElement, manifestSourceFile, manifestNames);
        }

        if (!resDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            resDir.mkdirs();
        }
        if (!attrFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            attrFile.createNewFile();
        }
        saveXml(attrXml, attrFile);
        saveXml(manifestXml, manifestFile);
    }

    static void renameDir(File file, String oldName, String newName) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    renameDir(f, oldName, newName);
                }
            }
            if (file.getName().equals(oldName)) {
                //noinspection ResultOfMethodCallIgnored
                file.renameTo(new File(file.getParentFile(), newName));
            }
        }
    }

    static void renamePackage(File file, String replace, String replacement) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    renamePackage(f, replace, replacement);
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
                String lineSeparator = System.getProperty("line.separator");
                while ((line = br.readLine()) != null) {
                    line = line.replaceAll(replace, replacement);
                    caw.write(line);
                    caw.append(lineSeparator);
                }
                br.close();
                FileWriter fw = new FileWriter(file);
                caw.writeTo(fw);
                fw.close();
            }

        }
    }

    static void renameXmlPackage(File file, String replace, String replacement) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    renameXmlPackage(f, replace, replacement);
                }
            }
        } else if (file.exists()) {
            boolean isRes = file.getAbsolutePath().endsWith(".xml");
            if (isRes) {
                System.out.println("file: " + file);
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                CharArrayWriter caw = new CharArrayWriter();
                String line;
                String lineSeparator = System.getProperty("line.separator");
                while ((line = br.readLine()) != null) {
                    line = line.replaceAll(replace, replacement);
                    caw.write(line);
                    caw.append(lineSeparator);
                }
                br.close();
                FileWriter fw = new FileWriter(file);
                caw.writeTo(fw);
                fw.close();
            }

        }
    }


    static void replaceBuildConfig(File file, String replace, String replacement) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    replaceBuildConfig(f, replace, replacement);
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
                    if (line.matches(replace)) {
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

    static void addImportR(File file, String replace, String replacement) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    addImportR(f, replace, replacement);
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

    static void mergeAttr(SAXReader saxReader, Document attrXml, File file, ArrayList<String> names) throws DocumentException, IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    mergeAttr(saxReader, attrXml, f, names);
                }
            }
        } else if (file.exists()) {
            boolean isXml = file.getAbsolutePath().endsWith(".xml");
            if (isXml) {
                Document xml = saxReader.read(file);
                List<Element> elements = xml.getDocument().getRootElement().elements();
                System.out.println("file: " + file);
                Iterator<Element> iterator = elements.iterator();
                while (iterator.hasNext()) {
                    Element e = iterator.next();
                    if (e.getName().equals("attr")) {
                        System.out.println("<" + e.getName() + " name=\"" + e.attributeValue("name") + "\" format=\"" + e.attributeValue("format") + "\" />");
                        String name = e.attributeValue("name");
                        if (!names.contains(name)) {
                            names.add(name);
                            attrXml.getRootElement().add(e.detach());
                        }
                        iterator.remove();
                    }
                }
                saveXml(xml, file);
            }
        }
    }

    static void mergeManifest(SAXReader saxReader, Element manifestRootElement, Element manifestApplicationElement, File file, ArrayList<String> names) throws DocumentException, IOException {
        if (file.exists()) {
            boolean isXml = file.getAbsolutePath().endsWith(".xml");
            if (isXml) {
                Document xml = saxReader.read(file);
                List<Element> elements = xml.getDocument().getRootElement().elements();
                System.out.println("file: " + file);
                for (Element e : elements) {
                    if (e.getName().equals("uses-permission")) {
                        System.out.println("<" + e.getName() + " name=\"" + e.attributeValue("name") + "\" />");
                        String name = e.attributeValue("name");
                        if (!names.contains(name)) {
                            names.add(name);
                            manifestRootElement.add(e.detach());
                        }
                    } else if (e.getName().equals("application")) {
                        List<Element> components = e.elements();
                        for (Element c : components) {
                            manifestApplicationElement.add(c.detach());
                        }
                    }
                }
            }
        }
    }

    static void saveXml(Document doc, File file) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(StandardCharsets.UTF_8.name());
        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
        writer.setEscapeText(false);
        writer.write(doc);
        writer.close();
    }

}