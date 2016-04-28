/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dia.umpire.params;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author dmitriya
 */
public interface PropertyFileContent {

    List<String> getLinesInOriginalFile();

    Map<Integer, PropLine> getMapLines();

    Map<String, Integer> getMapProps();

    Properties getProps();
    
}
