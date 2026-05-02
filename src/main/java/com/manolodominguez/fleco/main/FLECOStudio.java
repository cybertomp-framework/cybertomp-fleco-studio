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
package com.manolodominguez.fleco.main;

import com.manolodominguez.fleco.gui.MainWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for launching FLECO Studio, the graphical interface used to
 * interact with the FLECO algorithm. This class configures the look and feel
 * and initializes the main application window.
 *
 * <p>
 * This class is intended to be used when running FLECO Studio as a standalone
 * application rather than as a library.</p>
 *
 * @author Manuel
 */
public final class FLECOStudio {

    /**
     * Default constructor required for Javadoc compliance. This class is not
     * intended to be instantiated, but the constructor is kept for backward
     * compatibility.
     */
    private FLECOStudio() {
        // Prevent instantiation
    }

    /**
     * Application entry point. Initializes the graphical environment,
     * configures the look and feel, and displays the main window of FLECO
     * Studio.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(final String[] args) {
        final Logger logger = LoggerFactory.getLogger(FLECOStudio.class);

        // Enable text antialiasing for improved readability
        System.setProperty("awt.useSystemAAFontSettings", "on");

        try {
            boolean nimbusSet = false;
            final UIManager.LookAndFeelInfo[] installedLafs = UIManager.getInstalledLookAndFeels();
            for (final UIManager.LookAndFeelInfo info : installedLafs) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    nimbusSet = true;
                    break;
                }
            }

            if (!nimbusSet) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                logger.info("Nimbus LaF not available. Using system Look & Feel.");
            }
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            logger.error("Failed to set Look & Feel for FLECO Studio.", e);
        }

        final MainWindow flecoGui = new MainWindow();
        SwingUtilities.invokeLater(() -> flecoGui.setVisible(true));
    }
}
