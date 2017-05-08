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
package umich.msfragger.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import net.java.balloontip.BalloonTip;
import umich.msfragger.gui.renderers.TableCellDoubleRenderer;
import umich.msfragger.params.fragger.MsfraggerParams;
import umich.msfragger.params.ThisAppProps;
import umich.msfragger.params.enums.CleavageType;
import umich.msfragger.params.enums.MassTolUnits;
import umich.msfragger.params.enums.MsLevel;
import umich.msfragger.params.fragger.Mod;
import umich.msfragger.util.DocumentFilters;
import umich.msfragger.util.StringUtils;
import umich.msfragger.util.SwingUtils;
/**
 *
 * @author Dmitry Avtonomov
 */
public class FraggerPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    public static final String PROP_FILECHOOSER_LAST_PATH = "msfragger.filechooser.path";
    
    private MsfraggerParams params;
    private static final String[] TABLE_VAR_MODS_COL_NAMES = {"Enabled", "Site (editable)", "Mass Delta (editable)"};
    private ModificationsTableModel tableModelVarMods;
    private static final String[] TABLE_ADD_MODS_COL_NAMES = {"Enabled", "Site", "Mass Delta (editable)"};
    private ModificationsTableModel tableModelAddMods;
    
    public static FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("LCMS files (mzML/mzXML/mgf)", "mzml", "mzxml", "mgf");
    
    
    /**
     * Creates new form FraggerPanel
     */
    public FraggerPanel() {
        initComponents();
        initMore();
    }

    private void initMore() {
        updateRowHeights(tableVarMods);
        tableVarMods.setDefaultRenderer(Double.class, new TableCellDoubleRenderer());
        tableVarMods.setFillsViewportHeight(true);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tableVarMods.getColumnModel().getColumn(0).setMaxWidth(150);
                tableVarMods.getColumnModel().getColumn(0).setMinWidth(20);
                tableVarMods.getColumnModel().getColumn(0).setPreferredWidth(50);
            }
        });
        tableAdditionalMods.setDefaultRenderer(Double.class, new TableCellDoubleRenderer());
        tableAdditionalMods.setFillsViewportHeight(true);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tableAdditionalMods.getColumnModel().getColumn(0).setMaxWidth(150);
                tableAdditionalMods.getColumnModel().getColumn(0).setMinWidth(20);
                tableAdditionalMods.getColumnModel().getColumn(0).setPreferredWidth(50);
            }
        });
        
        params = new MsfraggerParams();
        try {
            params.load();
            fillFormFromParams(params);
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Could not load default fragger params neither from temp file "
                            + "nor from the included one in the jar: " + ex.getMessage(), 
                    "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public String getFastaPath() {
        return textMsfraggerDb.getText().trim();
    }
    
    public String getFraggerBin() {
        return txtBinMsfragger.getText().trim();
    }
    
    public int getRamGb() {
        return (Integer)spinnerFraggerRam.getValue();
    }
    
    public String getOutputFileExt() {
        return textOutputFileExt.getText().trim();
    }
    
    private void fillFormFromParams(MsfraggerParams params) {
        textMsfraggerDb.setText(params.getDatabaseName());
        textFraggerDbFocusLost(null);
        spinnerFraggerThreads.setValue(params.getNumThreads());
        
        comboPrecursorMassTol.setSelectedItem(params.getPrecursorMassUnits().toString());
        spinnerPrecursorMassTol.setValue(params.getPrecursorMassTolerance());
        
        comboPrecursorTrueTol.setSelectedItem(params.getPrecursorTrueUnits().toString());
        spinnerPrecursorTrueTol.setValue(params.getPrecursorTrueTolerance());
        
        comboFragMassTol.setSelectedItem(params.getFragmentMassUnits().toString());
        spinnerFragMassTol.setValue(params.getFragmentMassTolerance());
        
        textOutputFileExt.setText(params.getOutputFileExtension());
        spinnerReportTopN.setValue(params.getOutputReportTopN());
        spinnerOutputMaxExpect.setValue(params.getOutputMaxExpect());
        
        textIsotopeError.setText(params.getIsotopeError());
        comboMsLevel.setSelectedItem(params.getMsLevel());
        spinnerPrecursorChargeLo.setValue(params.getPrecursorCharge()[0]);
        spinnerPrecursorChargeHi.setValue(params.getPrecursorCharge()[1]);
        checkOverrideCharge.setSelected(params.getOverrideCharge());
        
        textEnzymeName.setText(params.getSearchEnzymeName());
        textCutAfter.setText(params.getSearchEnzymeCutAfter());
        textButNotAfter.setText(params.getSearchEnzymeButNotAfter());
        
        comboCleavage.setSelectedItem(params.getNumEnzymeTermini().toString());
        spinnerMissedCleavages.setValue(params.getAllowedMissedCleavage());
        checkClipNTerm.setSelected(params.getClipNTermM());
        
        spinnerDigestLenMin.setValue(params.getDigestMinLength());
        spinnerDigestLenMax.setValue(params.getDigestMaxLength());
        spinnerDigestMassMin.setValue(params.getDigestMassRange()[0]);
        spinnerDigestMassMax.setValue(params.getDigestMassRange()[1]);
        spinnerMaxFragCharge.setValue(params.getMaxFragmentCharge());
        
        spinnerMinPeaks.setValue(params.getMinimumPeaks());
        spinnerUseTopNPeaks.setValue(params.getUseTopNPeaks());
        spinnerMinFragsModelling.setValue(params.getMinFragmentsModelling());
        spinnerMinMatchedFrags.setValue(params.getMinMatchedFragments());
        spinnerMinRatio.setValue(params.getMinimumRatio());
        spinnerClearMzRangeMin.setValue(params.getClearMzRange()[0]);
        spinnerClearMzRangeMax.setValue(params.getClearMzRange()[1]);
        
//        checkZeroBinAcceptExpect.setSelected(params.getZeroBinAcceptExpect());
//        checkZeroBinMultiplyExpect.setSelected(params.getZeroBinMultExpect());
//        checkTrackZeroTopN.setSelected(params.getTrackZeroTopN());
//        checkAddTopNComplementary.setSelected(params.getAddTopNComplementary());
        spinnerZeroBinAcceptExpect.setValue(params.getZeroBinAcceptExpect());
        spinnerZeroBinMultiplyExpect.setValue(params.getZeroBinMultExpect());
        spinnerTrackZeroTopN.setValue(params.getTrackZeroTopN());
        spinnerAddTopNComplementary.setValue(params.getAddTopNComplementary());
        
        checkMultipleVarMods.setSelected(params.getAllowMultipleVariableModsOnResidue());
        spinnerMaxVarModsPerMod.setValue(params.getMaxVariableModsPerMod());
        spinnerMaxCombos.setValue(params.getMaxVariableModsCombinations());
        
        // variable modifications
        Object[][] varModsData = new Object[MsfraggerParams.VAR_MOD_COUNT_MAX][3];
        for (int i = 0; i < MsfraggerParams.VAR_MOD_COUNT_MAX; i++) {
            varModsData[i][0] = false;
            varModsData[i][1] = null;
            varModsData[i][2] = null;
        }
        List<Mod> varMods = params.getVariableMods();
        for (int i = 0; i < varMods.size(); i++) {
            Mod m = varMods.get(i);
            varModsData[i][0] = m.isEnabled;
            varModsData[i][1] = m.sites;
            varModsData[i][2] = m.massDelta;
        }
        tableModelVarMods.setDataVector(varModsData, TABLE_VAR_MODS_COL_NAMES);
        
        // fixed modifications
        Object[][] addModsData = new Object[MsfraggerParams.ADDON_NAMES.length][3];
        for (int i = 0; i < MsfraggerParams.ADDON_NAMES.length; i++) {
            addModsData[i][0] = false;
            addModsData[i][1] = null;
            addModsData[i][2] = null;
        }
        List<Mod> addMods = params.getAdditionalMods();
        for (int i = 0; i < addMods.size(); i++) {
            Mod m = addMods.get(i);
            addModsData[i][0] = m.isEnabled;
            addModsData[i][1] = m.sites;
            addModsData[i][2] = m.massDelta;
        }
        tableModelAddMods.setDataVector(addModsData, TABLE_ADD_MODS_COL_NAMES);
    }
    
    public MsfraggerParams collectParams() throws IOException {
        MsfraggerParams p = new MsfraggerParams();
        p.loadDefaults();
        fillParamsFromForm(p);
        return p;
    }
    
    private void fillParamsFromForm(MsfraggerParams params) {
        params.setDatabaseName(textMsfraggerDb.getText());
        params.setNumThreads(utilSpinnerValue(spinnerMinPeaks, Integer.class));
        
        params.setPrecursorMassUnits(MassTolUnits.valueOf(comboPrecursorMassTol.getItemAt(comboPrecursorMassTol.getSelectedIndex())));
        params.setPrecursorMassTolerance((Double)spinnerPrecursorMassTol.getValue());
        
        params.setPrecursorTrueUnits(MassTolUnits.valueOf(comboPrecursorTrueTol.getItemAt(comboPrecursorTrueTol.getSelectedIndex())));
        params.setPrecursorTrueTolerance((Double)spinnerPrecursorTrueTol.getValue());
        
        params.setFragmentMassUnits(MassTolUnits.valueOf(comboFragMassTol.getItemAt(comboFragMassTol.getSelectedIndex())));
        params.setFragmentMassTolerance((Double)spinnerFragMassTol.getValue());
        
        params.setOutputFileExtension(textOutputFileExt.getText());
        params.setOutputReportTopN((Integer)spinnerReportTopN.getValue());
        params.setOutputMaxExpect((Double)spinnerOutputMaxExpect.getValue());
        
        params.setIsotopeError(textIsotopeError.getText());
        params.setMsLevel(MsLevel.valueOf(comboMsLevel.getItemAt(comboMsLevel.getSelectedIndex())));
        
        int zLo = (Integer)spinnerPrecursorChargeLo.getValue();
        int zHi = (Integer)spinnerPrecursorChargeHi.getValue();
        params.setPrecursorCharge(new int[] {zLo, zHi});
        params.setOverrideCharge(checkOverrideCharge.isSelected());
        
        params.setSearchEnzymeName(textEnzymeName.getText());
        params.setSearchEnzymeCutAfter(textCutAfter.getText());
        params.setSearchEnzymeButNotAfter(textButNotAfter.getText());
        
        params.setNumEnzymeTermini(CleavageType.valueOf(comboCleavage.getItemAt(comboCleavage.getSelectedIndex())));
        params.setAllowedMissedCleavage((Integer)spinnerMissedCleavages.getValue());
        params.setClipNTermM(checkClipNTerm.isSelected());
        
        params.setDigestMinLength((Integer)spinnerDigestLenMin.getValue());
        params.setDigestMaxLength((Integer)spinnerDigestLenMax.getValue());
        
        double massLo = (Double)spinnerDigestMassMin.getValue();
        double massHi = (Double)spinnerDigestMassMax.getValue();
        params.setDigestMassRange(new double[] {massLo, massHi});
        params.setMaxFragmentCharge((Integer)spinnerMaxFragCharge.getValue());
        
        params.setMinimumPeaks(utilSpinnerValue(spinnerMinPeaks, Integer.class));
        params.setUseTopNPeaks((Integer)spinnerUseTopNPeaks.getValue());
        
        params.setMinFragmentsModelling((Integer)spinnerMinFragsModelling.getValue());
        params.setMinMatchedFragments((Integer)spinnerMinMatchedFrags.getValue());
        params.setMinimumRatio((Double)spinnerMinRatio.getValue());
        
        double clearMzLo = (Double)spinnerClearMzRangeMin.getValue();
        double clearMzHi = (Double)spinnerClearMzRangeMax.getValue();
        params.setClearMzRange(new double[] {clearMzLo, clearMzHi});
        
//        params.setZeroBinAcceptExpect(checkZeroBinAcceptExpect.isSelected());
//        params.setZeroBinMultExpect(checkZeroBinMultiplyExpect.isSelected());
//        params.setTrackZeroTopN(checkTrackZeroTopN.isSelected());
//        params.setAddTopNComplementary(checkAddTopNComplementary.isSelected());
        params.setTrackZeroTopN((Integer)spinnerTrackZeroTopN.getValue());
        params.setZeroBinAcceptExpect((Double)spinnerZeroBinAcceptExpect.getValue());
        params.setZeroBinMultExpect((Double)spinnerZeroBinMultiplyExpect.getValue());
        params.setAddTopNComplementary((Integer)spinnerAddTopNComplementary.getValue());
        
        params.setAllowMultipleVariableModsOnResidue(checkMultipleVarMods.isSelected());
        params.setMaxVariableModsPerMod((Integer)spinnerMaxVarModsPerMod.getValue());
        params.setMaxVariableModsCombinations((Integer)spinnerMaxCombos.getValue());
        
        List<Mod> modsVar = tableModelVarMods.getModifications();
        params.setVariableMods(modsVar);
        
        List<Mod> modsAdd = tableModelAddMods.getModifications();
        params.setAdditionalMods(modsAdd);
    }
    
    private<T> T utilSpinnerValue(JSpinner spinner, Class<T> clazz) {
        return (T)spinner.getValue();
    }
    
    public String getPepxmlExtension() {
        return textOutputFileExt.getText().trim();
    }
    
    public MsfraggerParams getParamsFromForm() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    private synchronized TableModel getDefaultVarModTableModel() {
        if (tableModelVarMods != null)
            return tableModelVarMods;
        int cols = 3;
        Object[][] data = new Object[MsfraggerParams.VAR_MOD_COUNT_MAX][cols];
        for (int i = 0; i < data.length; i++) {
            data[i][0] = false;
            data[i][1] = null;
            data[i][2] = null;
        }

        tableModelVarMods = new ModificationsTableModel(
                TABLE_VAR_MODS_COL_NAMES,
                new Class<?>[] { Boolean.class, String.class, Double.class },
                new boolean[] {true, true, true},
                new int[] {0, 1, 2},
                data);

        return tableModelVarMods;
    }
    
    private synchronized TableModel getDefaultAddonTableModel() {
        if (tableModelAddMods != null)
            return tableModelAddMods;
        
        int cols = 3;
        Object[][] data = new Object[MsfraggerParams.ADDONS_HUMAN_READABLE.length][cols];
        for (int i = 0; i < data.length; i++) {
            data[i][0] = false;
            data[i][1] = MsfraggerParams.ADDONS_HUMAN_READABLE[i];
            data[i][2] = 0.0;
        }

        tableModelAddMods = new ModificationsTableModel(
                TABLE_ADD_MODS_COL_NAMES,
                new Class<?>[] {Boolean.class, String.class, Double.class},
                new boolean[] {true, false, true},
                new int[] {0, 1, 2},
                data);
        
        return tableModelAddMods;
    }
    
    private void updateRowHeights(JTable table) {
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = table.getRowHeight();

            for (int column = 0; column < table.getColumnCount(); column++) {
                Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }

            table.setRowHeight(row, rowHeight);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMsFragger = new javax.swing.JPanel();
        panelMsfraggerParams = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        textMsfraggerDb = new javax.swing.JTextField();
        btnSelectMsfraggerDb = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        comboCleavage = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        textEnzymeName = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        textCutAfter = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        textButNotAfter = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        spinnerMissedCleavages = new javax.swing.JSpinner();
        checkClipNTerm = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        spinnerDigestLenMin = new javax.swing.JSpinner();
        jLabel15 = new javax.swing.JLabel();
        spinnerDigestLenMax = new javax.swing.JSpinner();
        jLabel16 = new javax.swing.JLabel();
        spinnerDigestMassMin = new javax.swing.JSpinner();
        jLabel17 = new javax.swing.JLabel();
        spinnerDigestMassMax = new javax.swing.JSpinner();
        jLabel18 = new javax.swing.JLabel();
        spinnerMaxFragCharge = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        checkMultipleVarMods = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        spinnerMaxVarModsPerMod = new javax.swing.JSpinner();
        jLabel13 = new javax.swing.JLabel();
        spinnerMaxCombos = new javax.swing.JSpinner();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableVarMods = new javax.swing.JTable();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableAdditionalMods = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        spinnerMinPeaks = new javax.swing.JSpinner();
        jLabel21 = new javax.swing.JLabel();
        spinnerMinFragsModelling = new javax.swing.JSpinner();
        jLabel23 = new javax.swing.JLabel();
        spinnerMinRatio = new javax.swing.JSpinner();
        jLabel20 = new javax.swing.JLabel();
        spinnerUseTopNPeaks = new javax.swing.JSpinner();
        jLabel22 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        spinnerMinMatchedFrags = new javax.swing.JSpinner();
        spinnerClearMzRangeMin = new javax.swing.JSpinner();
        jLabel26 = new javax.swing.JLabel();
        spinnerClearMzRangeMax = new javax.swing.JSpinner();
        jPanel4 = new javax.swing.JPanel();
        spinnerTrackZeroTopN = new javax.swing.JSpinner();
        lblTrackZeroTopN = new javax.swing.JLabel();
        lblZeroBinAcceptExpect = new javax.swing.JLabel();
        spinnerZeroBinAcceptExpect = new javax.swing.JSpinner();
        lblZeroBinMultiplyExpect = new javax.swing.JLabel();
        spinnerZeroBinMultiplyExpect = new javax.swing.JSpinner();
        lblAddTopNComplementary = new javax.swing.JLabel();
        spinnerAddTopNComplementary = new javax.swing.JSpinner();
        btnDefaults = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnLoad = new javax.swing.JButton();
        spinnerFraggerThreads = new javax.swing.JSpinner();
        jLabel33 = new javax.swing.JLabel();
        spinnerFraggerRam = new javax.swing.JSpinner();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        comboPrecursorMassTol = new javax.swing.JComboBox<>();
        spinnerPrecursorMassTol = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        comboPrecursorTrueTol = new javax.swing.JComboBox<>();
        spinnerPrecursorTrueTol = new javax.swing.JSpinner();
        jLabel25 = new javax.swing.JLabel();
        textOutputFileExt = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        spinnerReportTopN = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        comboFragMassTol = new javax.swing.JComboBox<>();
        spinnerFragMassTol = new javax.swing.JSpinner();
        jLabel28 = new javax.swing.JLabel();
        spinnerOutputMaxExpect = new javax.swing.JSpinner();
        textIsotopeError = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        comboMsLevel = new javax.swing.JComboBox<>();
        jLabel30 = new javax.swing.JLabel();
        spinnerPrecursorChargeLo = new javax.swing.JSpinner();
        jLabel29 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        spinnerPrecursorChargeHi = new javax.swing.JSpinner();
        checkOverrideCharge = new javax.swing.JCheckBox();
        chkRunMsfragger = new javax.swing.JCheckBox();
        panelMsfraggerBin = new javax.swing.JPanel();
        btnSelectMsfraggerBin = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtBinMsfragger = new javax.swing.JTextField();
        btnDownloadMsfragger = new javax.swing.JButton();

        panelMsfraggerParams.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));

        jLabel1.setText("Sequence Database");
        jLabel1.setToolTipText("<html>Location of sequence database");

        textMsfraggerDb.setToolTipText("<html>Location of sequence database");
        textMsfraggerDb.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textFraggerDbFocusLost(evt);
            }
        });

        btnSelectMsfraggerDb.setText("Browse");
        btnSelectMsfraggerDb.setToolTipText("<html>Location of sequence database");
        btnSelectMsfraggerDb.setActionCommand("btnFraggerDb");
        btnSelectMsfraggerDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectMsfraggerDbActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Digest"));

        comboCleavage.setModel(createCleavageComboBoxModel());

        jLabel11.setText("Cleavage");

        jLabel8.setText("Enzyme Name");

        textEnzymeName.setText("Trypsin");
        textEnzymeName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textEnzymeNameActionPerformed(evt);
            }
        });

        jLabel9.setText("Cut After");

        textCutAfter.setDocument(DocumentFilters.getFilter("[^A-Z]+"));
        textCutAfter.setText("KR");
        textCutAfter.setToolTipText("Enzyme cleaves after these residues");

        jLabel10.setText("But Not After");

        textButNotAfter.setDocument(DocumentFilters.getFilter("[^A-Z]+"));
        textButNotAfter.setText("P");
        textButNotAfter.setToolTipText("Enzyme cleaves if previous residues are not followed by this one");

        jLabel7.setText("Missed Cleavages");

        spinnerMissedCleavages.setModel(new javax.swing.SpinnerNumberModel(1, 0, 5, 1));

        checkClipNTerm.setSelected(true);
        checkClipNTerm.setText("Clip N-Term M");

        jLabel14.setText("Digest Length");

        spinnerDigestLenMin.setModel(new javax.swing.SpinnerNumberModel(5, 3, null, 1));
        spinnerDigestLenMin.setToolTipText("Min Peptide Length");

        jLabel15.setText("-");

        spinnerDigestLenMax.setModel(new javax.swing.SpinnerNumberModel(60, 3, null, 1));
        spinnerDigestLenMax.setToolTipText("Max Peptide Length");

        jLabel16.setText("Digest mass range");

        spinnerDigestMassMin.setModel(new javax.swing.SpinnerNumberModel(500.0d, 0.0d, null, 500.0d));

        jLabel17.setText("-");

        spinnerDigestMassMax.setModel(new javax.swing.SpinnerNumberModel(7000.0d, 0.0d, 7000.0d, 500.0d));

        jLabel18.setText("Max Fragment Charge");

        spinnerMaxFragCharge.setModel(new javax.swing.SpinnerNumberModel(2, 0, null, 1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel11)
                            .addComponent(jLabel8))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textEnzymeName, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(comboCleavage, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textCutAfter, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textButNotAfter, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinnerMissedCleavages, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(spinnerDigestLenMin, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel15))
                                    .addComponent(jLabel18))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(spinnerMaxFragCharge)
                                    .addComponent(spinnerDigestLenMax, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spinnerDigestMassMin, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spinnerDigestMassMax, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(checkClipNTerm))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(textEnzymeName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(textCutAfter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(textButNotAfter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboCleavage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel7)
                    .addComponent(spinnerMissedCleavages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(spinnerDigestLenMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(spinnerDigestLenMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(spinnerDigestMassMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(spinnerDigestMassMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(spinnerMaxFragCharge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkClipNTerm)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Modifications"));

        checkMultipleVarMods.setSelected(true);
        checkMultipleVarMods.setText("Multiple variable mods on residue");

        jLabel12.setText("Max var mods per mod");

        spinnerMaxVarModsPerMod.setModel(new javax.swing.SpinnerNumberModel(2, 0, 5, 1));

        jLabel13.setText("Max combos");

        spinnerMaxCombos.setModel(new javax.swing.SpinnerNumberModel(5000, 1, 65534, 50));

        tableVarMods.setModel(getDefaultVarModTableModel());
        tableVarMods.setToolTipText("<html>Variable Modifications.<br/>\nValues:<br/>\n<ul>\n<li>A-Z amino acid codes</li>\n<li>\"*\" for any amino acid</li>\n<li>\"[\" and \"]\" specifies protein termini</li>\n<li>\"n\" and \"c\" specifies peptide termini</li>\n</ul>");
        jScrollPane1.setViewportView(tableVarMods);

        jLabel31.setText("Variable Modifications");

        jLabel32.setText("Additional Modifications");

        tableAdditionalMods.setModel(getDefaultAddonTableModel());
        jScrollPane2.setViewportView(tableAdditionalMods);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spinnerMaxVarModsPerMod, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel13))
                                    .addComponent(checkMultipleVarMods))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinnerMaxCombos, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel32)
                            .addComponent(jLabel31))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(checkMultipleVarMods)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(spinnerMaxVarModsPerMod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(spinnerMaxCombos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Spectral Processing"));

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel19.setText("Min peaks");

        spinnerMinPeaks.setModel(new javax.swing.SpinnerNumberModel(6, 0, null, 1));
        spinnerMinPeaks.setToolTipText("required minimum number of peaks in spectrum to search (default 10)");

        jLabel21.setText("Min Frags Modelling");

        jLabel23.setText("Min Ratio");
        jLabel23.setToolTipText("filter peaks below this fraction of strongest peak");

        spinnerMinRatio.setModel(new javax.swing.SpinnerNumberModel(0.01d, 0.0d, null, 0.1d));
        spinnerMinRatio.setToolTipText("filter peaks below this fraction of strongest peak");

        jLabel20.setText("Use Top N Peaks");

        spinnerUseTopNPeaks.setModel(new javax.swing.SpinnerNumberModel(100, 0, null, 10));

        jLabel22.setText("Min Matched Frags");

        jLabel24.setText("Clear m/z range");
        jLabel24.setToolTipText("for iTRAQ/TMT type data; will clear out all peaks in the specified m/z range");

        spinnerMinMatchedFrags.setModel(new javax.swing.SpinnerNumberModel(6, 0, null, 1));

        spinnerClearMzRangeMin.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 10.0d));
        spinnerClearMzRangeMin.setToolTipText("for iTRAQ/TMT type data; will clear out all peaks in the specified m/z range");

        jLabel26.setText("-");

        spinnerClearMzRangeMax.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 10.0d));
        spinnerClearMzRangeMax.setToolTipText("for iTRAQ/TMT type data; will clear out all peaks in the specified m/z range");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel23)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(spinnerMinRatio, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(jLabel24))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(spinnerMinPeaks, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel20))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(spinnerMinFragsModelling, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel22))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spinnerUseTopNPeaks, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addComponent(spinnerClearMzRangeMin)
                    .addComponent(spinnerMinMatchedFrags))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinnerClearMzRangeMax, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinnerMinPeaks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(spinnerUseTopNPeaks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(spinnerMinFragsModelling, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(spinnerMinMatchedFrags, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(spinnerMinRatio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(spinnerClearMzRangeMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(spinnerClearMzRangeMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Open Search Parameters"));

        spinnerTrackZeroTopN.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 5));
        spinnerTrackZeroTopN.setToolTipText(lblTrackZeroTopN.getToolTipText());

        lblTrackZeroTopN.setText("Track Zero Top N");
        lblTrackZeroTopN.setToolTipText("<html>Track top N unmodified peptide results separately<br/>\nfrom main results internally for boosting features.<br/>\nShould be set to a number greater than<br/>\noutput_report_topN if zero bin boosting is desired<br/>"); // NOI18N

        lblZeroBinAcceptExpect.setText("Zero Bin Accept Expect");
        lblZeroBinAcceptExpect.setToolTipText("<html>Ranks a zero-bin hit above all non-zero-bin hit if it<br/>\nhas expectation less than this value.");

        spinnerZeroBinAcceptExpect.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 0.1d));
        spinnerZeroBinAcceptExpect.setToolTipText(lblZeroBinAcceptExpect.getToolTipText());

        lblZeroBinMultiplyExpect.setText("Zero Bin Multiply Expect");
        lblZeroBinMultiplyExpect.setToolTipText("<html>Multiplies expect value of PSMs in the zero-bin<br/>\nduring results ordering (set to less than 1 for<br/>\nboosting)"); // NOI18N

        spinnerZeroBinMultiplyExpect.setModel(new javax.swing.SpinnerNumberModel(1.0d, 0.0d, 1.0d, 0.05d));
        spinnerZeroBinMultiplyExpect.setToolTipText(lblZeroBinMultiplyExpect.getToolTipText());

        lblAddTopNComplementary.setText("Add Top N Complementary");
        lblAddTopNComplementary.setToolTipText("<html>Inserts complementary ions corresponding to the top<br/>\nN most intense fragments in each experimental<br/>\nspectra. Useful for recovery of modified peptides<br/>\nnear C-terminal in open search. Should be set to 0<br/>\n(disabled) otherwise"); // NOI18N

        spinnerAddTopNComplementary.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 2));
        spinnerAddTopNComplementary.setToolTipText(lblAddTopNComplementary.getToolTipText());

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblZeroBinAcceptExpect)
                    .addComponent(lblTrackZeroTopN))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(spinnerZeroBinAcceptExpect, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(lblZeroBinMultiplyExpect))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(spinnerTrackZeroTopN, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblAddTopNComplementary)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spinnerZeroBinMultiplyExpect, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                    .addComponent(spinnerAddTopNComplementary))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinnerTrackZeroTopN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTrackZeroTopN)
                    .addComponent(lblAddTopNComplementary)
                    .addComponent(spinnerAddTopNComplementary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblZeroBinAcceptExpect)
                    .addComponent(spinnerZeroBinAcceptExpect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblZeroBinMultiplyExpect)
                    .addComponent(spinnerZeroBinMultiplyExpect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        btnDefaults.setText("Defaults");
        btnDefaults.setToolTipText("Load default parameters");
        btnDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultsActionPerformed(evt);
            }
        });

        btnSave.setText("Save");
        btnSave.setToolTipText("<html>Save current configuration.");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnLoad.setText("Load");
        btnLoad.setToolTipText("<html>Load a previously saved configuration.<br/>\nYou can find a copy of parameter files (fragger.params) in your old <br/>\nsearch results directories, they are copied there automatically.");
        btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadActionPerformed(evt);
            }
        });

        spinnerFraggerThreads.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        jLabel33.setText("Threads");

        spinnerFraggerRam.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        jLabel34.setText("RAM");

        jLabel35.setText("GB");

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Matching Configuration"));

        jLabel3.setText("Precursor Mass Tolerance");

        comboPrecursorMassTol.setModel(createMassToleranceUnitsComboModel());
        comboPrecursorMassTol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboPrecursorMassTolActionPerformed(evt);
            }
        });

        spinnerPrecursorMassTol.setModel(new javax.swing.SpinnerNumberModel(50.0d, 0.0d, null, 5.0d));

        jLabel4.setText("Precursor True Tolerance");

        comboPrecursorTrueTol.setModel(createMassToleranceUnitsComboModel());
        comboPrecursorTrueTol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboPrecursorTrueTolActionPerformed(evt);
            }
        });

        spinnerPrecursorTrueTol.setModel(new javax.swing.SpinnerNumberModel(50.0d, 0.0d, null, 5.0d));

        jLabel25.setText("Output File Ext");

        textOutputFileExt.setText("pepXML");

        jLabel27.setText("Report Top N");

        spinnerReportTopN.setModel(new javax.swing.SpinnerNumberModel(3, 1, null, 1));

        jLabel5.setText("Fragment Mass Tolerance");

        comboFragMassTol.setModel(createMassToleranceUnitsComboModel());

        spinnerFragMassTol.setModel(new javax.swing.SpinnerNumberModel(50.0d, 0.0d, null, 5.0d));

        jLabel28.setText("Output Max Expect");

        spinnerOutputMaxExpect.setModel(new javax.swing.SpinnerNumberModel(50.0d, 0.0d, null, 1.0d));

        textIsotopeError.setDocument(getFilterIsotopeCorrection());
        textIsotopeError.setText("-1/0/1/2");
        textIsotopeError.setToolTipText("0=off, -1/0/1/2/3 (standard C13 error)");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText("Isotope Error");
        jLabel6.setToolTipText("0=off, -1/0/1/2/3 (standard C13 error)");

        comboMsLevel.setModel(createMsLevelComboBoxModel());

        jLabel30.setText("MS Level");

        jLabel29.setText("Precursor Charge");

        jLabel36.setText("-");

        checkOverrideCharge.setText("Override Charge");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(spinnerPrecursorChargeLo, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinnerPrecursorChargeHi))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(comboFragMassTol, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinnerFragMassTol, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(textIsotopeError))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkOverrideCharge)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel28)
                                    .addComponent(jLabel30))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(comboMsLevel, 0, 100, Short.MAX_VALUE)
                                    .addComponent(spinnerOutputMaxExpect)))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboPrecursorTrueTol, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinnerPrecursorTrueTol, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboPrecursorMassTol, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinnerPrecursorMassTol, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(39, 39, 39)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel27)
                            .addComponent(jLabel25))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(spinnerReportTopN, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textOutputFileExt))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(comboPrecursorMassTol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinnerPrecursorMassTol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(textOutputFileExt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(comboPrecursorTrueTol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinnerPrecursorTrueTol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)
                    .addComponent(spinnerReportTopN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboFragMassTol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinnerFragMassTol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel28)
                    .addComponent(spinnerOutputMaxExpect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textIsotopeError, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(comboMsLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinnerPrecursorChargeLo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)
                    .addComponent(jLabel36)
                    .addComponent(spinnerPrecursorChargeHi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkOverrideCharge)))
        );

        javax.swing.GroupLayout panelMsfraggerParamsLayout = new javax.swing.GroupLayout(panelMsfraggerParams);
        panelMsfraggerParams.setLayout(panelMsfraggerParamsLayout);
        panelMsfraggerParamsLayout.setHorizontalGroup(
            panelMsfraggerParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelMsfraggerParamsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMsfraggerParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMsfraggerParamsLayout.createSequentialGroup()
                        .addComponent(btnDefaults)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLoad)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerFraggerRam, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerFraggerThreads, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelMsfraggerParamsLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(textMsfraggerDb)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSelectMsfraggerDb)
                        .addContainerGap())))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelMsfraggerParamsLayout.setVerticalGroup(
            panelMsfraggerParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMsfraggerParamsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMsfraggerParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDefaults)
                    .addComponent(btnSave)
                    .addComponent(btnLoad)
                    .addComponent(spinnerFraggerThreads, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33)
                    .addComponent(spinnerFraggerRam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelMsfraggerParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(textMsfraggerDb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSelectMsfraggerDb))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        chkRunMsfragger.setSelected(true);
        chkRunMsfragger.setText("Run MSFragger");
        chkRunMsfragger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRunMsfraggerActionPerformed(evt);
            }
        });

        panelMsfraggerBin.setBorder(javax.swing.BorderFactory.createTitledBorder("MSFragger"));

        btnSelectMsfraggerBin.setText("Browse");
        btnSelectMsfraggerBin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectMsfraggerBinActionPerformed(evt);
            }
        });

        jLabel2.setText("MSFragger");

        txtBinMsfragger.setText(getDefaultTextMsfragger());
        txtBinMsfragger.setToolTipText("<html>Path to MSFragger binary (.jar file).<br/>\nIt will be searched in the \"Programs\" directory (check \"Run\" tab)<br/>\nand in system's PATH.");
        txtBinMsfragger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBinMsfraggerActionPerformed(evt);
            }
        });

        btnDownloadMsfragger.setText("Download MSFragger");
        btnDownloadMsfragger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadMsfraggerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelMsfraggerBinLayout = new javax.swing.GroupLayout(panelMsfraggerBin);
        panelMsfraggerBin.setLayout(panelMsfraggerBinLayout);
        panelMsfraggerBinLayout.setHorizontalGroup(
            panelMsfraggerBinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMsfraggerBinLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBinMsfragger)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSelectMsfraggerBin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDownloadMsfragger))
        );
        panelMsfraggerBinLayout.setVerticalGroup(
            panelMsfraggerBinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMsfraggerBinLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMsfraggerBinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelectMsfraggerBin)
                    .addComponent(jLabel2)
                    .addComponent(txtBinMsfragger, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDownloadMsfragger))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelMsFraggerLayout = new javax.swing.GroupLayout(panelMsFragger);
        panelMsFragger.setLayout(panelMsFraggerLayout);
        panelMsFraggerLayout.setHorizontalGroup(
            panelMsFraggerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMsFraggerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMsFraggerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelMsfraggerParams, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkRunMsfragger)
                    .addComponent(panelMsfraggerBin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelMsFraggerLayout.setVerticalGroup(
            panelMsFraggerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMsFraggerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkRunMsfragger)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelMsfraggerBin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelMsfraggerParams, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelMsFragger, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelMsFragger, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSelectMsfraggerBinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectMsfraggerBinActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setApproveButtonText("Select binary");
        fileChooser.setApproveButtonToolTipText("Select MSFragger jar");
        fileChooser.setDialogTitle("Select MSFragger jar");
        fileChooser.setMultiSelectionEnabled(false);
        //        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("JAR files", "jar");
        //        fileChooser.setFileFilter(fileNameExtensionFilter);

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        List<String> props = Arrays.asList(ThisAppProps.PROP_BIN_PATH_MSFRAGGER, ThisAppProps.PROP_BINARIES_IN);
        String fcPath = ThisAppProps.tryFindPath(props, true);
        SwingUtils.setFileChooserPath(fileChooser, fcPath);

        int showOpenDialog = fileChooser.showOpenDialog(SwingUtils.findParentComponentForDialog(this));
        switch (showOpenDialog) {
            case JFileChooser.APPROVE_OPTION:

                File f = fileChooser.getSelectedFile();
                txtBinMsfragger.setText(f.getAbsolutePath());
                ThisAppProps.save(ThisAppProps.PROP_BIN_PATH_MSFRAGGER, f);
                break;
        }
    }//GEN-LAST:event_btnSelectMsfraggerBinActionPerformed

    private void chkRunMsfraggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRunMsfraggerActionPerformed
        boolean selected = chkRunMsfragger.isSelected();
        Container[] comps;
        comps = new Container[] {
            panelMsfraggerBin,
            panelMsfraggerParams
        };
        for (Container c : comps) {
            SwingUtils.enableComponents(c, selected);
        }
    }//GEN-LAST:event_chkRunMsfraggerActionPerformed

    private void btnLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setApproveButtonText("Load");
        fc.setApproveButtonToolTipText("Load into the form");
        fc.setDialogTitle("Select saved file");
        fc.setMultiSelectionEnabled(false);
        String cached = ThisAppProps.load(PROP_FILECHOOSER_LAST_PATH);
        SwingUtils.setFileChooserPath(fc, cached);
        if (cached != null) {
            Path p = Paths.get(cached);
            if (Files.exists(p))
            fc.setSelectedFile(p.toFile());
        }
        fc.setAcceptAllFileFilterUsed(true);
        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("Properties/Params", "properties", "params", "para", "conf");
        fc.setFileFilter(fileNameExtensionFilter);

        Component parent = SwingUtils.findParentComponentForDialog(this);
        int saveResult = fc.showOpenDialog(parent);
        if (JFileChooser.APPROVE_OPTION == saveResult) {
            File selectedFile = fc.getSelectedFile();
            Path path = Paths.get(selectedFile.getAbsolutePath());
            if (Files.exists(path)) {
                try {
                    params.load(new FileInputStream(selectedFile));
                    fillFormFromParams(params);
                    params.save();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(parent, "<html>Could not load the saved file: <br/>" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parent, "<html>This is strange,<br/> "
                    + "but the file you chose to load doesn't exist anymore.", "Strange", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnLoadActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed

        // first save the current state to a temp file
        try {
            fillParamsFromForm(params);
            params.save();
        } catch (IOException ex) {
            // don't need to show anything, worst case we can't save temp files
        }

        // now save the actual user's choice
        JFileChooser fc = new JFileChooser();
        fc.setApproveButtonText("Save");
        fc.setApproveButtonToolTipText("Save to a file");
        fc.setDialogTitle("Choose where params file should be saved");
        fc.setMultiSelectionEnabled(false);
        SwingUtils.setFileChooserPath(fc, ThisAppProps.load(PROP_FILECHOOSER_LAST_PATH));
        fc.setSelectedFile(new File(MsfraggerParams.DEFAULT_FILE));
        Component parent = SwingUtils.findParentComponentForDialog(this);
        int saveResult = fc.showSaveDialog(parent);
        if (JFileChooser.APPROVE_OPTION == saveResult) {
            File selectedFile = fc.getSelectedFile();
            Path path = Paths.get(selectedFile.getAbsolutePath());
            // if exists, overwrite
            if (Files.exists(path)) {
                int overwrite = JOptionPane.showConfirmDialog(parent, "<html>File exists,<br/> overwrtie?", "Overwrite", JOptionPane.OK_CANCEL_OPTION);
                if (JOptionPane.OK_OPTION == overwrite) {
                    try {
                        Files.delete(path);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(parent, "Could not overwrite", "Overwrite", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            try {
                ThisAppProps.save(PROP_FILECHOOSER_LAST_PATH, path.toAbsolutePath().toString());
                MsfraggerParams saved = new MsfraggerParams();
                saved.load();               // load defaults
                fillParamsFromForm(saved);  // overwrite with data from form
                saved.save(new FileOutputStream(path.toFile()));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "<html>Could not save file: <br/>" + path.toString() +
                    "<br/>" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultsActionPerformed
        int confirmation = JOptionPane.showConfirmDialog(SwingUtils.findParentComponentForDialog(this),
            "Are you sure you want to load defaults?", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
        if (JOptionPane.OK_OPTION == confirmation) {
            try {
                params.loadDefaults();
                fillFormFromParams(params);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Could not load defaults", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnDefaultsActionPerformed

    private void textEnzymeNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textEnzymeNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textEnzymeNameActionPerformed

    private void comboPrecursorTrueTolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboPrecursorTrueTolActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboPrecursorTrueTolActionPerformed

    private void comboPrecursorMassTolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboPrecursorMassTolActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboPrecursorMassTolActionPerformed

    public JCheckBox getCheckboxIsRunFragger() {
        return chkRunMsfragger;
    }
    
    private void btnSelectMsfraggerDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectMsfraggerDbActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("FASTA files", "fa", "fasta");
        fileChooser.setFileFilter(fileNameExtensionFilter);
        fileChooser.setApproveButtonText("Select file");
        fileChooser.setApproveButtonToolTipText("Select");
        fileChooser.setDialogTitle("Choose FASTA file");
        fileChooser.setMultiSelectionEnabled(false);

        
        if (!StringUtils.isNullOrWhitespace(textMsfraggerDb.getText())) {
            try {
                File toFile = Paths.get(textMsfraggerDb.getText()).toFile();
                fileChooser.setCurrentDirectory(toFile);
            } catch (Exception e) {
                SwingUtils.setFileChooserPath(fileChooser, ThisAppProps.load(ThisAppProps.PROP_DB_FILE_IN));
            }
        } else {
            SwingUtils.setFileChooserPath(fileChooser, ThisAppProps.load(ThisAppProps.PROP_DB_FILE_IN));
        }

        int showOpenDialog = fileChooser.showOpenDialog(SwingUtils.findParentComponentForDialog(this));
        switch (showOpenDialog) {
            case JFileChooser.APPROVE_OPTION:
                File f = fileChooser.getSelectedFile();
                textMsfraggerDb.setText(f.getAbsolutePath());
                ThisAppProps.save(ThisAppProps.PROP_DB_FILE_IN, f.getAbsolutePath());
            break;
        }
    }//GEN-LAST:event_btnSelectMsfraggerDbActionPerformed

    private void textFraggerDbFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFraggerDbFocusLost
        String text = textMsfraggerDb.getText().trim();
        if (StringUtils.isNullOrWhitespace(text)) {
            BalloonTip myBalloonTip = new BalloonTip(textMsfraggerDb, "Can't be left empty!");
            return;
        }

        Path path = Paths.get(text);
        if (!Files.exists(path)) {
            BalloonTip myBalloonTip = new BalloonTip(textMsfraggerDb, "File does not exist!");
            return;
        }
        if (Files.isDirectory(path)) {
            BalloonTip myBalloonTip = new BalloonTip(textMsfraggerDb, "Should not be a directory!");
            return;
        }
    }//GEN-LAST:event_textFraggerDbFocusLost

    private void btnDownloadMsfraggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadMsfraggerActionPerformed
        try {
            Desktop.getDesktop().browse(URI.create("https://secure.nouvant.com/umich/technology/7143/license/633"));
        } catch (IOException ex) {
            Logger.getLogger(MsfraggerGuiFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnDownloadMsfraggerActionPerformed

    private void txtBinMsfraggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBinMsfraggerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBinMsfraggerActionPerformed

    
    public static PlainDocument getFilterIsotopeCorrection() {
        final String filteredCharsRegex = "[^\\-\\d/]";
        PlainDocument doc = new PlainDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int off, String str, AttributeSet attr)
                    throws BadLocationException {
                str = str.replaceAll(filteredCharsRegex, "");
                fb.insertString(off, str, attr);
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int off, int len, String str, AttributeSet attr)
                    throws BadLocationException {
                str = str.replaceAll(filteredCharsRegex, "");
                fb.replace(off, len, str, attr);
            }
        });
        return doc;
    }
        
    private String getDefaultTextMsfragger() {
        String path = ThisAppProps.load(ThisAppProps.PROP_BIN_PATH_MSFRAGGER);
        return path == null ? "MSFragger.jar" : path;
    }

    public JTextField getTxtBinMsfragger() {
        return txtBinMsfragger;
    }

    public JTextField getTxtMsfraggerDb() {
        return textMsfraggerDb;
    }
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDefaults;
    private javax.swing.JButton btnDownloadMsfragger;
    private javax.swing.JButton btnLoad;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSelectMsfraggerBin;
    private javax.swing.JButton btnSelectMsfraggerDb;
    private javax.swing.JCheckBox checkClipNTerm;
    private javax.swing.JCheckBox checkMultipleVarMods;
    private javax.swing.JCheckBox checkOverrideCharge;
    private javax.swing.JCheckBox chkRunMsfragger;
    private javax.swing.JComboBox<String> comboCleavage;
    private javax.swing.JComboBox<String> comboFragMassTol;
    private javax.swing.JComboBox<String> comboMsLevel;
    private javax.swing.JComboBox<String> comboPrecursorMassTol;
    private javax.swing.JComboBox<String> comboPrecursorTrueTol;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAddTopNComplementary;
    private javax.swing.JLabel lblTrackZeroTopN;
    private javax.swing.JLabel lblZeroBinAcceptExpect;
    private javax.swing.JLabel lblZeroBinMultiplyExpect;
    private javax.swing.JPanel panelMsFragger;
    private javax.swing.JPanel panelMsfraggerBin;
    private javax.swing.JPanel panelMsfraggerParams;
    private javax.swing.JSpinner spinnerAddTopNComplementary;
    private javax.swing.JSpinner spinnerClearMzRangeMax;
    private javax.swing.JSpinner spinnerClearMzRangeMin;
    private javax.swing.JSpinner spinnerDigestLenMax;
    private javax.swing.JSpinner spinnerDigestLenMin;
    private javax.swing.JSpinner spinnerDigestMassMax;
    private javax.swing.JSpinner spinnerDigestMassMin;
    private javax.swing.JSpinner spinnerFragMassTol;
    private javax.swing.JSpinner spinnerFraggerRam;
    private javax.swing.JSpinner spinnerFraggerThreads;
    private javax.swing.JSpinner spinnerMaxCombos;
    private javax.swing.JSpinner spinnerMaxFragCharge;
    private javax.swing.JSpinner spinnerMaxVarModsPerMod;
    private javax.swing.JSpinner spinnerMinFragsModelling;
    private javax.swing.JSpinner spinnerMinMatchedFrags;
    private javax.swing.JSpinner spinnerMinPeaks;
    private javax.swing.JSpinner spinnerMinRatio;
    private javax.swing.JSpinner spinnerMissedCleavages;
    private javax.swing.JSpinner spinnerOutputMaxExpect;
    private javax.swing.JSpinner spinnerPrecursorChargeHi;
    private javax.swing.JSpinner spinnerPrecursorChargeLo;
    private javax.swing.JSpinner spinnerPrecursorMassTol;
    private javax.swing.JSpinner spinnerPrecursorTrueTol;
    private javax.swing.JSpinner spinnerReportTopN;
    private javax.swing.JSpinner spinnerTrackZeroTopN;
    private javax.swing.JSpinner spinnerUseTopNPeaks;
    private javax.swing.JSpinner spinnerZeroBinAcceptExpect;
    private javax.swing.JSpinner spinnerZeroBinMultiplyExpect;
    private javax.swing.JTable tableAdditionalMods;
    private javax.swing.JTable tableVarMods;
    private javax.swing.JTextField textButNotAfter;
    private javax.swing.JTextField textCutAfter;
    private javax.swing.JTextField textEnzymeName;
    private javax.swing.JTextField textIsotopeError;
    private javax.swing.JTextField textMsfraggerDb;
    private javax.swing.JTextField textOutputFileExt;
    private javax.swing.JTextField txtBinMsfragger;
    // End of variables declaration//GEN-END:variables

    private ComboBoxModel<String> createMassToleranceUnitsComboModel() {
        String[] items = new String[MassTolUnits.values().length];
        for (int i = 0; i < items.length; i++) {
            items[i] = MassTolUnits.values()[i].toString();
        }
        return new DefaultComboBoxModel<>(items);
    }

    private ComboBoxModel<String> createMsLevelComboBoxModel() {
        String[] items = new String[MsLevel.values().length];
        for (int i = 0; i < items.length; i++) {
            items[i] = MsLevel.values()[i].toString();
        }
        
        return new DefaultComboBoxModel<>(items);
    }

    private ComboBoxModel<String> createCleavageComboBoxModel() {
        String[] items = new String[CleavageType.values().length];
        for (int i = 0; i < items.length; i++) {
            items[i] = CleavageType.values()[i].toString();
        }
        
        return new DefaultComboBoxModel<>(items);
    }

    public boolean isRunMsfragger() {
        return chkRunMsfragger.isSelected();
    }

    
}
