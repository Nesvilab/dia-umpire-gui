/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dia.umpire.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dmitry Avtonomov
 */
public class OsUtils {
    private OsUtils() {}
    
    public static boolean isWindows() {
        String osName = System.getProperty("os.name");
        if (osName == null)
            return true; // just the default
        return osName.toLowerCase().startsWith("win");
    }
    
    public static URI getCurrentJarPath() {
        try {
            CodeSource codeSource = OsUtils.class.getProtectionDomain().getCodeSource();
            URL location = codeSource.getLocation();
            return location.toURI();
        } catch (URISyntaxException ex) {
            Logger.getLogger(OsUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
