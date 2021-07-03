package com.liabit.build;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExtractPrivilegedPermission {

    public static void main(String[] args) {
        try {
            String path = BuildTool.class.getResource("").getFile();
            int index = path.indexOf("build_tool");
            String rootPath = path.substring(0, index + "build_tool".length());
            File root = new File(rootPath);

            SAXReader saxReader = new SAXReader();
            File manifestFile = new File(root, "Manifest.xml");
            Document manifestXml = saxReader.read(manifestFile);

            ArrayList<String> attrNameList = new ArrayList<>();

            List<Element> elements = manifestXml.getDocument().getRootElement().elements();
            Iterator<Element> iterator = elements.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                Element e = iterator.next();
                if (e.getName().equals("permission")) {
                    String name = e.attributeValue("name");
                    String protectionLevel = e.attributeValue("protectionLevel");
                    if (protectionLevel.contains("privileged")) {
                        System.out.println(name);
                        //System.out.println("privileged: " + name /*+ " : " + protectionLevel*/);
                        //System.out.println("privilegedPermissionList.add(\"" + name + "\")");
                        //System.out.println("privileged_permissions[" + i + "]=\"" + name + "\"");
                        i++;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


}
