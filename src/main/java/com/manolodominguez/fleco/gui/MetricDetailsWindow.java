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

import com.manolodominguez.fleco.genetics.Genes;
import com.manolodominguez.fleco.uleo.Categories;
import com.manolodominguez.fleco.uleo.Functions;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MetricDetailsWindow displays contextual information for a CyberTOMP metric.
 * <p>
 * The window can be constructed for a {@link Genes} metric, a
 * {@link Categories} metric, a {@link Functions} metric, or a generic business
 * asset view.
 * </p>
 *
 * <p>
 * This class is conservative in behavior: it preserves the original layout and
 * content while improving defensive checks, reducing duplication and adding
 * compact logging for invalid usage.
 * </p>
 *
 * <p>
 * Compatibility: Java 11, no additional dependencies.</p>
 *
 * @author Manuel Domínguez-Dorado
 */
@SuppressWarnings("serial")
public class MetricDetailsWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    /**
     * Default width of the window in pixels.
     */
    private static final int DEFAULT_WIDTH = 1000;

    /**
     * Default height of the window in pixels.
     */
    private static final int DEFAULT_HEIGHT = 600;

    /**
     * Logger for diagnostic messages.
     */
    private final Logger logger = LoggerFactory.getLogger(MetricDetailsWindow.class);

    /**
     * Parent frame used to compute initial position; must not be null.
     */
    private final Frame parent;

    /**
     * Image provider for icons.
     */
    private ImageBroker imageBroker;

    /**
     * Panel that holds action buttons.
     */
    private JPanel buttonsPanel;

    /**
     * Label for the metric acronym.
     */
    private JLabel labelAcronym;

    /**
     * Label that shows the acronym text.
     */
    private JLabel labelAcronymTxt;

    /**
     * Label for purpose section.
     */
    private JLabel labelPurpose;

    /**
     * Text area that shows the purpose.
     */
    private JTextArea textAreaPurpose;

    /**
     * Label for implementation tips section.
     */
    private JLabel labelImplementationTips;

    /**
     * Text area that shows implementation tips.
     */
    private JTextArea textAreaImplementationTips;

    /**
     * Label for references section.
     */
    private JLabel labelReferences;

    /**
     * Text area that shows references.
     */
    private JTextArea textAreaReferences;

    /**
     * Label for leading functional area section.
     */
    private JLabel labelLeadingArea;

    /**
     * Text area that shows the leading functional area.
     */
    private JTextArea textAreaLeadingArea;

    /**
     * Label for leading area responsibilities section.
     */
    private JLabel labelLeadingAreaResponsibilities;

    /**
     * Text area that shows the leading area responsibilities.
     */
    private JTextArea textAreaLeadingAreaResponsibilities;

    /**
     * Label for an additional tip section.
     */
    private JLabel labelAdditionalTip;

    /**
     * Text area that shows an additional tip.
     */
    private JTextArea textArealabelAdditionalTip;

    /**
     * Close button for the window.
     */
    private JButton closeButton;

    /**
     * Constructs a MetricDetailsWindow for a specific Genes metric.
     *
     * @param parent the parent frame used to compute the window position; must
     * not be null
     * @param gene the gene whose information will be displayed; must not be
     * null
     * @throws NullPointerException if parent or gene is null
     */
    public MetricDetailsWindow(final Frame parent, final Genes gene) {
        super();
        if (parent == null) {
            logger.error("MetricDetailsWindow constructor called with null parent (Genes)");
            throw new NullPointerException("parent must not be null");
        }
        if (gene == null) {
            logger.error("MetricDetailsWindow constructor called with null gene");
            throw new NullPointerException("gene must not be null");
        }
        this.parent = parent;
        setTitle(gene.getAcronym());

        labelAcronymTxt = new JLabel(gene.getAcronym());
        textAreaPurpose = new JTextArea(gene.getPurpose());
        textAreaImplementationTips = new JTextArea(gene.getImplementationTips());
        textAreaReferences = new JTextArea(gene.getReferences());
        textAreaLeadingArea = new JTextArea(gene.getLeadingFunctionalArea().getAreaName());
        textAreaLeadingAreaResponsibilities = new JTextArea(gene.getLeadingFunctionalArea().getMainResponsibilities());

        initCommonComponents();
    }

    /**
     * Constructs a MetricDetailsWindow for a Categories metric.
     *
     * @param parent the parent frame used to compute the window position; must
     * not be null
     * @param category the category whose information will be displayed; must
     * not be null
     * @throws NullPointerException if parent or category is null
     */
    public MetricDetailsWindow(final Frame parent, final Categories category) {
        super();
        if (parent == null) {
            logger.error("MetricDetailsWindow constructor called with null parent (Categories)");
            throw new NullPointerException("parent must not be null");
        }
        if (category == null) {
            logger.error("MetricDetailsWindow constructor called with null category");
            throw new NullPointerException("category must not be null");
        }
        this.parent = parent;
        setTitle(category.getAcronym());

        labelAcronymTxt = new JLabel(category.getAcronym());
        textAreaPurpose = new JTextArea(category.getPurpose());
        textAreaImplementationTips = new JTextArea("Implementation tips are not provided at this level. Choose any nested, low-level metric to access to their corresponding implementation tips.");
        textAreaReferences = new JTextArea("References are not provided at this level. Choose any nested, low-level metric to access to their corresponding references.");
        textAreaLeadingArea = new JTextArea("Several functional areas are involved in leading this category's cyberecurity actions. Choose any nested, low-level metric to access to their corresponding leading functional area.");
        textAreaLeadingAreaResponsibilities = new JTextArea("Several functional areas are involved in leading this category's cyberecurity actions. Choose any nested, low-level metric to access to their corresponding leading functional area's responsibility.");

        initCommonComponents();
    }

    /**
     * Constructs a MetricDetailsWindow for a Functions metric.
     *
     * @param parent the parent frame used to compute the window position; must
     * not be null
     * @param function the function whose information will be displayed; must
     * not be null
     * @throws NullPointerException if parent or function is null
     */
    public MetricDetailsWindow(final Frame parent, final Functions function) {
        super();
        if (parent == null) {
            logger.error("MetricDetailsWindow constructor called with null parent (Functions)");
            throw new NullPointerException("parent must not be null");
        }
        if (function == null) {
            logger.error("MetricDetailsWindow constructor called with null function");
            throw new NullPointerException("function must not be null");
        }
        this.parent = parent;
        setTitle(function.getAcronym());

        labelAcronymTxt = new JLabel(function.getAcronym());
        textAreaPurpose = new JTextArea(function.getPurpose());
        textAreaImplementationTips = new JTextArea("Implementation tips are not provided at this level. Choose any nested, low-level metric to access to their corresponding implementation tips.");
        textAreaReferences = new JTextArea("References are not provided at this level. Choose any nested, low-level metric to access to their corresponding references.");
        textAreaLeadingArea = new JTextArea("Several functional areas are involved in leading this function's cyberecurity actions. Choose any nested, low-level metric to access to their corresponding leading functional area.");
        textAreaLeadingAreaResponsibilities = new JTextArea("Several functional areas are involved in leading this function's cyberecurity actions. Choose any nested, low-level metric to access to their corresponding leading functional area's responsibility.");

        initCommonComponents();
    }

    /**
     * Constructs a generic MetricDetailsWindow for a business (sub)asset.
     *
     * @param parent the parent frame used to compute the window position; must
     * not be null
     * @throws NullPointerException if parent is null
     */
    public MetricDetailsWindow(final Frame parent) {
        super();
        if (parent == null) {
            logger.error("MetricDetailsWindow constructor called with null parent (generic)");
            throw new NullPointerException("parent must not be null");
        }
        this.parent = parent;
        setTitle("BUSINESS (SUB)ASSET");

        labelAcronymTxt = new JLabel("BUSINESS (SUB)ASSET");
        textAreaPurpose = new JTextArea("Achieve a good, holistic, cybersecurity status for the business (sub)asset");
        textAreaImplementationTips = new JTextArea("Implementation tips are not provided at this level. Choose any nested, low-level metric to access to their corresponding implementation tips.");
        textAreaReferences = new JTextArea("References are not provided at this level. Choose any nested, low-level metric to access to their corresponding references.");
        textAreaLeadingArea = new JTextArea("Several functional areas are involved in leading this asset's cyberecurity actions. Choose any nested, low-level metric to access to their corresponding leading functional area.");
        textAreaLeadingAreaResponsibilities = new JTextArea("Several functional areas are involved in leading this asset's cyberecurity actions. Choose any nested, low-level metric to access to their corresponding leading functional area's responsibility.");

        initCommonComponents();
    }

    /**
     * Initializes common UI components, layout and behavior shared by all
     * constructors.
     *
     * <p>
     * This method centralizes component configuration to avoid duplication and
     * to ensure consistent properties for all text areas and controls.</p>
     */
    private void initCommonComponents() {
        imageBroker = new ImageBroker();

        addWindowStateListener((WindowEvent arg0) -> keep());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);

        buttonsPanel = new JPanel(new MigLayout("fillx, insets 20"));

        getContentPane().setLayout(new MigLayout("wrap 2, fill, insets 20",
                "[align left, shrink][align left, shrink]",
                "[align top, shrink][align top, shrink][align top, shrink][align top, shrink][align top, shrink][align top, shrink][align top, shrink]"));

        // Position and sizing
        try {
            setBounds((parent.getWidth() - DEFAULT_WIDTH) / 2, (parent.getHeight() - DEFAULT_HEIGHT) / 2, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        } catch (Exception ex) {
            // Defensive fallback: center on screen if parent dimensions are not available
            logger.debug("Could not compute bounds from parent; centering on screen: {}", ex.getMessage());
            final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds((screen.width - DEFAULT_WIDTH) / 2, (screen.height - DEFAULT_HEIGHT) / 2, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        }
        setMaximumSize(new Dimension((int) (DEFAULT_WIDTH * 1.2f), (int) (DEFAULT_HEIGHT * 1.2f)));
        setMinimumSize(new Dimension((int) (DEFAULT_WIDTH * 0.8f), (int) (DEFAULT_HEIGHT * 0.8f)));

        // Labels
        labelAcronym = new JLabel("Acronym: ");
        labelPurpose = new JLabel("Purpose: ");
        labelImplementationTips = new JLabel("Implementation tips: ");
        labelReferences = new JLabel("References: ");
        labelLeadingArea = new JLabel("Leading functional area: ");
        labelLeadingAreaResponsibilities = new JLabel("Area's responsibilities: ");

        // Add acronym row
        getContentPane().add(labelAcronym, "growx, wmin 10");
        getContentPane().add(labelAcronymTxt, "growx, wmin 10");

        // Configure and add text areas
        configureTextArea(textAreaPurpose);
        getContentPane().add(labelPurpose, "growx, wmin 10");
        getContentPane().add(textAreaPurpose, "growx, wmin 10");

        configureTextArea(textAreaImplementationTips);
        getContentPane().add(labelImplementationTips, "growx, wmin 10");
        getContentPane().add(textAreaImplementationTips, "growx, wmin 10");

        configureTextArea(textAreaReferences);
        getContentPane().add(labelReferences, "growx, wmin 10");
        getContentPane().add(textAreaReferences, "growx, wmin 10");

        configureTextArea(textAreaLeadingArea);
        getContentPane().add(labelLeadingArea, "growx, wmin 10");
        getContentPane().add(textAreaLeadingArea, "growx, wmin 10");

        configureTextArea(textAreaLeadingAreaResponsibilities);
        getContentPane().add(labelLeadingAreaResponsibilities, "growx, wmin 10");
        getContentPane().add(textAreaLeadingAreaResponsibilities, "growx, wmin 10");

        labelAdditionalTip = new JLabel("Additional tip:");
        textArealabelAdditionalTip = new JTextArea("The information in this window aims to serve as assistance in achieving the desired value for this metric. In any case, the cybersecurity team must convert all this information into specific tasks that will primarily depend on the nature of the asset in question. These tasks should be focused, considering the cybersecurity function and category that this metric contributes to, as well as the specialized field of the identified main functional area.");
        configureTextArea(textArealabelAdditionalTip);
        getContentPane().add(labelAdditionalTip, "growx, wmin 10");
        getContentPane().add(textArealabelAdditionalTip, "growx, wmin 10");

        // Buttons panel and close button
        getContentPane().add(buttonsPanel, "span, south, width 100%, height 20, wrap, align right");
        closeButton = new JButton("Close");
        closeButton.setMnemonic('C');
        closeButton.setIcon(imageBroker.getImageIcon16x16(AvailableImages.CLOSE));
        closeButton.addActionListener((ActionEvent e) -> dispose());
        buttonsPanel.add(closeButton, "align right");
    }

    /**
     * Configures a JTextArea with consistent properties used across the window.
     *
     * @param area the text area to configure; must not be null
     * @throws NullPointerException if area is null
     */
    private void configureTextArea(final JTextArea area) {
        if (area == null) {
            logger.error("configureTextArea called with null area");
            throw new NullPointerException("area must not be null");
        }
        area.setEditable(false);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setAutoscrolls(true);
        area.setCaretPosition(0);
    }

    /**
     * Prevents the window from being iconified or maximized by forcing normal
     * extended state whenever a window state change occurs.
     */
    private void keep() {
        this.setExtendedState(Frame.NORMAL);
    }
}
