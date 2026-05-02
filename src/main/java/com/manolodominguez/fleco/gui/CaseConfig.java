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
 * Represents the configuration of a FLECO case currently managed in FLECO
 * Studio.
 *
 * <p>
 * This class acts as a mutable state holder for the active case. It stores the
 * FLECO engine instance, initial and optional target chromosomes, strategic
 * constraints, persistence metadata (path and file name) and several flags used
 * by the UI to track initialization, save and modification state.</p>
 *
 * <p>
 * <b>Threading</b>: instances of this class are <b>not</b> thread-safe. If an
 * instance is shared between threads external synchronization must be
 * applied.</p>
 *
 * <p>
 * All public setters validate their inputs and will log an error before
 * throwing an {@link IllegalArgumentException} when a required parameter is
 * invalid.</p>
 */
public final class CaseConfig {

    /**
     * Logger instance for diagnostics and validation errors.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CaseConfig.class);

    /**
     * FLECO engine instance associated with this case. May be {@code null}
     * until set.
     */
    private FLECO fleco;

    /**
     * Initial chromosome state for the case. May be {@code null} until set.
     */
    private Chromosome initialStatus;

    /**
     * Strategic constraints applied to the case. May be {@code null} until set.
     */
    private StrategicConstraints strategicConstraints;

    /**
     * Optional target chromosome state. May be {@code null}.
     */
    private Chromosome targetStatus;

    /**
     * Full path and file name for persistence. May be {@code null} until set.
     */
    private String pathAndFileName;

    /**
     * Whether the case has been initialized.
     */
    private boolean initialized;

    /**
     * Whether the case has already been saved.
     */
    private boolean alreadySaved;

    /**
     * Whether the case has been modified since last save.
     */
    private boolean modified;

    /**
     * Current implementation group for the case. May be {@code null} until set.
     */
    private ImplementationGroups currentIG;

    /**
     * Creates a new empty case configuration with all fields reset to defaults.
     */
    public CaseConfig() {
        reset();
    }

    /**
     * Resets all internal state to default values.
     *
     * <p>
     * All object references are set to {@code null} and boolean flags to
     * {@code false}.</p>
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
     * Returns the FLECO engine instance.
     *
     * @return FLECO instance or {@code null} if not set
     */
    public FLECO getFleco() {
        return fleco;
    }

    /**
     * Sets the FLECO engine instance.
     *
     * @param fleco FLECO instance (must not be {@code null})
     * @throws IllegalArgumentException if {@code fleco} is {@code null}
     */
    public void setFleco(final FLECO fleco) {
        if (fleco == null) {
            LOGGER.error("setFleco: FLECO instance cannot be null");
            throw new IllegalArgumentException("FLECO instance cannot be null");
        }
        this.fleco = fleco;
    }

    /**
     * Returns the initial chromosome state.
     *
     * @return initial chromosome or {@code null} if not set
     */
    public Chromosome getInitialStatus() {
        return initialStatus;
    }

    /**
     * Sets the initial chromosome state.
     *
     * @param initialStatus chromosome (must not be {@code null})
     * @throws IllegalArgumentException if {@code initialStatus} is {@code null}
     */
    public void setInitialStatus(final Chromosome initialStatus) {
        if (initialStatus == null) {
            LOGGER.error("setInitialStatus: Initial status cannot be null");
            throw new IllegalArgumentException("Initial status cannot be null");
        }
        this.initialStatus = initialStatus;
    }

    /**
     * Returns the strategic constraints.
     *
     * @return strategic constraints or {@code null} if not set
     */
    public StrategicConstraints getStrategicConstraints() {
        return strategicConstraints;
    }

    /**
     * Sets the strategic constraints.
     *
     * @param strategicConstraints constraints (must not be {@code null})
     * @throws IllegalArgumentException if {@code strategicConstraints} is
     * {@code null}
     */
    public void setStrategicConstraints(final StrategicConstraints strategicConstraints) {
        if (strategicConstraints == null) {
            LOGGER.error("setStrategicConstraints: Strategic constraints cannot be null");
            throw new IllegalArgumentException("Strategic constraints cannot be null");
        }
        this.strategicConstraints = strategicConstraints;
    }

    /**
     * Returns the target chromosome state.
     *
     * @return target chromosome or {@code null} if not set
     */
    public Chromosome getTargetStatus() {
        return targetStatus;
    }

    /**
     * Sets the target chromosome state.
     *
     * <p>
     * This value is optional and may be {@code null} to indicate no explicit
     * target.</p>
     *
     * @param targetStatus target chromosome (may be {@code null})
     */
    public void setTargetStatus(final Chromosome targetStatus) {
        this.targetStatus = targetStatus;
    }

    /**
     * Returns the full file path associated with this case.
     *
     * @return full path or {@code null} if not set
     */
    public String getPathAndFileName() {
        return pathAndFileName;
    }

    /**
     * Returns only the file name portion of the configured path.
     *
     * @return file name or {@code null} if path is not set or empty
     */
    public String getFileName() {
        if (pathAndFileName == null) {
            return null;
        }
        final String trimmed = pathAndFileName.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return new File(trimmed).getName();
    }

    /**
     * Sets the full file path for persistence.
     *
     * <p>
     * The provided string is trimmed before storing. Empty or blank values are
     * rejected.</p>
     *
     * @param pathAndFileName path (must not be {@code null} or empty)
     * @throws IllegalArgumentException if {@code pathAndFileName} is
     * {@code null} or empty
     */
    public void setPathAndFileName(final String pathAndFileName) {
        if (pathAndFileName == null || pathAndFileName.trim().isEmpty()) {
            LOGGER.error("setPathAndFileName: Path and file name cannot be null or empty");
            throw new IllegalArgumentException("Path and file name cannot be null or empty");
        }
        this.pathAndFileName = pathAndFileName.trim();
    }

    /**
     * Convenience overload to set the path from a {@link File} instance.
     *
     * @param file file instance (must not be {@code null})
     * @throws IllegalArgumentException if {@code file} is {@code null}
     */
    public void setPathAndFileName(final File file) {
        if (file == null) {
            LOGGER.error("setPathAndFileName(File): file cannot be null");
            throw new IllegalArgumentException("file cannot be null");
        }
        setPathAndFileName(file.getPath());
    }

    /**
     * Returns whether the case has been initialized.
     *
     * @return {@code true} if initialized, otherwise {@code false}
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Sets the initialization state.
     *
     * @param initialized initialization flag
     */
    public void setInitialized(final boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Returns whether the case has already been saved.
     *
     * @return {@code true} if saved, otherwise {@code false}
     */
    public boolean isAlreadySaved() {
        return alreadySaved;
    }

    /**
     * Sets the saved state.
     *
     * @param alreadySaved saved flag
     */
    public void setAlreadySaved(final boolean alreadySaved) {
        this.alreadySaved = alreadySaved;
    }

    /**
     * Returns whether the case has been modified.
     *
     * @return {@code true} if modified, otherwise {@code false}
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Sets the modified state.
     *
     * @param modified modified flag
     */
    public void setModified(final boolean modified) {
        this.modified = modified;
    }

    /**
     * Returns the current implementation group.
     *
     * @return implementation group or {@code null} if not set
     */
    public ImplementationGroups getCurrentIG() {
        return currentIG;
    }

    /**
     * Sets the implementation group.
     *
     * @param currentIG implementation group (must not be {@code null})
     * @throws IllegalArgumentException if {@code currentIG} is {@code null}
     */
    public void setCurrentIG(final ImplementationGroups currentIG) {
        if (currentIG == null) {
            LOGGER.error("setCurrentIG: Implementation group cannot be null");
            throw new IllegalArgumentException("Implementation group cannot be null");
        }
        this.currentIG = currentIG;
    }
}
