/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dia.umpire.util;

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
}
