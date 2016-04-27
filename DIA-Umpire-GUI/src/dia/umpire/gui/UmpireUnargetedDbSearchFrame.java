/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dia.umpire.gui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author dattam
 */
public class UmpireUnargetedDbSearchFrame extends javax.swing.JFrame {

    /**
     * Creates new form UmpireUnargetedDbSearchPanel
     */
    public UmpireUnargetedDbSearchFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabPane = new javax.swing.JTabbedPane();
        panelInTabSelectFiles = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaSelectedFiles = new javax.swing.JTextArea();
        btnSelectRawFiles = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        panelInTabSeParams = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtRpMax = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtRfMax = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCorrThreshold = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDeltaApex = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRtOverlap = new javax.swing.JTextField();
        chkAdjustFragIntensity = new javax.swing.JCheckBox();
        chkBoostComplementaryIon = new javax.swing.JCheckBox();
        txtSelectedFile = new javax.swing.JTextField();
        btnBrowseUmpireParamFile = new javax.swing.JButton();
        panelInTabCometParams = new javax.swing.JPanel();
        panelRun = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tabPane.setToolTipText("");
        tabPane.setName(""); // NOI18N

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected files"));

        txtAreaSelectedFiles.setColumns(20);
        txtAreaSelectedFiles.setRows(5);
        txtAreaSelectedFiles.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtAreaSelectedFiles.setEnabled(false);
        jScrollPane1.setViewportView(txtAreaSelectedFiles);

        btnSelectRawFiles.setText("Add files");
        btnSelectRawFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectRawFilesActionPerformed(evt);
            }
        });

        jButton1.setText("Clear");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 703, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnSelectRawFiles)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelectRawFiles)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelInTabSelectFilesLayout = new javax.swing.GroupLayout(panelInTabSelectFiles);
        panelInTabSelectFiles.setLayout(panelInTabSelectFilesLayout);
        panelInTabSelectFilesLayout.setHorizontalGroup(
            panelInTabSelectFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInTabSelectFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelInTabSelectFilesLayout.setVerticalGroup(
            panelInTabSelectFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInTabSelectFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabPane.addTab("Select Raw Files", panelInTabSelectFiles);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Fragment grouping"));

        jLabel1.setText("RPmax");

        txtRpMax.setMinimumSize(new java.awt.Dimension(50, 20));
        txtRpMax.setPreferredSize(new java.awt.Dimension(50, 20));

        jLabel2.setText("RFmax");

        txtRfMax.setMinimumSize(new java.awt.Dimension(50, 20));
        txtRfMax.setPreferredSize(new java.awt.Dimension(50, 20));

        jLabel3.setText("CorrThreshold");

        txtCorrThreshold.setMinimumSize(new java.awt.Dimension(50, 20));
        txtCorrThreshold.setPreferredSize(new java.awt.Dimension(50, 20));
        txtCorrThreshold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCorrThresholdActionPerformed(evt);
            }
        });

        jLabel4.setText("DeltaApex");

        txtDeltaApex.setMinimumSize(new java.awt.Dimension(50, 20));
        txtDeltaApex.setPreferredSize(new java.awt.Dimension(50, 20));
        txtDeltaApex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDeltaApexActionPerformed(evt);
            }
        });

        jLabel5.setText("RTOverlap");

        txtRtOverlap.setMinimumSize(new java.awt.Dimension(50, 20));
        txtRtOverlap.setPreferredSize(new java.awt.Dimension(50, 20));

        chkAdjustFragIntensity.setText("AdjustFragIntensity");
        chkAdjustFragIntensity.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        chkBoostComplementaryIon.setText("BoostComplementaryIon");
        chkBoostComplementaryIon.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtRpMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRfMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addGap(4, 4, 4)
                        .addComponent(txtCorrThreshold, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDeltaApex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtRtOverlap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkAdjustFragIntensity)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkBoostComplementaryIon)))
                .addContainerGap(276, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtRpMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtRfMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtCorrThreshold, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtDeltaApex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtRtOverlap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAdjustFragIntensity)
                    .addComponent(chkBoostComplementaryIon)))
        );

        btnBrowseUmpireParamFile.setText("Browse");
        btnBrowseUmpireParamFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseUmpireParamFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelInTabSeParamsLayout = new javax.swing.GroupLayout(panelInTabSeParams);
        panelInTabSeParams.setLayout(panelInTabSeParamsLayout);
        panelInTabSeParamsLayout.setHorizontalGroup(
            panelInTabSeParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInTabSeParamsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInTabSeParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelInTabSeParamsLayout.createSequentialGroup()
                        .addComponent(txtSelectedFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBrowseUmpireParamFile)))
                .addContainerGap())
        );
        panelInTabSeParamsLayout.setVerticalGroup(
            panelInTabSeParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInTabSeParamsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInTabSeParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSelectedFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBrowseUmpireParamFile))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(296, Short.MAX_VALUE))
        );

        tabPane.addTab("Umpire Params", panelInTabSeParams);

        javax.swing.GroupLayout panelInTabCometParamsLayout = new javax.swing.GroupLayout(panelInTabCometParams);
        panelInTabCometParams.setLayout(panelInTabCometParamsLayout);
        panelInTabCometParamsLayout.setHorizontalGroup(
            panelInTabCometParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 772, Short.MAX_VALUE)
        );
        panelInTabCometParamsLayout.setVerticalGroup(
            panelInTabCometParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 428, Short.MAX_VALUE)
        );

        tabPane.addTab("Comet Params", panelInTabCometParams);

        javax.swing.GroupLayout panelRunLayout = new javax.swing.GroupLayout(panelRun);
        panelRun.setLayout(panelRunLayout);
        panelRunLayout.setHorizontalGroup(
            panelRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 772, Short.MAX_VALUE)
        );
        panelRunLayout.setVerticalGroup(
            panelRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 428, Short.MAX_VALUE)
        );

        tabPane.addTab("Run", panelRun);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane)
        );

        tabPane.getAccessibleContext().setAccessibleName("Umpire SE Params");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBrowseUmpireParamFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseUmpireParamFileActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("Umpire .params files", "params");
        fileChooser.setFileFilter(fileNameExtensionFilter);
        fileChooser.setApproveButtonText("Select file");
        fileChooser.setApproveButtonToolTipText("Load params from this file into the GUI");
        fileChooser.setDialogTitle("Choose Umpire SE param file");
        int openDialog = fileChooser.showOpenDialog(this);
        switch (openDialog) {
            case JFileChooser.APPROVE_OPTION:
                File file = fileChooser.getSelectedFile();
                txtSelectedFile.setText(Paths.get(file.getAbsolutePath()).toString());
                
                UmpireParams params = parseUmpireParamsFile(file);
                if (params == null) {
                    JOptionPane.showMessageDialog(this, "Could not load params file", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                boolean containsWindowType = params.containsKey("WindowType");
                if (!containsWindowType) {
                    JOptionPane.showMessageDialog(this, "Parameter file loaded, but did not contain WindowType", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                fillInUmpireParams(params);
                break;
        }
        
    }//GEN-LAST:event_btnBrowseUmpireParamFileActionPerformed

    private void btnSelectRawFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectRawFilesActionPerformed
        if (btnSelectRawFiles == evt.getSource()) {
            String approveText = "Select";
            JFileChooser fc = new JFileChooser();
            fc.setAcceptAllFileFilterUsed(true);
            fc.setApproveButtonText(approveText);
            fc.setDialogTitle("Choose raw data files");
            fc.setMultiSelectionEnabled(true);
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);


//            List<String> filePaths = splitTrim(txtFileIn.getText().trim(), ",");
//            for (int i = 0; i < filePaths.size(); i++) {
//                String filePath = filePaths.get(i);
//                Path p = Paths.get(filePath).toAbsolutePath();
//                if (Files.exists(p)) {
//                    fc.setSelectedFile(p.toFile());
//                }
//            }

            int retVal = fc.showDialog(this, approveText);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                File[] files = fc.getSelectedFiles();
                for (File f : files) {
                    txtAreaSelectedFiles.append(f.toString() + "\n");
                }
                
            } else {
                
            }
        }
    }//GEN-LAST:event_btnSelectRawFilesActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        txtAreaSelectedFiles.setText("");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtCorrThresholdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCorrThresholdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCorrThresholdActionPerformed

    private void txtDeltaApexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDeltaApexActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDeltaApexActionPerformed

    private static List<String> splitTrim(String input, String sep) {
        String[] split = input.split(sep);
        List<String> strings = new ArrayList<>(split.length);
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
            if (!split[i].isEmpty()) {
                strings.add(split[i]);
            }
        }
        return strings;
    }
    
    private UmpireParams parseUmpireParamsFile(File f) {
        UmpireParams params = new UmpireParams();
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f))) {
            params.load(bis);
            return params;
        } catch (IOException ex) {
            Logger.getLogger(UmpireUnargetedDbSearchFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private void fillInUmpireParams(UmpireParams params) {
        txtRpMax.setText(params.getProperty(UmpireParams.PROP_RPmax));
        txtRfMax.setText(params.getProperty(UmpireParams.PROP_RFmax));
        txtCorrThreshold.setText(params.getProperty(UmpireParams.PROP_CorrThreshold));
        txtDeltaApex.setText(params.getProperty(UmpireParams.PROP_DeltaApex));
        txtRtOverlap.setText(params.getProperty(UmpireParams.PROP_RTOverlap));
        chkAdjustFragIntensity.setSelected(params.getAdjustFragIntensity());
        chkBoostComplementaryIon.setSelected(params.getBoostComplementaryIon());
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UmpireUnargetedDbSearchFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UmpireUnargetedDbSearchFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UmpireUnargetedDbSearchFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UmpireUnargetedDbSearchFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UmpireUnargetedDbSearchFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowseUmpireParamFile;
    private javax.swing.JButton btnSelectRawFiles;
    private javax.swing.JCheckBox chkAdjustFragIntensity;
    private javax.swing.JCheckBox chkBoostComplementaryIon;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelInTabCometParams;
    private javax.swing.JPanel panelInTabSeParams;
    private javax.swing.JPanel panelInTabSelectFiles;
    private javax.swing.JPanel panelRun;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JTextArea txtAreaSelectedFiles;
    private javax.swing.JTextField txtCorrThreshold;
    private javax.swing.JTextField txtDeltaApex;
    private javax.swing.JTextField txtRfMax;
    private javax.swing.JTextField txtRpMax;
    private javax.swing.JTextField txtRtOverlap;
    private javax.swing.JTextField txtSelectedFile;
    // End of variables declaration//GEN-END:variables
}
