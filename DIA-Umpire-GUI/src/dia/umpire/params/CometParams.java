package dia.umpire.params;

import dia.umpire.exceptions.ParsingException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dmitry Avtonomov on 2016-04-27.
 */
public class CometParams implements PropertyFileContent {
    public static final String COMET_ENZYME_INFO = "[COMET_ENZYME_INFO]";
    protected Map<Integer, String> cometEnzymeInfos = new TreeMap<>();
    protected String firstLine;
    protected Properties props = new Properties();
    protected List<String> linesInOriginalFile = new ArrayList<>();
    protected Map<Integer, PropLine> mapLines= new TreeMap<>();
    protected Map<String, Integer> mapProps = new HashMap<>();
    
    public static final String FILE_BASE_NAME = "comet";
    public static final String FILE_BASE_EXT = "params";

    protected String binPhilosopher;

    /** This file is in the jar, use getResourceAsStream() to get it. */
    public static final String DEFAULT_FILE = "comet.params";

    public static final String PROP_database_name = "database_name";
    public static final String PROP_peptide_mass_tolerance = "peptide_mass_tolerance";
    public static final String PROP_fragment_bin_tol = "fragment_bin_tol";
    public static final String PROP_fragment_bin_offset = "fragment_bin_offset";
    public static final String PROP_theoretical_fragment_ions = "theoretical_fragment_ions";
//    public static final String PROP_ = "";

    @Override
    public List<String> getLinesInOriginalFile() {
        return linesInOriginalFile;
    }

    public void setLinesInOriginalFile(List<String> linesInOriginalFile) {
        this.linesInOriginalFile = linesInOriginalFile;
    }

    @Override
    public Map<Integer, PropLine> getMapLines() {
        return mapLines;
    }

    public void setMapLines(Map<Integer, PropLine> mapLines) {
        this.mapLines = mapLines;
    }

    @Override
    public Map<String, Integer> getMapProps() {
        return mapProps;
    }

    public void setMapProps(Map<String, Integer> mapProps) {
        this.mapProps = mapProps;
    }

    public String getBinPhilosopher() {
        return binPhilosopher;
    }

    public void setBinPhilosopher(String binPhilosopher) {
        this.binPhilosopher = binPhilosopher;
    }
    
    public Map<Integer, String> getCometEnzymeInfos() {
        return cometEnzymeInfos;
    }

    public String getFirstLine() {
        return firstLine;
    }

    @Override
    public Properties getProps() {
        return props;
    }

    public static CometParams parseDefault() throws ParsingException {
        InputStream is = CometParams.class.getResourceAsStream(DEFAULT_FILE);
        return CometParams.parse(is);
    }

    public static CometParams parse(InputStream is) throws ParsingException {

        CometParams cometParams = new CometParams();
        Properties properties = new Properties();

        Pattern propRegex = Pattern.compile("^\\s*([^=]+?)\\s*=\\s*(.+?)\\s*", Pattern.CASE_INSENSITIVE);


        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")))) {
            int lineNum = 0;
            String line;
            while ((line = br.readLine()) != null) {
                lineNum++;
                cometParams.linesInOriginalFile.add(line);
                Properties props = new Properties();
                boolean hasComments = false;
                boolean hasProperty = false;
                int indexOfHash = line.indexOf('#');
                if (indexOfHash >= 0) {
                    hasComments = true;
                    if (indexOfHash > 2) {
                        // if it's over 2, then there is a possibility for having a
                        // property before it. A property needs at least one char for
                        // name, then '=' sign and at least one char for value
                        int indexOfEquals = line.substring(0, indexOfHash).indexOf('=');
                        if (indexOfEquals > 2) {
                            // ok, this might be a property
                            String possiblePropString = line.substring(0, indexOfHash);
                            addString(propRegex, possiblePropString, line, indexOfHash, cometParams, lineNum);
                        }
                    } else {
                        // index of hash is small and does not allow for a property, must be a simple string
                        cometParams.mapLines.put(lineNum, new PropLine(line, null, null, null));
                    }
                } else {
                    // it must be a pure property string or just a meaningless string
                    int indexOfEquals = line.indexOf('=');
                    if (indexOfEquals > 2) {
                        String possiblePropString = line;
                        addString(propRegex, possiblePropString, line, indexOfHash, cometParams, lineNum);
                    }
                }
            }

        } catch (IOException e) {
            throw new ParsingException("Error reading comet params file", e);
        }

//        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path.toString()))) {
//            Properties properties = new Properties();
//
//            properties.load(bis);
//            Pattern enzymePattern = Pattern.compile("^([\\d]+)\\..*");
//            Pattern valuePattern = Pattern.compile("^(.*?)(?:\\s*#.*)$");
//            for (Object o : properties.keySet()) {
//                if (!(o instanceof String)) {
//                    throw new ParsingException("Enountered a key that is not a String");
//                } else {
//                    String key = (String)o;
//                    String value = properties.getProperty(key);
//                    Matcher matcherEnzyme = enzymePattern.matcher(key);
//                    if (matcherEnzyme.matches()) {
//                        cometParams.cometEnzymeInfos.put(Integer.parseInt(matcherEnzyme.group(1)), value);
//                    } else {
//                        Matcher m = valuePattern.matcher(value);
//                        if (m.matches()) {
//                            cometParams.props.put(key, m.group(1));
//                        } else {
//                            cometParams.props.put(key, value);
//                        }
//                    }
//                }
//
//            }
//        } catch (IOException e) {
//            throw new ParsingException("Error parsing Comet parameters", e);
//        }
        return cometParams;
    }

    private static void addString(Pattern propRegex, String possiblePropString, String line, int indexOfHash, CometParams cometParams, int lineNum) throws ParsingException {
        Matcher matcher = propRegex.matcher(possiblePropString);
        if (matcher.matches()) {
            String comment = null;
            if (indexOfHash >= 0)
                comment = line.substring(indexOfHash, line.length());
            String propName = matcher.group(1);
            String propVal =  matcher.group(2);
            if (propName.isEmpty())
                throw new ParsingException(String.format("Property on line number %d had empty name", lineNum));
            if (propVal.isEmpty())
                throw new ParsingException(String.format("Property on line number %d had empty value", lineNum));
            cometParams.mapLines.put(lineNum, new PropLine(null, propName, propVal, comment));
            cometParams.mapProps.put(propName, lineNum);
            cometParams.props.put(propName, propVal);
        } else {
            // we didn't find the property here, it's just a line
            cometParams.mapLines.put(lineNum, new PropLine(line, null, null, null));
        }
    }
}
