/*
 * Copyright 2017 dmitriya.
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import nu.studer.java.util.OrderedProperties;
import umich.msfragger.params.enums.MassTolUnits;
import umich.msfragger.util.PathUtils;
import umich.msfragger.util.PropertiesUtils;

/**
 *
 * @author dmitriya
 */
public class MsfraggerParams {
    private OrderedProperties props;
    
    public static final String PROP_database_name = "database_name";
    public static final String PROP_num_threads = "num_threads";
    public static final String PROP_precursor_mass_tolerance = "precursor_mass_tolerance";
    public static final String PROP_precursor_mass_units = "precursor_mass_units";
    public static final String PROP_precursor_true_tolerance = "precursor_true_tolerance";
    public static final String PROP_precursor_true_units = "precursor_true_units";
    public static final String PROP_fragment_mass_tolerance = "fragment_mass_tolerance";
    public static final String PROP_fragment_mass_units = "fragment_mass_units";
    public static final String PROP_isotope_error = "isotope_error";
    public static final String PROP_search_enzyme_name = "search_enzyme_name";
    public static final String PROP_search_enzyme_cutafter = "search_enzyme_cutafter";
    public static final String PROP_search_enzyme_butnotafter = "search_enzyme_butnotafter";
    public static final String PROP_num_enzyme_termini = "num_enzyme_termini";
    public static final String PROP_allowed_missed_cleavage = "allowed_missed_cleavage";
    public static final String PROP_clip_nTerm_M = "clip_nTerm_M";
    
    /** Followed by '_N' (underscore and a number), max 7 mods. */
    public static final String PROP_variable_mod = "variable_mod";
    public static final int VAR_MOD_COUNT_MAX = 7;  
    public static final String PROP_allow_multiple_variable_mods_on_residue = "allow_multiple_variable_mods_on_residue";
    public static final String PROP_max_variable_mods_per_mod = "max_variable_mods_per_mod";
    public static final String PROP_max_variable_mods_combinations = "max_variable_mods_combinations";
    
    public static final String PROP_output_file_extension = "output_file_extension";
    public static final String PROP_output_report_topN = "output_report_topN";
    public static final String PROP_output_max_expect = "output_max_expect";
    public static final String PROP_precursor_charge = "precursor_charge";
    public static final String PROP_override_charge = "override_charge";
    public static final String PROP_ms_level = "ms_level";
    public static final String PROP_digest_min_length = "digest_min_length";
    public static final String PROP_digest_max_length = "digest_max_length";
    public static final String PROP_digest_mass_range = "digest_mass_range";
    public static final String PROP_max_fragment_charge = "max_fragment_charge";
    
    
    // Open search params
    
    public static final String PROP_track_zero_topN = "track_zero_topN";
    public static final String PROP_zero_bin_accept_expect = "zero_bin_accept_expect";
    public static final String PROP_zero_bin_mult_expect = "zero_bin_mult_expect";
    public static final String PROP_add_topN_complementary = "add_topN_complementary";
    
    // Spectral processing
    
    public static final String PROP_minimum_peaks = "minimum_peaks";
    public static final String PROP_use_topN_peaks = "use_topN_peaks";
    public static final String PROP_min_fragments_modelling = "min_fragments_modelling";
    public static final String PROP_min_matched_fragments = "min_matched_fragments";
    public static final String PROP_minimum_ratio = "minimum_ratio";
    public static final String PROP_clear_mz_range = "clear_mz_range";
    public static final String PROP_add = "add";
    public static final String PROP_add_enabled = "add_enabled";
    //public static final String PROP_ = "";
    
    public static final String[] ADDON_NAMES = {"Cterm_peptide", "Nterm_peptide", "Cterm_protein", "Nterm_protein",
        "G_glycine", "A_alanine", "S_serine", "P_proline", "V_valine", "T_threonine", "C_cysteine", "L_leucine", 
        "I_isoleucine", "N_asparagine", "D_aspartic_acid", "Q_glutamine", "K_lysine", "E_glutamic_acid", "M_methionine", 
        "H_histidine", "F_phenylalanine", "R_arginine", "Y_tyrosine", "W_tryptophan",
        "B_user_amino_acid", "J_user_amino_acid", "O_user_amino_acid", "U_user_amino_acid", "X_user_amino_acid", "Z_user_amino_acid",
    };
    
    public static final String[] ADDONS_HUMAN_READABLE = {"C-Term Peptide", "N-Term Peptide", "C-Term Protein", "N-Term Protein", 
        "G (glycine)", "A (alanine)", "S (serine)", "P (proline)", "V (valine)", "T (threonine)", "C (cysteine)", "L (leucine)", 
        "I (isoleucine)", "N (asparagine)", "D (aspartic acid)", "Q (glutamine)", "K (lysine)", "E (glutamic acid)", "M (methionine)", 
        "H (histidine)", "F (phenylalanine)", "R (arginine)", "Y (tyrosine)", "W (tryptophan)", 
        "B ", "J", "O", "U", "X", "Z", };
    
    public static final String FILE_BASE_NAME = "fragger";
    public static final String FILE_BASE_EXT = "params";
    /** This file is in the jar, use getResourceAsStream() to get it.  */
    public static final String DEFAULT_FILE = "fragger.params";
    private static final long serialVersionUID = 1L;

    private static final DecimalFormat DF = new DecimalFormat("0.##########");
    private Map<String, String> comments;
        
    public MsfraggerParams() {
        props = new OrderedProperties();
        comments = new HashMap<>();
        comments.put(PROP_num_threads, "0=poll CPU to set num threads; else specify num threads directly (max 64)");
        comments.put(PROP_precursor_mass_units, "0=Daltons, 1=ppm");
        comments.put(PROP_precursor_true_units, "0=Daltons, 1=ppm");
        comments.put(PROP_fragment_mass_units, "0=Daltons, 1=ppm");
        comments.put(PROP_isotope_error, "0=off, -1/0/1/2/3 (standard C13 error)");
        comments.put(PROP_num_enzyme_termini, "2 for enzymatic, 1 for semi-enzymatic, 0 for nonspecific digestion");
        comments.put(PROP_allowed_missed_cleavage, "maximum value is 5");
        comments.put(PROP_precursor_charge, "precursor charge range to analyze; does not override any existing charge; 0 as 1st entry ignores parameter");
        comments.put(PROP_override_charge, "0=no, 1=yes to override existing precursor charge states with precursor_charge parameter");
        comments.put(PROP_ms_level, "MS level to analyze, valid are levels 2 (default) or 3");
        comments.put(PROP_digest_mass_range, "MH+ peptide mass range to analyze");
        comments.put(PROP_max_fragment_charge, "set maximum fragment charge state to analyze (allowed max 5)");
        comments.put(PROP_track_zero_topN, "in addition to topN results, keep track of top results in zero bin");
        comments.put(PROP_zero_bin_accept_expect, "boost top zero bin entry to top if it has expect under 0.01 - set to 0 to disable");
        comments.put(PROP_zero_bin_mult_expect, "disabled if above passes - multiply expect of zero bin for ordering purposes (does not affect reported expect)");
        comments.put(PROP_minimum_peaks, "required minimum number of peaks in spectrum to search (default 10)");
        comments.put(PROP_minimum_ratio, "filter peaks below this fraction of strongest peak");
        comments.put(PROP_clear_mz_range, "for iTRAQ/TMT type data; will clear out all peaks in the specified m/z range");
        comments.put(PROP_allow_multiple_variable_mods_on_residue, "static mods are not considered");
        comments.put(PROP_max_variable_mods_per_mod, "maximum of 5");
        comments.put(PROP_max_variable_mods_combinations, "maximum of 65534, limits number of modified peptides generated from sequence");
    }
    
    
    
    public static Path tempFilePath() {
        return Paths.get(PathUtils.getTempDir().toString(), DEFAULT_FILE);
    }
    
    /**
     * Loads properties either from the default properties file stored in the jar
     * or from the temp directory.
     * @throws IOException 
     */
    public void load() throws IOException {
        // first check if there is a temp file saved
        if (Files.exists(tempFilePath())) {
            try (FileInputStream fis = new FileInputStream(tempFilePath().toFile())) {
                load(fis);
            }
        } else {
            loadDefaults();
        }
        fixValues();
    }
    
    public void loadDefaults() throws IOException {
        load(MsfraggerParams.class.getResourceAsStream(DEFAULT_FILE));
        fixValues();
    }
    
    public void load(InputStream is) throws IOException {
        PropertiesUtils.readProperties(is, props);
        is.close();
        fixValues();
    }
    
    private void fixValues() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : props.entrySet()) {
            String v = e.getValue().trim();
            int indexOfHash = v.indexOf("#");
            if (indexOfHash >= 0) {
                String withoutComment = v.substring(0, indexOfHash).trim();
                map.put(e.getKey(), withoutComment);
            } else {
                map.put(e.getKey(), v);
            }
        }
        for (Map.Entry<String, String> e : map.entrySet()) {
            props.removeProperty(e.getKey());
            props.setProperty(e.getKey(), e.getValue());
        }
    }
    
    /**
     * Saves the current properties contents to a default temp file.
     * @throws IOException 
     */
    public Path save() throws IOException {
        Path temp = tempFilePath();
        if (Files.exists(temp)) {
            Files.delete(temp);
        }
        save(new FileOutputStream(temp.toFile()));
        return temp;
    }
    
    /**
     * Saves the current properties contents to a stream. With comments.
     * @param os
     * @throws IOException 
     */
    public void save(OutputStream os) throws IOException {
        PropertiesUtils.writePropertiesWithComments(os, props, comments);
        os.close();
    }

    public OrderedProperties getProps() {
        return props;
    }
    
    public String getDatabaseName() {
        return props.getProperty(PROP_database_name, "");
    }
    
    public void setDatabaseName(String databaseName) {
        props.setProperty(PROP_database_name, databaseName);
    }
    
    public int getNumThreads() {
        return Integer.parseInt(props.getProperty(PROP_num_threads, "1"));
    }
    
    public void setNumThreads(int numThreads) {
        props.setProperty(PROP_num_threads, Integer.toString(numThreads));
    }
    
    
    // =======================================================================
    public MassTolUnits getPrecursorMassUnits() {
        int v = Integer.parseInt(props.getProperty(PROP_precursor_mass_units, "1"));
        for (int i = 0; i < MassTolUnits.values().length; i++) {
            MassTolUnits u = MassTolUnits.values()[i];
            if (u.valueInParamsFile() == v)
                return u;
        }
        throw new IllegalStateException("Value for MassTolUnits stored in params file for property " + PROP_precursor_mass_units + 
                " does not correspond to enum values of MassTolUnits.");
    }
    
    public void setPrecursorMassUnits(MassTolUnits u) {
        props.setProperty(PROP_precursor_mass_units, Integer.toString(u.valueInParamsFile()));
    }
    
    public double getPrecursorMassTolerance() {
        return Double.parseDouble(props.getProperty(PROP_precursor_mass_tolerance, "50.0"));
    }
    
    public void setPrecursorMassTolerance(double v) {
        props.setProperty(PROP_precursor_mass_tolerance, DF.format(v));
    }
    
    
    // =======================================================================
    public MassTolUnits getPrecursorTrueUnits() {
        int v = Integer.parseInt(props.getProperty(PROP_precursor_true_units, "1"));
        for (int i = 0; i < MassTolUnits.values().length; i++) {
            MassTolUnits u = MassTolUnits.values()[i];
            if (u.valueInParamsFile() == v)
                return u;
        }
        throw new IllegalStateException("Value for MassTolUnits stored in params file for property " + PROP_precursor_true_units + 
                " does not correspond to enum values of MassTolUnits.");
    }
    
    public void setPrecursorTrueUnits(MassTolUnits u) {
        props.setProperty(PROP_precursor_true_units, Integer.toString(u.valueInParamsFile()));
    }
    
    public double getPrecursorTrueTolerance() {
        return Double.parseDouble(props.getProperty(PROP_precursor_true_tolerance, "50.0"));
    }
    
    public void setPrecursorTrueTolerance(double v) {
        props.setProperty(PROP_precursor_true_tolerance, DF.format(v));
    }
    
    
    // =======================================================================
    public MassTolUnits getFragmentMassUnits() {
        int v = Integer.parseInt(props.getProperty(PROP_fragment_mass_units, "1"));
        for (int i = 0; i < MassTolUnits.values().length; i++) {
            MassTolUnits u = MassTolUnits.values()[i];
            if (u.valueInParamsFile() == v)
                return u;
        }
        throw new IllegalStateException("Value for MassTolUnits stored in params file for property " + PROP_fragment_mass_units + 
                " does not correspond to enum values of MassTolUnits.");
    }
    
    public void setFragmentMassUnits(MassTolUnits u) {
        props.setProperty(PROP_fragment_mass_units, Integer.toString(u.valueInParamsFile()));
    }
    
    public double getPFragmentMassTolerance() {
        return Double.parseDouble(props.getProperty(PROP_fragment_mass_tolerance, "50.0"));
    }
    
    public void setFragmentMassTolerance(double v) {
        props.setProperty(PROP_fragment_mass_tolerance, DF.format(v));
    }
    
    public String getIsotopeError() {
        return props.getProperty(PROP_isotope_error, "-1/0/1/2");
    }
}
