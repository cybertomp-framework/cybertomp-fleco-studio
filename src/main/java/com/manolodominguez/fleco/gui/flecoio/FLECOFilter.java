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
package com.manolodominguez.fleco.gui.flecoio;

import java.io.File;
import java.util.Locale;
import javax.swing.filechooser.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File filter used in load/save dialogs to only allow FLECO files and
 * directories.
 *
 * <p>
 * Accepted files are those whose extension matches {@value #FLECO_EXTENSION}.
 * </p>
 *
 * @author Manuel Domínguez-Dorado
 */
public class FLECOFilter extends FileFilter {

    /**
     * Logger instance for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FLECOFilter.class);

    /**
     * FLECO file extension.
     */
    public static final String FLECO_EXTENSION = "fleco";

    /**
     * File description displayed in load/save dialogs.
     */
    private static final String DESCRIPTION = "FLECO case file";

    /**
     * Extension separator character.
     */
    private static final char EXTENSION_SEPARATOR = '.';

    /**
     * Error message used when a file parameter is null.
     */
    private static final String ERROR_NULL_FILE = "Parameter 'file' cannot be null.";

    /**
     * Constructs a new {@code FLECOFilter}.
     */
    public FLECOFilter() {
        super();
    }

    /**
     * Determines whether the specified file should be displayed in the
     * load/save dialog.
     *
     * <p>
     * Directories are always accepted to allow navigation. Files are accepted
     * only if their extension matches {@value #FLECO_EXTENSION}.
     * </p>
     *
     * @param file the file to evaluate.
     * @return {@code true} if the file should be displayed; {@code false}
     * otherwise.
     * @throws NullPointerException if {@code file} is {@code null}.
     */
    @Override
    public boolean accept(File file) {
        if (file == null) {
            LOGGER.error(ERROR_NULL_FILE);
            throw new NullPointerException(ERROR_NULL_FILE);
        }

        if (file.isDirectory()) {
            return true;
        }

        final String extension = getExtension(file);
        return FLECO_EXTENSION.equals(extension);
    }

    /**
     * Extracts the extension of the specified file.
     *
     * <p>
     * The returned extension is normalized to lowercase using the ROOT locale.
     * </p>
     *
     * @param file the file whose extension is to be extracted.
     * @return the file extension without the dot, or {@code null} if the file
     * has no valid extension.
     * @throws NullPointerException if {@code file} is {@code null}.
     */
    private String getExtension(File file) {
        if (file == null) {
            LOGGER.error(ERROR_NULL_FILE);
            throw new NullPointerException(ERROR_NULL_FILE);
        }

        final String fileName = file.getName();
        final int extensionIndex = fileName.lastIndexOf(EXTENSION_SEPARATOR);

        if (extensionIndex <= 0 || extensionIndex >= fileName.length() - 1) {
            return null;
        }

        return fileName.substring(extensionIndex + 1)
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Gets the description displayed in file chooser dialogs.
     *
     * @return the description displayed in file chooser dialogs.
     */
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
