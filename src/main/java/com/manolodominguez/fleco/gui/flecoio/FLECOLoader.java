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

import com.manolodominguez.fleco.genetics.Alleles;
import com.manolodominguez.fleco.genetics.Chromosome;
import com.manolodominguez.fleco.genetics.Genes;
import com.manolodominguez.fleco.gui.AvailableResources;
import com.manolodominguez.fleco.strategicconstraints.ComparisonOperators;
import com.manolodominguez.fleco.strategicconstraints.Constraint;
import com.manolodominguez.fleco.strategicconstraints.StrategicConstraints;
import com.manolodominguez.fleco.uleo.Categories;
import com.manolodominguez.fleco.uleo.Functions;
import com.manolodominguez.fleco.uleo.ImplementationGroups;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads FLECO cases from disk into memory.
 *
 * <p>
 * This class is responsible for:
 * </p>
 *
 * <ul>
 * <li>Reading FLECO JSON files.</li>
 * <li>Validating the JSON structure against the official schema.</li>
 * <li>Restoring chromosomes and strategic constraints into memory.</li>
 * </ul>
 *
 * <p>
 * The loader keeps the latest successfully loaded case state internally and
 * provides accessor methods for retrieving it.
 * </p>
 *
 * @author Manuel Domínguez-Dorado
 */
public class FLECOLoader {

    /**
     * JSON key for implementation group.
     */
    private static final String KEY_CASE_IMPLEMENTATION_GROUP = "caseIG";

    /**
     * JSON key for initial status.
     */
    private static final String KEY_INITIAL_STATUS = "initialStatus";

    /**
     * JSON key for target status.
     */
    private static final String KEY_TARGET_STATUS = "targetStatus";

    /**
     * JSON key for strategic constraints.
     */
    private static final String KEY_STRATEGIC_CONSTRAINTS = "strategicConstraints";

    /**
     * JSON key for target status flag.
     */
    private static final String KEY_HAS_TARGET_STATUS = "hasTargetStatus";

    /**
     * JSON key for gene.
     */
    private static final String KEY_GENE = "gene";

    /**
     * JSON key for allele.
     */
    private static final String KEY_ALLELE = "allele";

    /**
     * JSON key for operator.
     */
    private static final String KEY_OPERATOR = "operator";

    /**
     * JSON key for value.
     */
    private static final String KEY_VALUE = "value";

    /**
     * JSON key for function.
     */
    private static final String KEY_FUNCTION = "function";

    /**
     * JSON key for category.
     */
    private static final String KEY_CATEGORY = "category";

    /**
     * JSON key for asset.
     */
    private static final String KEY_ASSET = "asset";

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FLECOLoader.class);

    /**
     * Initial chromosome status loaded from the case.
     */
    private Chromosome initialStatus;

    /**
     * Strategic constraints loaded from the case.
     */
    private StrategicConstraints strategicConstraints;

    /**
     * Target chromosome status loaded from the case.
     */
    private Chromosome targetStatus;

    /**
     * Implementation group associated with the loaded case.
     */
    private ImplementationGroups implementationGroup;

    /**
     * Creates a new FLECO loader with empty internal state.
     */
    public FLECOLoader() {
        resetState();
    }

    /**
     * Loads a FLECO case from the specified file.
     *
     * <p>
     * The file content is parsed as JSON and validated against the official
     * FLECO JSON schema before being restored into memory.
     * </p>
     *
     * @param inputFile the file containing the FLECO case to load.
     * @return {@code true} if the case was successfully loaded. Otherwise,
     * {@code false}.
     * @throws IllegalArgumentException if {@code inputFile} is {@code null}.
     */
    public boolean load(final File inputFile) {
        if (inputFile == null) {
            LOGGER.error("Input file cannot be null");
            throw new IllegalArgumentException("Input file cannot be null");
        }

        resetState();

        try (InputStream inputStream = inputFile.toURI().toURL().openStream()) {
            final JSONObject jsonFLECOCase = new JSONObject(new JSONTokener(inputStream));

            if (!isValidJSONFLECOCase(jsonFLECOCase)) {
                LOGGER.warn("Invalid FLECO JSON schema. File: {}", inputFile.getAbsolutePath());
                return false;
            }

            return initializeFromJSON(jsonFLECOCase);
        } catch (JSONException ex) {
            LOGGER.warn("Unable to load FLECO case from file: {}", inputFile.getAbsolutePath(), ex);
            resetState();
            return false;
        } catch (IOException | RuntimeException ex) {
            LOGGER.warn("Unable to load FLECO case from file: {}", inputFile.getAbsolutePath(), ex);
            resetState();
            return false;
        }
    }

    /**
     * Initializes the loader internal state from a validated JSON object.
     *
     * @param validatedJSONFLECOCase validated JSON representation of a FLECO
     * case.
     * @return {@code true} if initialization succeeds. Otherwise,
     * {@code false}.
     * @throws IllegalArgumentException if {@code validatedJSONFLECOCase} is
     * {@code null}.
     */
    private boolean initializeFromJSON(final JSONObject validatedJSONFLECOCase) {
        if (validatedJSONFLECOCase == null) {
            LOGGER.error("Validated JSON FLECO case cannot be null");
            throw new IllegalArgumentException("Validated JSON FLECO case cannot be null");
        }

        try {
            implementationGroup = ImplementationGroups.valueOf(
                    validatedJSONFLECOCase.getString(KEY_CASE_IMPLEMENTATION_GROUP));

            initialStatus = new Chromosome(implementationGroup);
            loadChromosome(
                    validatedJSONFLECOCase.getJSONArray(KEY_INITIAL_STATUS),
                    initialStatus);

            strategicConstraints = new StrategicConstraints(implementationGroup);
            loadStrategicConstraints(
                    validatedJSONFLECOCase.getJSONArray(KEY_STRATEGIC_CONSTRAINTS));

            final boolean hasTargetStatus
                    = validatedJSONFLECOCase.getBoolean(KEY_HAS_TARGET_STATUS);

            if (hasTargetStatus) {
                targetStatus = new Chromosome(implementationGroup);
                loadChromosome(
                        validatedJSONFLECOCase.getJSONArray(KEY_TARGET_STATUS),
                        targetStatus);
            }

            return true;
        } catch (IllegalArgumentException | JSONException ex) {
            LOGGER.warn("Error initializing FLECO case from JSON", ex);
            resetState();
            return false;
        }
    }

    /**
     * Loads chromosome data from a JSON array into the specified chromosome.
     *
     * @param savedGenes JSON array containing gene definitions.
     * @param chromosome chromosome instance to update.
     * @throws JSONException if JSON parsing fails.
     * @throws IllegalArgumentException if enum conversion fails.
     */
    private void loadChromosome(
            final JSONArray savedGenes,
            final Chromosome chromosome) {

        Objects.requireNonNull(savedGenes, "savedGenes");
        Objects.requireNonNull(chromosome, "chromosome");

        for (int i = 0; i < savedGenes.length(); i++) {
            final JSONObject geneObject = savedGenes.getJSONObject(i);

            final Genes gene = Genes.valueOf(geneObject.getString(KEY_GENE));
            final Alleles allele = Alleles.valueOf(geneObject.getString(KEY_ALLELE));

            chromosome.updateAllele(gene, allele);
        }
    }

    /**
     * Loads strategic constraints from the specified JSON array.
     *
     * @param savedConstraints JSON array containing constraint definitions.
     * @throws JSONException if JSON parsing fails.
     * @throws IllegalArgumentException if enum conversion fails.
     */
    private void loadStrategicConstraints(final JSONArray savedConstraints) {
        Objects.requireNonNull(savedConstraints, "savedConstraints");

        for (int i = 0; i < savedConstraints.length(); i++) {
            final JSONObject constraintObject = savedConstraints.getJSONObject(i);

            final Float constraintValue = constraintObject.getFloat(KEY_VALUE);
            final ComparisonOperators constraintOperator
                    = ComparisonOperators.valueOf(
                            constraintObject.getString(KEY_OPERATOR));

            final Constraint constraint
                    = new Constraint(constraintOperator, constraintValue);

            if (constraintObject.has(KEY_GENE)) {
                final Genes constraintGene
                        = Genes.valueOf(constraintObject.getString(KEY_GENE));

                strategicConstraints.addConstraint(constraintGene, constraint);
                continue;
            }

            if (constraintObject.has(KEY_FUNCTION)) {
                final Functions constraintFunction
                        = Functions.valueOf(constraintObject.getString(KEY_FUNCTION));

                strategicConstraints.addConstraint(constraintFunction, constraint);
                continue;
            }

            if (constraintObject.has(KEY_CATEGORY)) {
                final Categories constraintCategory
                        = Categories.valueOf(constraintObject.getString(KEY_CATEGORY));

                strategicConstraints.addConstraint(constraintCategory, constraint);
                continue;
            }

            if (constraintObject.has(KEY_ASSET)) {
                strategicConstraints.addConstraint(constraint);
                continue;
            }

            LOGGER.warn("Invalid strategic constraint definition at index {}", i);
            throw new JSONException("Invalid strategic constraint definition");
        }
    }

    /**
     * Validates whether the specified JSON object represents a valid FLECO
     * case.
     *
     * @param jsonFLECOCase JSON object to validate.
     * @return {@code true} if the JSON object is valid according to the FLECO
     * schema. Otherwise, {@code false}.
     * @throws IllegalArgumentException if {@code jsonFLECOCase} is
     * {@code null}.
     */
    private boolean isValidJSONFLECOCase(final JSONObject jsonFLECOCase) {
        if (jsonFLECOCase == null) {
            LOGGER.error("JSON FLECO case cannot be null");
            throw new IllegalArgumentException("JSON FLECO case cannot be null");
        }

        try (InputStream schemaStream = getClass().getResourceAsStream(
                AvailableResources.FLECO_JSON_SCHEMA.getResource())) {

            if (schemaStream == null) {
                LOGGER.error("FLECO JSON schema resource not found");
                throw new IllegalStateException("FLECO JSON schema resource not found");
            }

            final JSONObject rawSchema
                    = new JSONObject(new JSONTokener(schemaStream));

            final Schema schema = SchemaLoader.load(rawSchema);

            schema.validate(jsonFLECOCase);

            return true;
        } catch (ValidationException ex) {
            LOGGER.warn("FLECO JSON validation failed", ex);
            return false;
        } catch (IOException | JSONException ex) {
            LOGGER.error("Error loading or parsing FLECO JSON schema", ex);
            return false;
        }
    }

    /**
     * Resets the internal loader state.
     */
    private void resetState() {
        implementationGroup = null;
        initialStatus = null;
        strategicConstraints = null;
        targetStatus = null;
    }

    /**
     * Returns the initial chromosome status loaded from the FLECO case.
     *
     * @return the initial chromosome status, or {@code null} if no case has
     * been loaded.
     */
    public Chromosome getInitialStatus() {
        return initialStatus;
    }

    /**
     * Returns the strategic constraints loaded from the FLECO case.
     *
     * @return the strategic constraints, or {@code null} if no case has been
     * loaded.
     */
    public StrategicConstraints getStrategicConstraints() {
        return strategicConstraints;
    }

    /**
     * Returns the target chromosome status loaded from the FLECO case.
     *
     * @return the target chromosome status, or {@code null} if no target status
     * exists or no case has been loaded.
     */
    public Chromosome getTargetStatus() {
        return targetStatus;
    }
}
