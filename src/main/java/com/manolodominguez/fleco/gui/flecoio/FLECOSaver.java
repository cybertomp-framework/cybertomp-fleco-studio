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

import com.manolodominguez.fleco.genetics.Chromosome;
import com.manolodominguez.fleco.strategicconstraints.StrategicConstraints;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Saves FLECO cases from memory to disk using the official JSON format.
 *
 * <p>
 * This class serializes:
 * </p>
 *
 * <ul>
 * <li>Initial chromosome status.</li>
 * <li>Strategic constraints.</li>
 * <li>Optional target chromosome status.</li>
 * </ul>
 *
 * <p>
 * The generated output is compatible with the FLECO loader and follows the
 * expected JSON schema.
 * </p>
 *
 * @author Manuel Domínguez-Dorado
 */
public class FLECOSaver {

    /**
     * JSON key for implementation group.
     */
    private static final String KEY_CASE_IMPLEMENTATION_GROUP = "caseIG";

    /**
     * JSON key for target status existence flag.
     */
    private static final String KEY_HAS_TARGET_STATUS = "hasTargetStatus";

    /**
     * JSON key for initial status.
     */
    private static final String KEY_INITIAL_STATUS = "initialStatus";

    /**
     * JSON key for strategic constraints.
     */
    private static final String KEY_STRATEGIC_CONSTRAINTS = "strategicConstraints";

    /**
     * JSON key for target status.
     */
    private static final String KEY_TARGET_STATUS = "targetStatus";

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FLECOSaver.class);

    /**
     * Initial chromosome status to save.
     */
    private final Chromosome initialStatus;

    /**
     * Strategic constraints to save.
     */
    private final StrategicConstraints strategicConstraints;

    /**
     * Target chromosome status to save.
     */
    private final Chromosome targetStatus;

    /**
     * Creates a new FLECO saver instance.
     *
     * @param initialStatus initial chromosome status to save.
     * @param strategicConstraints strategic constraints to save.
     * @param targetStatus optional target chromosome status to save.
     * @throws IllegalArgumentException if {@code initialStatus} is
     * {@code null}.
     */
    public FLECOSaver(
            final Chromosome initialStatus,
            final StrategicConstraints strategicConstraints,
            final Chromosome targetStatus) {

        if (initialStatus == null) {
            LOGGER.error("Initial status cannot be null");
            throw new IllegalArgumentException("Initial status cannot be null");
        }

        this.initialStatus = initialStatus;
        this.strategicConstraints = strategicConstraints;
        this.targetStatus = targetStatus;
    }

    /**
     * Saves the current FLECO case to the specified file.
     *
     * <p>
     * Existing file content will be overwritten.
     * </p>
     *
     * @param outputFile destination file.
     * @return {@code true} if the case was successfully saved. Otherwise,
     * {@code false}.
     * @throws IllegalArgumentException if {@code outputFile} is {@code null}.
     */
    public boolean save(final File outputFile) {
        if (outputFile == null) {
            LOGGER.error("Output file cannot be null");
            throw new IllegalArgumentException("Output file cannot be null");
        }

        if (strategicConstraints == null) {
            LOGGER.error("Strategic constraints cannot be null");
            throw new IllegalStateException("Strategic constraints cannot be null");
        }

        try (FileOutputStream outputStream = new FileOutputStream(outputFile); PrintStream output = new PrintStream(outputStream)) {

            writeJsonContent(output);

            return true;
        } catch (IOException ex) {
            LOGGER.error(
                    "Error saving FLECO case to disk. File: {}",
                    outputFile.getAbsolutePath(),
                    ex);

            return false;
        }
    }

    /**
     * Writes the FLECO JSON structure to the specified output stream.
     *
     * @param output destination print stream.
     * @throws NullPointerException if {@code output} is {@code null}.
     */
    private void writeJsonContent(final PrintStream output) {
        Objects.requireNonNull(output, "output");

        output.println("{");

        writeCaseHeader(output);
        writeInitialStatus(output);
        writeStrategicConstraints(output);
        writeTargetStatus(output);

        output.println("}");
    }

    /**
     * Writes the JSON header section.
     *
     * @param output destination print stream.
     */
    private void writeCaseHeader(final PrintStream output) {
        output.println(
                "\t\"" + KEY_CASE_IMPLEMENTATION_GROUP + "\":\""
                + initialStatus.getImplementationGroup().name() + "\",");

        output.println(
                "\t\"" + KEY_HAS_TARGET_STATUS + "\":"
                + (targetStatus != null) + ",");
    }

    /**
     * Writes the initial chromosome status section.
     *
     * @param output destination print stream.
     */
    private void writeInitialStatus(final PrintStream output) {
        output.println("\t\"" + KEY_INITIAL_STATUS + "\": [");
        output.print(initialStatus.getGenesAsJSONString());
        output.println("\t],");
    }

    /**
     * Writes the strategic constraints section.
     *
     * @param output destination print stream.
     */
    private void writeStrategicConstraints(final PrintStream output) {
        output.println("\t\"" + KEY_STRATEGIC_CONSTRAINTS + "\": [");
        output.print(strategicConstraints.getConstraintsAsJSONString());
        output.println("\t],");
    }

    /**
     * Writes the target chromosome status section.
     *
     * @param output destination print stream.
     */
    private void writeTargetStatus(final PrintStream output) {
        output.println("\t\"" + KEY_TARGET_STATUS + "\": [");

        if (targetStatus != null) {
            output.print(targetStatus.getGenesAsJSONString());
        }

        output.println("\t]");
    }
}
