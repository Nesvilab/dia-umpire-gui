package test;

import dia.umpire.gui.CometParams;
import dia.umpire.exceptions.ParsingException;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by Dmitry Avtonomov on 2016-04-27.
 */
public class TestProps {
    public static void main(String[] args) throws IOException, ParsingException {
//        String path = "E:\\nesvi\\dia-umpire-params\\comet.params.txt";
//        FileInputStream fis = new FileInputStream(path);
//        BufferedInputStream bis = new BufferedInputStream(fis);
//
//        Properties properties = new Properties();
//        properties.load(bis);
//        Pattern enzymePattern = Pattern.compile("^([\\d]+)\\..*");
//        Pattern valuePattern = Pattern.compile("^(.*?)(?:\\s*#.*)$");
//        for (Object o : properties.keySet()) {
//            if (!(o instanceof String)) {
//                System.err.printf("Not a string key: %s\n", o);
//            } else {
//                System.out.printf("Found key: %s\n", o);
//                String key = (String)o;
//                Matcher matcher = enzymePattern.matcher(key);
//                if (matcher.matches()) {
//                    System.out.printf("\t-----> Found enzyme key: %d, data: %s\n",
//                                      Integer.parseInt(matcher.group(1)), properties.getProperty(key));
//                } else {
//                    String value = properties.getProperty(key);
//                    Matcher m = valuePattern.matcher(value);
//                    if (m.matches()) {
//                        System.out.printf("\t ---->Found a value: %s\n", m.group(1));
//                    }
//                }
//            }
//
//        }
        String path = "E:\\nesvi\\dia-umpire-params\\comet.params.txt";
        CometParams cometParams = CometParams.parse(Paths.get(path));

        int a = 1;
    }
}
