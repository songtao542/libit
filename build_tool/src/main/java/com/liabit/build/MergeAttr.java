package com.liabit.build;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MergeAttr {
    static void merge(SAXReader saxReader, Document attrXml, File file, ArrayList<String> names) throws DocumentException, IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    merge(saxReader, attrXml, f, names);
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
                Xml.saveXml(xml, file);
            }
        }
    }
}
