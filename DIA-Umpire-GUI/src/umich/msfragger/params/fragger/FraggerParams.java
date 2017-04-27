/*
 * Copyright 2017 Dmitry Avtonomov.
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
package umich.msfragger.params.fragger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @deprecated use {@link MsfraggerParams} instead.
 * @author Dmitry Avtonomov
 */
@Deprecated
public class FraggerParams {
    
    public static final String FILE_BASE_NAME = "fragger";
    public static final String FILE_BASE_EXT = "params";
    /** This file is in the jar, use getResourceAsStream() to get it.  */
    public static final String DEFAULT_FILE = "fragger.params";
    
    private String database_name;
    public static final String PROP_database_name = "database_name";
    
    private int num_threads;
    public static final String PROP_num_threads = "num_threads";
    
    private double precursor_mass_tolerance;
    public static final String PROP_precursor_mass_tolerance = "precursor_mass_tolerance";

    private int precursor_mass_units;
    public static final String PROP_precursor_mass_units = "precursor_mass_units";

    private double precursor_true_tolerance;
    public static final String PROP_precursor_true_tolerance = "precursor_true_tolerance";

    private int precursor_true_units;
    public static final String PROP_precursor_true_units = "precursor_true_units";
    
    private double fragment_mass_tolerance;
    public static final String PROP_fragment_mass_tolerance = "fragment_mass_tolerance";

    private int fragment_mass_units;
    public static final String PROP_fragment_mass_units = "fragment_mass_units";
    
    private String isotope_error;
    public static final String PROP_isotope_error = "isotope_error";
    
    private String search_enzyme_name;
    public static final String PROP_search_enzyme_name = "search_enzyme_name";
    
    private String search_enzyme_cutafter;
    public static final String PROP_search_enzyme_cutafter = "search_enzyme_cutafter";
    
    private String search_enzyme_butnotafter;
    public static final String PROP_search_enzyme_butnotafter = "search_enzyme_butnotafter";
    
    private int num_enzyme_termini;
    public static final String PROP_num_enzyme_termini = "num_enzyme_termini";
    
    private int allowed_missed_cleavage;
    public static final String PROP_allowed_missed_cleavage = "allowed_missed_cleavage";
    
    private int clip_nTerm_M;
    public static final String PROP_clip_nTerm_M = "clip_nTerm_M";
    
    private String[] variable_mod;
    public static final String PROP_variable_mod = "variable_mod";

    private boolean[] variable_mod_enabled;
    public static final String PROP_variable_mod_enabled = "variable_mod_enabled";

    private String output_file_extension;
    public static final String PROP_output_file_extension = "output_file_extension";
    
    private int output_report_topN;
    public static final String PROP_output_report_topN = "output_report_topN";
    
    private double output_max_expect;
    public static final String PROP_output_max_expect = "output_max_expect";
    
    private int[] precursor_charge;
    public static final String PROP_precursor_charge = "precursor_charge";
    
    private int override_charge;
    public static final String PROP_override_charge = "override_charge";
    
    private int ms_level;
    public static final String PROP_ms_level = "ms_level";
    
    private int digest_min_length;
    public static final String PROP_digest_min_length = "digest_min_length";
    
    private int digest_max_length;
    public static final String PROP_digest_max_length = "digest_max_length";
    
    private double[] digest_mass_range;
    public static final String PROP_digest_mass_range = "digest_mass_range";
    
    private int max_fragment_charge;
    public static final String PROP_max_fragment_charge = "max_fragment_charge";
    
    private int track_zero_topN;
    public static final String PROP_track_zero_topN = "track_zero_topN";
    
    private int zero_bin_accept_expect;
    public static final String PROP_zero_bin_accept_expect = "zero_bin_accept_expect";
    
    private int zero_bin_mult_expect;
    public static final String PROP_zero_bin_mult_expect = "zero_bin_mult_expect";
    
    private int add_topN_complementary;
    public static final String PROP_add_topN_complementary = "add_topN_complementary";
    
    private int minimum_peaks;
    public static final String PROP_minimum_peaks = "minimum_peaks";
    
    private int use_topN_peaks;
    public static final String PROP_use_topN_peaks = "use_topN_peaks";
    
    private int min_fragments_modelling;
    public static final String PROP_min_fragments_modelling = "min_fragments_modelling";
    
    private int min_matched_fragments;
    public static final String PROP_min_matched_fragments = "min_matched_fragments";
    
    private double minimum_ratio;
    public static final String PROP_minimum_ratio = "minimum_ratio";
    
    private double[] clear_mz_range;
    public static final String PROP_clear_mz_range = "clear_mz_range";
    
    private String[] add;

    public static final String PROP_add = "add";

        
    /**
     * Get the value of add
     *
     * @return the value of add
     */
    public String[] getAdd() {
        return add;
    }

    /**
     * Set the value of add
     *
     * @param add new value of add
     */
    public void setAdd(String[] add) {
        String[] oldAdd = this.add;
        this.add = add;
        propertyChangeSupport.firePropertyChange(PROP_add, oldAdd, add);
    }

    /**
     * Get the value of add at specified index
     *
     * @param index the index of add
     * @return the value of add at specified index
     */
    public String getAdd(int index) {
        return this.add[index];
    }

    /**
     * Set the value of add at specified index.
     *
     * @param index the index of add
     * @param add new value of add at specified index
     */
    public void setAdd(int index, String add) {
        String oldAdd = this.add[index];
        this.add[index] = add;
        propertyChangeSupport.fireIndexedPropertyChange(PROP_add, index, oldAdd, add);
    }


    /**
     * Get the value of clear_mz_range
     *
     * @return the value of clear_mz_range
     */
    public double[] getClear_mz_range() {
        return clear_mz_range;
    }

    /**
     * Set the value of clear_mz_range
     *
     * @param clear_mz_range new value of clear_mz_range
     */
    public void setClear_mz_range(double[] clear_mz_range) {
        double[] oldClear_mz_range = this.clear_mz_range;
        this.clear_mz_range = clear_mz_range;
        propertyChangeSupport.firePropertyChange(PROP_clear_mz_range, oldClear_mz_range, clear_mz_range);
    }

    /**
     * Get the value of clear_mz_range at specified index
     *
     * @param index the index of clear_mz_range
     * @return the value of clear_mz_range at specified index
     */
    public double getClear_mz_range(int index) {
        return this.clear_mz_range[index];
    }

    /**
     * Set the value of clear_mz_range at specified index.
     *
     * @param index the index of clear_mz_range
     * @param clear_mz_range new value of clear_mz_range at specified index
     */
    public void setClear_mz_range(int index, double clear_mz_range) {
        double oldClear_mz_range = this.clear_mz_range[index];
        this.clear_mz_range[index] = clear_mz_range;
        propertyChangeSupport.fireIndexedPropertyChange(PROP_clear_mz_range, index, oldClear_mz_range, clear_mz_range);
    }


    /**
     * Get the value of minimum_ratio
     *
     * @return the value of minimum_ratio
     */
    public double getMinimum_ratio() {
        return minimum_ratio;
    }

    /**
     * Set the value of minimum_ratio
     *
     * @param minimum_ratio new value of minimum_ratio
     */
    public void setMinimum_ratio(double minimum_ratio) {
        double oldMinimum_ratio = this.minimum_ratio;
        this.minimum_ratio = minimum_ratio;
        propertyChangeSupport.firePropertyChange(PROP_minimum_ratio, oldMinimum_ratio, minimum_ratio);
    }


    /**
     * Get the value of min_matched_fragments
     *
     * @return the value of min_matched_fragments
     */
    public int getMin_matched_fragments() {
        return min_matched_fragments;
    }

    /**
     * Set the value of min_matched_fragments
     *
     * @param min_matched_fragments new value of min_matched_fragments
     */
    public void setMin_matched_fragments(int min_matched_fragments) {
        int oldMin_matched_fragments = this.min_matched_fragments;
        this.min_matched_fragments = min_matched_fragments;
        propertyChangeSupport.firePropertyChange(PROP_min_matched_fragments, oldMin_matched_fragments, min_matched_fragments);
    }


    /**
     * Get the value of min_fragments_modelling
     *
     * @return the value of min_fragments_modelling
     */
    public int getMin_fragments_modelling() {
        return min_fragments_modelling;
    }

    /**
     * Set the value of min_fragments_modelling
     *
     * @param min_fragments_modelling new value of min_fragments_modelling
     */
    public void setMin_fragments_modelling(int min_fragments_modelling) {
        int oldMin_fragments_modelling = this.min_fragments_modelling;
        this.min_fragments_modelling = min_fragments_modelling;
        propertyChangeSupport.firePropertyChange(PROP_min_fragments_modelling, oldMin_fragments_modelling, min_fragments_modelling);
    }


    /**
     * Get the value of use_topN_peaks
     *
     * @return the value of use_topN_peaks
     */
    public int getUse_topN_peaks() {
        return use_topN_peaks;
    }

    /**
     * Set the value of use_topN_peaks
     *
     * @param use_topN_peaks new value of use_topN_peaks
     */
    public void setUse_topN_peaks(int use_topN_peaks) {
        int oldUse_topN_peaks = this.use_topN_peaks;
        this.use_topN_peaks = use_topN_peaks;
        propertyChangeSupport.firePropertyChange(PROP_use_topN_peaks, oldUse_topN_peaks, use_topN_peaks);
    }


    /**
     * Get the value of minimum_peaks
     *
     * @return the value of minimum_peaks
     */
    public int getMinimum_peaks() {
        return minimum_peaks;
    }

    /**
     * Set the value of minimum_peaks
     *
     * @param minimum_peaks new value of minimum_peaks
     */
    public void setMinimum_peaks(int minimum_peaks) {
        int oldMinimum_peaks = this.minimum_peaks;
        this.minimum_peaks = minimum_peaks;
        propertyChangeSupport.firePropertyChange(PROP_minimum_peaks, oldMinimum_peaks, minimum_peaks);
    }

    
    /**
     * Get the value of add_topN_complementary
     *
     * @return the value of add_topN_complementary
     */
    public int getAdd_topN_complementary() {
        return add_topN_complementary;
    }

    /**
     * Set the value of add_topN_complementary
     *
     * @param add_topN_complementary new value of add_topN_complementary
     */
    public void setAdd_topN_complementary(int add_topN_complementary) {
        int oldAdd_topN_complementary = this.add_topN_complementary;
        this.add_topN_complementary = add_topN_complementary;
        propertyChangeSupport.firePropertyChange(PROP_add_topN_complementary, oldAdd_topN_complementary, add_topN_complementary);
    }


    /**
     * Get the value of zero_bin_mult_expect
     *
     * @return the value of zero_bin_mult_expect
     */
    public int getZero_bin_mult_expect() {
        return zero_bin_mult_expect;
    }

    /**
     * Set the value of zero_bin_mult_expect
     *
     * @param zero_bin_mult_expect new value of zero_bin_mult_expect
     */
    public void setZero_bin_mult_expect(int zero_bin_mult_expect) {
        int oldZero_bin_mult_expect = this.zero_bin_mult_expect;
        this.zero_bin_mult_expect = zero_bin_mult_expect;
        propertyChangeSupport.firePropertyChange(PROP_zero_bin_mult_expect, oldZero_bin_mult_expect, zero_bin_mult_expect);
    }


    /**
     * Get the value of zero_bin_accept_expect
     *
     * @return the value of zero_bin_accept_expect
     */
    public int getZero_bin_accept_expect() {
        return zero_bin_accept_expect;
    }

    /**
     * Set the value of zero_bin_accept_expect
     *
     * @param zero_bin_accept_expect new value of zero_bin_accept_expect
     */
    public void setZero_bin_accept_expect(int zero_bin_accept_expect) {
        int oldZero_bin_accept_expect = this.zero_bin_accept_expect;
        this.zero_bin_accept_expect = zero_bin_accept_expect;
        propertyChangeSupport.firePropertyChange(PROP_zero_bin_accept_expect, oldZero_bin_accept_expect, zero_bin_accept_expect);
    }


    /**
     * Get the value of track_zero_topN
     *
     * @return the value of track_zero_topN
     */
    public int getTrack_zero_topN() {
        return track_zero_topN;
    }

    /**
     * Set the value of track_zero_topN
     *
     * @param track_zero_topN new value of track_zero_topN
     */
    public void setTrack_zero_topN(int track_zero_topN) {
        int oldTrack_zero_topN = this.track_zero_topN;
        this.track_zero_topN = track_zero_topN;
        propertyChangeSupport.firePropertyChange(PROP_track_zero_topN, oldTrack_zero_topN, track_zero_topN);
    }


    /**
     * Get the value of max_fragment_charge
     *
     * @return the value of max_fragment_charge
     */
    public int getMax_fragment_charge() {
        return max_fragment_charge;
    }

    /**
     * Set the value of max_fragment_charge
     *
     * @param max_fragment_charge new value of max_fragment_charge
     */
    public void setMax_fragment_charge(int max_fragment_charge) {
        int oldMax_fragment_charge = this.max_fragment_charge;
        this.max_fragment_charge = max_fragment_charge;
        propertyChangeSupport.firePropertyChange(PROP_max_fragment_charge, oldMax_fragment_charge, max_fragment_charge);
    }


    /**
     * Get the value of digest_mass_range
     *
     * @return the value of digest_mass_range
     */
    public double[] getDigest_mass_range() {
        return digest_mass_range;
    }

    /**
     * Set the value of digest_mass_range
     *
     * @param digest_mass_range new value of digest_mass_range
     */
    public void setDigest_mass_range(double[] digest_mass_range) {
        double[] oldDigest_mass_range = this.digest_mass_range;
        this.digest_mass_range = digest_mass_range;
        propertyChangeSupport.firePropertyChange(PROP_digest_mass_range, oldDigest_mass_range, digest_mass_range);
    }

    /**
     * Get the value of digest_mass_range at specified index
     *
     * @param index the index of digest_mass_range
     * @return the value of digest_mass_range at specified index
     */
    public double getDigest_mass_range(int index) {
        return this.digest_mass_range[index];
    }

    /**
     * Set the value of digest_mass_range at specified index.
     *
     * @param index the index of digest_mass_range
     * @param digest_mass_range new value of digest_mass_range at specified
     * index
     */
    public void setDigest_mass_range(int index, double digest_mass_range) {
        double oldDigest_mass_range = this.digest_mass_range[index];
        this.digest_mass_range[index] = digest_mass_range;
        propertyChangeSupport.fireIndexedPropertyChange(PROP_digest_mass_range, index, oldDigest_mass_range, digest_mass_range);
    }


    /**
     * Get the value of digest_max_length
     *
     * @return the value of digest_max_length
     */
    public int getDigest_max_length() {
        return digest_max_length;
    }

    /**
     * Set the value of digest_max_length
     *
     * @param digest_max_length new value of digest_max_length
     */
    public void setDigest_max_length(int digest_max_length) {
        int oldDigest_max_length = this.digest_max_length;
        this.digest_max_length = digest_max_length;
        propertyChangeSupport.firePropertyChange(PROP_digest_max_length, oldDigest_max_length, digest_max_length);
    }


    /**
     * Get the value of digest_min_length
     *
     * @return the value of digest_min_length
     */
    public int getDigest_min_length() {
        return digest_min_length;
    }

    /**
     * Set the value of digest_min_length
     *
     * @param digest_min_length new value of digest_min_length
     */
    public void setDigest_min_length(int digest_min_length) {
        int oldDigest_min_length = this.digest_min_length;
        this.digest_min_length = digest_min_length;
        propertyChangeSupport.firePropertyChange(PROP_digest_min_length, oldDigest_min_length, digest_min_length);
    }


    /**
     * Get the value of ms_level
     *
     * @return the value of ms_level
     */
    public int getMs_level() {
        return ms_level;
    }

    /**
     * Set the value of ms_level
     *
     * @param ms_level new value of ms_level
     */
    public void setMs_level(int ms_level) {
        int oldMs_level = this.ms_level;
        this.ms_level = ms_level;
        propertyChangeSupport.firePropertyChange(PROP_ms_level, oldMs_level, ms_level);
    }


    /**
     * Get the value of override_charge
     *
     * @return the value of override_charge
     */
    public int getOverride_charge() {
        return override_charge;
    }

    /**
     * Set the value of override_charge
     *
     * @param override_charge new value of override_charge
     */
    public void setOverride_charge(int override_charge) {
        int oldOverride_charge = this.override_charge;
        this.override_charge = override_charge;
        propertyChangeSupport.firePropertyChange(PROP_override_charge, oldOverride_charge, override_charge);
    }


    /**
     * Get the value of precursor_charge
     *
     * @return the value of precursor_charge
     */
    public int[] getPrecursor_charge() {
        return precursor_charge;
    }

    /**
     * Set the value of precursor_charge
     *
     * @param precursor_charge new value of precursor_charge
     */
    public void setPrecursor_charge(int[] precursor_charge) {
        int[] oldPrecursor_charge = this.precursor_charge;
        this.precursor_charge = precursor_charge;
        propertyChangeSupport.firePropertyChange(PROP_precursor_charge, oldPrecursor_charge, precursor_charge);
    }

    /**
     * Get the value of precursor_charge at specified index
     *
     * @param index the index of precursor_charge
     * @return the value of precursor_charge at specified index
     */
    public int getPrecursor_charge(int index) {
        return this.precursor_charge[index];
    }

    /**
     * Set the value of precursor_charge at specified index.
     *
     * @param index the index of precursor_charge
     * @param precursor_charge new value of precursor_charge at specified index
     */
    public void setPrecursor_charge(int index, int precursor_charge) {
        int oldPrecursor_charge = this.precursor_charge[index];
        this.precursor_charge[index] = precursor_charge;
        propertyChangeSupport.fireIndexedPropertyChange(PROP_precursor_charge, index, oldPrecursor_charge, precursor_charge);
    }


    /**
     * Get the value of output_max_expect
     *
     * @return the value of output_max_expect
     */
    public double getOutput_max_expect() {
        return output_max_expect;
    }

    /**
     * Set the value of output_max_expect
     *
     * @param output_max_expect new value of output_max_expect
     */
    public void setOutput_max_expect(double output_max_expect) {
        double oldOutput_max_expect = this.output_max_expect;
        this.output_max_expect = output_max_expect;
        propertyChangeSupport.firePropertyChange(PROP_output_max_expect, oldOutput_max_expect, output_max_expect);
    }


    /**
     * Get the value of output_report_topN
     *
     * @return the value of output_report_topN
     */
    public int getOutput_report_topN() {
        return output_report_topN;
    }

    /**
     * Set the value of output_report_topN
     *
     * @param output_report_topN new value of output_report_topN
     */
    public void setOutput_report_topN(int output_report_topN) {
        int oldOutput_report_topN = this.output_report_topN;
        this.output_report_topN = output_report_topN;
        propertyChangeSupport.firePropertyChange(PROP_output_report_topN, oldOutput_report_topN, output_report_topN);
    }


    /**
     * Get the value of output_file_extension
     *
     * @return the value of output_file_extension
     */
    public String getOutput_file_extension() {
        return output_file_extension;
    }

    /**
     * Set the value of output_file_extension
     *
     * @param output_file_extension new value of output_file_extension
     */
    public void setOutput_file_extension(String output_file_extension) {
        String oldOutput_file_extension = this.output_file_extension;
        this.output_file_extension = output_file_extension;
        propertyChangeSupport.firePropertyChange(PROP_output_file_extension, oldOutput_file_extension, output_file_extension);
    }

    
    /**
     * Get the value of variable_mod_enabled
     *
     * @return the value of variable_mod_enabled
     */
    public boolean[] isVariable_mod_enabled() {
        return variable_mod_enabled;
    }

    /**
     * Set the value of variable_mod_enabled
     *
     * @param variable_mod_enabled new value of variable_mod_enabled
     */
    public void setVariable_mod_enabled(boolean[] variable_mod_enabled) {
        boolean[] oldVariable_mod_enabled = this.variable_mod_enabled;
        this.variable_mod_enabled = variable_mod_enabled;
        propertyChangeSupport.firePropertyChange(PROP_variable_mod_enabled, oldVariable_mod_enabled, variable_mod_enabled);
    }

    /**
     * Get the value of variable_mod_enabled at specified index
     *
     * @param index the index of variable_mod_enabled
     * @return the value of variable_mod_enabled at specified index
     */
    public boolean isVariable_mod_enabled(int index) {
        return this.variable_mod_enabled[index];
    }

    /**
     * Set the value of variable_mod_enabled at specified index.
     *
     * @param index the index of variable_mod_enabled
     * @param variable_mod_enabled new value of variable_mod_enabled at
     * specified index
     */
    public void setVariable_mod_enabled(int index, boolean variable_mod_enabled) {
        boolean oldVariable_mod_enabled = this.variable_mod_enabled[index];
        this.variable_mod_enabled[index] = variable_mod_enabled;
        propertyChangeSupport.fireIndexedPropertyChange(PROP_variable_mod_enabled, index, oldVariable_mod_enabled, variable_mod_enabled);
    }

    
    /**
     * Get the value of variable_mod
     *
     * @return the value of variable_mod
     */
    public String[] getVariable_mod() {
        return variable_mod;
    }

    /**
     * Set the value of variable_mod
     *
     * @param variable_mod new value of variable_mod
     */
    public void setVariable_mod(String[] variable_mod) {
        String[] oldVariable_mod = this.variable_mod;
        this.variable_mod = variable_mod;
        propertyChangeSupport.firePropertyChange(PROP_variable_mod, oldVariable_mod, variable_mod);
    }

    /**
     * Get the value of variable_mod at specified index
     *
     * @param index the index of variable_mod
     * @return the value of variable_mod at specified index
     */
    public String getVariable_mod(int index) {
        return this.variable_mod[index];
    }

    /**
     * Set the value of variable_mod at specified index.
     *
     * @param index the index of variable_mod
     * @param variable_mod new value of variable_mod at specified index
     */
    public void setVariable_mod(int index, String variable_mod) {
        String oldVariable_mod = this.variable_mod[index];
        this.variable_mod[index] = variable_mod;
        propertyChangeSupport.fireIndexedPropertyChange(PROP_variable_mod, index, oldVariable_mod, variable_mod);
    }



    /**
     * Get the value of clip_nTerm_M
     *
     * @return the value of clip_nTerm_M
     */
    public int getClip_nTerm_M() {
        return clip_nTerm_M;
    }

    /**
     * Set the value of clip_nTerm_M
     *
     * @param clip_nTerm_M new value of clip_nTerm_M
     */
    public void setClip_nTerm_M(int clip_nTerm_M) {
        int oldClip_nTerm_M = this.clip_nTerm_M;
        this.clip_nTerm_M = clip_nTerm_M;
        propertyChangeSupport.firePropertyChange(PROP_clip_nTerm_M, oldClip_nTerm_M, clip_nTerm_M);
    }

    

    /**
     * Get the value of num_enzyme_termini
     *
     * @return the value of num_enzyme_termini
     */
    public int getNum_enzyme_termini() {
        return num_enzyme_termini;
    }

    /**
     * Set the value of num_enzyme_termini
     *
     * @param num_enzyme_termini new value of num_enzyme_termini
     */
    public void setNum_enzyme_termini(int num_enzyme_termini) {
        int oldNum_enzyme_termini = this.num_enzyme_termini;
        this.num_enzyme_termini = num_enzyme_termini;
        propertyChangeSupport.firePropertyChange(PROP_num_enzyme_termini, oldNum_enzyme_termini, num_enzyme_termini);
    }

    /**
     * Get the value of allowed_missed_cleavage
     *
     * @return the value of allowed_missed_cleavage
     */
    public int getAllowed_missed_cleavage() {
        return allowed_missed_cleavage;
    }

    /**
     * Set the value of allowed_missed_cleavage
     *
     * @param allowed_missed_cleavage new value of allowed_missed_cleavage
     */
    public void setAllowed_missed_cleavage(int allowed_missed_cleavage) {
        int oldAllowed_missed_cleavage = this.allowed_missed_cleavage;
        this.allowed_missed_cleavage = allowed_missed_cleavage;
        propertyChangeSupport.firePropertyChange(PROP_allowed_missed_cleavage, oldAllowed_missed_cleavage, allowed_missed_cleavage);
    }


    /**
     * Get the value of search_enzyme_butnotafter
     *
     * @return the value of search_enzyme_butnotafter
     */
    public String getSearch_enzyme_butnotafter() {
        return search_enzyme_butnotafter;
    }

    /**
     * Set the value of search_enzyme_butnotafter
     *
     * @param search_enzyme_butnotafter new value of search_enzyme_butnotafter
     */
    public void setSearch_enzyme_butnotafter(String search_enzyme_butnotafter) {
        String oldSearch_enzyme_butnotafter = this.search_enzyme_butnotafter;
        this.search_enzyme_butnotafter = search_enzyme_butnotafter;
        propertyChangeSupport.firePropertyChange(PROP_search_enzyme_butnotafter, oldSearch_enzyme_butnotafter, search_enzyme_butnotafter);
    }


    /**
     * Get the value of search_enzyme_cutafter
     *
     * @return the value of search_enzyme_cutafter
     */
    public String getSearch_enzyme_cutafter() {
        return search_enzyme_cutafter;
    }

    /**
     * Set the value of search_enzyme_cutafter
     *
     * @param search_enzyme_cutafter new value of search_enzyme_cutafter
     */
    public void setSearch_enzyme_cutafter(String search_enzyme_cutafter) {
        String oldSearch_enzyme_cutafter = this.search_enzyme_cutafter;
        this.search_enzyme_cutafter = search_enzyme_cutafter;
        propertyChangeSupport.firePropertyChange(PROP_search_enzyme_cutafter, oldSearch_enzyme_cutafter, search_enzyme_cutafter);
    }


    /**
     * Get the value of search_enzyme_name
     *
     * @return the value of search_enzyme_name
     */
    public String getSearch_enzyme_name() {
        return search_enzyme_name;
    }

    /**
     * Set the value of search_enzyme_name
     *
     * @param search_enzyme_name new value of search_enzyme_name
     */
    public void setSearch_enzyme_name(String search_enzyme_name) {
        String oldSearch_enzyme_name = this.search_enzyme_name;
        this.search_enzyme_name = search_enzyme_name;
        propertyChangeSupport.firePropertyChange(PROP_search_enzyme_name, oldSearch_enzyme_name, search_enzyme_name);
    }


    /**
     * Get the value of isotope_error
     *
     * @return the value of isotope_error
     */
    public String getIsotope_error() {
        return isotope_error;
    }

    /**
     * Set the value of isotope_error
     *
     * @param isotope_error new value of isotope_error
     */
    public void setIsotope_error(String isotope_error) {
        String oldIsotope_error = this.isotope_error;
        this.isotope_error = isotope_error;
        propertyChangeSupport.firePropertyChange(PROP_isotope_error, oldIsotope_error, isotope_error);
    }


    /**
     * Get the value of fragment_mass_units
     *
     * @return the value of fragment_mass_units
     */
    public int getFragment_mass_units() {
        return fragment_mass_units;
    }

    /**
     * Set the value of fragment_mass_units
     *
     * @param fragment_mass_units new value of fragment_mass_units
     */
    public void setFragment_mass_units(int fragment_mass_units) {
        int oldFragment_mass_units = this.fragment_mass_units;
        this.fragment_mass_units = fragment_mass_units;
        propertyChangeSupport.firePropertyChange(PROP_fragment_mass_units, oldFragment_mass_units, fragment_mass_units);
    }

    
    /**
     * Get the value of fragment_mass_tolerance
     *
     * @return the value of fragment_mass_tolerance
     */
    public double getFragment_mass_tolerance() {
        return fragment_mass_tolerance;
    }

    /**
     * Set the value of fragment_mass_tolerance
     *
     * @param fragment_mass_tolerance new value of fragment_mass_tolerance
     */
    public void setFragment_mass_tolerance(double fragment_mass_tolerance) {
        double oldFragment_mass_tolerance = this.fragment_mass_tolerance;
        this.fragment_mass_tolerance = fragment_mass_tolerance;
        propertyChangeSupport.firePropertyChange(PROP_fragment_mass_tolerance, oldFragment_mass_tolerance, fragment_mass_tolerance);
    }


    /**
     * Get the value of precursor_true_units
     *
     * @return the value of precursor_true_units
     */
    public int getPrecursor_true_units() {
        return precursor_true_units;
    }

    /**
     * Set the value of precursor_true_units
     *
     * @param precursor_true_units new value of precursor_true_units
     */
    public void setPrecursor_true_units(int precursor_true_units) {
        int oldPrecursor_true_units = this.precursor_true_units;
        this.precursor_true_units = precursor_true_units;
        propertyChangeSupport.firePropertyChange(PROP_precursor_true_units, oldPrecursor_true_units, precursor_true_units);
    }

    
    /**
     * Get the value of precursor_true_tolerance
     *
     * @return the value of precursor_true_tolerance
     */
    public double getPrecursor_true_tolerance() {
        return precursor_true_tolerance;
    }

    /**
     * Set the value of precursor_true_tolerance
     *
     * @param precursor_true_tolerance new value of precursor_true_tolerance
     */
    public void setPrecursor_true_tolerance(double precursor_true_tolerance) {
        double oldPrecursor_true_tolerance = this.precursor_true_tolerance;
        this.precursor_true_tolerance = precursor_true_tolerance;
        propertyChangeSupport.firePropertyChange(PROP_precursor_true_tolerance, oldPrecursor_true_tolerance, precursor_true_tolerance);
    }

    
    /**
     * Get the value of precursor_mass_units
     *
     * @return the value of precursor_mass_units
     */
    public int getPrecursor_mass_units() {
        return precursor_mass_units;
    }

    /**
     * Set the value of precursor_mass_units
     *
     * @param precursor_mass_units new value of precursor_mass_units
     */
    public void setPrecursor_mass_units(int precursor_mass_units) {
        int oldPrecursor_mass_units = this.precursor_mass_units;
        this.precursor_mass_units = precursor_mass_units;
        propertyChangeSupport.firePropertyChange(PROP_precursor_mass_units, oldPrecursor_mass_units, precursor_mass_units);
    }

    /**
     * Get the value of precursor_mass_tolerance
     *
     * @return the value of precursor_mass_tolerance
     */
    public double getPrecursor_mass_tolerance() {
        return precursor_mass_tolerance;
    }

    /**
     * Set the value of precursor_mass_tolerance
     *
     * @param precursor_mass_tolerance new value of precursor_mass_tolerance
     */
    public void setPrecursor_mass_tolerance(double precursor_mass_tolerance) {
        double oldPrecursor_mass_tolerance = this.precursor_mass_tolerance;
        this.precursor_mass_tolerance = precursor_mass_tolerance;
        propertyChangeSupport.firePropertyChange(PROP_precursor_mass_tolerance, oldPrecursor_mass_tolerance, precursor_mass_tolerance);
    }

    

    /**
     * Get the value of num_threads
     *
     * @return the value of num_threads
     */
    public int getNum_threads() {
        return num_threads;
    }

    /**
     * Set the value of num_threads
     *
     * @param num_threads new value of num_threads
     */
    public void setNum_threads(int num_threads) {
        int oldNum_threads = this.num_threads;
        this.num_threads = num_threads;
        propertyChangeSupport.firePropertyChange(PROP_num_threads, oldNum_threads, num_threads);
    }

    

    /**
     * Get the value of database_name
     *
     * @return the value of database_name
     */
    public String getDatabase_name() {
        return database_name;
    }

    /**
     * Set the value of database_name
     *
     * @param database_name new value of database_name
     */
    public void setDatabase_name(String database_name) {
        String oldDatabase_name = this.database_name;
        this.database_name = database_name;
        propertyChangeSupport.firePropertyChange(PROP_database_name, oldDatabase_name, database_name);
    }

    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
