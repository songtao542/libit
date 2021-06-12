package com.liabit.build;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MergeAndroidManifest {

    static void merge(SAXReader saxReader,
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
                                if (index >= 0) {
                                    name = name.substring(index + 1);
                                }
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
