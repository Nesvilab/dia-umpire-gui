/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package umich.msfragger.gui;

import umich.msfragger.params.UmpireQuantParams;
import umich.msfragger.exceptions.ParsingException;
import umich.msfragger.params.CometParams;
import umich.msfragger.params.PeptideProphetParams;
import umich.msfragger.params.Philosopher;
import umich.msfragger.params.ProteinProphetParams;
import umich.msfragger.params.ThisAppProps;
import umich.msfragger.params.UmpireParams;
import umich.msfragger.util.LogUtils;
import umich.msfragger.util.OsUtils;
import umich.msfragger.util.StringUtils;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;
import umich.msfragger.gui.api.DataConverter;
import umich.msfragger.gui.api.SimpleETable;
import umich.msfragger.gui.api.SimpleETableTransferHandler;
import umich.msfragger.gui.api.SimpleUniqueTableModel;
import umich.msfragger.gui.api.TableModelColumn;
import umich.msfragger.params.fragger.MsfraggerParams;
import umich.msfragger.util.FileDrop;
import umich.msfragger.util.PathUtils;
import umich.msfragger.util.SwingUtils;

/**
 *
 * @author dattam
 */
public class MsfraggerGuiFrame extends javax.swing.JFrame {

    protected FraggerPanel fraggerPanel;
    protected ExecutorService exec;
    private final List<Process> submittedProcesses = new ArrayList<>(100);
    
    SimpleETable tableRawFiles;
    SimpleUniqueTableModel<Path> tableModelRawFiles;
    FileDrop tableRawFilesFileDrop;

    /**
     * Creates new form UmpireUnargetedDbSearchPanel
     */
    public MsfraggerGuiFrame() {
        initComponents();
        initMore();
    }

    

    private void initMore() {
        exec = Executors.newFixedThreadPool(1);
        fraggerPanel = new FraggerPanel();
        scrollPaneMsFragger.setViewportView(fraggerPanel);
        scrollPaneMsFragger.getVerticalScrollBar().setUnitIncrement(16);
        
        
        tableModelRawFiles = createTableModelRawFiles();
        tableRawFiles = new SimpleETable(tableModelRawFiles);
        tableRawFiles.addComponentsEnabledOnNonEmptyData(btnRawClear);
        tableRawFiles.addComponentsEnabledOnNonEmptySelection(btnRawRemove);
        tableRawFiles.fireInitialization();
        tableRawFiles.setFillsViewportHeight(true);
        scrollPaneRawFiles.setViewportView(tableRawFiles);
        
        // Drag and drop support for files from Explorer to the Application
        
        
        // this works only when tableRawFiles.setFillsViewportHeight(true) is called.
//        tableRawFiles.setDragEnabled(true);
//        tableRawFiles.setDropMode(DropMode.ON);
//        tableRawFiles.setFillsViewportHeight(true);
//        TransferHandler origHandler = tableRawFiles.getTransferHandler();
//        SimpleETableTransferHandler newHandler = new SimpleETableTransferHandler();
//        tableRawFiles.setTransferHandler(newHandler);
        
        // dropping onto enclosing JPanel works.
        tableRawFilesFileDrop = new FileDrop(panelSelectedFiles, true, new FileDrop.Listener() {
            @Override
            public void filesDropped(File[] files) {
                ArrayList<Path> paths = new ArrayList<>(files.length);
                for (File f : files) {
                    boolean isDirectory = f.isDirectory();
                    if (!isDirectory) {
                        if (FraggerPanel.fileNameExtensionFilter.accept(f))
                            paths.add(Paths.get(f.getAbsolutePath()));
                    } else {
                        PathUtils.traverseDirectoriesAcceptingFiles(f, FraggerPanel.fileNameExtensionFilter, paths);
                    }
                }
                tableModelRawFiles.dataAddAll(paths);
            }
        });
        
        
    }
    
    
    public SimpleUniqueTableModel<Path> createTableModelRawFiles() {
        if (tableModelRawFiles != null)
            return tableModelRawFiles; 
        List<TableModelColumn<Path, ?>> cols = new ArrayList<>();
       
        TableModelColumn<Path, String> colPath = new TableModelColumn<>("Path", String.class, false, new DataConverter<Path, String>() {
            @Override public String convert(Path data) {
                return data.toString();
            }
        });
        cols.add(colPath);
        
        tableModelRawFiles = new SimpleUniqueTableModel<>(cols, 0);
        return tableModelRawFiles;
    }
    
    private String getDefaultPhilosopherBinName() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dia/umpire/gui/Bundle"); // NOI18N
        String winName = bundle.getString("default.philosopher.win"); // NOI18N
        String nixName = bundle.getString("default.philosopher.nix"); // NOI18N
        if (OsUtils.isWindows())
            return winName;
        return nixName;
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
        panelSelectedFiles = new javax.swing.JPanel();
        btnRawAddFiles = new javax.swing.JButton();
        btnRawClear = new javax.swing.JButton();
        scrollPaneRawFiles = new javax.swing.JScrollPane();
        btnRawAddFolder = new javax.swing.JButton();
        btnRawRemove = new javax.swing.JButton();
        panelMsFragger = new javax.swing.JPanel();
        scrollPaneMsFragger = new javax.swing.JScrollPane();
        panelPeptideProphet = new javax.swing.JPanel();
        panelPeptideProphetBin = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        btnSelectPeptideProphetBin = new javax.swing.JButton();
        txtBinPeptideProphet = new javax.swing.JTextField();
        chkRunPeptideProphet = new javax.swing.JCheckBox();
        panelPeptideProphetOptions = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        txtPeptideProphetSeqDb = new javax.swing.JTextField();
        btnSelectPeptideProphetSeqDbPath = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtPeptideProphetCmdLineOptions = new javax.swing.JTextArea();
        panelProteinProphet = new javax.swing.JPanel();
        chkRunProteinProphet = new javax.swing.JCheckBox();
        panelProteinProphetBin = new javax.swing.JPanel();
        btnBinProteinProphet = new javax.swing.JButton();
        txtBinProteinProphet = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        panelProteinProphetOptions = new javax.swing.JPanel();
        btnProteinProphetSeqDb = new javax.swing.JButton();
        txtProteinProphetSeqDb = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtProteinProphetCmdLineOpts = new javax.swing.JTextArea();
        jLabel40 = new javax.swing.JLabel();
        chkProteinProphetAddInteractPepXmlsSeparately = new javax.swing.JCheckBox();
        jLabel41 = new javax.swing.JLabel();
        txtProteinProphetOutputFile = new javax.swing.JTextField();
        panelRun = new javax.swing.JPanel();
        btnRun = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        btnClearConsole = new javax.swing.JButton();
        consoleScrollPane = new javax.swing.JScrollPane();
        console = new umich.msfragger.gui.TextConsole();
        DefaultCaret caret = (DefaultCaret) console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        lblOutputDir = new javax.swing.JLabel();
        btnSelectWrkingDir = new javax.swing.JButton();
        txtWorkingDir = new javax.swing.JTextField();
        btnBrowseProgramsDir = new javax.swing.JButton();
        txtProgramsDir = new javax.swing.JTextField();
        lblProgramsDir = new javax.swing.JLabel();
        btnAbout = new javax.swing.JButton();
        checkDryRun = new javax.swing.JCheckBox();
        btnCheckJavaVersion = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MSFragger");
        setIconImages(loadIcon());
        setName("frameMain"); // NOI18N

        tabPane.setToolTipText("");
        tabPane.setName(""); // NOI18N

        panelSelectedFiles.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected files"));

        btnRawAddFiles.setText("Add files");
        btnRawAddFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRawAddFilesActionPerformed(evt);
            }
        });

        btnRawClear.setText("Clear");
        btnRawClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRawClearActionPerformed(evt);
            }
        });

        btnRawAddFolder.setText("Add Folder");
        btnRawAddFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRawAddFolderActionPerformed(evt);
            }
        });

        btnRawRemove.setText("Remove");
        btnRawRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRawRemoveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelSelectedFilesLayout = new javax.swing.GroupLayout(panelSelectedFiles);
        panelSelectedFiles.setLayout(panelSelectedFilesLayout);
        panelSelectedFilesLayout.setHorizontalGroup(
            panelSelectedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSelectedFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSelectedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneRawFiles)
                    .addGroup(panelSelectedFilesLayout.createSequentialGroup()
                        .addComponent(btnRawClear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRawRemove)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRawAddFiles)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRawAddFolder)
                        .addGap(0, 302, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelSelectedFilesLayout.setVerticalGroup(
            panelSelectedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSelectedFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneRawFiles, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSelectedFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRawAddFiles)
                    .addComponent(btnRawClear)
                    .addComponent(btnRawAddFolder)
                    .addComponent(btnRawRemove))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelInTabSelectFilesLayout = new javax.swing.GroupLayout(panelInTabSelectFiles);
        panelInTabSelectFiles.setLayout(panelInTabSelectFilesLayout);
        panelInTabSelectFilesLayout.setHorizontalGroup(
            panelInTabSelectFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInTabSelectFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSelectedFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelInTabSelectFilesLayout.setVerticalGroup(
            panelInTabSelectFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInTabSelectFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSelectedFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabPane.addTab("Select Raw Files", panelInTabSelectFiles);

        javax.swing.GroupLayout panelMsFraggerLayout = new javax.swing.GroupLayout(panelMsFragger);
        panelMsFragger.setLayout(panelMsFraggerLayout);
        panelMsFraggerLayout.setHorizontalGroup(
            panelMsFraggerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneMsFragger, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
        );
        panelMsFraggerLayout.setVerticalGroup(
            panelMsFraggerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPaneMsFragger, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
        );

        tabPane.addTab("MSFragger", panelMsFragger);

        panelPeptideProphetBin.setBorder(javax.swing.BorderFactory.createTitledBorder("PeptideProphet"));
        panelPeptideProphetBin.setToolTipText("If you're using Philosopher to run PeptideProhphet, make sure 'peptideprophet' is the first command in Cmd Line Options text");

        jLabel33.setText("PeptideProphet");
        jLabel33.setToolTipText("");

        btnSelectPeptideProphetBin.setText("Browse");
        btnSelectPeptideProphetBin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectPeptideProphetBinActionPerformed(evt);
            }
        });

        txtBinPeptideProphet.setText(getDefaultTextPeptideProphet());
        txtBinPeptideProphet.setToolTipText("");
        txtBinPeptideProphet.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBinPeptideProphetFocusLost(evt);
            }
        });

        javax.swing.GroupLayout panelPeptideProphetBinLayout = new javax.swing.GroupLayout(panelPeptideProphetBin);
        panelPeptideProphetBin.setLayout(panelPeptideProphetBinLayout);
        panelPeptideProphetBinLayout.setHorizontalGroup(
            panelPeptideProphetBinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPeptideProphetBinLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBinPeptideProphet)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSelectPeptideProphetBin)
                .addContainerGap())
        );
        panelPeptideProphetBinLayout.setVerticalGroup(
            panelPeptideProphetBinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPeptideProphetBinLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPeptideProphetBinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(btnSelectPeptideProphetBin)
                    .addComponent(txtBinPeptideProphet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        chkRunPeptideProphet.setSelected(true);
        chkRunPeptideProphet.setText("Run PeptideProphet");
        chkRunPeptideProphet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRunPeptideProphetActionPerformed(evt);
            }
        });

        panelPeptideProphetOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));

        jLabel34.setText("Cmd Line Options");

        jLabel35.setText("Sequence Database");
        jLabel35.setToolTipText("If left empty, will try to get value from Comet search sequence database");

        txtPeptideProphetSeqDb.setToolTipText("If left empty, will try to get value from Comet search sequence database");

        btnSelectPeptideProphetSeqDbPath.setText("Browse");
        btnSelectPeptideProphetSeqDbPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectPeptideProphetSeqDbPathActionPerformed(evt);
            }
        });

        txtPeptideProphetCmdLineOptions.setColumns(20);
        txtPeptideProphetCmdLineOptions.setLineWrap(true);
        txtPeptideProphetCmdLineOptions.setRows(5);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("umich/msfragger/gui/Bundle"); // NOI18N
        txtPeptideProphetCmdLineOptions.setText(bundle.getString("peptideprophet.cmd.line.opts")); // NOI18N
        txtPeptideProphetCmdLineOptions.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txtPeptideProphetCmdLineOptions);

        javax.swing.GroupLayout panelPeptideProphetOptionsLayout = new javax.swing.GroupLayout(panelPeptideProphetOptions);
        panelPeptideProphetOptions.setLayout(panelPeptideProphetOptionsLayout);
        panelPeptideProphetOptionsLayout.setHorizontalGroup(
            panelPeptideProphetOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPeptideProphetOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPeptideProphetOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelPeptideProphetOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPeptideProphetOptionsLayout.createSequentialGroup()
                        .addComponent(txtPeptideProphetSeqDb)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSelectPeptideProphetSeqDbPath))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPeptideProphetOptionsLayout.setVerticalGroup(
            panelPeptideProphetOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPeptideProphetOptionsLayout.createSequentialGroup()
                .addGroup(panelPeptideProphetOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelectPeptideProphetSeqDbPath)
                    .addComponent(txtPeptideProphetSeqDb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPeptideProphetOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPeptideProphetOptionsLayout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelPeptideProphetLayout = new javax.swing.GroupLayout(panelPeptideProphet);
        panelPeptideProphet.setLayout(panelPeptideProphetLayout);
        panelPeptideProphetLayout.setHorizontalGroup(
            panelPeptideProphetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPeptideProphetLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPeptideProphetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPeptideProphetBin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelPeptideProphetLayout.createSequentialGroup()
                        .addComponent(chkRunPeptideProphet)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(panelPeptideProphetOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPeptideProphetLayout.setVerticalGroup(
            panelPeptideProphetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPeptideProphetLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkRunPeptideProphet)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPeptideProphetBin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPeptideProphetOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(444, Short.MAX_VALUE))
        );

        tabPane.addTab("PeptideProphet", panelPeptideProphet);

        chkRunProteinProphet.setSelected(true);
        chkRunProteinProphet.setText("Run ProteinProphet");
        chkRunProteinProphet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRunProteinProphetActionPerformed(evt);
            }
        });

        panelProteinProphetBin.setBorder(javax.swing.BorderFactory.createTitledBorder("ProteinProphet"));

        btnBinProteinProphet.setText("Browse");
        btnBinProteinProphet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBinProteinProphetActionPerformed(evt);
            }
        });

        txtBinProteinProphet.setText(getDefaultTextProteinProphet());
        txtBinProteinProphet.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBinProteinProphetFocusLost(evt);
            }
        });

        jLabel38.setText("ProteinProphet");

        javax.swing.GroupLayout panelProteinProphetBinLayout = new javax.swing.GroupLayout(panelProteinProphetBin);
        panelProteinProphetBin.setLayout(panelProteinProphetBinLayout);
        panelProteinProphetBinLayout.setHorizontalGroup(
            panelProteinProphetBinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelProteinProphetBinLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBinProteinProphet)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBinProteinProphet)
                .addContainerGap())
        );
        panelProteinProphetBinLayout.setVerticalGroup(
            panelProteinProphetBinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProteinProphetBinLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelProteinProphetBinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBinProteinProphet)
                    .addComponent(txtBinProteinProphet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelProteinProphetOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));

        btnProteinProphetSeqDb.setText("Browse");
        btnProteinProphetSeqDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProteinProphetSeqDbActionPerformed(evt);
            }
        });

        txtProteinProphetSeqDb.setToolTipText("If not specified, the value will be taken from PeptideProphet or Comet tabs");

        jLabel39.setText("Sequence Database");
        jLabel39.setToolTipText("Not Used Now. If not specified, the value will be taken from PeptideProphet or Comet tabs");

        txtProteinProphetCmdLineOpts.setColumns(20);
        txtProteinProphetCmdLineOpts.setLineWrap(true);
        txtProteinProphetCmdLineOpts.setRows(5);
        txtProteinProphetCmdLineOpts.setText(bundle.getString("proteinprophet.cmd.line.opts")); // NOI18N
        txtProteinProphetCmdLineOpts.setWrapStyleWord(true);
        jScrollPane4.setViewportView(txtProteinProphetCmdLineOpts);

        jLabel40.setText("Cmd Line Options");

        chkProteinProphetAddInteractPepXmlsSeparately.setSelected(true);
        chkProteinProphetAddInteractPepXmlsSeparately.setText("Use 'interact-*pep.xml' as file filter for ProteinProphet (Philosopher only)");
        chkProteinProphetAddInteractPepXmlsSeparately.setToolTipText("<html>If checked will use 'interact-*pep.xml' to match pep.xml files to be passed to ProteinProphet.<br/> Otherwise will add files as separate entries, \nwhich might cause problems on Windows<br/> when there are many pepxml files, as the length of command line parameter string is limited to 8192 chars."); // NOI18N

        jLabel41.setText("Output File");

        txtProteinProphetOutputFile.setText(bundle.getString("default.prot.xml.filename")); // NOI18N

        javax.swing.GroupLayout panelProteinProphetOptionsLayout = new javax.swing.GroupLayout(panelProteinProphetOptions);
        panelProteinProphetOptions.setLayout(panelProteinProphetOptionsLayout);
        panelProteinProphetOptionsLayout.setHorizontalGroup(
            panelProteinProphetOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProteinProphetOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelProteinProphetOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelProteinProphetOptionsLayout.createSequentialGroup()
                        .addGroup(panelProteinProphetOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel41)
                            .addComponent(jLabel39))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelProteinProphetOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelProteinProphetOptionsLayout.createSequentialGroup()
                                .addComponent(txtProteinProphetSeqDb)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnProteinProphetSeqDb))
                            .addComponent(txtProteinProphetOutputFile)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelProteinProphetOptionsLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel40)
                        .addGap(8, 8, 8)
                        .addComponent(jScrollPane4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelProteinProphetOptionsLayout.createSequentialGroup()
                        .addGap(0, 200, Short.MAX_VALUE)
                        .addComponent(chkProteinProphetAddInteractPepXmlsSeparately)))
                .addContainerGap())
        );
        panelProteinProphetOptionsLayout.setVerticalGroup(
            panelProteinProphetOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProteinProphetOptionsLayout.createSequentialGroup()
                .addGroup(panelProteinProphetOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProteinProphetSeqDb)
                    .addComponent(txtProteinProphetSeqDb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelProteinProphetOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(txtProteinProphetOutputFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(panelProteinProphetOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelProteinProphetOptionsLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                        .addComponent(jLabel40)
                        .addGap(123, 123, 123))
                    .addGroup(panelProteinProphetOptionsLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkProteinProphetAddInteractPepXmlsSeparately)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout panelProteinProphetLayout = new javax.swing.GroupLayout(panelProteinProphet);
        panelProteinProphet.setLayout(panelProteinProphetLayout);
        panelProteinProphetLayout.setHorizontalGroup(
            panelProteinProphetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProteinProphetLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelProteinProphetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelProteinProphetBin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelProteinProphetLayout.createSequentialGroup()
                        .addComponent(chkRunProteinProphet)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(panelProteinProphetOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelProteinProphetLayout.setVerticalGroup(
            panelProteinProphetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProteinProphetLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkRunProteinProphet)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelProteinProphetBin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelProteinProphetOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(397, Short.MAX_VALUE))
        );

        tabPane.addTab("ProteinProphet", panelProteinProphet);

        btnRun.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnRun.setText("Run");
        btnRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRunActionPerformed(evt);
            }
        });

        btnStop.setText("Stop");
        btnStop.setEnabled(false);
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        btnClearConsole.setText("Clear console");
        btnClearConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearConsoleActionPerformed(evt);
            }
        });

        consoleScrollPane.setViewportView(console);

        lblOutputDir.setText("Output dir");
        lblOutputDir.setToolTipText("<html>All the output will be placed into this directory.<br/>\nUmpire-SE always generates output near mzXML files, <br/>\nif you stop processing early, then these files might not have been<br/>\nmoved to the Output Dir yet. For this case there is a button<br/>\non \"DIA-Umpire SE\" tab to Clean Up the generated files."); // NOI18N

        btnSelectWrkingDir.setText("Browse");
        btnSelectWrkingDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectWrkingDirActionPerformed(evt);
            }
        });

        txtWorkingDir.setToolTipText(lblOutputDir.getToolTipText());

        btnBrowseProgramsDir.setText("Browse");
        btnBrowseProgramsDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseProgramsDirActionPerformed(evt);
            }
        });

        txtProgramsDir.setText(getDefaultTextProgramsDir());
        txtProgramsDir.setToolTipText("<html>The .jar files and executables will be searched here. <br/> Executables (e.g. msconvert.exe) will also be searched using system's PATH variable.");

        lblProgramsDir.setText("Programs dir");
        lblProgramsDir.setToolTipText("<html>The .jar files and executables will be searched here. <br/>\nExecutables (e.g. msconvert.exe) will also be searched using system's PATH variable."); // NOI18N

        btnAbout.setText("About");
        btnAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAboutActionPerformed(evt);
            }
        });

        checkDryRun.setText("Dry Run");
        checkDryRun.setToolTipText("<html>Only print the commands to execute, <br/>\nbut don't actually execute them.");

        btnCheckJavaVersion.setText("Check Java Version");
        btnCheckJavaVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckJavaVersionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRunLayout = new javax.swing.GroupLayout(panelRun);
        panelRun.setLayout(panelRunLayout);
        panelRunLayout.setHorizontalGroup(
            panelRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRunLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(consoleScrollPane)
                    .addGroup(panelRunLayout.createSequentialGroup()
                        .addComponent(btnRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkDryRun)
                        .addGap(18, 18, 18)
                        .addComponent(btnCheckJavaVersion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 187, Short.MAX_VALUE)
                        .addComponent(btnAbout)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClearConsole))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRunLayout.createSequentialGroup()
                        .addGroup(panelRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblOutputDir)
                            .addComponent(lblProgramsDir))
                        .addGap(7, 7, 7)
                        .addGroup(panelRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRunLayout.createSequentialGroup()
                                .addComponent(txtProgramsDir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBrowseProgramsDir))
                            .addGroup(panelRunLayout.createSequentialGroup()
                                .addComponent(txtWorkingDir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSelectWrkingDir)))))
                .addContainerGap())
        );
        panelRunLayout.setVerticalGroup(
            panelRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRunLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBrowseProgramsDir)
                    .addComponent(txtProgramsDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProgramsDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOutputDir)
                    .addComponent(btnSelectWrkingDir)
                    .addComponent(txtWorkingDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRunLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRun)
                    .addComponent(btnStop)
                    .addComponent(btnClearConsole)
                    .addComponent(btnAbout)
                    .addComponent(checkDryRun)
                    .addComponent(btnCheckJavaVersion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(consoleScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabPane.addTab("Run", panelRun);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabPane))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane)
        );

        tabPane.getAccessibleContext().setAccessibleName("MSFragger");
        tabPane.getAccessibleContext().setAccessibleDescription("Run MSFragger pipeline");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAboutActionPerformed

        // for copying style
        JLabel label = new JLabel();
        Font font = label.getFont();

        // create some css from the label's font
        StringBuilder style = new StringBuilder("font-family:" + font.getFamily() + ";");
        style.append("font-weight:").append(font.isBold() ? "bold" : "normal").append(";");
        style.append("font-size:").append(font.getSize()).append("pt;");

        JEditorPane ep = new JEditorPane("text/html", "<html><body style=\"" + style + "\">"
            + "GUI Wrapper for running MSFragger - Ultrafast Proteomics Search Engine<br/>"
            + "University of Michigan, 2017<br/>"
            + "<a href=\"http://nesvilab.org/\">Alexey Nesvizhskii lab</a><br/>&nbsp;<br/>&nbsp;"
            //                + "MSFragger authors and contributors:<br/>"
            //                + "<ul>"
            //                + "<li>Andy Kong</li>"
            //                + "<li>Dmitry Avtonomov</li>"
            //                + "<li>Alexey Nesvizhskii</li>"
            //                + "</ul>"
            + "<a href=\"http://www.nature.com/nmeth/journal/v14/n5/full/nmeth.4256.html\">Original MSFragger paper link</a><br/>"
            + "Reference: <b>doi:10.1038/nmeth.4256</b>"
            + "</body></html>");

        // handle link events
        ep.addHyperlinkListener(new HyperlinkListener()
            {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (URISyntaxException | IOException ex) {
                            Logger.getLogger(MsfraggerGuiFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            ep.setEditable(false);
            ep.setBackground(label.getBackground());

            // show
            JOptionPane.showMessageDialog(this, ep, "About", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnAboutActionPerformed

    private void btnBrowseProgramsDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseProgramsDirActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setApproveButtonText("Select directory");
        fileChooser.setApproveButtonToolTipText("Select directory with Umpire, Philosopher etc");
        fileChooser.setDialogTitle("Select DIA-Umpire and Philosopher directory");
        fileChooser.setMultiSelectionEnabled(false);
        //        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("JAR files", "jar");
        //        fileChooser.setFileFilter(fileNameExtensionFilter);

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        ThisAppProps.setFilechooserPathToCached(fileChooser, ThisAppProps.PROP_BINARIES_IN);

        if (!txtProgramsDir.getText().isEmpty()) {
            File toFile = Paths.get(txtProgramsDir.getText()).toFile();
            fileChooser.setCurrentDirectory(toFile);
        }

        int showOpenDialog = fileChooser.showOpenDialog(this);
        switch (showOpenDialog) {
            case JFileChooser.APPROVE_OPTION:

            File f = fileChooser.getSelectedFile();
            txtProgramsDir.setText(f.getAbsolutePath());
            ThisAppProps.saveFilechooserPathToCached(f, ThisAppProps.PROP_BINARIES_IN);
            saveProgramsDir();
            break;
        }
    }//GEN-LAST:event_btnBrowseProgramsDirActionPerformed

    private void btnSelectWrkingDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectWrkingDirActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        //FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("FASTA files", "fa", "fasta");
        //fileChooser.setFileFilter(fileNameExtensionFilter);
        fileChooser.setApproveButtonText("Select directory");
        fileChooser.setApproveButtonToolTipText("Select");
        fileChooser.setDialogTitle("Choose working directory");
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        ThisAppProps.setFilechooserPathToCached(fileChooser, ThisAppProps.PROP_LCMS_FILES_IN);
        //setFilechooserPathToCached(fileChooser, ThisAppProps.PROP_PARAMS_FILE_IN);

        if (!txtWorkingDir.getText().isEmpty()) {
            File toFile = Paths.get(txtWorkingDir.getText()).toFile();
            fileChooser.setCurrentDirectory(toFile);
        }

        int showOpenDialog = fileChooser.showOpenDialog(this);
        switch (showOpenDialog) {
            case JFileChooser.APPROVE_OPTION:
                File f = fileChooser.getSelectedFile();
                txtWorkingDir.setText(f.getAbsolutePath());
                ThisAppProps.saveTextFieldToCache(txtWorkingDir, ThisAppProps.PROP_LCMS_FILES_IN);
                break;
        }
    }//GEN-LAST:event_btnSelectWrkingDirActionPerformed

    private void btnClearConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearConsoleActionPerformed
        console.setText("");
    }//GEN-LAST:event_btnClearConsoleActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        btnRun.setEnabled(true);
        btnStop.setEnabled(false);

        if (exec != null) {
            exec.shutdownNow();
        }
        for (Process p : submittedProcesses) {
            p.destroy();
        }
        submittedProcesses.clear();
    }//GEN-LAST:event_btnStopActionPerformed

    private void btnRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunActionPerformed
        resetRunButtons(false);

        if (
            !fraggerPanel.isRunMsfragger() &&
            !chkRunPeptideProphet.isSelected() &&
            !chkRunProteinProphet.isSelected()) {
            JOptionPane.showMessageDialog(this, "Nothing to run.\n"
                + "Please mark checkboxes in other tabs to run processing tools.", "Error", JOptionPane.WARNING_MESSAGE);
            resetRunButtons(true);
            return;
        }
        
        
        final TextConsole textConsole = console;
        final String workingDir = txtWorkingDir.getText();
        if (workingDir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Output directory can't be left empty.\n"
                + "Please select an existing directory for the output.", "Error", JOptionPane.WARNING_MESSAGE);
            resetRunButtons(true);
            return;
        }
        final Path workingDirPath = Paths.get(workingDir);
        if (!Files.exists(workingDirPath) || !Files.isDirectory(workingDirPath)) {
            JOptionPane.showMessageDialog(this, "Output directory doesn't exist\n"
                + "or is a file rather than a directory.", "Error", JOptionPane.WARNING_MESSAGE);
            resetRunButtons(true);
            return;
        }

        List<String> lcmsFilePaths = getLcmsFilePaths();
        if (lcmsFilePaths.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No LC/MS data files selected.\n"
                + "Check 'Select Raw Files' tab.", "Error", JOptionPane.WARNING_MESSAGE);
            resetRunButtons(true);
            return;
        }

        List<ProcessBuilder> processBuilders = new ArrayList();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dateString = df.format(new Date());
        String programsDir = txtProgramsDir.getText().trim();

        if (!OsUtils.isWindows()) {
            // On Linux create symlinks to mzXML files
            try {
                createLcmsFileSymlinks(Paths.get(workingDir));
            } catch (IOException ex) {
                String msg = String.format("Something went wronng when creating symlinks to LCMS files.\n%s", ex.getMessage());
                JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);

                resetRunButtons(true);
                return;
            }
        } else {
            // On windows copy the files over to the working directory
//            List<ProcessBuilder> processBuildersCopyFiles = processBuildersCopyFiles(programsDir, workingDir, lcmsFilePaths);
//            processBuilders.addAll(processBuildersCopyFiles);
        }

        // we will now compose parameter objects for running processes.
        // at first we will try to load the base parameter files, if the file paths
        // in the GUI are not empty. If empty, we will load the defaults and
        // add params from the GUI to it.

        List<ProcessBuilder> processBuildersFragger = processBuildersFragger(programsDir, workingDir, lcmsFilePaths, dateString);
        if (processBuildersFragger == null) {
            resetRunButtons(true);
            return;
        }
        processBuilders.addAll(processBuildersFragger);

        List<ProcessBuilder> processBuildersPeptideProphet = processBuildersPeptideProphet(programsDir, workingDir, lcmsFilePaths);
        if (processBuildersPeptideProphet == null) {
            resetRunButtons(true);
            return;
        }
        processBuilders.addAll(processBuildersPeptideProphet);

        List<ProcessBuilder> processBuildersProteinProphet = processBuildersProteinProphet(programsDir, workingDir, lcmsFilePaths);
        if (processBuildersProteinProphet == null) {
            resetRunButtons(true);
            return;
        }
        processBuilders.addAll(processBuildersProteinProphet);
        
        if (!OsUtils.isWindows()) {
            // On Linux we created symlinks to mzXML files, leave them there
        } else {
            // On windows we copied the files over to the working directory
            // so will delete them now
//            List<ProcessBuilder> processBuildersDeleteFiles = processBuildersDeleteFiles(workingDir, lcmsFilePaths);
//            processBuilders.addAll(processBuildersDeleteFiles);
        }

        LogUtils.println(console, String.format("Will execute %d commands:", processBuilders.size()));
        for (final ProcessBuilder pb : processBuilders) {
            StringBuilder sb = new StringBuilder();
            List<String> command = pb.command();
            for (String commandPart : command)
            sb.append(commandPart).append(" ");
            LogUtils.println(console, sb.toString());
            LogUtils.println(console, "");
        }
        LogUtils.println(console, "~~~~~~~~~~~~~~~~~~~~~~");
        LogUtils.println(console, "");
        LogUtils.println(console, "");

        if (checkDryRun.isSelected()) {
            LogUtils.println(console, "Dry Run selected, not running the commands.");
            resetRunButtons(true);
            return;
        }
        
        try // run everything
        {
            exec = Executors.newFixedThreadPool(1);
            for (final ProcessBuilder pb : processBuilders) {

                pb.directory(Paths.get(workingDir).toFile());

                REHandler reHandler;
                reHandler = new REHandler(new Runnable() {
                    @Override
                    public void run() {
                        Process process = null;
                        try {
                            List<String> command = pb.command();
                            StringBuilder sb = new StringBuilder("Executing command:\n$> ");
                            for (String commandPart : command)
                            sb.append(commandPart).append(" ");
                            String toAppend = sb.toString();
                            LogUtils.println(console, toAppend);
                            process = pb.start();
                            toAppend = "Process started";
                            LogUtils.println(console, toAppend);

                            InputStream err = process.getErrorStream();
                            InputStream out = process.getInputStream();
                            while (true) {
                                Thread.sleep(200L);
                                int errAvailable = err.available();
                                if (errAvailable > 0) {
                                    byte[] bytes = new byte[errAvailable];
                                    int read = err.read(bytes);
                                    toAppend = new String(bytes);
                                    LogUtils.println(console, toAppend);
                                }
                                int outAvailable = out.available();
                                if (outAvailable > 0) {
                                    byte[] bytes = new byte[outAvailable];
                                    int read = out.read(bytes);
                                    toAppend = new String(bytes);
                                    LogUtils.println(console, toAppend);
                                }
                                try {
                                    int exitValue = process.exitValue();
                                    toAppend = String.format("Process finished, exit value: %d\n", exitValue);
                                    LogUtils.println(console, toAppend);
                                    break;
                                } catch (IllegalThreadStateException ignore) {
                                    // this error is thrown by process.exitValue() if the underlying process has not yet finished
                                }
                            }

                        } catch (IOException ex) {
                            String toAppend = String.format("IOException: Error in process,\n%s", ex.getMessage());
                            LogUtils.println(console, toAppend);
                        } catch (InterruptedException ex) {
                            if (process != null) {
                                process.destroy();
                            }
                            String toAppend = String.format("InterruptedException: Error in process,\n%s", ex.getMessage());
                            LogUtils.println(console, toAppend);
                        }
                    }
                }, console, System.err);
                exec.submit(reHandler);

                // On windows try to schedule copied mzXML file deletion
                if (OsUtils.isWindows()) {
                    REHandler deleteTask = new REHandler(new Runnable() {
                        @Override
                        public void run() {
                            List<String> lcmsFiles = getLcmsFilePaths();
                            List<Path> copiedFiles = getLcmsFilePathsInWorkdir(Paths.get(workingDir));
                            if (lcmsFiles.size() != copiedFiles.size())
                            throw new IllegalStateException("LCMS file list sizes should be equal.");
                            for (int i = 0; i < lcmsFiles.size(); i++) {
                                Path origPath = Paths.get(lcmsFiles.get(i));
                                Path linkPath = copiedFiles.get(i);
                                if (!linkPath.getParent().equals(origPath.getParent()))
                                linkPath.toFile().deleteOnExit();
                            }

                        }
                    }, console, System.err);
                    exec.submit(deleteTask);
                }
            }
        } finally {

        }

        final JButton btnStartPtr = btnRun;
        final JButton btnStopPtr = btnStop;
        REHandler finalizerTask = new REHandler(new Runnable() {
            @Override
            public void run() {
                submittedProcesses.clear();
                btnRun.setEnabled(true);
                btnStop.setEnabled(false);
                LogUtils.println(console, String.format("========================="));
                LogUtils.println(console, String.format("==="));
                LogUtils.println(console, String.format("===        Done"));
                LogUtils.println(console, String.format("==="));
                LogUtils.println(console, String.format("========================="));
            }
        }, console, System.err);

        exec.submit(finalizerTask);

        exec.shutdown();
    }//GEN-LAST:event_btnRunActionPerformed

    private void btnProteinProphetSeqDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProteinProphetSeqDbActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("FASTA files", "fa", "fasta");
        fileChooser.setFileFilter(fileNameExtensionFilter);
        fileChooser.setApproveButtonText("Select file");
        fileChooser.setApproveButtonToolTipText("Select");
        fileChooser.setDialogTitle("Choose FASTA file");
        fileChooser.setMultiSelectionEnabled(false);

        ThisAppProps.setFilechooserPathToCached(fileChooser, ThisAppProps.PROP_PARAMS_FILE_IN);

        if (!txtProteinProphetSeqDb.getText().isEmpty()) {
            File toFile = Paths.get(txtProteinProphetSeqDb.getText()).toFile();
            fileChooser.setCurrentDirectory(toFile);
        }

        int showOpenDialog = fileChooser.showOpenDialog(this);
        switch (showOpenDialog) {
            case JFileChooser.APPROVE_OPTION:

            File f = fileChooser.getSelectedFile();
            txtProteinProphetSeqDb.setText(f.getAbsolutePath());

            break;
        }
    }//GEN-LAST:event_btnProteinProphetSeqDbActionPerformed

    private void txtBinProteinProphetFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBinProteinProphetFocusLost
        saveBinProteinProphet();
    }//GEN-LAST:event_txtBinProteinProphetFocusLost

    private void btnBinProteinProphetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBinProteinProphetActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setApproveButtonText("Select binary");
        fileChooser.setApproveButtonToolTipText("Select ProteinProphet or Philosopher binary");
        fileChooser.setDialogTitle("Select binary to use for ProteinProphet");
        fileChooser.setMultiSelectionEnabled(false);
        //        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("JAR files", "jar");
        //        fileChooser.setFileFilter(fileNameExtensionFilter);

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        ThisAppProps.setFilechooserPathToCached(fileChooser, ThisAppProps.PROP_BINARIES_IN);

        if (!txtBinProteinProphet.getText().isEmpty()) {
            File toFile = Paths.get(txtBinProteinProphet.getText()).toFile();
            fileChooser.setCurrentDirectory(toFile);
        }

        int showOpenDialog = fileChooser.showOpenDialog(this);
        switch (showOpenDialog) {
            case JFileChooser.APPROVE_OPTION:

            File f = fileChooser.getSelectedFile();
            txtBinProteinProphet.setText(f.getAbsolutePath());
            ThisAppProps.saveFilechooserPathToCached(f, ThisAppProps.PROP_BINARIES_IN);
            saveBinProteinProphet();
            break;
        }
    }//GEN-LAST:event_btnBinProteinProphetActionPerformed

    private void chkRunProteinProphetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRunProteinProphetActionPerformed
        boolean selected = chkRunProteinProphet.isSelected();
        Container[] comps = new Container[] {
            panelProteinProphetBin,
            panelProteinProphetOptions
        };
        for (Container c : comps) {
            SwingUtils.enableComponents(c, selected);
        }
    }//GEN-LAST:event_chkRunProteinProphetActionPerformed

    private void btnSelectPeptideProphetSeqDbPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectPeptideProphetSeqDbPathActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("FASTA files", "fa", "fasta");
        fileChooser.setFileFilter(fileNameExtensionFilter);
        fileChooser.setApproveButtonText("Select file");
        fileChooser.setApproveButtonToolTipText("Select");
        fileChooser.setDialogTitle("Choose FASTA file");
        fileChooser.setMultiSelectionEnabled(false);

        ThisAppProps.setFilechooserPathToCached(fileChooser, ThisAppProps.PROP_PARAMS_FILE_IN);

        if (!txtPeptideProphetSeqDb.getText().isEmpty()) {
            File toFile = Paths.get(txtPeptideProphetSeqDb.getText()).toFile();
            fileChooser.setCurrentDirectory(toFile);
        }

        int showOpenDialog = fileChooser.showOpenDialog(this);
        switch (showOpenDialog) {
            case JFileChooser.APPROVE_OPTION:

            File f = fileChooser.getSelectedFile();
            txtPeptideProphetSeqDb.setText(f.getAbsolutePath());

            break;
        }
    }//GEN-LAST:event_btnSelectPeptideProphetSeqDbPathActionPerformed

    private void chkRunPeptideProphetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRunPeptideProphetActionPerformed
        boolean selected = chkRunPeptideProphet.isSelected();
        Container[] comps = new Container[] {
            panelPeptideProphetBin,
            panelPeptideProphetOptions
        };
        for (Container c : comps) {
            SwingUtils.enableComponents(c, selected);
        }
    }//GEN-LAST:event_chkRunPeptideProphetActionPerformed

    private void txtBinPeptideProphetFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBinPeptideProphetFocusLost
        saveBinPeptideProphet();
    }//GEN-LAST:event_txtBinPeptideProphetFocusLost

    private void btnSelectPeptideProphetBinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectPeptideProphetBinActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setApproveButtonText("Select binary");
        fileChooser.setApproveButtonToolTipText("Select PeptideProphet or Philosopher binary");
        fileChooser.setDialogTitle("Select binary to use for PeptideProphet");
        fileChooser.setMultiSelectionEnabled(false);
        //        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("JAR files", "jar");
        //        fileChooser.setFileFilter(fileNameExtensionFilter);

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        ThisAppProps.setFilechooserPathToCached(fileChooser, ThisAppProps.PROP_BINARIES_IN);

        if (!txtBinPeptideProphet.getText().isEmpty()) {
            File toFile = Paths.get(txtBinPeptideProphet.getText()).toFile();
            fileChooser.setCurrentDirectory(toFile);
        }

        int showOpenDialog = fileChooser.showOpenDialog(this);
        switch (showOpenDialog) {
            case JFileChooser.APPROVE_OPTION:

            File f = fileChooser.getSelectedFile();
            txtBinPeptideProphet.setText(f.getAbsolutePath());
            ThisAppProps.saveFilechooserPathToCached(f, ThisAppProps.PROP_BINARIES_IN);
            saveBinPeptideProphet();
            break;
        }
    }//GEN-LAST:event_btnSelectPeptideProphetBinActionPerformed

    private void btnRawClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRawClearActionPerformed
        tableModelRawFiles.dataClear();
    }//GEN-LAST:event_btnRawClearActionPerformed

    private void btnRawAddFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRawAddFilesActionPerformed
        if (btnRawAddFiles == evt.getSource()) {
            String approveText = "Select";
            JFileChooser fc = new JFileChooser();
            fc.setAcceptAllFileFilterUsed(true);
            FileNameExtensionFilter fileNameExtensionFilter = FraggerPanel.fileNameExtensionFilter;
            fc.setFileFilter(fileNameExtensionFilter);
            fc.setApproveButtonText(approveText);
            fc.setDialogTitle("Choose raw data files");
            fc.setMultiSelectionEnabled(true);
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

            ThisAppProps.setFilechooserPathToCached(fc, ThisAppProps.PROP_LCMS_FILES_IN);

            int retVal = fc.showDialog(this, approveText);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                File[] files = fc.getSelectedFiles();
                if (files.length > 0) {
                    ThisAppProps.saveFilechooserPathToCached(files[0], ThisAppProps.PROP_LCMS_FILES_IN);
                    List<Path> paths = new ArrayList<>(files.length);
                    for (File f : files) {
                        paths.add(Paths.get(f.getAbsolutePath()));
                    }
                    tableModelRawFiles.dataAddAll(paths);
                }

            } else {

            }
        }
    }//GEN-LAST:event_btnRawAddFilesActionPerformed

    private void btnRawRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRawRemoveActionPerformed
        int[] sel = tableRawFiles.getSelectedRows();
        if (sel.length == 0)
            return;
        List<Path> toRemove = new ArrayList<>();
        for (int i = 0; i < sel.length; i++) {
            toRemove.add(tableModelRawFiles.dataGet(sel[i]));
        }
        tableRawFiles.getSelectionModel().clearSelection();
        tableModelRawFiles.dataRemoveAll(toRemove);
    }//GEN-LAST:event_btnRawRemoveActionPerformed

    private void btnRawAddFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRawAddFolderActionPerformed
        throw new UnsupportedOperationException("Not implemented");
    }//GEN-LAST:event_btnRawAddFolderActionPerformed

    private void btnCheckJavaVersionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckJavaVersionActionPerformed
//        LinkedHashMap<String, String> map = new LinkedHashMap<>();
//        map.put("java.version", "")
        List<String> propNames = Arrays.asList(
                "java.version",
                "java.vm.specification.version",
                "java.vm.specification.vendor",
                "java.vm.specification.name",
                "java.vm.version",
                "java.vm.vendor",
                "java.vm.name",
                "java.specification.version",
                "java.specification.vendor",
                "java.specification.name"
                );
        StringBuilder sb = new StringBuilder("Java Info:\n");
        for (String propName : propNames) {
            String val = System.getProperty(propName);
            if (!StringUtils.isNullOrWhitespace(val)) {
                sb.append(propName).append(": ").append(val).append("\n");
            }
        }
        
        LogUtils.println(console, sb.toString());
    }//GEN-LAST:event_btnCheckJavaVersionActionPerformed



    
    
    private void resetRunButtons(boolean runEnabled) {
        btnRun.setEnabled(runEnabled);
        btnStop.setEnabled(!runEnabled);
    }
    
    private List<String> getLcmsFilePaths() {
        ArrayList<Path> paths = tableModelRawFiles.dataCopy();
        ArrayList<String> result = new ArrayList<>(paths.size());
        for (Path p : paths) {
            result.add(p.toString());
        }
        return result;
    }
    
    /**
     * This returns the paths to files to be created. Might be symlinks or actual file copies.
     * It does not create the files!
     * @param workDir
     * @return 
     */
    private List<Path> getLcmsFilePathsInWorkdir(Path workDir) {
        List<String> lcmsFilePaths = getLcmsFilePaths();
        ArrayList<Path> result = new ArrayList<>();
        for (String lcmsFilePath : lcmsFilePaths) {
            result.add(workDir.resolve(Paths.get(lcmsFilePath).getFileName()));
        }
        return result;
    }
    
    private void createLcmsFileSymlinks(Path workDir) throws IOException {
        List<String> lcmsFilePaths = getLcmsFilePaths();
        List<Path> paths = new ArrayList<>();
        for (String s : lcmsFilePaths) {
            paths.add(Paths.get(s));
        }
        
        List<Path> links = getLcmsFilePathsInWorkdir(workDir);
        for (int i = 0; i < paths.size(); i++) {
            Path lcmsPath = paths.get(i);
            Path link = links.get(i);
            if (link.equals(lcmsPath))
                return;
            if (Files.exists(link)) {
                // if that link already exists we need to make sure it points to
                // the same file
                if (!Files.isSymbolicLink(link)) {
                    throw new FileAlreadyExistsException(link.toString(), null, "A file already exists and is not a symbolic link");
                }
                Path linkTarget = Files.readSymbolicLink(link);
                if(!linkTarget.equals(lcmsPath)) {
                    String msg = String.format("A symblic link to mzXML file already exists, but points to a different file: %s", link);
                    throw new FileAlreadyExistsException(link.toString(), null, msg);
                }
                return;
            }
            Files.createSymbolicLink(link, lcmsPath);
        }
    }
    
    /**
     * Get the name of the file less the provided suffix.
     * @param path the filename component will be taken
     * @param suffix lowercase suffix
     * @return filename less suffix
     */
    private String getFileNameLessSuffix(Path path, String suffix) {
        String name = path.getFileName().toString();
        int indexOf = name.toLowerCase().indexOf(suffix);
        return indexOf >= 0 ? name.substring(0, indexOf) : name;
    }
    
    /**
     * @param path Path to lcms file
     * @return file paths resolved using lcmsFilePath's parent
     */
    private UmpireGarbageFiles getUmpireSeGarbageFiles(Path lcmsFilePath) {
        UmpireGarbageFiles umpireGarbageFiles = new UmpireGarbageFiles();
        String fileNameLessSuffix = getFileNameLessSuffix(lcmsFilePath, ".mzxml");
        Path fileOriginDir = lcmsFilePath.getParent();
        
        for (String fileToMove : UmpireGarbageFiles.filesToMove) {
            umpireGarbageFiles.toMove.add(fileOriginDir.resolve(fileToMove).toString());
        }
        
        for (String suffix : UmpireGarbageFiles.fileNameSuffixesToMove) {
            String filenameToMove = fileNameLessSuffix + suffix;
            String file = fileOriginDir.resolve(filenameToMove).toString();
            umpireGarbageFiles.toMove.add(file);
        }
        return umpireGarbageFiles;
    }
    
    private static class UmpireGarbageFiles {
        static List<String> filesToMove = Arrays.asList("diaumpire_se.log");
        static List<String> fileNameSuffixesToMove = Arrays.asList(
                "_Peak", ".DIAWindowsFS", ".RTidxFS", 
                ".ScanClusterMapping_Q1", ".ScanClusterMapping_Q2", ".ScanClusterMapping_Q3",
                ".ScanidxFS", ".ScanPosFS", ".ScanRTFS", "_diasetting.ser", "_params.ser",
                "_Q1.mgf", "_Q2.mgf", "_Q3.mgf");
        List<String> toMove = new ArrayList<>();
    }
    
    private List<ProcessBuilder> processBuildersCopyFiles(String programsDir, String workingDir, List<String> lcmsFilePaths) {
        List<ProcessBuilder> processBuilders = new LinkedList<>();
        
        URI currentJarUri = OsUtils.getCurrentJarPath();
        String currentJarPath = Paths.get(currentJarUri).toAbsolutePath().toString();
        Path wd = Paths.get(workingDir);
        
        for (String lcmsFilePath : lcmsFilePaths) {
            
            Path fileIn = Paths.get(lcmsFilePath);
            if (fileIn.getParent().equals(wd))
                continue;
            
            List<String> commands = new ArrayList<>();
            commands.add("java");
            commands.add("-cp");
            commands.add(currentJarPath);
            commands.add("dia.umpire.util.FileCopy");
            commands.add(lcmsFilePath);
            Path copyTo = Paths.get(workingDir, Paths.get(lcmsFilePath).getFileName().toString());
            commands.add(copyTo.toString());
            ProcessBuilder pb = new ProcessBuilder(commands);
            processBuilders.add(pb);
        }
        return processBuilders;
    }
    
    private boolean checkLcmsFileForDeletion(String workingDir, String lcmsFilePath) {
        Path wd = Paths.get(workingDir);
        Path file = Paths.get(lcmsFilePath);
        return !wd.equals(file.getParent());
    }
    
    private List<ProcessBuilder> processBuildersDeleteFiles(String workingDir, List<String> lcmsFilePaths) {
        List<ProcessBuilder> processBuilders = new LinkedList<>();
        
        URI currentJarUri = OsUtils.getCurrentJarPath();
        String currentJarPath = Paths.get(currentJarUri).toAbsolutePath().toString();
        
        Path wd = Paths.get(workingDir);
        
        for (String lcmsFilePath : lcmsFilePaths) {
            
            if (wd.equals(Paths.get(lcmsFilePath).getParent()))
                continue;
            
            List<String> commands = new ArrayList<>();
            commands.add("java");
            commands.add("-cp");
            commands.add(currentJarPath);
            commands.add("dia.umpire.util.FileDelete");
            Path copyTo = Paths.get(workingDir, Paths.get(lcmsFilePath).getFileName().toString());
            commands.add(copyTo.toString());
            ProcessBuilder pb = new ProcessBuilder(commands);
            processBuilders.add(pb);
        }
        return processBuilders;
    }
    
    private String getBinJava(String programsDir) {
        String binJava = "java";
        binJava = PathUtils.testBinaryPath(binJava, programsDir);
        if (binJava != null)
            return binJava;
        JOptionPane.showMessageDialog(this, "Java could not be found.\n"
                + "please make sure you have it installed \n"
                + "and that java.exe can be found on PATH", "Error", JOptionPane.ERROR_MESSAGE);
        return null;
    }
    
//    private List<ProcessBuilder> processBuildersUmpire(String programsDir, String workingDir, List<String> lcmsFilePaths, String dateStr) {
//        List<ProcessBuilder> processBuilders = new LinkedList<>();
//        if (chkRunUmpire.isSelected()) {
//            
//            String binJava = getBinJava(programsDir);
//            
//            if (binJava == null)
//                return null;
//            
//            String binUmpire = txtBinUmpire.getText();
//            if (binUmpire.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "[DIA Umpire SE tab]\nDIA Umpire SE binary can't be an empty string", "Error", JOptionPane.ERROR_MESSAGE);
//                return null;
//            }
//            binUmpire = testFilePath(binUmpire, programsDir);
//            if (binUmpire == null) {
//                JOptionPane.showMessageDialog(this, "[DIA Umpire SE tab]\nCould not locate DIA-Umpire SE jar", "Error", JOptionPane.ERROR_MESSAGE);
//                return null;
//            }
//            
//            String binMsconvert = txtBinMsconvert.getText();
//            if (binMsconvert.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "[DIA Umpire SE tab]\nMSConvert binary can't be an empty string", "Error", JOptionPane.ERROR_MESSAGE);
//                return null;
//            }
//            binMsconvert = testBinaryPath(binMsconvert, programsDir);
//            if (binMsconvert == null) {
//                JOptionPane.showMessageDialog(this, "MSConvert binary could not be found \n"
//                        + "on PATH or in the working directory", "Error", JOptionPane.ERROR_MESSAGE);
//                return null;
//            }
//            
//            
//            try {
//                // Running Umpire
//                UmpireParams collectedUmpireParams = collectUmpireParams();
//                
//                // writing umpire params file
//                String umpireParamsFileName = UmpireParams.FILE_BASE_NAME + "_" + dateStr + "." + UmpireParams.FILE_BASE_EXT;
//                Path umpireParamsFilePath = Paths.get(workingDir, umpireParamsFileName);
//                FileOutputStream fos = new FileOutputStream(umpireParamsFilePath.toFile());
//                PropertiesUtils.writePropertiesContent(collectedUmpireParams, fos);
//                
//                // run umpire for each file
//                Object value = spinnerRam.getModel().getValue();
//                int ram = (Integer)spinnerRam.getModel().getValue();
//                if (ram < MIN_RAM)
//                    ram = MIN_RAM;
//                
//                List<String> createdMgfFiles = new ArrayList<>();
//                List<String> createdMzXmlFiles = new ArrayList<>();
//                Path wdPath = Paths.get(workingDir).toAbsolutePath();
//                List<Path> lcmsFileSymlinks = getLcmsFilePathsInWorkdir(wdPath);
//                for (Path lcmsSymlink : lcmsFileSymlinks) {
//                    Path curMzxmlPath = lcmsSymlink;
//                    Path curMzxmlFileName = curMzxmlPath.getFileName();
//                    Path curMzxmlFileDir = curMzxmlPath.getParent();
//                    
//                    // umpire
//                    //  java -jar -Xmx8G DIA_Umpire_SE.jar mzMXL_file diaumpire_se.params
//                    List<String> commands = new ArrayList<>();
//                    commands.add("java");
//                    //commands.add("-d64");
//                    commands.add("-jar");
//                    StringBuilder sb = new StringBuilder().append("-Xmx").append(ram).append("m");
//                    commands.add(sb.toString());
//                    commands.add(binUmpire);
//                    commands.add(curMzxmlPath.toString());
//                    commands.add(umpireParamsFilePath.toString());
//                    
//                    ProcessBuilder pb = new ProcessBuilder(commands);
//                    processBuilders.add(pb);
//                    
//                    
//                    // check if the working dir is the dir where the mzXML file was
//                    // if it is, then don't do anything, if it is not, then copy
//                    // UmpireSE outputs to the working directory
//                    // and also create symlinks to the original files
//                    
//                    if (!wdPath.equals(curMzxmlFileDir)) {
//                        // find the curernt gui JAR location
//                        URI currentJarUri = OsUtils.getCurrentJarPath();
//                        String currentJarPath = Paths.get(currentJarUri).toAbsolutePath().toString();
//                        
//                        
//                        // working dir is different from mzXML file location, need to move output
//                        UmpireGarbageFiles umpireGarbageFiles = getUmpireSeGarbageFiles(curMzxmlPath);
//                        
//                        for (String path : umpireGarbageFiles.toMove) {
//                            List<String> commandsFileMove = new ArrayList<>();
//                            commandsFileMove.add("java");
//                            commandsFileMove.add("-cp");
//                            commandsFileMove.add(currentJarPath);
//                            commandsFileMove.add("dia.umpire.util.FileMove");
//                            String origin = curMzxmlFileDir.resolve(Paths.get(path).getFileName()).toString();
//                            String destination = wdPath.resolve(Paths.get(path).getFileName()).toString();
//                            commandsFileMove.add(origin);
//                            commandsFileMove.add(destination);
//                            ProcessBuilder pbFileMove = new ProcessBuilder(commandsFileMove);
//                            processBuilders.add(pbFileMove);
//                        }
//                        
////                        // creating symlink
////                        List<String> commandsSymlink = new ArrayList<>();
////                        commandsSymlink.add("java");
////                        commandsSymlink.add("-cp");
////                        commandsSymlink.add(currentJarPath);
////                        commandsSymlink.add("dia.umpire.util.FileSymlink");
////                        String origin = curMzxmlPath.toString();
////                        String symlink = wdPath.resolve(curMzxmlFileName).toString();
////                        commandsSymlink.add(origin);
////                        commandsSymlink.add(symlink);
////                        ProcessBuilder pbSymlink = new ProcessBuilder(commandsSymlink);
////                        processBuilders.add(pbSymlink);
//                    }
//                    
//                    
//                    // msconvert
//                    for (int i = 1; i <= 3; i++) {
//                        List<String> commandsMsconvert = new ArrayList<>();
//                        commandsMsconvert.add(binMsconvert);
//                        commandsMsconvert.add("--verbose");
//                        commandsMsconvert.add("--32");
//                        commandsMsconvert.add("--zlib");
//                        commandsMsconvert.add("--mzXML");
//                        commandsMsconvert.add("--outdir");
//                        commandsMsconvert.add(workingDir);
//
//                        String s = curMzxmlFileName.toString();
//                        int indexOf = s.toLowerCase().indexOf(".mzxml");
//                        String baseName = curMzxmlFileName.toString().substring(0, indexOf);
//                        Path createdMgf = Paths.get(workingDir, baseName+"_Q"+i+".mgf");
//                        Path createdMzXml = Paths.get(workingDir, baseName+"_Q"+i+".mzxml");
//                        commandsMsconvert.add(createdMgf.toString());
//                        
//                        ProcessBuilder pbMsconv = new ProcessBuilder(commandsMsconvert);
//                        processBuilders.add(pbMsconv);
//                        createdMgfFiles.add(createdMgf.toString());
//                        createdMzXmlFiles.add(createdMzXml.toString());
//                    }
//                }
//                
//            } catch (ParsingException ex) {
//                JOptionPane.showMessageDialog(this, "Error collecting user variables for Umpire SE.\n",
//                        "Error", JOptionPane.ERROR_MESSAGE);
//                return null;
//            } catch (FileNotFoundException | FileWritingException ex) {
//                JOptionPane.showMessageDialog(this, "Error writing Umpire SE parameters file to working dir.\n",
//                        "Error", JOptionPane.ERROR_MESSAGE);
//                return null;
//            }
//        }
//        return processBuilders;
//    }
    
//    private String getUmpireQuantFastaPath() {
//        String fastaPath = txtUmpireQuantSeqDb.getText().trim();
//        if (fastaPath.isEmpty()) {
//            fastaPath = txtProteinProphetSeqDb.getText().trim();
//            if (fastaPath.isEmpty()) {
//                fastaPath = txtPeptideProphetSeqDb.getText().trim();
//                if (fastaPath.isEmpty()) {
//                    fastaPath = txtCometSeqDb.getText().trim();
//                    if (fastaPath.isEmpty()) {
//                        return null;
//                    }
//                }       
//            }
//        }
//        return fastaPath.isEmpty() ? null : fastaPath;
//    }
    
    private Path getCombinedProtFilePath(String workingDir) {
        String combinedProtFile = txtProteinProphetOutputFile.getText().trim();
        if (combinedProtFile.isEmpty()) {
            JOptionPane.showMessageDialog(this, String.format("Please specify ProteinProphet output path on ProteinProphet tab.\n"
                    + "This is needed even if you're not running ProteinProphet right now.\n"
                    + "In which case check the box to run it, add the filename and uncheck the filebox.\n"
                    + "Sorry for the inconvenience."),
                    "Errors", JOptionPane.ERROR_MESSAGE);
            return null;
        } else {
            Path combinedProtFileFullPath = Paths.get(workingDir, combinedProtFile).toAbsolutePath();
            // can't check if file exists beforehand
//            if (!Files.exists(combinedProtFileFullPath)) {
//                JOptionPane.showMessageDialog(this, String.format("Could not find combined protxml file (for Umpire Quant).\n"
//                        + "Location: %s", combinedProtFileFullPath.toString()),
//                    "Errors", JOptionPane.ERROR_MESSAGE);
//                return null;
//            }
            return combinedProtFileFullPath;
        }
    }
    
//    private List<ProcessBuilder> processBuildersUmpireQuant(String programsDir, String workingDir, List<String> lcmsFilePaths, String dateStr) {
//        List<ProcessBuilder> processBuilders = new LinkedList<>();
//        if (chkRunUmpireQuant.isSelected()) {
//            
//            String binJava = "java";
//            binJava = testBinaryPath(binJava, programsDir);
//            if (binJava == null) {
//                JOptionPane.showMessageDialog(this, "Java could not be found.\n"
//                        + "please make sure you have it installed \n"
//                        + "and that java.exe can be found on PATH", "Error", JOptionPane.ERROR_MESSAGE);
//                return null;
//            }
//            
//            
//            String jarUmpireQuant = txtBinUmpireQuant.getText().trim();
//            if (jarUmpireQuant.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "[DIA Umpire Quant tab]\nDIA Umpire Quant jar can't be empty string", "Error", JOptionPane.ERROR_MESSAGE);
//                return null;
//            }
//            jarUmpireQuant = testFilePath(jarUmpireQuant, programsDir);
//            if (jarUmpireQuant == null) {
//                JOptionPane.showMessageDialog(this, "[DIA Umpire Quant tab]\nCould not locate DIA-Umpire Quant jar", "Error", JOptionPane.ERROR_MESSAGE);
//                return null;
//            }
//            
//            String fastaPath = getUmpireQuantFastaPath();
//            if (fastaPath == null) {
//                JOptionPane.showMessageDialog(this, "Fasta file (DIA-Umpire Quant) path can't be empty",
//                                "Warning", JOptionPane.WARNING_MESSAGE);
//                return null;
//            }
//            
//            String fastaPathOrig = new String(fastaPath);
//            fastaPath = testFilePath(fastaPath, workingDir);
//            if (fastaPath == null) {
//                JOptionPane.showMessageDialog(this, String.format("Could not find fasta file (DIA-Umpire Quant) at:\n%s", fastaPathOrig),
//                        "Errors", JOptionPane.ERROR_MESSAGE);
//                return null;
//            }
//            
//            
//            Path combinedProtFilePath = getCombinedProtFilePath(workingDir);
//            if (combinedProtFilePath == null) {
//                return null; // all popus are already shown by getCombinedProtFilePath()
//            }
//            
//            
//            
//            try {
//                // Running Umpire Quant
//                UmpireQuantParams collectedParams = collectUmpireQuantParams();
//                Properties props = collectedParams.getProps();
//                
//                
//                // writing umpire params file
//                String paramsFileName = UmpireQuantParams.FILE_BASE_NAME + "_" + dateStr + "." + UmpireQuantParams.FILE_BASE_EXT;
//                Path paramsFilePath = Paths.get(workingDir, paramsFileName);
//                FileOutputStream fos = new FileOutputStream(paramsFilePath.toFile());
//                PropertiesUtils.writePropertiesContent(collectedParams, fos);
//                
//                // run umpire for each file
//                Object value = spinnerRam.getModel().getValue();
//                int ram = (Integer)spinnerRam.getModel().getValue();
//                if (ram < MIN_RAM)
//                    ram = MIN_RAM;
//                
//                // java -jar -Xmx22732m ~/dia/DIA-Umpire/DIA_Umpire_Quant.jar ~/workdir/DIA_Umpire_Quant/diaumpire_quant_params.txt
//                List<String> commands = new ArrayList<>();
//                commands.add("java");
//                commands.add("-jar");
//                StringBuilder sb = new StringBuilder().append("-Xmx").append(ram).append("m");
//                commands.add(sb.toString());
//                commands.add(jarUmpireQuant);
//                commands.add(paramsFilePath.toString());
//
//                ProcessBuilder pb = new ProcessBuilder(commands);
//                processBuilders.add(pb);
//                
//            } catch (ParsingException ex) {
//                JOptionPane.showMessageDialog(this, "Error collecting user variables for Umpire Quant.\n",
//                        "Error", JOptionPane.ERROR_MESSAGE);
//                return null;
//            } catch (FileNotFoundException | FileWritingException ex) {
//                JOptionPane.showMessageDialog(this, "Error writing Umpire Quant parameters file to working dir.\n",
//                        "Error", JOptionPane.ERROR_MESSAGE);
//                return null;
//            }
//        }
//        return processBuilders;
//    }
    
    private boolean isPhilosopherBin(String binPathToCheck) {
        Pattern isPhilosopherRegex = Pattern.compile("philosopher", Pattern.CASE_INSENSITIVE);
        Matcher matcher = isPhilosopherRegex.matcher(binPathToCheck);
        return matcher.find();
    }
    
    private List<ProcessBuilder> processBuildersFragger(String programsDir, String workingDir, List<String> lcmsFilePaths, String dateStr) {
        List<ProcessBuilder> builders = new LinkedList<>();
        if (fraggerPanel.isRunMsfragger()) {
            
            String bin = fraggerPanel.getFraggerBin();
            if (StringUtils.isNullOrWhitespace(bin)) {
                JOptionPane.showMessageDialog(this, "Binary for running Fragger can not be an empty string.\n",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            bin = PathUtils.testFilePath(bin, programsDir);
            if (bin == null) {
                JOptionPane.showMessageDialog(this, "Binary for running Fragger not found or could not be run.\n"
                        + "Neither on PATH, nor in the working directory",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            String fastaPath = fraggerPanel.getFastaPath();
            if (StringUtils.isNullOrWhitespace(fastaPath)) {
                JOptionPane.showMessageDialog(this, "Fasta file path (Fragger) can't be empty",
                    "Warning", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            
            
            MsfraggerParams params = null;
            try {
                params = fraggerPanel.collectParams();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Could not collect MSFragger params from GUI.\n",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
            }
            
            Path savedParamsPath = Paths.get(workingDir, MsfraggerParams.DEFAULT_FILE);
            try {
                params.save(new FileOutputStream(savedParamsPath.toFile()));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Could not save fragger.params file to working dir.\n",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
            }
            
            int ramGb = fraggerPanel.getRamGb();
            
            Map<String, String> mapRawToPep = createPepxmlFilePathsDirty(lcmsFilePaths, params.getOutputFileExtension());
            for (String filePath : lcmsFilePaths) {
                // Fragger search
                Path path = Paths.get(filePath);
                ArrayList<String> cmd = new ArrayList<>();
                cmd.add("java");
                cmd.add("-jar");
                if (ramGb > 0) {
                    cmd.add(new StringBuilder().append("-Xmx").append(ramGb).append("G").toString());
                }
                cmd.add(bin);
                cmd.add(savedParamsPath.toString());
                cmd.add(path.toAbsolutePath().toString());
                ProcessBuilder pb = new ProcessBuilder(cmd);
                builders.add(pb);
                
                // Move the resulting file
                String pepFile = mapRawToPep.get(filePath);
                Path pepPath = Paths.get(pepFile);
                Path wdPath = Paths.get(workingDir);
                
                // check if the working dir is the dir where the mzXML file was
                // if it is, then don't do anything, if it is not, then copy
                // UmpireSE outputs to the working directory
                // and also create symlinks to the original files

                if (!wdPath.equals(pepPath.getParent())) {
                    // find the curernt gui JAR location
                    URI currentJarUri = OsUtils.getCurrentJarPath();
                    String currentJarPath = Paths.get(currentJarUri).toAbsolutePath().toString();

                    // working dir is different from pepxml file location, need to move output
                    List<String> commandsFileMove = new ArrayList<>();
                    commandsFileMove.add("java");
                    commandsFileMove.add("-cp");
                    commandsFileMove.add(currentJarPath);
                    commandsFileMove.add("umich.msfragger.util.FileMove");
                    String origin = pepPath.toAbsolutePath().toString();
                    String destination = Paths.get(wdPath.toString(), pepPath.getFileName().toString()).toString();
                    commandsFileMove.add(origin);
                    commandsFileMove.add(destination);
                    ProcessBuilder pbFileMove = new ProcessBuilder(commandsFileMove);
                    builders.add(pbFileMove);
                }
            }
        }
        
        return builders;
    }
    
    private Map<String, String> createPepxmlFilePathsDirty(List<String> lcmsFilePaths, String ext) {
        HashMap<String, String> pepxmls = new HashMap<>();
        for (String s : lcmsFilePaths) {
            String baseName = s.substring(0, s.lastIndexOf(".") + 1);
            pepxmls.put(s, baseName + ext);
        }
        return pepxmls;
    }
    
    private Map<String, String> createPepxmlFilePathsAfterMove(Map<String, String> dirtyPepXmls, String workingDir) {
        HashMap<String, String> pepxmls = new HashMap<>();
        Path wd = Paths.get(workingDir);
        for (Map.Entry<String, String> entry : dirtyPepXmls.entrySet()) {
            String raw = entry.getKey();
            String pepxmlDirty = entry.getValue();
            Path pepxmlClean = wd.resolve(Paths.get(pepxmlDirty).getFileName()).toAbsolutePath();
            pepxmls.put(raw, pepxmlClean.toString());
        }
        return pepxmls;
    }
    
//    private List<ProcessBuilder> processBuildersComet(String programsDir, String workingDir, List<String> lcmsFilePaths, String dateStr) {
//        List<ProcessBuilder> processBuilders = new LinkedList<>();
//        if (chkRunCometSearch.isSelected()) {
//            try {
//                CometParams collectedCometParams = collectCometParams();
//                
//                String bin = txtBinComet.getText().trim();
//                if (bin.isEmpty()) {
//                    JOptionPane.showMessageDialog(this, "Binary for running Comet can not be an empty string.\n",
//                        "Error", JOptionPane.ERROR_MESSAGE);
//                    return null;
//                }
//                bin = testBinaryPath(bin, programsDir);
//                if (bin == null) {
//                    JOptionPane.showMessageDialog(this, "Binary for running Comet not found or could not be run.\n"
//                            + "Neither on PATH, nor in the working directory",
//                        "Error", JOptionPane.ERROR_MESSAGE);
//                    return null;
//                }
//                
//                String fastaPath = txtCometSeqDb.getText().trim();
//                if (fastaPath.isEmpty()) {
//                    JOptionPane.showMessageDialog(this, "Fasta file (Comet) path can't be empty",
//                        "Warning", JOptionPane.WARNING_MESSAGE);
//                    return null;
//                }
//                String fastaPathOrig = new String(fastaPath);
//                fastaPath = testFilePath(fastaPath, workingDir);
//                if (fastaPath == null) {
//                    JOptionPane.showMessageDialog(this, String.format("Could not find fasta file (Comet) at:\n%s", fastaPathOrig),
//                            "Errors", JOptionPane.ERROR_MESSAGE);
//                    return null;
//                }
//                
//                    
//
//                // writing Comet params file
//                String cometParamsFileName = CometParams.FILE_BASE_NAME + "_" + dateStr + "." + CometParams.FILE_BASE_EXT;
//                Path cometParamsFilePath = Paths.get(workingDir, cometParamsFileName);
//                FileOutputStream fos = new FileOutputStream(cometParamsFilePath.toFile());
//                PropertiesUtils.writePropertiesContent(collectedCometParams, fos);
//                
//                // run comet for each file
//                Object value = spinnerRam.getModel().getValue();
//                int ram = (Integer)spinnerRam.getModel().getValue();
//                if (ram < 1)
//                    ram = 1;
//                
//                List<String> createdMzXmlFiles = new ArrayList<>();
//                boolean isPhilosopher = isPhilosopherBin(bin);
//                for (String filePath : lcmsFilePaths) {
//                    // Comet
//                    for (int i = 1; i <= 3; i++) {
//                        List<String> commands = new ArrayList<>();
//                        commands.add(bin);
//                        if (isPhilosopher)
//                            commands.add(Philosopher.CMD_COMET);
//                        
//                        String cmdOpts = txtCometCmdLineOpts.getText().trim();
//                        if (!cmdOpts.isEmpty()) {
//                            String[] opts = cmdOpts.split("\\s+");
//                            for (String opt : opts) {
//                                if (!opt.isEmpty()) {
//                                    if (!isPhilosopher && opt.equals(Philosopher.CMD_COMET)) // for non-philosopher skip this option
//                                        continue;
//                                    commands.add(opt);
//                                }
//                            }
//                        }
//                        
//                        commands.add("--param");
//                        commands.add(cometParamsFilePath.toString());
//
//                        Path curMzXMl = Paths.get(filePath);
//                        Path mzXmlFileName = curMzXMl.getFileName();
//                    
//                        String s = mzXmlFileName.toString();
//                        int indexOf = s.toLowerCase().indexOf(".mzxml");
//                        String baseName = mzXmlFileName.toString().substring(0, indexOf);
//                        Path createdMzXml = Paths.get(workingDir, baseName+"_Q"+i+".mzXML");
//                        commands.add(createdMzXml.toString());
//                        ProcessBuilder pb = new ProcessBuilder(commands);
//                        Map<String, String> env = pb.environment();
//                        // set environment 
//                        String ENV_WEBSERVER_ROOT = "WEBSERVER_ROOT";
//                        String webroot = env.get(ENV_WEBSERVER_ROOT);
//                        if (webroot == null) {
//                            env.put(ENV_WEBSERVER_ROOT, "fake-WEBSERVER_ROOT-value");
//                        }
//                        processBuilders.add(pb);
//                        createdMzXmlFiles.add(createdMzXml.toString());
//                    }
//                }
//                
//            } catch (ParsingException ex) {
//                JOptionPane.showMessageDialog(this, "Error collecting user variables for Comet Search.\n",
//                        "Error", JOptionPane.ERROR_MESSAGE);
//                return null;
//            } catch (FileNotFoundException | FileWritingException ex) {
//                JOptionPane.showMessageDialog(this, "Error collecting user variables for Comet Search.\n",
//                        "Error", JOptionPane.ERROR_MESSAGE);
//                return null;
//            }
//        }
//        return processBuilders;
//    }
    
    /**
     * Creates the ProcessBuilders for running PeptideProphet.
     * @param workingDir
     * @param lcmsFilePaths
     * @return null in case of errors, or a list of process builders.
     */
    private List<ProcessBuilder> processBuildersPeptideProphet(String programsDir, String workingDir, List<String> lcmsFilePaths) {
        List<ProcessBuilder> processBuilders = new LinkedList<>();
        if (chkRunPeptideProphet.isSelected()) {
            String bin = txtBinPeptideProphet.getText().trim();
            if (StringUtils.isNullOrWhitespace(bin)) {
                JOptionPane.showMessageDialog(this, "Philosopher (PeptideProphet) binary can not be an empty string.\n",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            bin = PathUtils.testBinaryPath(bin, programsDir);
            if (bin == null) {
                JOptionPane.showMessageDialog(this, "Philosopher (PeptideProphet) binary not found.\n"
                        + "Neither on PATH, nor in the working directory",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }


            String fastaPath = txtPeptideProphetSeqDb.getText().trim();
            if (StringUtils.isNullOrWhitespace(fastaPath)) {
                fastaPath = fraggerPanel.getTxtMsfraggerDb().getText().trim();
                if (StringUtils.isNullOrWhitespace(fastaPath)) {
                    JOptionPane.showMessageDialog(this, "Fasta file (PeptideProphet) path can't be empty",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
            }
            String fastaPathOrig = fastaPath;
            fastaPath = PathUtils.testFilePath(fastaPath, workingDir);
            if (fastaPath == null) {
                JOptionPane.showMessageDialog(this, String.format("Could not find fasta file (PeptideProphet) at:\n%s", fastaPathOrig),
                        "Errors", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            PeptideProphetParams peptideProphetParams = new PeptideProphetParams();
            peptideProphetParams.setCmdLineParams(txtPeptideProphetCmdLineOptions.getText().trim());

            
            String philosopherPeptideprophetCmd = "peptideprophet";
            boolean isPhilosopher = isPhilosopherBin(bin);
            Map<String, String> pepxmlDirty = createPepxmlFilePathsDirty(lcmsFilePaths, fraggerPanel.getOutputFileExt());
            Map<String, String> pepxmlClean = createPepxmlFilePathsAfterMove(pepxmlDirty, workingDir);
            for (String rawFilePath : lcmsFilePaths) {
                // Comet
                List<String> commands = new ArrayList<>();
                commands.add(bin);
                if (isPhilosopher) // for philosopher we always add the correct command
                    commands.add(Philosopher.CMD_PEPTIDE_PROPHET);

                if (!peptideProphetParams.getCmdLineParams().isEmpty()) {
                    String cmdOpts = peptideProphetParams.getCmdLineParams();
                    List<String> opts = StringUtils.splitCommandLine(cmdOpts);
                    for (String opt : opts) {
                        if (!opt.isEmpty()) {
                            if (opt.equals(Philosopher.CMD_PEPTIDE_PROPHET))
                                continue;
                            commands.add(opt);
                        }
                    }
                }
                commands.add("--database");
                commands.add(fastaPath);
                
                String pepxmlInWd = pepxmlClean.get(rawFilePath);
                if (pepxmlInWd == null) {
                    JOptionPane.showMessageDialog(this, "PeptideProphet process could not figure where a pepxml was.\n"
                            + "RAW: " + rawFilePath + "\n",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                
                commands.add(pepxmlInWd);
                ProcessBuilder pb = new ProcessBuilder(commands);
                Map<String, String> env = pb.environment();
                // set environment 
                String ENV_WEBSERVER_ROOT = "WEBSERVER_ROOT";
                String webroot = env.get(ENV_WEBSERVER_ROOT);
                if (webroot == null) {
                    env.put(ENV_WEBSERVER_ROOT, "fake-WEBSERVER_ROOT-value");
                }
                processBuilders.add(pb);
            }
        }
        return processBuilders;
    }
    
    /**
     * Creates the processBuilders for running ProteinProphet.
     * @return null in case of error, empty list if nothing needs to be added.
     */
    private List<ProcessBuilder> processBuildersProteinProphet(String programsDir, String workingDir, List<String> lcmsFilePaths) {
        if (chkRunProteinProphet.isSelected()) {
            String bin = txtBinProteinProphet.getText().trim();
            if (StringUtils.isNullOrWhitespace(bin)) {
                JOptionPane.showMessageDialog(this, "ProteinProphet binary can not be an empty string.\n",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            bin = PathUtils.testBinaryPath(bin, programsDir);
            if (bin == null) {
                JOptionPane.showMessageDialog(this, "ProteinProphet binary not found or could not be launched.\n"
                        + "Neither on PATH, nor in the working directory",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            String outputFileName = txtProteinProphetOutputFile.getText().trim();
            if (outputFileName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ProteinProphet output file name can not be an empty string.\n",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            } else if (!outputFileName.toLowerCase().endsWith(".prot.xml")) {
                JOptionPane.showMessageDialog(this, "ProteinProphet output file name must end with '.prot.xml'.\n",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            } else {
                int index = outputFileName.trim().toLowerCase().indexOf(".prot.xml");
                if (index <= 0) {
                    JOptionPane.showMessageDialog(this, "ProteinProphet output file name must have content before '.prot.xml'.\n",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }

            ProteinProphetParams proteinProphetParams = new ProteinProphetParams();
            proteinProphetParams.setCmdLineParams(txtProteinProphetCmdLineOpts.getText());


            List<String> createdInteractFiles = new ArrayList<>();
            List<String> commands = new ArrayList<>();
            commands.add(bin);
            boolean isPhilosopher = isPhilosopherBin(bin);
            
            
            if (isPhilosopher) {
                commands.add(Philosopher.CMD_PROTEIN_PROPHET);
                commands.add("--output");
                commands.add(txtProteinProphetOutputFile.getText());
                
                // for Philosopher command line flags go before files
                String cmdLineOpts = proteinProphetParams.getCmdLineParams().trim();
                if (!cmdLineOpts.isEmpty()) {
                    List<String> opts = StringUtils.splitCommandLine(cmdLineOpts);
                    commands.addAll(opts);
                }
                
                if (chkProteinProphetAddInteractPepXmlsSeparately.isSelected()) {
                    commands.add("interact-*.pep.xml");
                } else {
                    for (String filePath : lcmsFilePaths) {
                        for (int i = 1; i <= 3; i++) {
                            Path curMzXMl = Paths.get(filePath);
                            Path mzXmlFileName = curMzXMl.getFileName();
                            String s = mzXmlFileName.toString();
                            int indexOf = s.toLowerCase().indexOf(".mzxml");
                            String baseName = mzXmlFileName.toString().substring(0, indexOf);
                            //Path createdPepXml = Paths.get(curMzXMl.getParent().toString(), "interact-" + baseName + "_Q" + i + ".pep.xml");
                            Path createdPepXml = Paths.get(workingDir, "interact-" + baseName + "_Q" + i + ".pep.xml");
                            commands.add(createdPepXml.toString());
                            createdInteractFiles.add(createdPepXml.toString());
                        }
                    }
                }
            } else {
                for (String filePath : lcmsFilePaths) {
                    for (int i = 1; i <= 3; i++) {
                        Path curMzXMl = Paths.get(filePath);
                        Path mzXmlFileName = curMzXMl.getFileName();
                        String s = mzXmlFileName.toString();
                        int indexOf = s.toLowerCase().indexOf(".mzxml");
                        String baseName = mzXmlFileName.toString().substring(0, indexOf);
                        //Path createdPepXml = Paths.get(curMzXMl.getParent().toString(), "interact-" + baseName + "_Q" + i + ".pep.xml");
                        Path createdPepXml = Paths.get(workingDir, "interact-" + baseName + "_Q" + i + ".pep.xml");
                        commands.add(createdPepXml.toString());
                        createdInteractFiles.add(createdPepXml.toString());
                    }
                }
                
                // output file
                commands.add(txtProteinProphetOutputFile.getText());
                
                // for native ProteinProphet command line flags go in the end
                String cmdLineOpts = proteinProphetParams.getCmdLineParams().trim();
                if (!cmdLineOpts.isEmpty()) {
                    List<String> opts = StringUtils.splitCommandLine(cmdLineOpts);
                    commands.addAll(opts);
                }
            }
            
            
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(Paths.get(workingDir).toFile());
            Map<String, String> env = pb.environment();
            
            // add this variable so that TPP didn't try to use webserver stuff
            String ENV_XML_ONLY = "XML_ONLY";
            env.put(ENV_XML_ONLY, "1");

            // collect variables from system
            StringBuilder pathEnv = new StringBuilder();
            Set<String> mergedKeys = new HashSet<>();
            Set<String> envKeys = env.keySet();
            for (String key : envKeys) {
                if (key.toLowerCase().equals("path")) {
                    String pathVal = env.get(key);
                    pathVal = pathVal.trim();
                    pathEnv.append(pathVal);
                    if (!pathVal.endsWith(";"))
                        pathEnv.append(";");
                    mergedKeys.add(key);
                }
            }
            for (String key : mergedKeys) {
                env.remove(key);
            }
            
            String ENV_PATH = "PATH";
            Path binPath = Paths.get(bin);
            String binFolder = null;
            if (binPath.isAbsolute()) {
                // the path to the executable was specified as absolute, other needed files must be there as well
                binFolder = binPath.toAbsolutePath().getParent().toString();
            } else if (Files.exists(binPath)) {
                binFolder = binPath.toAbsolutePath().getParent().toString();
            } else {
                binPath = Paths.get(workingDir, bin);
                if (Files.exists(binPath)) {
                    binFolder = binPath.toAbsolutePath().getParent().toString();
                }
            }
            if (binFolder != null) {
                pathEnv.append(";").append(binFolder);
            }
            String pathEnvValue = pathEnv.toString();
            env.put(ENV_PATH, pathEnvValue);
            
            // for native TPP we will add some magic variables
//            if (!isPhilosopher) {
//                String ENV_XML_ONLY = "XML_ONLY";
//                env.put(ENV_XML_ONLY, "1");
//
//                String ENV_PATH = "PATH";
//                String envPath = env.get(ENV_PATH);
//                if (envPath == null) {
//                    envPath = "";
//                } else {
//                    envPath = envPath.trim();
//                }
//                StringBuilder sbEnvPath = new StringBuilder(envPath);
//                if (sbEnvPath.length() != 0)
//                    sbEnvPath.append(";");
//                // the ProteinProphet can be either in working directory, or in some directory
//                // that we can get from the executable absolute path
//                String binFolder = null;
//                try {
//                    Path binPath = Paths.get(bin);
//                    if (binPath.isAbsolute()) {
//                        // the path to the executable was specified as absolute, other needed files must be there as well
//                        binFolder = binPath.toAbsolutePath().getParent().toString();
//                    } else if (Files.exists(binPath)) {
//                        binFolder = binPath.toAbsolutePath().getParent().toString();
//                    } else {
//                        binPath = Paths.get(workingDir, bin);
//                        if (Files.exists(binPath)) {
//                            binFolder = binPath.toAbsolutePath().getParent().toString();
//                        }
//                    }                  
//                } catch (Exception ignore) {
//                    // let's hope that everything ProteinProphet needs can be found on system PATH
//                }
//                if (binFolder != null) {
//                    sbEnvPath.append(binFolder);
//                    env.put(ENV_PATH, sbEnvPath.toString());
//                }
//            }
            return Arrays.asList(pb);
        }
        return Collections.emptyList();
    }
    
    private Path getWorkingDir() {
        String wdStr = txtWorkingDir.getText().trim();
        Path path = Paths.get(wdStr).toAbsolutePath();
        return path;
    }

    private String getDefaultTextMsfragger() {
        String value = ThisAppProps.loadPropFromCache(ThisAppProps.PROP_TEXTFIELD_PATH_MSFRAGGER);
        if (value != null)
            return value;
        ResourceBundle bundle = ResourceBundle.getBundle("umich/msfragger/gui/Bundle"); // NOI18N
        return bundle.getString("default.msfragger.jar");
    }
    
    private String getDefaultTextProgramsDir() {
        String value = ThisAppProps.loadPropFromCache(ThisAppProps.PROP_BINARIES_IN);
        return value == null ? "" : value;
    }

    private String getDefaultTextMsconvert() {
        String value = ThisAppProps.loadPropFromCache(ThisAppProps.PROP_TEXTFIELD_PATH_MSCONVERT);
        if (value != null)
            return value;

        String binaryName;
        ResourceBundle bundle = ResourceBundle.getBundle("dia/umpire/gui/Bundle"); // NOI18N
        if (OsUtils.isWindows()) {
            binaryName = bundle.getString("default.msconvert.win");
        } else {
            binaryName = bundle.getString("default.msconvert.nix");
        }
        String testedBinaryPath = PathUtils.testBinaryPath(binaryName, null);
        if (testedBinaryPath != null && !testedBinaryPath.isEmpty())
            return testedBinaryPath;
        
        
        if (OsUtils.isWindows()) {
            try {
                // on Windows try to find MSConvert in a few predefined locations
                List<String> paths = Arrays.asList(
                        "program files (x64)",
                        "program files"
                );
                String folder = "proteowizard";
                String folder2 = "pwiz";
                final String toSearch = "msconvert.exe";

                final Holder<Path> foundPathHolder = new Holder<>();

                FileVisitor<Path> fileVisitor = new FileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (file.getFileName().toString().toLowerCase().equals(toSearch)) {
                            foundPathHolder.obj = file;
                            return FileVisitResult.TERMINATE;
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                };


                Iterable<Path> rootDirs = FileSystems.getDefault().getRootDirectories();
                for (Path rootDir: rootDirs) {
                    try {
                        DirectoryStream<Path> dirStream = Files.newDirectoryStream(rootDir);
                        for (Path file: dirStream) {
                            for (String path : paths) {
                                if (file.getFileName().toString().toLowerCase().startsWith(path)) {
                                    // search for proteowizard
                                    DirectoryStream<Path> dirStream2 = Files.newDirectoryStream(file);
                                    for (Path file2 : dirStream2) {
                                        String toLowerCase = file2.getFileName().toString().toLowerCase();
                                        if (toLowerCase.startsWith(folder) || toLowerCase.startsWith(folder2)) {
                                            // this might be a proteo wizard folder, recursively search it
                                            Files.walkFileTree(file2, fileVisitor);
                                            if (foundPathHolder.obj != null) {
                                                return foundPathHolder.obj.toAbsolutePath().toString();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException ex) {
                        // doesn't matter
                    }
                }
            } catch (Exception e) {
                // we don't care if anything within this block breaks
            }
        }
        return "";
    }
    private class Holder<T> {
        T obj;
    }

    private String getDefaultTextPeptideProphet() {
        String value = ThisAppProps.loadPropFromCache(ThisAppProps.PROP_TEXTFIELD_PATH_PEPTIDE_PROPHET);
        if (value != null)
            return value;
        return getDefaultPhilosopherBinName();
    }

    private String getDefaultTextProteinProphet() {
        String value = ThisAppProps.loadPropFromCache(ThisAppProps.PROP_TEXTFIELD_PATH_PROTEIN_PROPHET);
        if (value != null)
            return value;
        return getDefaultPhilosopherBinName();
    }

    

    


    private void saveProgramsDir() {
        ThisAppProps.saveTextFieldToCache(txtProgramsDir, ThisAppProps.PROP_BINARIES_IN);
    }
    
//    private void saveBinUmpireSe() {
//        saveTextFieldToCache(txtBinUmpire, ThisAppProps.PROP_TEXTFIELD_PATH_UMPIRE_SE);
//    }
//    
//    private void saveBinMsconvert() {
//        saveTextFieldToCache(txtBinMsconvert, ThisAppProps.PROP_TEXTFIELD_PATH_MSCONVERT);
//    }
//    
//    private void saveBinComet() {
//        saveTextFieldToCache(txtBinComet, ThisAppProps.PROP_TEXTFIELD_PATH_COMET);
//    }
    
    private void saveBinPeptideProphet() {
        ThisAppProps.saveTextFieldToCache(txtBinProteinProphet, ThisAppProps.PROP_TEXTFIELD_PATH_PROTEIN_PROPHET);
    }
    
    private void saveBinProteinProphet() {
        ThisAppProps.saveTextFieldToCache(txtBinProteinProphet, ThisAppProps.PROP_TEXTFIELD_PATH_PROTEIN_PROPHET);
    }
    
    
    
    private CometParams loadCometParamsFile(File file) throws ParsingException {
        try (FileInputStream fis = new FileInputStream(file)) {
            CometParams params = CometParams.parse(fis);
            return params;
        } catch (FileNotFoundException ex) {
            throw new ParsingException(ex);
        } catch (IOException ex) {
            throw new ParsingException(ex);
        }
    }

//    private void fillCometParamFields(CometParams cometParams) throws ParsingException {
//        Properties props = cometParams.getProps();
//
//        String databasePath  = props.getProperty(CometParams.PROP_database_name);
//        if (databasePath == null) {
//            //throw new ParsingException("Could not find database path in the parsed properties");
//        } else {
//            txtCometSeqDb.setText(databasePath);
//        }
//
//        fmtpeptide_mass_tolerance.setText(props.getProperty(CometParams.PROP_peptide_mass_tolerance));
//        fmtfragment_bin_tol.setText(props.getProperty(CometParams.PROP_fragment_bin_tol));
//        fmtfragment_bin_offset.setText(props.getProperty(CometParams.PROP_fragment_bin_offset));
//        fmttheoretical_fragment_ions.setText(props.getProperty(CometParams.PROP_theoretical_fragment_ions));
//    }
    
    private static List<Image> loadIcon() {
        // Icon attribution string:
        // <div>Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
        List<Image> images = new ArrayList<>();
        int[] sizes = {16, 24, 32, 64, 128, 256, 512};
        final String path = "icons/";
        final String baseName = "bolt-";
        final String ext = ".png";
        for (int size : sizes) {
            String location = path + baseName + Integer.toString(size) + ext;
            Image icon = Toolkit.getDefaultToolkit().getImage(MsfraggerGuiFrame.class.getResource(location));
            images.add(icon);
        }
        return images;
    }


    private UmpireParams loadUmpireParamsFile(File file) throws ParsingException {
        try (FileInputStream fis = new FileInputStream(file)) {
            UmpireParams params = UmpireParams.parse(fis);
            return params;
        } catch (FileNotFoundException ex) {
            throw new ParsingException(ex);
        } catch (IOException ex) {
            throw new ParsingException(ex);
        }
    }
    
    private UmpireQuantParams loadUmpireQuantParamsFile(File file) throws ParsingException {
        try (FileInputStream fis = new FileInputStream(file)) {
            UmpireQuantParams params = UmpireQuantParams.parse(fis);
            return params;
        } catch (FileNotFoundException ex) {
            throw new ParsingException(ex);
        } catch (IOException ex) {
            throw new ParsingException(ex);
        }
    }

//    private void fillInUmpireParams(UmpireParams params) {
//        fmtRPmax.setText(params.getProps().getProperty(UmpireParams.PROP_RPmax));
//        fmtRFmax.setText(params.getProps().getProperty(UmpireParams.PROP_RFmax));
//        fmtCorrThreshold.setText(params.getProps().getProperty(UmpireParams.PROP_CorrThreshold));
//        fmtDeltaApex.setText(params.getProps().getProperty(UmpireParams.PROP_DeltaApex));
//        fmtRTOverlap.setText(params.getProps().getProperty(UmpireParams.PROP_RTOverlap));
//        
//        boolean getAdjustFragIntensity = Boolean.valueOf(params.getProps().getProperty(UmpireParams.PROP_AdjustFragIntensity));
//        boolean getBoostComplementaryIon = Boolean.valueOf(params.getProps().getProperty(UmpireParams.PROP_BoostComplementaryIon));
//        chkAdjustFragIntensity.setSelected(getAdjustFragIntensity);
//        chkBoostComplementaryIon.setSelected(getBoostComplementaryIon);
//
//        fmtMS1PPM.setText(params.getProps().getProperty(UmpireParams.PROP_MS1PPM));
//        fmtMS2PPM.setText(params.getProps().getProperty(UmpireParams.PROP_MS2PPM));
//        fmtSN.setText(params.getProps().getProperty(UmpireParams.PROP_SN));
//        fmtMS2SN.setText(params.getProps().getProperty(UmpireParams.PROP_MS2SN));
//        fmtMinMSIntensity.setText(params.getProps().getProperty(UmpireParams.PROP_MinMSIntensity));
//        fmtMinMSMSIntensity.setText(params.getProps().getProperty(UmpireParams.PROP_MinMSMSIntensity));
//        fmtMaxCurveRTRange.setText(params.getProps().getProperty(UmpireParams.PROP_MaxCurveRTRange));
//        fmtNoMissedScan.setText(params.getProps().getProperty(UmpireParams.PROP_NoMissedScan));
//        fmtMinFrag.setText(params.getProps().getProperty(UmpireParams.PROP_MinFrag));
//        chkEstimateBG.setSelected(Boolean.valueOf(params.getProps().getProperty(UmpireParams.PROP_EstimateBG)));
//        fmtMinNoPeakCluster.setText(params.getProps().getProperty(UmpireParams.PROP_MinNoPeakCluster));
//        fmtMaxNoPeakCluster.setText(params.getProps().getProperty(UmpireParams.PROP_MaxNoPeakCluster));
//
//
//        fmtWindowSize.setText(params.getProps().getProperty(UmpireParams.PROP_WindowSize));
//        //.setText(params.getProperty(UmpireParams.PROP_));
//
//
//    }
    
//    private void fillInUmpireQuantParams(UmpireQuantParams params) {
//        Properties p = params.getProps();
//        txtDecoyPrefix.setText(p.getProperty(UmpireQuantParams.PROP_DecoyPrefix));
//        Boolean doInternalSearch = Boolean.valueOf(p.getProperty(UmpireQuantParams.PROP_InternalLibSearch));
//        chkInternalLibSearch.setSelected(doInternalSearch);
//        fmtPeptideFDR.setText(p.getProperty(UmpireQuantParams.PROP_PeptideFDR));
//        fmtProteinFDR.setText(p.getProperty(UmpireQuantParams.PROP_ProteinFDR));
//        Boolean datasetFdr = Boolean.valueOf(p.getProperty(UmpireQuantParams.PROP_DataSetLevelPepFDR));
//        chkDataSetLevelPepFDR.setSelected(datasetFdr);
//        String filterWeightName = p.getProperty(UmpireQuantParams.PROP_FilterWeight);
//        comboFilterWeight.getModel().setSelectedItem(filterWeightName);
//        fmtMinWeight.setText(p.getProperty(UmpireQuantParams.PROP_MinWeight));
//        fmtTopNFrag.setText(p.getProperty(UmpireQuantParams.PROP_TopNFrag));
//        fmtTopNPep.setText(p.getProperty(UmpireQuantParams.PROP_TopNPep));
//        fmtFreq.setText(p.getProperty(UmpireQuantParams.PROP_Freq));
//    }

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
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
            java.util.logging.Logger.getLogger(MsfraggerGuiFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, e1);
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException e2) {
                java.util.logging.Logger.getLogger(MsfraggerGuiFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, e2);
            }
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MsfraggerGuiFrame frame = new MsfraggerGuiFrame();
                frame.setVisible(true);
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbout;
    private javax.swing.JButton btnBinProteinProphet;
    private javax.swing.JButton btnBrowseProgramsDir;
    private javax.swing.JButton btnCheckJavaVersion;
    private javax.swing.JButton btnClearConsole;
    private javax.swing.JButton btnProteinProphetSeqDb;
    private javax.swing.JButton btnRawAddFiles;
    private javax.swing.JButton btnRawAddFolder;
    private javax.swing.JButton btnRawClear;
    private javax.swing.JButton btnRawRemove;
    private javax.swing.JButton btnRun;
    private javax.swing.JButton btnSelectPeptideProphetBin;
    private javax.swing.JButton btnSelectPeptideProphetSeqDbPath;
    private javax.swing.JButton btnSelectWrkingDir;
    private javax.swing.JButton btnStop;
    private javax.swing.JCheckBox checkDryRun;
    private javax.swing.JCheckBox chkProteinProphetAddInteractPepXmlsSeparately;
    private javax.swing.JCheckBox chkRunPeptideProphet;
    private javax.swing.JCheckBox chkRunProteinProphet;
    private umich.msfragger.gui.TextConsole console;
    private javax.swing.JScrollPane consoleScrollPane;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblOutputDir;
    private javax.swing.JLabel lblProgramsDir;
    private javax.swing.JPanel panelInTabSelectFiles;
    private javax.swing.JPanel panelMsFragger;
    private javax.swing.JPanel panelPeptideProphet;
    private javax.swing.JPanel panelPeptideProphetBin;
    private javax.swing.JPanel panelPeptideProphetOptions;
    private javax.swing.JPanel panelProteinProphet;
    private javax.swing.JPanel panelProteinProphetBin;
    private javax.swing.JPanel panelProteinProphetOptions;
    private javax.swing.JPanel panelRun;
    private javax.swing.JPanel panelSelectedFiles;
    private javax.swing.JScrollPane scrollPaneMsFragger;
    private javax.swing.JScrollPane scrollPaneRawFiles;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JTextField txtBinPeptideProphet;
    private javax.swing.JTextField txtBinProteinProphet;
    private javax.swing.JTextArea txtPeptideProphetCmdLineOptions;
    private javax.swing.JTextField txtPeptideProphetSeqDb;
    private javax.swing.JTextField txtProgramsDir;
    private javax.swing.JTextArea txtProteinProphetCmdLineOpts;
    private javax.swing.JTextField txtProteinProphetOutputFile;
    private javax.swing.JTextField txtProteinProphetSeqDb;
    private javax.swing.JTextField txtWorkingDir;
    // End of variables declaration//GEN-END:variables

}
