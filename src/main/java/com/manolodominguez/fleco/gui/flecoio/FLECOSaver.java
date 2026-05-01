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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements FLECO file saver that can store a case from memory to a
 * file on disk.
 *
 * @author Manuel Domínguez-Dorado
 */
public class FLECOSaver {

    private Chromosome initialStatus;
    private StrategicConstraints strategicConstraints;
    private Chromosome targetStatus;
    private FileOutputStream outputStream;
    private PrintStream output;

    private final Logger logger = LoggerFactory.getLogger(FLECOSaver.class);

    /**
     * This is the constructor of the class.It creates a new FLECO loader and
     * sets its initial values.
     *
     * @author Manuel Domínguez-Dorado
     * @param initialStatus the initial status of the FLECO case being saved.
     * @param strategicConstraints the strategic constraints of the FLECO case
     * being saved.
     * @param targetStatus the target status of the FLECO case being saved.
     */
    public FLECOSaver(Chromosome initialStatus, StrategicConstraints strategicConstraints, Chromosome targetStatus) {
        if (initialStatus == null) {
            logger.error("initialStatus is null");
            throw new IllegalArgumentException("initialStatus is null");
        }
        this.initialStatus = initialStatus;
        this.strategicConstraints = strategicConstraints;
        this.targetStatus = targetStatus;
        outputStream = null;
        output = null;
    }

    /**
     * This method save a FLECO case from memory to a file on disk.
     *
     * @author Manuel Domínguez-Dorado
     * @param outputFile the destination file for the case being saved.
     * @return true, if the FLECO case is saved to the specified file.
     * Otherwise, false.
     */
    public boolean save(File outputFile) {
        if (outputFile == null) {
            logger.error("outputFile is null");
            throw new IllegalArgumentException("outputFile is null");
        }
        try {
            outputStream = new FileOutputStream(outputFile);
            output = new PrintStream(this.outputStream);
            output.println("{");
            output.println("\t\"caseIG\":\"" + initialStatus.getImplementationGroup().name() + "\",");
            if (targetStatus == null) {
                output.println("\t\"hasTargetStatus\":false,");
            } else {
                output.println("\t\"hasTargetStatus\":true,");
            }
            // INITIAL STATUS
            output.println("\t\"initialStatus\": [");
            output.print(initialStatus.getGenesAsJSONString());
            output.println("\t],");
            // STRATEGIC CONSTRAINTS
            output.println("\t\"strategicConstraints\": [");
            output.print(strategicConstraints.getConstraintsAsJSONString());
            output.println("\t],");
            // TARGET STATUS
            output.println("\t\"targetStatus\": [");
            if (targetStatus != null) {
                output.print(targetStatus.getGenesAsJSONString());
            }
            output.println("\t]");
            output.println("}");
            outputStream.close();
            output.close();
        }
        catch (IOException e) {
            logger.error("Error saving FLECO case to disk");
            return false;
        }
        return true;
    }

}
