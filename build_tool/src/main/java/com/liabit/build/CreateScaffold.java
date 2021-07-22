package com.liabit.build;

import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CreateScaffold {

    private static final Gson sGson = new Gson();

    public static void createScaffoldProject(String[] args) {
        if (args != null && args.length > 0) {
            for (String a : args) {
                System.out.println("arg: " + a);
            }

            String configFilePath = args[1];
            System.out.println("config: " + configFilePath);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(new File(configFilePath)));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                Config config = sGson.fromJson(builder.toString(), Config.class);

                System.out.println("config: " + config);
                String path = BuildTool.class.getResource("").getFile();
                int index = path.indexOf("build_tool");
                String parentPath = path.substring(0, index);

                File outputDir = new File(config.projectOutputDirectory, config.projectName);

                if (outputDir.exists()) {
                    FileUtils.deleteDirectory(outputDir);
                }
                boolean createOutputDirResult = outputDir.mkdirs();
                System.out.println("create output dir : " + createOutputDirResult);

                // 创建 settings.gradle
                createSettingsGradle(outputDir);

                // 拷贝 gradle 目录
                String gradlePath = parentPath + "gradle";
                FileUtils.copyDirectoryToDirectory(new File(gradlePath), outputDir);

                // 拷贝 build.gradle
                File buildGradleFile = new File(parentPath, "build.gradle");
                FileUtils.copyToDirectory(buildGradleFile, outputDir);

                // 拷贝 gradle.properties
                File gradlePropertiesFile = new File(parentPath, "gradle.properties");
                FileUtils.copyToDirectory(gradlePropertiesFile, outputDir);

                // 拷贝 scaffold 目录
                String scaffoldPath = parentPath + "scaffold";
                System.out.println("scaffoldPath: " + scaffoldPath);
                FileUtils.copyDirectoryToDirectory(new File(scaffoldPath), outputDir);
                File scaffoldDir = new File(outputDir, "scaffold");
                File scaffoldRenameDir = new File(outputDir, "app");
                boolean scaffoldRenameResult = scaffoldDir.renameTo(scaffoldRenameDir);
                System.out.println("rename scaffold : " + scaffoldRenameResult);
                correctAppBuildGradle(scaffoldRenameDir, config);
                correctPackageName(new File(scaffoldRenameDir, "src"), config);
                // 重命名代码目录名称
                File destScaffoldJavaDir = new File(scaffoldRenameDir, "src" + File.separator + "main" + File.separator + "java");
                System.out.println("scaffold java dir : " + destScaffoldJavaDir);
                renameDir(destScaffoldJavaDir, "scaffold", config.packageName.replace("com.", ""));
                // 修改 app AndroidManifest.xml
                File appManifestFile = new File(scaffoldRenameDir, "src" + File.separator + "main" + File.separator + "AndroidManifest.xml");
                correctManifestFile(appManifestFile, config);

                correctAppJava(destScaffoldJavaDir, config);

                File destScaffoldResDir = new File(scaffoldRenameDir, "src" + File.separator + "main" + File.separator + "res");
                System.out.println("scaffold res dir : " + destScaffoldResDir);
                correctAppResources(destScaffoldResDir, config);

                // 拷贝 scaffold_network 目录
                String scaffoldNetworkPath = parentPath + "scaffold_network";
                System.out.println("scaffoldNetworkPath: " + scaffoldNetworkPath);
                FileUtils.copyDirectoryToDirectory(new File(scaffoldNetworkPath), outputDir);
                File scaffoldNetworkDir = new File(outputDir, "scaffold_network");
                File scaffoldNetworkRenameDir = new File(outputDir, "network");
                boolean scaffoldNetworkRenameResult = scaffoldNetworkDir.renameTo(scaffoldNetworkRenameDir);
                System.out.println("rename scaffold_network : " + scaffoldNetworkRenameResult);
                correctNetworkBuildGradle(scaffoldNetworkRenameDir, config);
                correctPackageName(new File(scaffoldNetworkRenameDir, "src"), config);
                // 重命名代码目录名称
                File destScaffoldNetworkJavaDir = new File(scaffoldNetworkRenameDir, "src" + File.separator + "main" + File.separator + "java");
                System.out.println("scaffold network java dir : " + destScaffoldNetworkJavaDir);
                renameDir(destScaffoldNetworkJavaDir, "scaffold", config.packageName.replace("com.", ""));
                // 修改 network AndroidManifest.xml
                File networkManifestFile = new File(scaffoldNetworkRenameDir, "src" + File.separator + "main" + File.separator + "AndroidManifest.xml");
                correctManifestFile(networkManifestFile, config);


            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    private static void createSettingsGradle(File outputDir) throws IOException {
        File settingGradleFile = new File(outputDir, "settings.gradle");
        boolean success = settingGradleFile.createNewFile();
        System.out.println("create settings.gradle : " + success);
        BufferedWriter writer = new BufferedWriter(new FileWriter(settingGradleFile));
        writer.write("include ':app'");
        writer.write("\n");
        writer.write("include ':network'");
        writer.flush();
        writer.close();
    }

    private static void renameFile(File sourceFile, String destFileName) {
        File destFile = new File(sourceFile.getParent(), destFileName);
        boolean renameResult = sourceFile.renameTo(destFile);
        System.out.println("rename " + sourceFile + " -> " + destFile + " : " + renameResult);
    }

    private static void correctAppBuildGradle(File appDir, Config config) throws Exception {
        File aarFile = new File(appDir, "libs/scaffold.aar");
        File aarSourcesFile = new File(appDir, "libs/scaffold-sources.jar");
        renameFile(aarFile, config.projectName + ".aar");
        renameFile(aarSourcesFile, config.projectName + "-sources.jar");

        File buildGradleFile = new File(appDir, "build.gradle");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(buildGradleFile), StandardCharsets.UTF_8));
        CharArrayWriter caw = new CharArrayWriter();
        String line;
        String lineSeparator = System.getProperty("line.separator");
        while ((line = br.readLine()) != null) {
            line = line.replaceAll("com.scaffold", config.packageName);
            line = line.replaceAll("scaffold.aar", config.projectName + ".aar");
            line = line.replaceAll("scaffold_network", "network");
            caw.write(line);
            caw.append(lineSeparator);
        }
        br.close();
        FileWriter fw = new FileWriter(buildGradleFile);
        caw.writeTo(fw);
        fw.close();
    }

    private static void correctNetworkBuildGradle(File appDir, Config config) throws Exception {
        File buildGradleFile = new File(appDir, "build.gradle");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(buildGradleFile), StandardCharsets.UTF_8));
        CharArrayWriter caw = new CharArrayWriter();
        String line;
        String lineSeparator = System.getProperty("line.separator");
        while ((line = br.readLine()) != null) {
            line = line.replaceAll("\\.\\./scaffold/libs/scaffold.aar", "\\.\\./app/libs/" + config.projectName + ".aar");
            caw.write(line);
            caw.append(lineSeparator);
        }
        br.close();
        FileWriter fw = new FileWriter(buildGradleFile);
        caw.writeTo(fw);
        fw.close();
    }

    private static void correctAppJava(File javaDir, Config config) throws Exception {
        File tabDir = new File(javaDir, config.packageName.replace(".", File.separator) + File.separator + "ui" + File.separator + "tab");
        File homeDir = new File(tabDir, "home");
        File tab1Dir = new File(homeDir.getParent(), config.tab1);
        boolean renameHome = homeDir.renameTo(tab1Dir);
        System.out.println("rename ui home dir: " + renameHome);
        renameFile(new File(tab1Dir, "HomeFragment.kt"), upperFirst(config.tab1) + "Fragment.kt");
        renameFile(new File(tab1Dir, "HomeViewModel.kt"), upperFirst(config.tab1) + "ViewModel.kt");
        replace(new File(tab1Dir, upperFirst(config.tab1) + "Fragment.kt"), line -> {
            line = line.replace("Home", upperFirst(config.tab1));
            line = line.replace("home", config.tab1);
            return line;
        });
        replace(new File(tab1Dir, upperFirst(config.tab1) + "ViewModel.kt"), line -> {
            line = line.replace("Home", upperFirst(config.tab1));
            line = line.replace("home", config.tab1);
            return line;
        });

        File discoverDir = new File(tabDir, "discover");
        File tab2Dir = new File(discoverDir.getParent(), config.tab2);
        boolean renameDiscover = discoverDir.renameTo(tab2Dir);
        System.out.println("rename ui discover dir: " + renameDiscover);
        renameFile(new File(tab2Dir, "DiscoverFragment.kt"), upperFirst(config.tab2) + "Fragment.kt");
        renameFile(new File(tab2Dir, "DiscoverViewModel.kt"), upperFirst(config.tab2) + "ViewModel.kt");
        replace(new File(tab2Dir, upperFirst(config.tab2) + "Fragment.kt"), line -> {
            line = line.replace("Discover", upperFirst(config.tab2));
            line = line.replace("discover", config.tab2);
            return line;
        });
        replace(new File(tab2Dir, upperFirst(config.tab2) + "ViewModel.kt"), line -> {
            line = line.replace("Discover", upperFirst(config.tab2));
            line = line.replace("discover", config.tab2);
            return line;
        });

        File storeDir = new File(tabDir, "store");
        File tab3Dir = new File(storeDir.getParent(), config.tab3);
        boolean renameStore = storeDir.renameTo(tab3Dir);
        System.out.println("rename ui store dir: " + renameStore);
        renameFile(new File(tab3Dir, "StoreFragment.kt"), upperFirst(config.tab3) + "Fragment.kt");
        renameFile(new File(tab3Dir, "StoreViewModel.kt"), upperFirst(config.tab3) + "ViewModel.kt");
        replace(new File(tab3Dir, upperFirst(config.tab3) + "Fragment.kt"), line -> {
            line = line.replace("Store", upperFirst(config.tab3));
            line = line.replace("store", config.tab3);
            return line;
        });
        replace(new File(tab3Dir, upperFirst(config.tab3) + "ViewModel.kt"), line -> {
            line = line.replace("Store", upperFirst(config.tab3));
            line = line.replace("store", config.tab3);
            // 恢复误操作
            line = line.replace("onRethirdInstanceState", "onRestoreInstanceState");
            return line;
        });

        File profileDir = new File(tabDir, "profile");
        File tab4Dir = new File(profileDir.getParent(), config.tab4);
        boolean renameProfile = profileDir.renameTo(tab4Dir);
        System.out.println("rename ui profile dir: " + renameProfile);
        renameFile(new File(tab4Dir, "ProfileFragment.kt"), upperFirst(config.tab4) + "Fragment.kt");
        renameFile(new File(tab4Dir, "ProfileViewModel.kt"), upperFirst(config.tab4) + "ViewModel.kt");
        replace(new File(tab4Dir, upperFirst(config.tab4) + "Fragment.kt"), line -> {
            line = line.replace("Profile", upperFirst(config.tab4));
            line = line.replace("profile", config.tab4);
            return line;
        });
        replace(new File(tab4Dir, upperFirst(config.tab4) + "ViewModel.kt"), line -> {
            line = line.replace("Profile", upperFirst(config.tab4));
            line = line.replace("profile", config.tab4);
            return line;
        });

    }

    private static void correctAppResources(File resDir, Config config) throws Exception {
        File drawableDir = new File(resDir, "drawable");
        renameFile(new File(drawableDir, "ic_navi_home.xml"), "ic_navi_" + config.tab1 + ".xml");
        renameFile(new File(drawableDir, "ic_navi_home_select.xml"), "ic_navi_" + config.tab1 + "_select.xml");
        renameFile(new File(drawableDir, "ic_navi_home_selector.xml"), "ic_navi_" + config.tab1 + "_selector.xml");
        File tab1SelectorFile = new File(drawableDir, "ic_navi_" + config.tab1 + "_selector.xml");
        replace(tab1SelectorFile, line -> {
            line = line.replaceAll("navi_home", "navi_" + config.tab1);
            return line;
        });

        renameFile(new File(drawableDir, "ic_navi_discover.xml"), "ic_navi_" + config.tab2 + ".xml");
        renameFile(new File(drawableDir, "ic_navi_discover_select.xml"), "ic_navi_" + config.tab2 + "_select.xml");
        renameFile(new File(drawableDir, "ic_navi_discover_selector.xml"), "ic_navi_" + config.tab2 + "_selector.xml");
        File tab2SelectorFile = new File(drawableDir, "ic_navi_" + config.tab2 + "_selector.xml");
        replace(tab2SelectorFile, line -> {
            line = line.replaceAll("navi_discover", "navi_" + config.tab2);
            return line;
        });

        renameFile(new File(drawableDir, "ic_navi_store.xml"), "ic_navi_" + config.tab3 + ".xml");
        renameFile(new File(drawableDir, "ic_navi_store_select.xml"), "ic_navi_" + config.tab3 + "_select.xml");
        renameFile(new File(drawableDir, "ic_navi_store_selector.xml"), "ic_navi_" + config.tab3 + "_selector.xml");
        File tab3SelectorFile = new File(drawableDir, "ic_navi_" + config.tab3 + "_selector.xml");
        replace(tab3SelectorFile, line -> {
            line = line.replaceAll("navi_store", "navi_" + config.tab3);
            return line;
        });

        renameFile(new File(drawableDir, "ic_navi_profile.xml"), "ic_navi_" + config.tab4 + ".xml");
        renameFile(new File(drawableDir, "ic_navi_profile_select.xml"), "ic_navi_" + config.tab4 + "_select.xml");
        renameFile(new File(drawableDir, "ic_navi_profile_selector.xml"), "ic_navi_" + config.tab4 + "_selector.xml");
        File tab4SelectorFile = new File(drawableDir, "ic_navi_" + config.tab4 + "_selector.xml");
        replace(tab4SelectorFile, line -> {
            line = line.replaceAll("navi_profile", "navi_" + config.tab4);
            return line;
        });

        File layoutDir = new File(resDir, "layout");
        renameFile(new File(layoutDir, "fragment_home.xml"), "fragment_" + config.tab1 + ".xml");
        renameFile(new File(layoutDir, "fragment_home_list_item.xml"), "fragment_" + config.tab1 + "_list_item.xml");
        renameFile(new File(layoutDir, "fragment_discover.xml"), "fragment_" + config.tab2 + ".xml");
        renameFile(new File(layoutDir, "fragment_store.xml"), "fragment_" + config.tab3 + ".xml");
        renameFile(new File(layoutDir, "fragment_profile.xml"), "fragment_" + config.tab4 + ".xml");
        File[] layoutFiles = layoutDir.listFiles();
        assert layoutFiles != null;
        for (File layoutFile : layoutFiles) {
            replace(layoutFile, line -> {
                line = line.replaceAll("com.scaffold", config.packageName);
                return line;
            });
        }


        File stringsFile = new File(resDir, "values" + File.separator + "strings.xml");
        replace(stringsFile, line -> {
            line = line.replaceAll("navi_home", "navi_" + config.tab1);
            line = line.replaceAll("navi_discover", "navi_" + config.tab2);
            line = line.replaceAll("navi_store", "navi_" + config.tab3);
            line = line.replaceAll("navi_profile", "navi_" + config.tab4);

            line = line.replaceAll("首页", config.tab1Name);
            line = line.replaceAll("发现", config.tab2Name);
            line = line.replaceAll("商城", config.tab3Name);
            line = line.replaceAll("我的", config.tab4Name);
            return line;
        });

        File navigationFile = new File(resDir, "navigation" + File.separator + "bottom_navigation.xml");
        replace(navigationFile, line -> {
            line = line.replaceAll("com.scaffold", config.packageName);

            line = line.replaceAll("navi_home", "navi_" + config.tab1);
            line = line.replaceAll("navi_discover", "navi_" + config.tab2);
            line = line.replaceAll("navi_store", "navi_" + config.tab3);
            line = line.replaceAll("navi_profile", "navi_" + config.tab4);

            line = line.replaceAll("fragment_home", "fragment_" + config.tab1);
            line = line.replaceAll("fragment_discover", "fragment_" + config.tab2);
            line = line.replaceAll("fragment_store", "fragment_" + config.tab3);
            line = line.replaceAll("fragment_profile", "fragment_" + config.tab4);

            line = line.replaceAll("HomeFragment", upperFirst(config.tab1) + "Fragment");
            line = line.replaceAll("DiscoverFragment", upperFirst(config.tab2) + "Fragment");
            line = line.replaceAll("StoreFragment", upperFirst(config.tab3) + "Fragment");
            line = line.replaceAll("ProfileFragment", upperFirst(config.tab4) + "Fragment");

            line = line.replaceAll("tab\\.home", "tab." + config.tab1);
            line = line.replaceAll("tab\\.discover", "tab." + config.tab2);
            line = line.replaceAll("tab\\.store", "tab." + config.tab3);
            line = line.replaceAll("tab\\.profile", "tab." + config.tab4);

            return line;
        });
        File menuFile = new File(resDir, "menu" + File.separator + "navi_menu.xml");
        replace(menuFile, line -> {
            line = line.replaceAll("navi_home", "navi_" + config.tab1);
            line = line.replaceAll("navi_discover", "navi_" + config.tab2);
            line = line.replaceAll("navi_store", "navi_" + config.tab3);
            line = line.replaceAll("navi_profile", "navi_" + config.tab4);
            return line;
        });

    }

    private static String upperFirst(String in) {
        String first = in.substring(0, 1);
        return first.toUpperCase() + in.substring(1);
    }

    interface ReplaceAction {
        String replace(String line);
    }

    private static void replace(File file, ReplaceAction action) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        CharArrayWriter caw = new CharArrayWriter();
        String line;
        String lineSeparator = System.getProperty("line.separator");
        while ((line = br.readLine()) != null) {
            line = action.replace(line);
            caw.write(line);
            caw.append(lineSeparator);
        }
        br.close();
        FileWriter fw = new FileWriter(file);
        caw.writeTo(fw);
        fw.close();
    }

    private static void correctManifestFile(File manifestFile, Config config) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(manifestFile), StandardCharsets.UTF_8));
        CharArrayWriter caw = new CharArrayWriter();
        String line;
        String lineSeparator = System.getProperty("line.separator");
        while ((line = br.readLine()) != null) {
            line = line.replaceAll("com\\.scaffold", config.packageName);
            caw.write(line);
            caw.append(lineSeparator);
        }
        br.close();
        FileWriter fw = new FileWriter(manifestFile);
        caw.writeTo(fw);
        fw.close();
    }

    private static void correctPackageName(File srcFile, Config config) throws Exception {
        if (srcFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            if (files != null) {
                for (File f : files) {
                    correctPackageName(f, config);
                }
            }
        } else if (srcFile.exists()) {
            boolean isJava = srcFile.getAbsolutePath().endsWith(".java");
            boolean isKt = srcFile.getAbsolutePath().endsWith(".kt");
            if (isJava || isKt) {
                System.out.println("file: " + srcFile);
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile), StandardCharsets.UTF_8));
                CharArrayWriter caw = new CharArrayWriter();
                String line;
                String lineSeparator = System.getProperty("line.separator");
                while ((line = br.readLine()) != null) {
                    line = line.replaceAll("com\\.scaffold", config.packageName);
                    caw.write(line);
                    caw.append(lineSeparator);
                }
                br.close();
                FileWriter fw = new FileWriter(srcFile);
                caw.writeTo(fw);
                fw.close();
            }
        }
    }

    private static void renameDir(File file, String oldName, String newName) throws IOException {
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


    public static class Config {

        public String projectName;
        public String projectOutputDirectory;

        public String packageName;

        public String tab1;
        public String tab2;
        public String tab3;
        public String tab4;

        public String tab1Name;
        public String tab2Name;
        public String tab3Name;
        public String tab4Name;

        @Override
        public String toString() {
            return "Config{" +
                    "projectName='" + projectName + '\'' +
                    ", projectOutputDirectory='" + projectOutputDirectory + '\'' +
                    ", packageName='" + packageName + '\'' +
                    ", tab1='" + tab1 + '\'' +
                    ", tab2='" + tab2 + '\'' +
                    ", tab3='" + tab3 + '\'' +
                    ", tab4='" + tab4 + '\'' +
                    ", tab1Name='" + tab1Name + '\'' +
                    ", tab2Name='" + tab2Name + '\'' +
                    ", tab3Name='" + tab3Name + '\'' +
                    ", tab4Name='" + tab4Name + '\'' +
                    '}';
        }
    }


}
