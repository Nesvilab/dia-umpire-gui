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

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.nio.file.Paths;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import umich.msfragger.params.ThisAppProps;

/**
 *
 * @author Dmitry Avtonomov
 */
public class SwingUtils {

    public static void saveFilechooserPathToCached(File file, String propName) {
        ThisAppProps thisAppProps = ThisAppProps.loadFromTemp();
        if (thisAppProps == null) {
            thisAppProps = new ThisAppProps();
        }
        thisAppProps.setProperty(propName, file.getAbsolutePath());
        thisAppProps.save();
    }

    public static void setFilechooserPathToCached(JFileChooser fileChooser, String propName) {
        ThisAppProps thisAppProps = ThisAppProps.loadFromTemp();
        if (thisAppProps == null) {
            return;
        }
        String inputPath = thisAppProps.getProperty(propName);
        if (inputPath != null) {
            File file = Paths.get(inputPath).toFile();
            fileChooser.setCurrentDirectory(file);
        }
    }

    public static boolean loadTextFieldFromCache(JTextField txt, String propName) {
        String cached = ThisAppProps.loadPropFromCache(propName);
        if (cached == null) {
            return false;
        }
        txt.setText(cached);
        return true;
    }

    public static void saveTextFieldToCache(JTextField txt, String propName) {
        String text = txt.getText().trim();
        if (!text.isEmpty()) {
            ThisAppProps.savePropToCache(propName, text);
        }
    }
    private SwingUtils() {}
    
    public static void enableComponents(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enable);
            if (component instanceof Container) {
                enableComponents((Container)component, enable);
            }
        }
    }
}
