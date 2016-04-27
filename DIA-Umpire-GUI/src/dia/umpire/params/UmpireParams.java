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
    
    public static final String PROP_MS1PPM = "SE.MS1PPM";
    public static final String PROP_MS2PPM = "SE.MS2PPM";
    public static final String PROP_SN = "SE.SN";
    public static final String PROP_MS2SN = "SE.MS2SN";
    public static final String PROP_MinMSIntensity = "SE.MinMSIntensity";
    public static final String PROP_MinMSMSIntensity = "SE.MinMSMSIntensity";
    public static final String PROP_MaxCurveRTRange = "SE.MaxCurveRTRange";
    public static final String PROP_NoMissedScan = "SE.NoMissedScan";
    public static final String PROP_MinFrag = "SE.MinFrag";
    public static final String PROP_EstimateBG = "SE.EstimateBG";
    public static final String PROP_MinNoPeakCluster = "SE.MinNoPeakCluster";
    public static final String PROP_MaxNoPeakCluster = "SE.MaxNoPeakCluster";
    
    public static final String PROP_WindowType = "WindowType";
    public static final String PROP_WindowSize = "WindowSize";
    
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
