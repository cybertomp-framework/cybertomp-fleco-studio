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

import com.manolodominguez.fleco.gui.flecoio.FLECOSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an enum implementation to identify all available images that can be
 * used in FLECO Studio.
 *
 * @author Manuel Domínguez-Dorado
 */
public enum AvailableImages {
    NOT_FOUND("notfound.png"),
    ABOUT("about.png"),
    EXIT("exit.png"),
    GENES("genes.png"),
    LICENSE("license.png"),
    LOAD("load.png"),
    NEW("new.png"),
    RANDOM("random.png"),
    RULES("rules.png"),
    RUN("run.png"),
    SAVE("save.png"),
    QUESTION("question.png"),
    SAVE_AS("saveas.png"),
    CLOSE("close.png");

    private final String imageFileName;

    private final Logger logger = LoggerFactory.getLogger(AvailableImages.class);
    
    /**
     * This is the constructor of the enum. It creates a new enum item and
     * associates a filename to it.
     *
     * @param imageFilename the filename that corresponds to the new available
     * image created.
     * @author Manuel Domínguez Dorado
     */
    private AvailableImages(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    /**
     * This methods gets the complete file path to the image associated to the
     * enum item in a 16x16 pixel format.
     *
     * @author Manuel Domínguez Dorado
     * @return the complete file path to the image associated to the enum item
     * in a 16x16 pixel format.
     */
    public String getPath16x16() {
        return AvailableImages.IMAGES_PATH + ICON_16 + imageFileName;
    }

    /**
     * This methods gets the complete file path to the image associated to the
     * enum item in a 32x32 pixel format.
     *
     * @author Manuel Domínguez Dorado
     * @return the complete file path to the image associated to the enum item
     * in a 32x32 pixel format.
     */
    public String getPath32x32() {
        return AvailableImages.IMAGES_PATH + ICON_32 + imageFileName;
    }

    private static final String IMAGES_PATH = "/com/manolodominguez/fleco/gui/";
    private static final String ICON_16 = "16x16/";
    private static final String ICON_32 = "32x32/";
}
