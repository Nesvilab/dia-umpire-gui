package dia.umpire.params;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by Dmitry Avtonomov on 2016-04-28.
 */

public class ThisAppProps extends Properties {
    //private static final Logger log = LoggerFactory.getLogger(ThisAppProps.class);
    public static final String PROP_PARAMS_FILE_IN = "path.params.file.in";
    public static final String PROP_BINARIES_IN = "path.params.bins.in";
    public static final String PROP_JAR_IN = "path.params.jar.in";
    public static final String PROP_LCMS_FILES_IN = "path.lcms.files.in";
    public static final String PROP_FILE_OUT = "path.file.out";

    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    public static final String TEMP_FILE_NAME = "diaumpiregui.cache";
    
    public static final String PROP_TEXTFIELD_PATH_UMPIRE_SE = "path.textfield.dia-umpire-se";
    public static final String PROP_TEXTFIELD_PATH_MSCONVERT = "path.textfield.msconvert";
    public static final String PROP_TEXTFIELD_PATH_COMET = "path.textfield.comet";
    public static final String PROP_TEXTFIELD_PATH_PEPTIDE_PROPHET = "path.textfield.peptide-prophet";
    public static final String PROP_TEXTFIELD_PATH_PROTEIN_PROPHET = "path.textfield.protein-prophet";

    public static ThisAppProps loadFromTemp()  {
        Path path = Paths.get(TEMP_DIR, TEMP_FILE_NAME);
        if (!Files.exists(path)) {
            return null;
        }
        try {
            ThisAppProps props = new ThisAppProps();
            props.load(new FileInputStream(path.toFile()));
            return props;

        } catch (IOException ex) {
            //log.warn("Could not load properties from temporary directory: {}", ex.getMessage());
        }

        return null;
    }

    public void save() {
        Path path = Paths.get(TEMP_DIR, TEMP_FILE_NAME);
        try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
            store(fos, "DIA Umpire GUI runtime properties");
        } catch (IOException ex) {
            //log.warn("Could not load properties from temporary directory: {}", ex.getMessage());
        }
    }
}
