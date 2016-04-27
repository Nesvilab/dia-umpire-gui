package dia.umpire.params;

import dia.umpire.exceptions.ParsingException;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dmitry Avtonomov on 2016-04-27.
 */
public class CometParams {
    public static final String COMET_ENZYME_INFO = "[COMET_ENZYME_INFO]";
    protected Map<Integer, String> cometEnzymeInfos = new TreeMap<>();
    protected String firstLine;
    protected Properties props = new Properties();
    
    
    public static final String PROP_database_name = "database_name";
    public static final String PROP_peptide_mass_tolerance = "peptide_mass_tolerance";
    public static final String PROP_fragment_bin_tol = "fragment_bin_tol";
    public static final String PROP_fragment_bin_offset = "fragment_bin_offset";
    public static final String PROP_theoretical_fragment_ions = "theoretical_fragment_ions";
//    public static final String PROP_ = "";

    public Map<Integer, String> getCometEnzymeInfos() {
        return cometEnzymeInfos;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public Properties getProps() {
        return props;
    }

    
    
    public static CometParams parse(Path path) throws ParsingException {

        CometParams cometParams = new CometParams();

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line = br.readLine();
            cometParams.firstLine = line;
        } catch (IOException e) {
            throw new ParsingException("Error reading the first line of the file", e);
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path.toString()))) {
            Properties properties = new Properties();

            properties.load(bis);
            Pattern enzymePattern = Pattern.compile("^([\\d]+)\\..*");
            Pattern valuePattern = Pattern.compile("^(.*?)(?:\\s*#.*)$");
            for (Object o : properties.keySet()) {
                if (!(o instanceof String)) {
                    throw new ParsingException("Enountered a key that is not a String");
                } else {
                    String key = (String)o;
                    String value = properties.getProperty(key);
                    Matcher matcherEnzyme = enzymePattern.matcher(key);
                    if (matcherEnzyme.matches()) {
                        cometParams.cometEnzymeInfos.put(Integer.parseInt(matcherEnzyme.group(1)), value);
                    } else {
                        Matcher m = valuePattern.matcher(value);
                        if (m.matches()) {
                            cometParams.props.put(key, m.group(1));
                        } else {
                            cometParams.props.put(key, value);
                        }
                    }
                }

            }
        } catch (IOException e) {
            throw new ParsingException("Error parsing Comet parameters", e);
        }
        return cometParams;
    }
}
