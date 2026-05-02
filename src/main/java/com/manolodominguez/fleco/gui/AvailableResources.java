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

/**
 * Enumeration of all available internal resources used by FLECO.
 *
 * <p>
 * This enum centralizes access to classpath-based resources (such as JSON
 * schemas) to avoid scattering string literals across the codebase and to
 * improve maintainability and consistency.
 * </p>
 */
public enum AvailableResources {

    /**
     * JSON schema used to validate FLECO case files.
     */
    FLECO_JSON_SCHEMA("FLECOJSONSchema.json");

    /**
     * Base classpath directory where all FLECO resources are located.
     */
    private static final String RESOURCES_PATH = "/com/manolodominguez/fleco/json/";

    /**
     * File name of the resource associated with this enum constant.
     */
    private final String resourceFileName;

    /**
     * Creates a new resource enum constant.
     *
     * @param resourceFileName the file name of the resource. Must not be
     * {@code null} or empty.
     * @throws IllegalArgumentException if {@code resourceFileName} is null or
     * empty
     */
    AvailableResources(final String resourceFileName) {
        if (resourceFileName == null || resourceFileName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "resourceFileName cannot be null or empty");
        }
        this.resourceFileName = resourceFileName;
    }

    /**
     * Returns the full classpath location of the resource.
     *
     * <p>
     * The returned path is intended for use with {@code ClassLoader}
     * resource-loading mechanisms.
     * </p>
     *
     * @return the full classpath resource path
     */
    public String getResource() {
        return RESOURCES_PATH + resourceFileName;
    }
}
