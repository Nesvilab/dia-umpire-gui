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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
     * Returns the value for the program, that will work with process builder.<br/>
     * Null if no working combo could be found.
     */
    public static String testBinaryPath(String programName, String workingDir) {
        // First try running just the program, hoping that it's in the PATH
        List<String> commands = new LinkedList<>();
        commands.add(programName);
        ProcessBuilder pb = new ProcessBuilder(commands);
        try {
            Process proc = pb.start();
            proc.destroy();
            return programName;
        } catch (Exception e1) {
            // could not run the program, it was not on PATH
            // Try running the program using absolute path
            if (StringUtils.isNullOrWhitespace(workingDir)) {
                return null;
            }
            try {
                commands = new LinkedList<>();
                String absolutePathProgramName = Paths.get(workingDir, programName).toAbsolutePath().toString();
                commands.add(absolutePathProgramName);
                pb = new ProcessBuilder(commands);
                Process proc = pb.start();
                proc.destroy();
                return absolutePathProgramName;
            } catch (Exception e2) {
                // could not run the program even with absolute path
            }
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
