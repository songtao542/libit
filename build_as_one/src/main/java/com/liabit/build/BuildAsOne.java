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

class BuildAsOne {

    static String[] mergeList = {
            "../viewbinding/src/main", // viewbinding
            "../widget/src/main", // widget
            "../recyclerview/src/main", // recyclerview
            "../timerview/src/main", // timerview
            "../ext/src/main", // ext
            "../popup/src/main", // popup
            "../citypicker/src/main", // citypicker
            "../photoview/src/main", // photoview
            "../numberpicker/src/main", // picker
            "../picker/src/main", // picker
            "../dialog/src/main", // dialog
            "../picker_integrate/src/main" // picker_integrate
//            "../screenrecord/src/main" // screenrecord
    };

    public static void main(String[] args) throws DocumentException, IOException {
        String path = BuildAsOne.class.getResource("").getFile();
        int index = path.indexOf("build_as_one");
        String rootPath = path.substring(0, index + "build_as_one".length());
        File destDir = new File(rootPath, "../libit/src/main/");
        File resDir = new File(destDir, "res/values/");

        SAXReader saxReader = new SAXReader();
        File attrFile = new File(resDir, "attrs.xml");
        Document attrXml = DocumentHelper.createDocument();
        attrXml.addElement("resources");
        ArrayList<String> attrNames = new ArrayList<>();

        File manifestFile = new File(destDir, "AndroidManifest.xml");
        Document manifestXml = DocumentHelper.createDocument();
        Element manifestRootElement = manifestXml.addElement("manifest");
        manifestRootElement.addAttribute("package", "com.liabit");
        manifestRootElement.addNamespace("android", "http://schemas.android.com/apk/res/android");
        Element manifestApplicationElement = manifestRootElement.addElement("application");
        ArrayList<String> manifestNames = new ArrayList<>();

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

            File resSourceDir = new File(source.getCanonicalFile(), "res");
            if (resSourceDir.exists()) {
                File destResDir = new File(destDir, destName + "_res").getCanonicalFile();
                FileUtils.deleteDirectory(destResDir);
                System.out.println(resSourceDir + " -> " + destResDir);
                FileUtils.copyDirectory(resSourceDir, destResDir);
                mergeAttr(saxReader, attrXml, destResDir, attrNames);
            }
            File manifestSourceFile = new File(source.getCanonicalFile(), "AndroidManifest.xml");

            mergeManifest(saxReader, manifestRootElement, manifestApplicationElement, manifestSourceFile, manifestNames);
        }

        saveXml(attrXml, attrFile);
        saveXml(manifestXml, manifestFile);
    }

    static void addImportR(File file, String replace, String replacement) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                addImportR(f, replace, replacement);
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
                while ((line = br.readLine()) != null) {
                    boolean isPackageRow = line.contains("package ");
                    if (isPackageRow) {
                        // 如果是 package 所在行，则在该行之后添加 import R 语句
                        caw.write(line);
                        caw.append(System.getProperty("line.separator"));
                        caw.write(importR);
                    } else {
                        // 如果是 import R 的行，则直接跳过
                        if (line.matches(replace)) {
                            continue;
                        }
                        caw.write(line);
                    }
                    caw.append(System.getProperty("line.separator"));
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
            for (File f : files) {
                mergeAttr(saxReader, attrXml, f, names);
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