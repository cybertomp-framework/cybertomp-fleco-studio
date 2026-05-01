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
import com.manolodominguez.fleco.strategicconstraints.StrategicConstraints;
import com.manolodominguez.fleco.uleo.ImplementationGroups;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the configuration of a case that is going to be managed
 * in FLECO Studio.
 *
 * @author Manuel Domínguez-Dorado
 */
public class CaseConfig {

    private FLECO fleco;
    private Chromosome initialStatus;
    private StrategicConstraints strategicConstraints;
    private Chromosome targetStatus;
    private String pathAndFileName;
    private boolean initialized;
    private boolean alreadySaved;
    private boolean modified;
    private ImplementationGroups currentIG;

    private final Logger logger = LoggerFactory.getLogger(CaseConfig.class);
    
    /**
     * This is the constructor of the class. It creates a new instance and
     * assigns the attributes their initial values.
     *
     * @author Manuel Domínguez-Dorado
     */
    public CaseConfig() {
        fleco = null;
        initialStatus = null;
        strategicConstraints = null;
        targetStatus = null;
        pathAndFileName = null;
        initialized = false;
        alreadySaved = false;
        modified = false;
        currentIG = null;
    }

    /**
     * This method reset all the attributes to their default values.
     *
     * @author Manuel Domínguez-Dorado
     */
    public void reset() {
        fleco = null;
        initialStatus = null;
        strategicConstraints = null;
        targetStatus = null;
        pathAndFileName = null;
        initialized = false;
        alreadySaved = false;
        modified = false;
        currentIG = null;
    }

    /**
     * This method gets the FLECO instance of the case being considered.
     *
     * @author Manuel Domínguez-Dorado
     * @return the FLECO instacne of the case being considered.
     */
    public FLECO getFleco() {
        return fleco;
    }

    /**
     * This method sets the FLECO instance of the case being considered using
     * the FLECO instance specified as an argument.
     *
     * @author Manuel Domínguez-Dorado
     * @param fleco the FLECO instance of the case being considered.
     */
    public void setFleco(FLECO fleco) {
        this.fleco = fleco;
    }

    /**
     * This method gets the initial status of the case being considered.
     *
     * @author Manuel Domínguez-Dorado
     * @return the initial status of the case being considered
     */
    public Chromosome getInitialStatus() {
        return initialStatus;
    }

    /**
     * This method sets the initial status of the case being considered using
     * the one specified as an argument.
     *
     * @author Manuel Domínguez-Dorado
     * @param initialStatus the initial status of the case being considered.
     */
    public void setInitialStatus(Chromosome initialStatus) {
        this.initialStatus = initialStatus;
    }

    /**
     * This method gets the strategic constraints of the case being considered.
     *
     * @author Manuel Domínguez-Dorado
     * @return the strategic constraints of the case being considered.
     */
    public StrategicConstraints getStrategicConstraints() {
        return strategicConstraints;
    }

    /**
     * This method sets the strategic constraints of the case being considered
     * using the one specified as an argument.
     *
     * @author Manuel Domínguez-Dorado
     * @param strategicConstraints the strategic constraints of the case being
     * considered.
     */
    public void setStrategicConstraints(StrategicConstraints strategicConstraints) {
        this.strategicConstraints = strategicConstraints;
    }

    /**
     * This method gets the target status of the case being considered.
     *
     * @author Manuel Domínguez-Dorado
     * @return the target status of the case being considered.
     */
    public Chromosome getTargetStatus() {
        return targetStatus;
    }

    /**
     * This method sets the trarget status of the case being considered using
     * the one specified as an argument.
     *
     * @author Manuel Domínguez-Dorado
     * @param targetStatus the trarget status of the case being considered.
     */
    public void setTargetStatus(Chromosome targetStatus) {
        this.targetStatus = targetStatus;
    }

    /**
     * This method gets the path and filename of the case being considered.
     *
     * @author Manuel Domínguez-Dorado
     * @return the path and filename of the case being considered.
     */
    public String getPathAndFileName() {
        return pathAndFileName;
    }

    /**
     * This method gets the filename of the case being considered.
     *
     * @author Manuel Domínguez-Dorado
     * @return the filename of the case being considered.
     */
    public String getFileName() {
        if (pathAndFileName != null) {
            File file = new File(pathAndFileName);
            return file.getName();
        }
        return null;
    }

    /**
     * This method sets the path and filename of the case being considered using
     * the one specified as an argument.
     *
     * @author Manuel Domínguez-Dorado
     * @param pathAndFileName the path and filename of the case being
     * considered.
     */
    public void setPathAndFileName(String pathAndFileName) {
        this.pathAndFileName = pathAndFileName;
    }

    /**
     * This method returns whether the case being considered has been
     * initialized or not.
     *
     * @author Manuel Domínguez-Dorado
     * @return TRUE, if the case has been initialized. Otherwise, FALSE.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * This method sets whether the case being considered has been initialized
     * or not, depending on the value of the parameter.
     *
     * @author Manuel Domínguez-Dorado
     * @param initialized whether the case being considered has been initialized
     * or not.
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * This method returns whether the case being considered has been already
     * saved or not.
     *
     * @author Manuel Domínguez-Dorado
     * @return TRUE if the case has been already saved. Otherwise, FALSE.
     */
    public boolean isAlreadySaved() {
        return alreadySaved;
    }

    /**
     * This method sets whether the case being considered has already been saved
     * or not, depending on the value of the parameter.
     *
     * @author Manuel Domínguez-Dorado
     * @param alreadySaved whether the case being considered has already been
     * saved or not.
     */
    public void setAlreadySaved(boolean alreadySaved) {
        this.alreadySaved = alreadySaved;
    }

    /**
     * This method returns whether the case being considered has been modified
     * after saved or not.
     *
     * @author Manuel Domínguez-Dorado
     * @return TRUE, if the case has been modified after saved. Otherwise,
     * FALSE.
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * This method sets whether the case being considered has been modified
     * after saved or not, depending on the value of the parameter.
     *
     * @author Manuel Domínguez-Dorado
     * @param modified whether the case being considered has been modified after
     * saved or not.
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /**
     * This method gets the implementation group of the case being considered.
     *
     * @author Manuel Domínguez-Dorado
     * @return the implementation group of the case being considered.
     */
    public ImplementationGroups getCurrentIG() {
        return currentIG;
    }

    /**
     * This method sets the implementation group of the case being considered
     * using the one specified as an argument.
     *
     * @author Manuel Domínguez-Dorado
     * @param currentIG the implementation group of the case being considered.
     */
    public void setCurrentIG(ImplementationGroups currentIG) {
        this.currentIG = currentIG;
    }

}
