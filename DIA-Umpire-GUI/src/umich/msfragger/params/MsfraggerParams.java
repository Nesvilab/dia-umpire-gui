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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import nu.studer.java.util.OrderedProperties;
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

    
    
    public MsfraggerParams() {
        props = new OrderedProperties();
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
            load(new FileInputStream(tempFilePath().toFile()));
        } else {
            load(MsfraggerParams.class.getResourceAsStream(DEFAULT_FILE));
        }
    }
    
    public void load(InputStream is) throws IOException {
        PropertiesUtils.readProperties(is, props);
    }

    public OrderedProperties getProps() {
        return props;
    }
    
    
}
