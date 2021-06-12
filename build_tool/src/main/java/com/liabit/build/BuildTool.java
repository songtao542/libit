package com.liabit.build;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
        File destMainDir = new File(rootPath, "../libit/src/main/");
        File destMainResDir = new File(destMainDir, "res/values/");

        // 创建 attrs.xml 用于记录所有 module 共享的 <attr/>
        SAXReader saxReader = new SAXReader();
        File attrFile = new File(destMainResDir, "attrs.xml");
        Document attrXml = DocumentHelper.createDocument();
        attrXml.addElement("resources");
        ArrayList<String> attrNameList = new ArrayList<>();

        // 包名，默认为 com.liabit.
        String defaultPackageName = "com.liabit.";
        String packageName = domain + ".";
        // 代码目录名，默认为 liabit
        String defaultDirName = "liabit";
        String dirName = domain.replace("com.", "");
        if (dirName.length() == 0) {
            throw new RuntimeException("包名少为两段, 例如：com.liabit");
        }
        if (dirName.split("\\.").length > 2) {
            throw new RuntimeException("包名分段最多为三段, 例如：com.liabit.client");
        }

        if (!destMainDir.exists()) {
            boolean result = destMainDir.mkdirs();
            System.out.println("create dest main dir " + result);
        }
        // 清空 main dir
        FileUtils.cleanDirectory(destMainDir);

        // 创建 jniLibs 目录
        File jniLibsDir = new File(destMainDir, "jniLibs");
        if (!jniLibsDir.exists()) {
            boolean result = jniLibsDir.mkdirs();
            System.out.println("create jniLibs dir " + result);
        }
        // 清空 jniLibs 目录
        FileUtils.cleanDirectory(jniLibsDir);

        // 创建 AndroidManifest.xml
        File manifestFile = new File(destMainDir, "AndroidManifest.xml");
        if (!manifestFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            manifestFile.createNewFile();
        }
        // AndroidManifest.xml 中 package 的值
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
        Xml.saveXml(manifestXml, manifestFile);

        for (String merge : mergeList) {
            // module 源代码路径，例如 /path/to/project/libit/viewbinding/src/main
            File source = new File(rootPath, merge);
            String name = merge.replace("../", "");
            String destName = name.substring(0, name.indexOf("/"));
            if (name.equals("matisse/src_crop/main")) {
                destName = "matisse_crop";
            }
            File javaSourceDir = new File(source.getCanonicalFile(), "java");
            File destJavaDir = new File(destMainDir, destName).getCanonicalFile();
            FileUtils.deleteDirectory(destJavaDir);

            System.out.println(javaSourceDir + " -> " + destJavaDir);
            // 拷贝 module 中的Java源代码到目标目录
            FileUtils.copyDirectory(javaSourceDir, destJavaDir);

            // 修改源代码中的 import R
            String importR;
            if (manifestPackageSuffix == null) {
                importR = "import " + packageName + ".R";
            } else {
                importR = "import " + packageName + manifestPackageSuffix + ".R";
            }
            ReplaceImportR.replace(destJavaDir, "import com\\.liabit\\..*\\.R", importR);

            // 修改源代码中的 import BuildConfig
            String importBuildConfig;
            if (manifestPackageSuffix == null) {
                importBuildConfig = "import " + packageName + ".BuildConfig";
            } else {
                importBuildConfig = "import " + packageName + manifestPackageSuffix + ".BuildConfig";
            }
            ReplaceImportBuildConfig.replace(destJavaDir, "import com\\.liabit\\..*\\.BuildConfig", importBuildConfig);

            // 修改源代码中的 import BuildConfig
            String importDataBinding;
            if (manifestPackageSuffix == null) {
                importDataBinding = "import " + packageName + ".databinding";
            } else {
                importDataBinding = "import " + packageName + manifestPackageSuffix + ".databinding";
            }
            ReplaceDataBindingPackage.replace(destJavaDir, "import com\\.liabit\\..*\\.databinding", importDataBinding);

            // 如果 packageName 需要更改，则替换源代码中的 packageName
            if (!defaultPackageName.equals(packageName)) {
                // 修改源代码中的包名
                ReplacePackageName.replace(destJavaDir, defaultPackageName, packageName);
            }
            // 如果源代码目录需要更改，则重命名源代码目录名
            if (!defaultDirName.equals(dirName)) {
                // 重命名文件夹
                renameDir(destJavaDir, defaultDirName, dirName);
            }

            // 拷贝 module 中的 jniLibs 中的so文件
            File jniSourceDir = new File(source.getCanonicalFile(), "jniLibs");
            if (jniSourceDir.exists()) {
                CopyJniLib.copy(jniSourceDir, jniLibsDir);
            }

            // module 的 res 目录
            File resSourceDir = new File(source.getCanonicalFile(), "res");

            // 收集类名
            HashMap<String, String> srcPathMap = new HashMap<>();
            String packagePath = packageName.replace(".", File.separator);
            collectFilePath(packagePath, destMainDir, srcPathMap);

            if (resSourceDir.exists()) {
                File destResDir = new File(destMainDir, destName + "_res").getCanonicalFile();
                FileUtils.deleteDirectory(destResDir);
                System.out.println(resSourceDir + " -> " + destResDir);
                FileUtils.copyDirectory(resSourceDir, destResDir);
                // 提取 module 中 公用的 <attr/> 属性
                MergeAttr.merge(saxReader, attrXml, destResDir, attrNameList);
                // 替换 xml 文件中（主要是 layout 文件）出现的包名
                if (!defaultPackageName.equals(packageName)) {
                    // 重命名
                    ReplacePackageNameInXml.replace(destResDir, defaultPackageName, packageName);
                }
            }

            // 源文件
            File manifestSourceFile = new File(source.getCanonicalFile(), "AndroidManifest.xml");
            mergeManifest(saxReader, manifestRootElement, manifestApplicationElement, manifestSourceFile, manifestNames, srcPathMap);
        }

        if (!destMainResDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            destMainResDir.mkdirs();
        }
        if (!attrFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            attrFile.createNewFile();
        }
        Xml.saveXml(attrXml, attrFile);
        Xml.saveXml(manifestXml, manifestFile);
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

    static void collectFilePath(String packagePath, File file, HashMap<String, String> resultMap) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    collectFilePath(packagePath, f, resultMap);
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
                resultMap.put(srcName, path);
            }
        }
    }


    static void mergeManifest(SAXReader saxReader,
                              Element manifestRootElement,
                              Element manifestApplicationElement,
                              File file,
                              ArrayList<String> names,
                              HashMap<String, String> srcPathMap) throws DocumentException, IOException {
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
                            if (srcPathMap.get(name) != null) {
                                copy.addAttribute("name", srcPathMap.get(name));
                            }
                            manifestApplicationElement.add(copy.detach());
                        }
                    }
                }
            }
        }
    }

}