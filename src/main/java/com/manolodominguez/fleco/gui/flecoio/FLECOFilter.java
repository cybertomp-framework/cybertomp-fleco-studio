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
import javax.swing.filechooser.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a filter that is used in load/save dialogs to only
 * permit FLECO files.
 *
 * @author Manuel Domínguez-Dorado
 */
public class FLECOFilter extends FileFilter {

    private final Logger logger = LoggerFactory.getLogger(FLECOFilter.class);

    /**
     * This is the constructor of the class. It creates a new file filter and
     * sets its initial values.
     *
     * @author Manuel Domínguez-Dorado
     */
    public FLECOFilter() {
        super();
    }

    /**
     * This method, applied to a file, is used to know whether the file should
     * be showed in the load/save dialog or not.
     *
     * @author Manuel Domínguez-Dorado
     * @param file the file that is being considered to be showed in the dialog
     * or not.
     * @return true if the file should be showed in the dialog. otherwise,
     * false.
     */
    @Override
    public boolean accept(File file) {
        if (!file.isDirectory()) {
            String extension = this.getExtension(file);
            if (extension != null) {
                return extension.equals(FLECOFilter.FLECO_EXTENSION);
            }
        } else {
            return true;
        }
        return false;
    }

    /**
     * This method extract and return the extension of the file specified as an
     * argument.
     *
     * @author Manuel Domínguez-Dorado
     * @param file the file whose extension is being extracted.
     * @return the extension of the file specified as an argument.
     */
    private String getExtension(File file) {
        String extension = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            extension = s.substring(i + 1).toLowerCase();
        }
        return extension;
    }

    /**
     * This method returns the FLECO case description to be showed in the
     * load/save dialogs.
     *
     * @author Manuel Domínguez-Dorado
     * @return the FLECO case description.
     */
    @Override
    public String getDescription() {
        return "FLECO case file";
    }

    public static final String FLECO_EXTENSION = "fleco";
}
