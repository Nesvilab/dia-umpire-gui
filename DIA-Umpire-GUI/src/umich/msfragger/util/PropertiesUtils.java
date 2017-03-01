/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.msfragger.util;

import umich.msfragger.exceptions.FileWritingException;
import umich.msfragger.params.PropLine;
import umich.msfragger.params.PropertyFileContent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dmitriya
 */
public class PropertiesUtils {
    private PropertiesUtils() {}

    /**
     * Write the content of the property file with possible modifications to a new
     * file, keeping the formatting as close to original as possible.
     * @param pfc modified contents of the file
     * @param out The stream should be connected to a file. The stream will be closed after this call.
     * @throws umich.msfragger.exceptions.FileWritingException
     */
    public static void writePropertiesContent(PropertyFileContent pfc, OutputStream out) throws FileWritingException {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true)) {
            
            Map<Integer, PropLine> mapLines = pfc.getMapLines();
            Properties props = pfc.getProps();
            HashSet<String> propNamesWritten = new HashSet<String>();
            for (Map.Entry<Integer, PropLine> entry : mapLines.entrySet()) {
                int lineNum = entry.getKey();
                PropLine propLine = entry.getValue();
                if (propLine.isSimpleLine()) {
                    pw.println(propLine.getJustALine());
                } else {
                    String propName = propLine.getName();
                    String propValue = props.getProperty(propName);
                    pw.print(propName + " = " + propValue);
                    propNamesWritten.add(propName);
                    if (propLine.getComment() != null) {
                        pw.print("\t\t\t" + propLine.getComment());
                    }
                    pw.println();
                }
            }
            Set<String> stringPropertyNames = props.stringPropertyNames();
            // if there was something else added on top of what was in the file
            // we will append to the end of the file
            for (String propName : stringPropertyNames) {
                if (propNamesWritten.contains(propName))
                    continue;
                pw.println(propName + " = " + props.getProperty(propName));
            }
        } finally {
            if (out != null)
                try {
                    out.close();
            } catch (IOException ex) {
                throw new FileWritingException("This is strange, error happened while trying to close the output stream.");
            }
        }
        
    }
}
