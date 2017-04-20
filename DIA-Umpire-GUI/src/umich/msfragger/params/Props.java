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
package umich.msfragger.params;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Dmitry Avtonomov
 */
public class Props {
    public static final String COMMENT = "#";
    
    
    public void load(InputStream is) throws IOException {
        
    }
    
    public void save(OutputStream os) throws IOException {
        
    }
    
    public static class Line {
        String propName = "";
        String propValue = "";
        String comment = "";
    }
}
