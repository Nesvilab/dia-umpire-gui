/*
 * Copyright 2017 Dmitry Avtonomov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package umich.msfragger.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.filechooser.FileFilter;
import umich.msfragger.gui.MsfraggerGuiFrame;

/**
 *
 * @author Dmitry Avtonomov
 */
public class PathUtils {
    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    public static String testFilePath(String fileName, String dir) {
        try {
            Path fileNameWasAbsolute = Paths.get(fileName);
            if (Files.exists(fileNameWasAbsolute)) {
                return fileNameWasAbsolute.toAbsolutePath().toString();
            }
        } catch (Exception e) {
            // something wrong with the path
        }
        try {
            Path fileNameWasRelative = Paths.get(dir, fileName);
            if (Files.exists(fileNameWasRelative)) {
                return fileNameWasRelative.toAbsolutePath().toString();
            }
        } catch (Exception e) {
            // something wrong with the path
        }
        return null;
    }
    
    /**
     * Searches for a file first in provided paths, then in working dir, then in system path.<br/>
     * If you want to search near the JAR file currently being executed, add
     * its path to the 'paths' parameter.
     * 
     * @param searchSystemPath
     * @see #getCurrentJarPath() 
     * 
     * @param name  File name to search for.
     * @param paths
     * @return 
     */
    public static Path findFile(String name, boolean searchSystemPath, String... paths) {
        
        // provided paths
        for (String path : paths) {
            if (StringUtils.isNullOrWhitespace(path))
                continue;
            Path p = Paths.get(path, name);
            if (Files.exists(p)) {
                return p;
            }
        }
        
        Path wdPath = Paths.get(name).toAbsolutePath();
        if (Files.exists(wdPath)) {
            return wdPath;
        }
        
        // system PATH
        String envPath = System.getenv("PATH");
        if (!StringUtils.isNullOrWhitespace(envPath)) {
            String[] split = envPath.split(File.pathSeparator);
            for (String s : split) {
                Path p = Paths.get(s, name);
                if (Files.exists(p)) {
                    return p;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Searches for a file first in provided paths, then in system path.<br/>
     * If you want to search near the JAR file currently being executed, add
     * its path to the 'paths' parameter.
     * 
     * @param searchSystemPath
     * @see #getCurrentJarPath() 
     * 
     * @param name  File name to search for.
     * @param paths
     * @return 
     */
    public static Path findFile(Pattern name, boolean searchSystemPath, String... paths) {
        
        List<String> searchPaths = new ArrayList<>(paths.length);
        for (String path : paths) {
            if (StringUtils.isNullOrWhitespace(path)) {
                searchPaths.add(path);
            }
        }
        if (searchSystemPath) {
            String envPath = System.getenv("PATH");
            if (!StringUtils.isNullOrWhitespace(envPath)) {
                String[] split = envPath.split(File.pathSeparator);
                for (String s : split) {
                    if (!StringUtils.isNullOrWhitespace(s)) {
                        searchPaths.add(s);
                    }
                }
            }
        }
        
        // search the paths
        for (String path : searchPaths) {
            Path dirSearch = searchDirectory(name, Paths.get(path));
            if (dirSearch != null) {
                return dirSearch;
            }
        }
        
        return null;
    }
    
    private static Path searchDirectory(Pattern name, Path p) {
        if (!Files.isDirectory(p))
            return null;
        try {
            Iterator<Path> dirIt = Files.newDirectoryStream(p).iterator();
            while(dirIt.hasNext()) {
                Path next = dirIt.next();
                if (!Files.isDirectory(next) && name.matcher(next.getFileName().toString()).matches()) {
                    return next;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PathUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Returns the value for the program, that will work with process builder.<br/>
     * Tries the supplied paths first, then tries to run without any path, i.e.
     * uses the system's PATH variable.
     * @param program  Name of the program to run, e.g. 'start.exe'.
     * @param paths  Additional paths to search in.
     * @return  Null if no working combo has been found.
     */
    public static String testBinaryPath(String program, String... paths) {
        
        // look in provided paths
        for (String path : paths) {
            if (StringUtils.isNullOrWhitespace(path))
                continue;
            try {
                List<String> commands = new LinkedList<>();
                String absPath = Paths.get(path, program).toAbsolutePath().toString();
                commands.add(absPath);
                ProcessBuilder pb = new ProcessBuilder(commands);
                Process proc = pb.start();
                proc.destroy();
                return absPath;
            } catch (Exception e1) {
                // could not run the program with absolute path
            }
        }
        
        // now final resort - try running just the program, hoping that it's in the PATH
        List<String> commands = new LinkedList<>();
        commands.add(program);
        ProcessBuilder pb = new ProcessBuilder(commands);
        try {
            Process proc = pb.start();
            proc.destroy();
            return program;
        } catch (Exception e1) {
            
        }
        return null;
    }

    public static void traverseDirectoriesAcceptingFiles(File dir, FileFilter filter, List<Path> accepted) {
        if (!dir.isDirectory()) {
            if (filter.accept(dir)) {
                accepted.add(Paths.get(dir.getAbsolutePath()));
            }
        }
        Path dirPath = Paths.get(dir.getAbsolutePath());
        try {
            DirectoryStream<Path> ds = Files.newDirectoryStream(dirPath);
            Iterator<Path> it = ds.iterator();
            while (it.hasNext()) {
                File next = it.next().toFile();
                boolean isDir = next.isDirectory();
                if (isDir) {
                    traverseDirectoriesAcceptingFiles(next, filter, accepted);
                } else {
                    if (filter.accept(next)) {
                        accepted.add(Paths.get(next.getAbsolutePath()));
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MsfraggerGuiFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    private PathUtils() {}
    
    /**
     * System-wide temporary directory.
     * @return 
     */
    public static Path getTempDir() {
        if (TEMP_DIR == null || TEMP_DIR.isEmpty())
            throw new IllegalStateException("Could not locate system-wide temporary directory");
        return Paths.get(TEMP_DIR);
    }
}
