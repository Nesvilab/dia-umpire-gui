/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dia.umpire.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Dmitry Avtonomov
 */
public class StringUtils {
    private StringUtils() {}
    
    
    /**
     * Will trim all whitespace and return the non-zero length strings that are left.
     * @param regex e.g. "\\s+" to split on any number of whitespaces
     * @param line to be split
     * @return empty array if there was nothing but whitespace
     */
    public static List<String> split(String regex, String line) {
        line = line.trim();
        if (line.isEmpty())
            return Collections.emptyList();
        String[] split = line.split(regex);
        LinkedList<String> list = new LinkedList<>();
        for (String s : split) {
            s = s.trim();
            if (!s.isEmpty())
                list.add(s);
        }
        return list;
    }
    
    public static List<String> splitCommandLine(String line) {
        String pattern = "(\"[^\"]+\"|[^\\s\"]+)";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(line);
        LinkedList<String> list = new LinkedList<>();
        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return list;
    }
    
}
