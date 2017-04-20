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

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Dmitry Avtonomov
 */
public class PathUtils {
    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    
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
