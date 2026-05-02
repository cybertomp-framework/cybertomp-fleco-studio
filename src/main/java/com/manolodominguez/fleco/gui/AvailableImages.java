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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enum that defines all available image resources used in FLECO Studio.
 *
 * <p>
 * Each constant represents an image file name that can be resolved into a
 * classpath resource path for different icon resolutions (16x16 and 32x32).
 * </p>
 *
 * <p>
 * This enum centralizes image resource management to avoid scattered string
 * literals across the UI layer and improve maintainability.
 * </p>
 */
public enum AvailableImages {

    /**
     * Image used when a resource is not found.
     */
    NOT_FOUND("notfound.png"),
    /**
     * About dialog icon.
     */
    ABOUT("about.png"),
    /**
     * Exit application icon.
     */
    EXIT("exit.png"),
    /**
     * Genes-related icon.
     */
    GENES("genes.png"),
    /**
     * License information icon.
     */
    LICENSE("license.png"),
    /**
     * Load action icon.
     */
    LOAD("load.png"),
    /**
     * New item/action icon.
     */
    NEW("new.png"),
    /**
     * Random generation icon.
     */
    RANDOM("random.png"),
    /**
     * Rules/help icon.
     */
    RULES("rules.png"),
    /**
     * Run/execute action icon.
     */
    RUN("run.png"),
    /**
     * Save action icon.
     */
    SAVE("save.png"),
    /**
     * Question/help icon.
     */
    QUESTION("question.png"),
    /**
     * Save-as action icon.
     */
    SAVE_AS("saveas.png"),
    /**
     * Close window/action icon.
     */
    CLOSE("close.png");

    /**
     * Base path for all image resources inside the classpath.
     */
    private static final String IMAGES_PATH = "/com/manolodominguez/fleco/gui/";

    /**
     * Subdirectory for 16x16 icons.
     */
    private static final String ICON_16 = "16x16/";

    /**
     * Subdirectory for 32x32 icons.
     */
    private static final String ICON_32 = "32x32/";

    /**
     * File name associated with the enum constant.
     */
    private final String imageFileName;

    /**
     * Creates a new enum constant bound to a specific image file name.
     *
     * @param imageFileName the image file name associated with this enum
     * constant. Must not be {@code null} or empty.
     * @throws IllegalArgumentException if {@code imageFileName} is null or
     * empty
     */
    AvailableImages(final String imageFileName) {
        if (imageFileName == null || imageFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("imageFileName cannot be null or empty");
        }
        this.imageFileName = imageFileName;
    }

    /**
     * Returns the classpath resource path for the 16x16 version of this image.
     *
     * @return the full resource path for the 16x16 icon
     */
    public String getPath16x16() {
        return IMAGES_PATH + ICON_16 + imageFileName;
    }

    /**
     * Returns the classpath resource path for the 32x32 version of this image.
     *
     * @return the full resource path for the 32x32 icon
     */
    public String getPath32x32() {
        return IMAGES_PATH + ICON_32 + imageFileName;
    }
}
