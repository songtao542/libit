package com.liabit.build;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

public class Xml {

    static void saveXml(Document doc, File file) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(StandardCharsets.UTF_8.name());
        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
        writer.setEscapeText(false);
        writer.write(doc);
        writer.close();
    }
}
