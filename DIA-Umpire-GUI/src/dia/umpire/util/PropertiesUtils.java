/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dia.umpire.util;

import dia.umpire.exceptions.FileWritingException;
import dia.umpire.params.PropLine;
import dia.umpire.params.PropertyFileContent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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
     * @throws dia.umpire.exceptions.FileWritingException
     */
    public static void writePropertiesContent(PropertyFileContent pfc, OutputStream out) throws FileWritingException {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true)) {
            
            Map<Integer, PropLine> mapLines = pfc.getMapLines();
            for (Map.Entry<Integer, PropLine> entry : mapLines.entrySet()) {
                int lineNum = entry.getKey();
                PropLine propLine = entry.getValue();
                if (propLine.isSimpleLine()) {
                    pw.println(propLine.getJustALine());
                } else {
                    pw.print(propLine.getValue() + " = " + propLine.getValue());
                    if (propLine.getComment() != null) {
                        pw.print("\t\t\t" + propLine.getComment());
                    }
                    pw.println();
                }
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
