/* 
 *******************************************************************************
 * CyberTOMP® is a cybersecurity framework that helps organizations manage and 
 * assess security at a tactical and operational level by focusing on business 
 * assets. It provides structured processes, metrics, and roles to align people,
 * technology, and supply chains, enabling informed decision-making based on 
 * asset criticality. Compatible with standards like ISO 27001 and NIST, it also
 * incorporates practical tools and optimization techniques to deliver a 
 * cohesive, measurable, and efficient approach to cybersecurity.
 * 
 * Within CyberTOMP®, FLECO (Fast, Lightweight, and Efficient Cybersecurity 
 * Optimization) is an adaptive, constrained genetic algorithm designed to 
 * support asset cybersecurity teams in decision-making throughout the 
 * application of the CyberTOMP® framework. The cybertomp-fleco-studio tool
 * provides a visual way of usion/applying CyberTOMP® FLECO algorithm through a 
 * standalone Swing application.
 *
 * Visit https://cybertomp.org to learn more about the CyberTOMP® framework, 
 * the collaborative project behind it, its components, and its research 
 * foundations, all of which continuously evolve based on empirical evidence and
 * solid, verifiable principles.
 *
 *******************************************************************************
 * Copyright (C) Manuel Dominguez Dorado - ingeniero@ManoloDominguez.com.
 * 
 * This program is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see 
 * https://www.gnu.org/licenses/lgpl-3.0.en.html.
 *******************************************************************************
 */
package com.manolodominguez.fleco.gui;

import com.manolodominguez.fleco.algorithm.FLECO;
import com.manolodominguez.fleco.genetics.Chromosome;
import com.manolodominguez.fleco.genetics.Genes;
import com.manolodominguez.fleco.gui.flecoio.FLECOFilter;
import com.manolodominguez.fleco.gui.flecoio.FLECOLoader;
import com.manolodominguez.fleco.gui.flecoio.FLECOSaver;
import com.manolodominguez.fleco.strategicconstraints.ComparisonOperators;
import com.manolodominguez.fleco.strategicconstraints.Constraint;
import com.manolodominguez.fleco.strategicconstraints.StrategicConstraints;
import com.manolodominguez.fleco.uleo.Categories;
import com.manolodominguez.fleco.uleo.Functions;
import com.manolodominguez.fleco.uleo.ImplementationGroups;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumnModel;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MainWindow is the primary Swing frame for CyberTOMP® FLECO Studio. It wires
 * UI controls, handles user actions (new, load, save, run FLECO, etc.) and
 * coordinates the CaseConfig and FLECO execution lifecycle.
 *
 * <p>
 * This class is intentionally conservative in refactoring: it preserves the
 * original behavior while improving readability, reducing duplication and
 * adding defensive checks and compact logging where appropriate.</p>
 *
 * <p>
 * Compatibility: Java 11, no additional dependencies.</p>
 *
 * @author Manuel Domínguez-Dorado
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame implements IFLECOGUI, IFLECOTableModelChangeListener {

    private static final long serialVersionUID = 1L;

    /**
     * Application menu bar.
     */
    private JMenuBar menuBar;

    /**
     * Case menu and its items.
     */
    private JMenu menuCase;
    private JMenuItem menuCaseItemNewRandom;
    private JMenuItem menuCaseItemNew;
    private JMenuItem menuCaseItemLoad;
    private JMenuItem menuCaseItemSave;
    private JMenuItem menuCaseItemSaveAs;
    private JMenuItem menuCaseItemRunFLECO;
    private JMenuItem menuCaseItemExit;

    /**
     * About menu and its items.
     */
    private JMenu menuAbout;
    private JMenuItem menuAboutItemAbout;
    private JMenuItem menuAboutItemLicense;

    /**
     * Toolbar and controls.
     */
    private JToolBar toolBar;
    private JLabel messageSpace;
    private FLECOProgressBar progressBar;
    private FLECOTableModel tableModel;
    private JTable table;
    private TableColumnAdjuster tableColumnAdjuster;
    private JComboBox<Float> comboBoxColumn1;
    private JComboBox<String> comboBoxColumn2;
    private JScrollPane scrollPane;

    /**
     * Buttons referenced from toolbar and menu actions.
     */
    private IFLECOGUI gui;
    private JButton runButton;
    private JButton randomButton;
    private JButton newButton;
    private JButton loadButton;
    private JButton saveButton;
    private JButton saveAsButton;
    private JButton generateConstraintsButton;

    /**
     * Pop-up window for metric details.
     */
    private MetricDetailsWindow popUp;

    /**
     * Implementation group options for dialogs.
     */
    private final Object[] igOptions = {ImplementationGroups.IG1, ImplementationGroups.IG2, ImplementationGroups.IG3};

    /**
     * Image provider for icons.
     */
    private final ImageBroker imageBroker = new ImageBroker();

    /**
     * Current case configuration and runtime state.
     */
    private CaseConfig caseConfig;

    /**
     * Logger for the class.
     */
    private final Logger logger = LoggerFactory.getLogger(MainWindow.class);

    /**
     * Constructs the main application window and initializes UI components.
     *
     * @throws HeadlessException if the environment does not support a display.
     */
    public MainWindow() throws HeadlessException {
        super();
        this.caseConfig = new CaseConfig();
        this.gui = this;
        this.popUp = null;

        getContentPane().setLayout(new MigLayout("fillx, filly"));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(screenSize.width * 1 / 8, screenSize.height * 1 / 8, screenSize.width * 3 / 4, screenSize.height * 3 / 4);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        initMenuBar();
        initToolBar();
        initMainTableAndProgress();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                onExit();
            }
        });
    }

    /**
     * Initialize and wire the menu bar and its items.
     */
    private void initMenuBar() {
        // First, a window icon is added.
        setIconImage(ImageBroker.getInstance().getImage32x32(AvailableImages.WINDOW_ICON));

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        if (caseConfig.getFileName() != null) {
            setTitle("CyberTOMP® FLECO Studio - " + caseConfig.getFileName());
        } else {
            setTitle("CyberTOMP® FLECO Studio - No case is active!");
        }

        menuCase = new JMenu("Case");
        menuCase.setMnemonic('C');
        menuBar.add(menuCase);

        menuCaseItemNew = new JMenuItem("New");
        menuCaseItemNew.setMnemonic('N');
        menuCaseItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        menuCaseItemNew.setIcon(imageBroker.getImageIcon16x16(AvailableImages.NEW));
        menuCaseItemNew.addActionListener(e -> onNew());

        menuCaseItemNewRandom = new JMenuItem("New random");
        menuCaseItemNewRandom.setMnemonic('r');
        menuCaseItemNewRandom.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        menuCaseItemNewRandom.setIcon(imageBroker.getImageIcon16x16(AvailableImages.RANDOM));
        menuCaseItemNewRandom.addActionListener(e -> onRandom());

        menuCaseItemLoad = new JMenuItem("Load");
        menuCaseItemLoad.setMnemonic('L');
        menuCaseItemLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        menuCaseItemLoad.setIcon(imageBroker.getImageIcon16x16(AvailableImages.LOAD));
        menuCaseItemLoad.addActionListener(e -> onLoad());

        menuCaseItemSave = new JMenuItem("Save");
        menuCaseItemSave.setMnemonic('S');
        menuCaseItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        menuCaseItemSave.setIcon(imageBroker.getImageIcon16x16(AvailableImages.SAVE));
        menuCaseItemSave.setEnabled(false);
        menuCaseItemSave.addActionListener(e -> onSave());

        menuCaseItemSaveAs = new JMenuItem("Save as");
        menuCaseItemSaveAs.setMnemonic('a');
        menuCaseItemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        menuCaseItemSaveAs.setIcon(imageBroker.getImageIcon16x16(AvailableImages.SAVE_AS));
        menuCaseItemSaveAs.setEnabled(false);
        menuCaseItemSaveAs.addActionListener(e -> onSaveAs());

        menuCaseItemRunFLECO = new JMenuItem("Run FLECO algorithm");
        menuCaseItemRunFLECO.setMnemonic('R');
        menuCaseItemRunFLECO.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        menuCaseItemRunFLECO.setIcon(imageBroker.getImageIcon16x16(AvailableImages.RUN));
        menuCaseItemRunFLECO.setEnabled(false);
        menuCaseItemRunFLECO.addActionListener(e -> onRunFLECO());

        menuCaseItemExit = new JMenuItem("Exit");
        menuCaseItemExit.setMnemonic('E');
        menuCaseItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        menuCaseItemExit.setIcon(imageBroker.getImageIcon16x16(AvailableImages.EXIT));
        menuCaseItemExit.addActionListener(e -> onExit());

        menuCase.add(menuCaseItemNewRandom);
        menuCase.add(menuCaseItemNew);
        menuCase.add(menuCaseItemLoad);
        menuCase.add(menuCaseItemSave);
        menuCase.add(menuCaseItemSaveAs);
        menuCase.add(menuCaseItemRunFLECO);
        menuCase.addSeparator();
        menuCase.add(menuCaseItemExit);

        menuAbout = new JMenu("About");
        menuAbout.setMnemonic('A');
        menuBar.add(menuAbout);

        menuAboutItemAbout = new JMenuItem("About CyberTOMP® FLECO Studio");
        menuAboutItemAbout.setMnemonic('b');
        menuAboutItemAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        menuAboutItemAbout.setIcon(imageBroker.getImageIcon16x16(AvailableImages.ABOUT));
        menuAboutItemAbout.addActionListener(e -> onAbout());

        menuAboutItemLicense = new JMenuItem("CyberTOMP® FLECO Studio license");
        menuAboutItemLicense.setMnemonic('F');
        menuAboutItemLicense.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
        menuAboutItemLicense.setIcon(imageBroker.getImageIcon16x16(AvailableImages.LICENSE));
        menuAboutItemLicense.addActionListener(e -> onFLECOLicense());

        menuAbout.add(menuAboutItemAbout);
        menuAbout.add(menuAboutItemLicense);
    }

    /**
     * Initialize toolbar, buttons and their listeners.
     */
    private void initToolBar() {
        toolBar = new JToolBar();
        toolBar.setLayout(new MigLayout("gapx 5px"));

        runButton = new JButton();
        runButton.setIcon(imageBroker.getImageIcon32x32(AvailableImages.RUN));
        runButton.setFocusable(false);
        runButton.setEnabled(false);
        runButton.setToolTipText("Run FLECO to find a solution to discuss");
        runButton.addActionListener(e -> onRunFLECO());

        randomButton = new JButton();
        randomButton.setIcon(imageBroker.getImageIcon32x32(AvailableImages.RANDOM));
        randomButton.setFocusable(false);
        randomButton.setToolTipText("Generate a random current state to test and learn");
        randomButton.addActionListener(e -> onRandom());

        newButton = new JButton();
        newButton.setIcon(imageBroker.getImageIcon32x32(AvailableImages.NEW));
        newButton.setFocusable(false);
        newButton.setToolTipText("Create a new case");
        newButton.addActionListener(e -> onNew());

        loadButton = new JButton();
        loadButton.setIcon(imageBroker.getImageIcon32x32(AvailableImages.LOAD));
        loadButton.setFocusable(false);
        loadButton.setToolTipText("Load a case previously saved");
        loadButton.addActionListener(e -> onLoad());

        saveButton = new JButton();
        saveButton.setIcon(imageBroker.getImageIcon32x32(AvailableImages.SAVE));
        saveButton.setFocusable(false);
        saveButton.setEnabled(false);
        saveButton.setToolTipText("Save the latest changes to the already saved case");
        saveButton.addActionListener(e -> onSave());

        saveAsButton = new JButton();
        saveAsButton.setIcon(imageBroker.getImageIcon32x32(AvailableImages.SAVE_AS));
        saveAsButton.setFocusable(false);
        saveAsButton.setEnabled(false);
        saveAsButton.setToolTipText("Save the case choosing a name and location");
        saveAsButton.addActionListener(e -> onSaveAs());

        generateConstraintsButton = new JButton();
        generateConstraintsButton.setIcon(imageBroker.getImageIcon32x32(AvailableImages.RULES));
        generateConstraintsButton.setFocusable(false);
        generateConstraintsButton.setEnabled(false);
        generateConstraintsButton.setToolTipText("Set current cybersecurity status as minimum to compute target status");
        generateConstraintsButton.addActionListener(e -> onConstraints());

        messageSpace = new JLabel();
        messageSpace.setBackground(Color.WHITE);
        messageSpace.setOpaque(true);
        messageSpace.setHorizontalAlignment(SwingConstants.CENTER);
        messageSpace.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        messageSpace.setText("Load an existing case or create a new one");

        toolBar.add(randomButton);
        toolBar.add(newButton);
        toolBar.add(loadButton);
        toolBar.add(saveButton);
        toolBar.add(saveAsButton);
        toolBar.add(generateConstraintsButton);
        toolBar.add(new JSeparator(SwingConstants.VERTICAL));
        toolBar.add(runButton);
        toolBar.add(new JSeparator(SwingConstants.VERTICAL));
        toolBar.add(messageSpace, "width 100%");
        toolBar.setFloatable(false);
        toolBar.setOrientation(JToolBar.HORIZONTAL);
        getContentPane().add(toolBar, "span, north, width 100%, wrap");
    }

    /**
     * Initialize main table, scroll pane and progress bar.
     */
    private void initMainTableAndProgress() {
        table = new JTable();
        table.setEnabled(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableColumnAdjuster = new TableColumnAdjuster(table);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                JTable src = (JTable) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2 && src.getSelectedRow() != -1) {
                    int col = src.getSelectedColumn();
                    if (col == 0 || col == 1 || col == 2) {
                        onDoubleClicOnTable(src);
                    }
                }
            }
        });

        scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, "span, width 100%, height 100%, wrap");

        progressBar = new FLECOProgressBar();
        getContentPane().add(progressBar, "span, width 100%, height 20, wrap");
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
    }

    /**
     * Handles a double click on the main table. Tries to interpret the first
     * column value as Genes, Categories or Functions (in that order) and opens
     * the corresponding MetricDetailsWindow. If none match, opens a generic
     * MetricDetailsWindow.
     *
     * <p>
     * This method preserves the original fallback behavior but reduces
     * duplication and improves readability.</p>
     *
     * @param table the table where the double click occurred; must not be null.
     */
    private void onDoubleClicOnTable(final JTable table) {
        if (table == null) {
            logger.warn("onDoubleClicOnTable called with null table reference");
            return;
        }
        final int row = table.getSelectedRow();
        if (row < 0) {
            logger.debug("onDoubleClicOnTable: no row selected");
            return;
        }

        final Object value = tableModel.getValueAt(row, 0);
        if (!(value instanceof String)) {
            safeDisposePopup();
            popUp = new MetricDetailsWindow(this);
            popUp.setVisible(true);
            return;
        }
        final String key = (String) value;

        // Try enums in order: Genes, Categories, Functions
        MetricDetailsWindow created = tryCreatePopupFromEnum(Genes.class, key);
        if (created == null) {
            created = tryCreatePopupFromEnum(Categories.class, key);
        }
        if (created == null) {
            created = tryCreatePopupFromEnum(Functions.class, key);
        }
        if (created == null) {
            safeDisposePopup();
            popUp = new MetricDetailsWindow(this);
            popUp.setVisible(true);
            return;
        }
        safeDisposePopup();
        popUp = created;
        popUp.setVisible(true);
    }

    /**
     * Attempts to create a MetricDetailsWindow using an enum type and a string
     * value. If the enum's valueOf fails, returns null.
     *
     * @param <E> the enum type
     * @param enumClass the enum class to try
     * @param value the string value to convert
     * @return a MetricDetailsWindow constructed with the enum constant, or null
     * if conversion failed
     */
    private <E extends Enum<E>> MetricDetailsWindow tryCreatePopupFromEnum(final Class<E> enumClass, final String value) {
        try {
            final E constant = Enum.valueOf(enumClass, value);
            // MetricDetailsWindow has overloaded constructors for Genes, Categories, Functions
            if (constant instanceof Genes) {
                return new MetricDetailsWindow(this, (Genes) constant);
            }
            if (constant instanceof Categories) {
                return new MetricDetailsWindow(this, (Categories) constant);
            }
            if (constant instanceof Functions) {
                return new MetricDetailsWindow(this, (Functions) constant);
            }
            return null;
        } catch (IllegalArgumentException ex) {
            // Not the expected enum constant; this is normal fallback behavior.
            logger.debug("Value '{}' is not a member of {}.", value, enumClass.getSimpleName());
            return null;
        }
    }

    /**
     * Safely disposes the currently shown popup if any.
     */
    private void safeDisposePopup() {
        if (popUp != null) {
            try {
                popUp.dispose();
            } catch (Exception ex) {
                logger.debug("Exception while disposing popup: {}", ex.getMessage());
            } finally {
                popUp = null;
            }
        }
    }

    /**
     * Loads a previously saved FLECO case from disk. Preserves original user
     * prompts and behavior while improving readability and adding defensive
     * checks.
     */
    private void onLoad() {
        boolean load = false;
        if (caseConfig.isInitialized()) {
            if (caseConfig.isAlreadySaved()) {
                if (caseConfig.isModified()) {
                    int option = JOptionPane.showInternalConfirmDialog(this.getContentPane(),
                            "Save changes before loading a new case?", null,
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                            imageBroker.getImageIcon32x32(AvailableImages.QUESTION));
                    if (option == JOptionPane.OK_OPTION) {
                        if (onSave()) {
                            load = true;
                        }
                    } else if (option == JOptionPane.NO_OPTION) {
                        load = true;
                    }
                } else {
                    load = true;
                }
            } else {
                int option = JOptionPane.showInternalConfirmDialog(this.getContentPane(),
                        "Save the case before loading a new one?", null,
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                        imageBroker.getImageIcon32x32(AvailableImages.QUESTION));
                if (option == JOptionPane.OK_OPTION) {
                    if (onSaveAs()) {
                        load = true;
                    }
                } else if (option == JOptionPane.NO_OPTION) {
                    load = true;
                }
            }
        } else {
            load = true;
        }

        if (!load) {
            return;
        }

        boolean openProcessFinished = false;
        while (!openProcessFinished) {
            final JFileChooser loadDialog = new JFileChooser();
            loadDialog.setFileFilter(new FLECOFilter());
            loadDialog.setDialogType(JFileChooser.CUSTOM_DIALOG);
            loadDialog.setApproveButtonMnemonic('O');
            loadDialog.setApproveButtonText("Ok");
            loadDialog.setDialogTitle("Load FLECO case");
            loadDialog.setAcceptAllFileFilterUsed(false);
            loadDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);

            final int fileChoosingResult = loadDialog.showOpenDialog(this);
            if (fileChoosingResult != JFileChooser.APPROVE_OPTION) {
                openProcessFinished = true;
                continue;
            }

            final File selected = loadDialog.getSelectedFile();
            if (selected == null) {
                JOptionPane.showInternalMessageDialog(this.getContentPane(),
                        "No file selected.\nTry again.", null, JOptionPane.INFORMATION_MESSAGE,
                        imageBroker.getImageIcon32x32(AvailableImages.ABOUT));
                continue;
            }
            if (!selected.exists()) {
                JOptionPane.showInternalMessageDialog(this.getContentPane(),
                        "The specified file does not exist.\nTry again.", null, JOptionPane.INFORMATION_MESSAGE,
                        imageBroker.getImageIcon32x32(AvailableImages.ABOUT));
                continue;
            }
            if (!selected.canRead()) {
                JOptionPane.showInternalMessageDialog(this.getContentPane(),
                        "The specified file cannot be read.\nTry again.", null, JOptionPane.INFORMATION_MESSAGE,
                        imageBroker.getImageIcon32x32(AvailableImages.ABOUT));
                continue;
            }

            final FLECOLoader flecoLoader = new FLECOLoader();
            final boolean isLoaded = flecoLoader.load(selected);
            if (!isLoaded) {
                JOptionPane.showInternalMessageDialog(this.getContentPane(),
                        "This file is not a FLECO case.\nTry again.", null, JOptionPane.INFORMATION_MESSAGE,
                        imageBroker.getImageIcon32x32(AvailableImages.ABOUT));
                continue;
            }

            // Apply loaded data to caseConfig
            caseConfig.reset();
            caseConfig.setCurrentIG(flecoLoader.getInitialStatus().getImplementationGroup());
            if (caseConfig.getCurrentIG() != null) {
                caseConfig.setInitialStatus(flecoLoader.getInitialStatus());
                caseConfig.setStrategicConstraints(flecoLoader.getStrategicConstraints());
                caseConfig.setTargetStatus(flecoLoader.getTargetStatus());
                tableModel = new FLECOTableModel(caseConfig.getInitialStatus(), caseConfig.getStrategicConstraints());
                tableModel.setTargetStatus(caseConfig.getTargetStatus());
                configureMainTable(tableModel);
                caseConfig.setAlreadySaved(true);
                caseConfig.setModified(false);
                caseConfig.setPathAndFileName(selected.getAbsolutePath());

                // AFTER: enable/disable controls consistently
                enableControlsAfterCaseLoad();

                progressBar.setValue(0);
                messageSpace.setText("Set the values of current status, constraint operator, and contraint value and run FLECO");
                if (caseConfig.getFileName() != null) {
                    setTitle("CyberTOMP® FLECO Studio - " + caseConfig.getFileName());
                } else {
                    setTitle("CyberTOMP® FLECO Studio - Current case is not saved!");
                }
                caseConfig.setInitialized(true);
            }
            openProcessFinished = true;
        }
    }

    /**
     * Helper to enable/disable UI controls after a case is
     * loaded/created/saved. This consolidates repeated UI state updates into a
     * single place without changing the original behavior.
     */
    private void enableControlsAfterCaseLoad() {
        randomButton.setEnabled(true);
        newButton.setEnabled(true);
        loadButton.setEnabled(true);
        saveButton.setEnabled(caseConfig.isAlreadySaved() && caseConfig.isModified());
        saveAsButton.setEnabled(true);
        runButton.setEnabled(true);
        generateConstraintsButton.setEnabled(true);
        menuCaseItemNew.setEnabled(true);
        menuCaseItemLoad.setEnabled(true);
        menuCaseItemSave.setEnabled(caseConfig.isAlreadySaved() && caseConfig.isModified());
        menuCaseItemSaveAs.setEnabled(true);
        menuCaseItemRunFLECO.setEnabled(true);
        menuCaseItemExit.setEnabled(true);
        menuAbout.setEnabled(true);
        menuAboutItemAbout.setEnabled(true);
        menuAboutItemLicense.setEnabled(true);
        table.setEnabled(true);
        menuBar.setEnabled(true);
        menuCase.setEnabled(true);
        menuAbout.setEnabled(true);
    }

    /**
     * Saves the current case to the path stored in caseConfig. Returns true if
     * the save operation succeeded.
     *
     * @return true if saved successfully, false otherwise.
     */
    private boolean onSave() {
        if (!caseConfig.isInitialized()) {
            logger.debug("onSave called but caseConfig is not initialized");
            return false;
        }

        final FLECOSaver flecoSaver = new FLECOSaver(caseConfig.getInitialStatus(), caseConfig.getStrategicConstraints(), caseConfig.getTargetStatus());
        final boolean savedCorrectly = flecoSaver.save(new File(caseConfig.getPathAndFileName()));
        if (savedCorrectly) {
            caseConfig.setAlreadySaved(true);
            caseConfig.setInitialized(true);
            caseConfig.setModified(false);
        } else {
            JOptionPane.showInternalMessageDialog(this.getContentPane(),
                    "There were errors when saving the case to disk.\nTry again choosing Save As to another place.",
                    null, JOptionPane.INFORMATION_MESSAGE, imageBroker.getImageIcon32x32(AvailableImages.ABOUT));
        }

        // AFTER: keep UI consistent with state
        enableControlsAfterCaseLoad();
        progressBar.setValue(0);
        messageSpace.setText("Set the values of current status, constraint operator, and contraint value and run FLECO");
        if (caseConfig.getFileName() != null) {
            setTitle("CyberTOMP® FLECO Studio - " + caseConfig.getFileName());
        } else {
            setTitle("CyberTOMP® FLECO Studio - unnamed.fleco");
        }
        return savedCorrectly;
    }

    /**
     * Saves the current case prompting the user for a file location. Returns
     * true if the save operation succeeded.
     *
     * @return true if saved successfully, false otherwise.
     */
    private boolean onSaveAs() {
        if (!caseConfig.isInitialized()) {
            logger.debug("onSaveAs called but caseConfig is not initialized");
            return false;
        }

        final JFileChooser saveAsDialog = new JFileChooser();
        saveAsDialog.setFileFilter(new FLECOFilter());
        saveAsDialog.setDialogType(JFileChooser.CUSTOM_DIALOG);
        saveAsDialog.setApproveButtonMnemonic('O');
        saveAsDialog.setApproveButtonText("Ok");
        saveAsDialog.setDialogTitle("Save current FLECO case");
        saveAsDialog.setAcceptAllFileFilterUsed(false);
        if (caseConfig.getFileName() != null) {
            saveAsDialog.setSelectedFile(new File(caseConfig.getPathAndFileName()));
        }
        saveAsDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);

        final int result = saveAsDialog.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        String path = saveAsDialog.getSelectedFile().getPath();
        if (path == null) {
            logger.warn("onSaveAs: selected file path is null");
            return false;
        }
        caseConfig.setPathAndFileName(path);
        int i = caseConfig.getPathAndFileName().lastIndexOf('.');
        String extension = null;
        if (i > 0 && i < caseConfig.getPathAndFileName().length() - 1) {
            extension = caseConfig.getPathAndFileName().substring(i + 1).toLowerCase();
        }
        if (extension == null || !extension.equals("fleco")) {
            caseConfig.setPathAndFileName(caseConfig.getPathAndFileName() + ".fleco");
        }

        saveAsDialog.setSelectedFile(new File(caseConfig.getPathAndFileName()));
        final FLECOSaver flecoSaver = new FLECOSaver(caseConfig.getInitialStatus(), caseConfig.getStrategicConstraints(), caseConfig.getTargetStatus());
        final boolean savedCorrectly = flecoSaver.save(new File(caseConfig.getPathAndFileName()));
        if (savedCorrectly) {
            caseConfig.setAlreadySaved(true);
            caseConfig.setInitialized(true);
            caseConfig.setModified(false);
        } else {
            JOptionPane.showInternalMessageDialog(this.getContentPane(),
                    "There were errors when saving the case to disk.\nTry again choosing another place.",
                    null, JOptionPane.INFORMATION_MESSAGE, imageBroker.getImageIcon32x32(AvailableImages.ABOUT));
        }

        // AFTER: keep UI consistent with state
        enableControlsAfterCaseLoad();
        progressBar.setValue(0);
        messageSpace.setText("Set the values of current status, constraint operator, and contraint value and run FLECO");
        if (caseConfig.getFileName() != null) {
            setTitle("CyberTOMP® FLECO Studio - " + caseConfig.getFileName());
        } else {
            setTitle("CyberTOMP® FLECO Studio - unnamed.fleco");
        }
        return savedCorrectly;
    }

    /**
     * Handles the Exit action. Prompts the user to save if necessary and
     * disposes the frame when appropriate.
     */
    private void onExit() {
        safeDisposePopup();
        if (!caseConfig.isInitialized()) {
            dispose();
            return;
        }

        if (caseConfig.isAlreadySaved()) {
            if (caseConfig.isModified()) {
                int option = JOptionPane.showInternalConfirmDialog(this.getContentPane(),
                        "Save changes before exit?", null, JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, imageBroker.getImageIcon32x32(AvailableImages.QUESTION));
                if (option == JOptionPane.OK_OPTION) {
                    onSave();
                    if (!caseConfig.isModified()) {
                        dispose();
                    }
                } else if (option == JOptionPane.NO_OPTION) {
                    dispose();
                }
            } else {
                dispose();
            }
        } else {
            int option = JOptionPane.showInternalConfirmDialog(this.getContentPane(),
                    "Save the current case before exit?", null, JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, imageBroker.getImageIcon32x32(AvailableImages.QUESTION));
            if (option == JOptionPane.OK_OPTION) {
                onSaveAs();
                if (caseConfig.isAlreadySaved() && !caseConfig.isModified()) {
                    dispose();
                }
            } else if (option == JOptionPane.NO_OPTION) {
                dispose();
            }
        }
    }

    /**
     * Opens the project's GitHub page in the default browser. Logs failures
     * compactly and shows a user-friendly message.
     */
    private void onAbout() {
        final String urlString = "https://github.com/cybertomp-framework/cybertomp-fleco-studio";
        if (!Desktop.isDesktopSupported()) {
            logger.warn("Desktop API not supported; cannot open URL {}", urlString);
            JOptionPane.showInternalMessageDialog(this.getContentPane(),
                    "It was not possible to access the home page of\nFLECO project.", null,
                    JOptionPane.INFORMATION_MESSAGE, imageBroker.getImageIcon32x32(AvailableImages.ABOUT));
            return;
        }
        try {
            final URL url = new URL(urlString);
            Desktop.getDesktop().browse(url.toURI());
        } catch (IOException | URISyntaxException ex) {
            logger.warn("Failed to open About URL {}: {}", urlString, ex.getMessage());
            JOptionPane.showInternalMessageDialog(this.getContentPane(),
                    "It was not possible to access the home page of\nFLECO project.", null,
                    JOptionPane.INFORMATION_MESSAGE, imageBroker.getImageIcon32x32(AvailableImages.ABOUT));
        }
    }

    /**
     * Opens the LGPL license page in the default browser. Logs failures and
     * shows a user-friendly message.
     */
    private void onFLECOLicense() {
        final String urlString = "https://www.gnu.org/licenses/lgpl-3.0.html";
        if (!Desktop.isDesktopSupported()) {
            logger.warn("Desktop API not supported; cannot open URL {}", urlString);
            JOptionPane.showInternalMessageDialog(this.getContentPane(),
                    "It was not possible to access the home page of\nLGPL-3.0-or-later license.", null,
                    JOptionPane.INFORMATION_MESSAGE, imageBroker.getImageIcon32x32(AvailableImages.ABOUT));
            return;
        }
        try {
            final URL url = new URL(urlString);
            Desktop.getDesktop().browse(url.toURI());
        } catch (IOException | URISyntaxException ex) {
            logger.warn("Failed to open license URL {}: {}", urlString, ex.getMessage());
            JOptionPane.showInternalMessageDialog(this.getContentPane(),
                    "It was not possible to access the home page of\nLGPL-3.0-or-later license.", null,
                    JOptionPane.INFORMATION_MESSAGE, imageBroker.getImageIcon32x32(AvailableImages.ABOUT));
        }
    }

    /**
     * Links the provided FLECOTableModel to the main table and configures cell
     * editors and renderers. This method preserves the original column setup
     * and removes the CyberTOMP Metric Key column from the view while keeping
     * it in the model.
     *
     * @param tableModel the model to associate the GUI's table to; must not be
     * null.
     */
    private void configureMainTable(final FLECOTableModel tableModel) {
        if (tableModel == null) {
            logger.warn("configureMainTable called with null tableModel");
            throw new NullPointerException("tableModel must not be null");
        }
        this.tableModel = tableModel;
        table.setModel(tableModel);
        tableModel.setChangeEventListener(this);

        comboBoxColumn1 = new JComboBox<>();
        comboBoxColumn1.addItem(0.0f);
        comboBoxColumn1.addItem(0.33f);
        comboBoxColumn1.addItem(0.67f);
        comboBoxColumn1.addItem(1.00f);
        table.getColumnModel().getColumn(FLECOTableModel.CURRENT_STATUS).setCellEditor(new DefaultCellEditor(comboBoxColumn1));

        comboBoxColumn2 = new JComboBox<>();
        comboBoxColumn2.addItem("N/A");
        comboBoxColumn2.addItem("LESS");
        comboBoxColumn2.addItem("LESS_OR_EQUAL");
        comboBoxColumn2.addItem("EQUAL");
        comboBoxColumn2.addItem("GREATER_OR_EQUAL");
        comboBoxColumn2.addItem("GREATER");
        table.getColumnModel().getColumn(FLECOTableModel.CONSTRAINT_OPERATOR).setCellEditor(new DefaultCellEditor(comboBoxColumn2));

        table.getColumnModel().getColumn(FLECOTableModel.TARGET_STATUS).setCellRenderer(new TargetStatusCellRenderer());
        tableColumnAdjuster.adjustColumns();

        // Remove the "CyberTOMP Metric Key" column from the view, but keep it in the model.
        final TableColumnModel tableColumnModel = table.getColumnModel();
        try {
            tableColumnModel.removeColumn(tableColumnModel.getColumn(FLECOTableModel.CYBERTOMP_METRIC_KEY));
        } catch (Exception ex) {
            // Defensive: if column indices changed, log and continue without failing.
            logger.debug("Could not remove CYBERTOMP_METRIC_KEY column from view: {}", ex.getMessage());
        }
    }

    /**
     * Creates strategic constraints from the current initial status by setting
     * each expected outcome to GREATER_OR_EQUAL with the current allele DLI.
     */
    private void onConstraints() {
        final StrategicConstraints sc = caseConfig.getStrategicConstraints();
        if (sc == null) {
            logger.warn("onConstraints called but strategic constraints are null");
            return;
        }
        sc.removeAll();
        for (Genes gene : Genes.getGenesFor(caseConfig.getInitialStatus().getImplementationGroup())) {
            final Constraint constraint = new Constraint(ComparisonOperators.GREATER_OR_EQUAL, caseConfig.getInitialStatus().getAllele(gene).getDLI());
            sc.addConstraint(gene, constraint);
        }
        tableModel.setStrategicConstraints(sc);
    }

    /**
     * Creates a new randomized initial status. Prompts the user to save current
     * case if needed. Preserves original behavior and UI state updates.
     */
    private void onRandom() {
        boolean newRandom = false;
        if (caseConfig.isInitialized()) {
            if (caseConfig.isAlreadySaved()) {
                if (caseConfig.isModified()) {
                    int option = JOptionPane.showInternalConfirmDialog(this.getContentPane(),
                            "Save changes before creating a new random case?", null,
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                            imageBroker.getImageIcon32x32(AvailableImages.QUESTION));
                    if (option == JOptionPane.OK_OPTION) {
                        if (onSave()) {
                            newRandom = true;
                        }
                    } else if (option == JOptionPane.NO_OPTION) {
                        newRandom = true;
                    }
                } else {
                    newRandom = true;
                }
            } else {
                int option = JOptionPane.showInternalConfirmDialog(this.getContentPane(),
                        "Save the case before creating a new random one?", null,
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                        imageBroker.getImageIcon32x32(AvailableImages.QUESTION));
                if (option == JOptionPane.OK_OPTION) {
                    if (onSaveAs()) {
                        newRandom = true;
                    }
                } else if (option == JOptionPane.NO_OPTION) {
                    newRandom = true;
                }
            }
        } else {
            newRandom = true;
        }

        if (!newRandom) {
            return;
        }

        final ImplementationGroups auxIG = (ImplementationGroups) JOptionPane.showInputDialog(this,
                "Choose the implementation group for the new random case", null,
                JOptionPane.PLAIN_MESSAGE, imageBroker.getImageIcon32x32(AvailableImages.GENES),
                igOptions, ImplementationGroups.IG1);

        if (auxIG == null) {
            return;
        }

        caseConfig.reset();
        caseConfig.setCurrentIG(auxIG);
        caseConfig.setInitialized(true);
        caseConfig.setInitialStatus(new Chromosome(caseConfig.getCurrentIG()));
        caseConfig.getInitialStatus().randomizeGenes();
        caseConfig.setStrategicConstraints(new StrategicConstraints(caseConfig.getCurrentIG()));
        tableModel = new FLECOTableModel(caseConfig.getInitialStatus(), caseConfig.getStrategicConstraints());
        configureMainTable(tableModel);

        enableControlsAfterCaseLoad();
        progressBar.setValue(0);
        messageSpace.setText("Set the values of current status, constraint operator, and contraint value and run FLECO");
        if (caseConfig.getFileName() != null) {
            setTitle("CyberTOMP® FLECO Studio - " + caseConfig.getFileName());
        } else {
            setTitle("CyberTOMP® FLECO Studio - Current case is not saved!");
        }
        caseConfig.setInitialized(true);
    }

    /**
     * Creates a new empty case. Prompts the user to save current case if
     * needed. Preserves original behavior and UI state updates.
     */
    private void onNew() {
        boolean donew = false;
        if (caseConfig.isInitialized()) {
            if (caseConfig.isAlreadySaved()) {
                if (caseConfig.isModified()) {
                    int option = JOptionPane.showInternalConfirmDialog(this.getContentPane(),
                            "Save changes before creating a new case?", null,
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                            imageBroker.getImageIcon32x32(AvailableImages.QUESTION));
                    if (option == JOptionPane.OK_OPTION) {
                        if (onSave()) {
                            donew = true;
                        }
                    } else if (option == JOptionPane.NO_OPTION) {
                        donew = true;
                    }
                } else {
                    donew = true;
                }
            } else {
                int option = JOptionPane.showInternalConfirmDialog(this.getContentPane(),
                        "Save the case before creating a new one?", null,
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                        imageBroker.getImageIcon32x32(AvailableImages.QUESTION));
                if (option == JOptionPane.OK_OPTION) {
                    if (onSaveAs()) {
                        donew = true;
                    }
                } else if (option == JOptionPane.NO_OPTION) {
                    donew = true;
                }
            }
        } else {
            donew = true;
        }

        if (!donew) {
            return;
        }

        final ImplementationGroups auxIG = (ImplementationGroups) JOptionPane.showInputDialog(this,
                "Choose the implementation group for the new case", null,
                JOptionPane.PLAIN_MESSAGE, imageBroker.getImageIcon32x32(AvailableImages.GENES),
                igOptions, ImplementationGroups.IG1);

        if (auxIG == null) {
            return;
        }

        caseConfig.reset();
        caseConfig.setCurrentIG(auxIG);
        caseConfig.setInitialized(true);
        caseConfig.setInitialStatus(new Chromosome(caseConfig.getCurrentIG()));
        caseConfig.setStrategicConstraints(new StrategicConstraints(caseConfig.getCurrentIG()));
        tableModel = new FLECOTableModel(caseConfig.getInitialStatus(), caseConfig.getStrategicConstraints());
        configureMainTable(tableModel);

        enableControlsAfterCaseLoad();
        progressBar.setValue(0);
        messageSpace.setText("Set the values of current status, constraint operator, and contraint value and run FLECO");
        if (caseConfig.getFileName() != null) {
            setTitle("CyberTOMP® FLECO Studio - " + caseConfig.getFileName());
        } else {
            setTitle("CyberTOMP® FLECO Studio - Current case is not saved!");
        }
        caseConfig.setInitialized(true);
    }

    /**
     * Starts FLECO execution after prompting the user to save if necessary.
     * This method preserves the original behavior and delegates progress
     * reporting to the configured progress bar and SwingWorker.
     */
    private void onRunFLECO() {
        boolean run = false;
        if (caseConfig.isInitialized()) {
            if (caseConfig.isAlreadySaved()) {
                if (caseConfig.isModified()) {
                    int option = JOptionPane.showInternalConfirmDialog(this.getContentPane(),
                            "This action may change the target status.\nSave changes before proceeding?", null,
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                            imageBroker.getImageIcon32x32(AvailableImages.QUESTION));
                    if (option == JOptionPane.OK_OPTION) {
                        if (onSave()) {
                            run = true;
                        }
                    } else if (option == JOptionPane.NO_OPTION) {
                        run = true;
                    }
                } else {
                    run = true;
                }
            } else {
                int option = JOptionPane.showInternalConfirmDialog(this.getContentPane(),
                        "This action may change the target status.\nSave case before proceeding?", null,
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                        imageBroker.getImageIcon32x32(AvailableImages.QUESTION));
                if (option == JOptionPane.OK_OPTION) {
                    if (onSaveAs()) {
                        run = true;
                    }
                } else if (option == JOptionPane.NO_OPTION) {
                    run = true;
                }
            }
        } else {
            run = true;
        }

        if (!run) {
            return;
        }

        // BEFORE: disable UI controls while FLECO runs
        randomButton.setEnabled(false);
        newButton.setEnabled(false);
        loadButton.setEnabled(false);
        saveButton.setEnabled(caseConfig.isAlreadySaved() && caseConfig.isModified());
        saveAsButton.setEnabled(false);
        runButton.setEnabled(false);
        generateConstraintsButton.setEnabled(false);
        menuCaseItemNew.setEnabled(false);
        menuCaseItemLoad.setEnabled(false);
        menuCaseItemSave.setEnabled(caseConfig.isAlreadySaved() && caseConfig.isModified());
        menuCaseItemSaveAs.setEnabled(false);
        menuCaseItemRunFLECO.setEnabled(false);
        menuCaseItemExit.setEnabled(false);
        menuAbout.setEnabled(false);
        menuAboutItemAbout.setEnabled(false);
        menuAboutItemLicense.setEnabled(false);
        table.setEnabled(false);
        menuBar.setEnabled(false);
        menuCase.setEnabled(false);
        menuAbout.setEnabled(false);
        messageSpace.setText("FLECO is running...");
        progressBar.setValue(0);
        tableModel.removeTargetStatus();

        // MAIN: create and execute FLECO
        final int initialPopulation = 30;
        final int maxSeconds = 30;
        final float crossoverProbability = 0.90f;
        caseConfig.setFleco(new FLECO(initialPopulation, maxSeconds, crossoverProbability,
                tableModel.getImplementationGroup(), tableModel.getInitialStatus(), tableModel.getStrategicConstraints()));
        caseConfig.getFleco().setProgressEventListener(progressBar);
        final FLECOSwingWorker flecoSwingWorker = new FLECOSwingWorker(caseConfig.getFleco(), gui);
        flecoSwingWorker.execute();
        // afterOnRunFLECO will be called by the SwingWorker when finished
    }

    /**
     * Called by the FLECO SwingWorker when execution finishes. Updates UI and
     * case state accordingly.
     */
    @Override
    public void afterOnRunFLECO() {
        enableControlsAfterCaseLoad();
        progressBar.setValue(100);
        if (caseConfig.getFleco() != null && caseConfig.getFleco().hasConverged()) {
            messageSpace.setText("CyberTOMP® FLECO execution has finished. A compliant combination was found!");
        } else {
            messageSpace.setText("CyberTOMP® FLECO execution has finished. No compliant combination was found! Could be the constraints too restrictive?");
        }
        if (caseConfig.getFleco() != null) {
            caseConfig.setTargetStatus(caseConfig.getFleco().getBestChromosome());
            tableModel.setTargetStatus(caseConfig.getTargetStatus());
        } else {
            logger.warn("afterOnRunFLECO called but FLECO instance is null");
        }
    }

    /**
     * Notifies the GUI that the table model changed. Marks the case as modified
     * and updates save controls and window title accordingly.
     */
    @Override
    public void onFLECOTableModelChanged() {
        caseConfig.setModified(true);
        if (caseConfig.isAlreadySaved() && caseConfig.isModified()) {
            saveButton.setEnabled(true);
            menuCaseItemSave.setEnabled(true);
            setTitle("CyberTOMP® FLECO Studio - " + caseConfig.getFileName() + "*");
        } else {
            saveButton.setEnabled(false);
            menuCaseItemSave.setEnabled(false);
        }
    }
}
