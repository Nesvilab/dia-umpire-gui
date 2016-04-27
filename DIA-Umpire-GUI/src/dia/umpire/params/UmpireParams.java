/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dia.umpire.params;

import java.util.Properties;

/**
 *
 * @author dattam
 */
public class UmpireParams extends Properties {
    public static final String PROP_RPmax = "RPmax";
    public static final String PROP_RFmax = "RFmax";
    public static final String PROP_CorrThreshold = "CorrThreshold";
    public static final String PROP_DeltaApex = "DeltaApex";
    public static final String PROP_RTOverlap = "RTOverlap";
    public static final String PROP_AdjustFragIntensity = "AdjustFragIntensity";
    public static final String PROP_BoostComplementaryIon = "BoostComplementaryIon";
//    public final String PROP_ = "";
//    public final String PROP_ = "";
//    public final String PROP_ = "";
//    public final String PROP_ = "";
//    public final String PROP_ = "";

    public UmpireParams() {
    }

    public UmpireParams(Properties defaults) {
        super(defaults);
    }
    
    public Integer getRpMax() {
        String property = getProperty(PROP_RPmax);
        return Integer.parseInt(property);
    }
    
    public Integer getRfMax() {
        String property = getProperty(PROP_RFmax);
        return Integer.parseInt(property);
    }
    
    public Double getCorrThreshold() {
        String property = getProperty(PROP_CorrThreshold);
        return Double.parseDouble(property);
    }
    
    public Double getDeltaApex() {
        String property = getProperty(PROP_DeltaApex);
        return Double.parseDouble(property);
    }
    
    public Double getRTOverlap() {
        String property = getProperty(PROP_RTOverlap);
        return Double.parseDouble(property);
    }
    
    public Boolean getAdjustFragIntensity() {
        String property = getProperty(PROP_AdjustFragIntensity);
        return Boolean.valueOf(property);
    }
    
    public Boolean getBoostComplementaryIon() {
        String property = getProperty(PROP_BoostComplementaryIon);
        return Boolean.valueOf(property);
    }
}
