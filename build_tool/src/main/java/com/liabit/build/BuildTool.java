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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 执行 main 方法将所有模块儿的源码拷贝的 libit 模块儿中，以方便编译出一个完整的 aar
 */
class BuildTool {

    private static boolean isValidPackage(String packageName) {
        // Java/Android合法包名，可以包含大写字母、小写字母、数字和下划线，用点(英文句号)分隔称为段，且至少包含2个段，隔开的每一段都必须以字母开头
        Pattern pattern = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)+([.][a-zA-Z_][a-zA-Z0-9_]*)+$");
        Matcher matcher = pattern.matcher(packageName);
        return matcher.matches();
    }

    private static HashMap<String, String> sSrcPathMap = new HashMap<>();

    public static void main(String[] args) throws DocumentException, IOException {
        if (args != null) {
            int i = 0;
            for (String a : args) {
                System.out.println("args " + i + ": " + a);
                i++;
            }
        }
        String[] mergeList = null;
        String domain = "com.liabit";
        String manifestPackageSuffix = null;
        sSrcPathMap.clear();
        if (args != null && args.length > 0) {
            for (String a : args) {
                System.out.println("arg: " + a);
            }
            String arg = args[0];
            if ("vpn".equals(arg)) {
                mergeList = ModuleManifest.vpnMergeList;
            } else if ("theme".equals(arg)) {
                mergeList = ModuleManifest.themeStoreMergeList;
            } else if ("sport".equals(arg)) {
                mergeList = ModuleManifest.sportMergeList;
            } else {
                if ("matisse".equals(arg)) {
                    mergeList = new String[]{ModuleManifest.moduleMap.get("matisse"), ModuleManifest.moduleMap.get("matisse_crop")};
                } else {
                    String module = ModuleManifest.moduleMap.get(arg);
                    if (module != null) {
                        mergeList = new String[]{module};
                    }
                }
            }
            if (args.length > 1) {
                String pk = args[1];
                System.out.println("package: " + pk);
                if (isValidPackage(pk)) {
                    System.out.println("package valid: true");
                    domain = args[1];
                }
            }

            if (args.length > 2) {
                String pk = args[2];
                System.out.println("manifest package suffix: " + pk);
                Pattern pattern = Pattern.compile("^([a-z]+)");
                Matcher matcher = pattern.matcher(pk);
                if (!matcher.matches()) {
                    throw new IllegalArgumentException("manifest package suffix 只能是小写字母组成");
                }
                manifestPackageSuffix = args[2];
            }
        }
        if (mergeList == null) {
            throw new IllegalArgumentException("请通过 --args 参数选择源码集(--args=vpn 或者 --args=theme 或者 --args=sport)");
        }

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

        //String packageName = "com.liabit.";
        String packageName = domain + ".";
        String dirName = domain.replace("com.", "");
        if (dirName.length() == 0) {
            throw new RuntimeException("包名少为两段, 例如：com.liabit");
        }
        if (dirName.split("\\.").length > 2) {
            throw new RuntimeException("包名分段最多为三段, 例如：com.liabit.client");
        }

        if (!destDir.exists()) {
            boolean result = destDir.mkdirs();
            System.out.println("Create dest dir " + result);
        }
        FileUtils.cleanDirectory(destDir);

        File jniLibsDir = new File(destDir, "jniLibs");
        if (!jniLibsDir.exists()) {
            boolean result = jniLibsDir.mkdirs();
            System.out.println("Create jni dir " + result);
        }
        FileUtils.cleanDirectory(jniLibsDir);

        File manifestFile = new File(destDir, "AndroidManifest.xml");
        if (!manifestFile.exists()) {
            manifestFile.createNewFile();
        }
        String manifestPackage;
        if (manifestPackageSuffix == null) {
            manifestPackage = packageName.substring(0, packageName.length() - 1);
        } else {
            manifestPackage = packageName + manifestPackageSuffix;
        }
        Document manifestXml = DocumentHelper.createDocument();
        Element manifestRootElement = manifestXml.addElement("manifest");
        manifestRootElement.addAttribute("package", manifestPackage);
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
            if (name.equals("matisse/src_crop/main")) {
                destName = "matisse_crop";
            }
            File javaSourceDir = new File(source.getCanonicalFile(), "java");
            File destJavaDir = new File(destDir, destName).getCanonicalFile();
            FileUtils.deleteDirectory(destJavaDir);

            System.out.println(javaSourceDir + " -> " + destJavaDir);
            FileUtils.copyDirectory(javaSourceDir, destJavaDir);

            // 替换 import R
            String importR;
            if (manifestPackageSuffix == null) {
                importR = "import " + packageName + ".R";
            } else {
                importR = "import " + packageName + manifestPackageSuffix + ".R";
            }
            addImportR(destJavaDir, "import com\\.liabit\\..*\\.R", importR);
            // 替换 BuildConfig
            String importBuildConfig;
            if (manifestPackageSuffix == null) {
                importBuildConfig = "import " + packageName + ".BuildConfig";
            } else {
                importBuildConfig = "import " + packageName + manifestPackageSuffix + ".BuildConfig";
            }
            replaceBuildConfig(destJavaDir, "import com\\.liabit\\..*\\.BuildConfig", importBuildConfig);

            if (!"com.liabit.".equals(packageName)) {
                // 重命名
                renamePackage(destJavaDir, "com.liabit.", packageName);
            }
            if (!"liabit".equals(dirName)) {
                // 重命名文件夹
                renameDir(destJavaDir, "liabit", dirName);
            }

            File jniSourceDir = new File(source.getCanonicalFile(), "jniLibs");
            if (jniSourceDir.exists()) {
                copyJniLibs(jniSourceDir, jniLibsDir);
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

            sSrcPathMap.clear();
            String packagePath = packageName.replace(".", File.separator);
            collectFilePath(packagePath, destDir);

            // 源文件
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

    static void copyJniLibs(File jniLibsDir, File destJniLibsDir) {
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

    static void collectFilePath(String packagePath, File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    collectFilePath(packagePath, f);
                }
            }
        } else {
            boolean isJava = file.getAbsolutePath().endsWith(".java");
            boolean isKt = file.getAbsolutePath().endsWith(".kt");
            if (isJava || isKt) {
                String absPath = file.getAbsolutePath();
                int index = absPath.indexOf(packagePath);
                String path = absPath.substring(index)
                        .replace(File.separator, ".")
                        .replace(".kt", "")
                        .replace(".java", "");
                String srcName = file.getName()
                        .replace(".kt", "")
                        .replace(".java", "");
                System.out.println("file path: " + srcName + " -> " + path);
                sSrcPathMap.put(srcName, path);
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

    static void mergeManifest(SAXReader saxReader,
                              Element manifestRootElement,
                              Element manifestApplicationElement,
                              File file,
                              ArrayList<String> names) throws DocumentException, IOException {
        if (file.exists()) {
            boolean isXml = file.getAbsolutePath().endsWith(".xml");
            if (isXml) {
                Document xml = saxReader.read(file);
                List<Element> elements = xml.getDocument().getRootElement().elements();
                System.out.println("manifest file: " + file);
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
                            String name = c.attributeValue("name");
                            if (name != null) {
                                int index = name.lastIndexOf(".");
                                name = name.substring(index + 1);
                            }
                            Element copy = c.createCopy();
                            copy.addAttribute("name", sSrcPathMap.get(name));
                            manifestApplicationElement.add(copy.detach());
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