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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import nu.studer.java.util.OrderedProperties;
import umich.msfragger.util.StringUtils;

/**
 *
 * @author Dmitry Avtonomov
 */
public class Props {
    public static final String COMMENT = "#";
    private LinkedHashMap<String, Prop> map;
    private LinkedHashMap<String, String> comments;
    
    
    public void load(InputStream is) throws IOException {
        
    }
    
    public void save(OutputStream os) throws IOException {
        
    }
    
    public void setProp(String name, String value) {
        setProp(name, value, null, true);
    }
    
    public void setProp(String name, String value, String comment, boolean isEnabled) {
        if (StringUtils.isNullOrWhitespace(name))
            throw new IllegalArgumentException("Name can't be null or whitespace");
        if (value == null)
            value = "";
        map.put(name, new Prop(name, value, comment, isEnabled));
        if (comment != null && !StringUtils.isNullOrWhitespace(comment)) {
            comments.put(name, comment);
        }
    }
    
    public void removeProp(String name) {
        if (StringUtils.isNullOrWhitespace(name))
            throw new IllegalArgumentException("Name can't be null or whitespace");
    }
    
    public Prop getProp(String name) {
        return map.get(name);
    }
    
    public String getComment(String name) {
        return comments.get(name);
    }
    
    public class Prop {
        public final String name;
        public final String value;
        public final String comment;
        public final boolean isEnabled;

        public Prop(String name, String value, String comment, boolean isEnabled) {
            this.name = name;
            this.value = value;
            this.comment = comment;
            this.isEnabled = isEnabled;
        }
    }
    
    
    /**
     * Loads properties from a stream. Typically a FileInputStream or an input 
     * stream that you get via Class.getResourceAsStream() method.
     * @param is  Stream is closed after reading.
     * @param properties  
     * @throws IOException 
     */
    private void readProps(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line;
        while (while)
        
        is.close();
    }
    
    /**
     * Writes to the stream (buffers it), includes comments after each parameter.
     * @param os  The stream is closed after writing.
     * @param properties
     * @param comments
     * @throws IOException 
     */
    private void writeProps(OutputStream os) throws IOException {
        Set<Map.Entry<String, String>> entries = properties.entrySet();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        for (Map.Entry<String, String> e : entries) {
            bw.write(e.getKey());
            bw.write(" = ");
            bw.write(e.getValue());
            if (comments != null && !comments.isEmpty()) {
                String comment = comments.get(e.getKey());
                if (comment != null && !comment.isEmpty()) {
                    bw.write("\t\t\t# ");
                    bw.write(comment);
                }
            }
            bw.write("\n");
        }
        bw.write("\n");
        
        bw.flush();
        os.close();
    }
}
