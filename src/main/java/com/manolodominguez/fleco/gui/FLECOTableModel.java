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

import com.manolodominguez.fleco.genetics.Alleles;
import com.manolodominguez.fleco.genetics.Chromosome;
import com.manolodominguez.fleco.genetics.Genes;
import com.manolodominguez.fleco.strategicconstraints.ComparisonOperators;
import com.manolodominguez.fleco.strategicconstraints.Constraint;
import com.manolodominguez.fleco.strategicconstraints.StrategicConstraints;
import com.manolodominguez.fleco.uleo.Categories;
import com.manolodominguez.fleco.uleo.FunctionalAreas;
import com.manolodominguez.fleco.uleo.Functions;
import com.manolodominguez.fleco.uleo.ImplementationGroups;
import java.util.EnumMap;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Table model used by FLECO Studio to present CyberTOMP metrics, current/target
 * statuses and strategic constraints.
 *
 * <p>
 * This model is backed by an {@link Chromosome} representing the initial status
 * and an optional {@link Chromosome} representing the target status. It also
 * uses {@link StrategicConstraints} to show and edit constraints.</p>
 *
 * <p>
 * <b>Threading</b>: Swing table models are expected to be accessed from the
 * Event Dispatch Thread. This class is mutable and not thread-safe; external
 * synchronization is required if accessed from multiple threads.</p>
 *
 * <p>
 * Public setters validate their inputs and will log an error before throwing an
 * {@link IllegalArgumentException} when a required parameter is invalid.</p>
 *
 * @author Manuel Domínguez-Dorado
 */
public class FLECOTableModel extends AbstractTableModel {

    /**
     * Column indices.
     */
    public static final int CYBERTOMP_METRIC_KEY = 0;
    public static final int CYBERTOMP_METRIC_ACRONYM = 1;
    public static final int CYBERTOMP_METRIC_NAME = 2;
    public static final int FUNCTIONAL_AREA = 3;
    public static final int CURRENT_STATUS = 4;
    public static final int CONSTRAINT_OPERATOR = 5;
    public static final int CONSTRAINT_VALUE = 6;
    public static final int TARGET_STATUS = 7;

    /**
     * Number of columns in the model.
     */
    private static final int MAX_COLUMNS = 8;

    /**
     * Row index reserved for the asset (top-level).
     */
    private static final int ASSET_ROW = 0;

    /**
     * Text used when no constraint is defined.
     */
    private static final String NO_CONSTRAINT = "N/A";

    /**
     * Serialization id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger for diagnostics.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FLECOTableModel.class);

    /**
     * Strategic constraints applied to the case. Guaranteed non-null after
     * construction.
     */
    private StrategicConstraints strategicConstraints;

    /**
     * Implementation group used to determine applicable
     * functions/categories/genes.
     */
    private ImplementationGroups implementationGroup;

    /**
     * Ordered list of metric keys (Asset, Function, Category, Gene names).
     */
    private String[] metricsKeys;

    /**
     * Initial status chromosome (may be null after removeInitialStatus).
     */
    private Chromosome initialStatus;

    /**
     * Cached computed values for initial status.
     */
    private EnumMap<Genes, Float> genesValuesInitialStatus;
    private EnumMap<Categories, Float> categoriesValuesInitialStatus;
    private EnumMap<Functions, Float> functionsValuesInitialStatus;
    private Float assetValueInitialStatus;

    /**
     * Optional target status chromosome.
     */
    private Chromosome targetStatus;

    /**
     * Cached computed values for target status.
     */
    private EnumMap<Genes, Float> genesValuesTargetStatus;
    private EnumMap<Categories, Float> categoriesValuesTargetStatus;
    private EnumMap<Functions, Float> functionsValuesTargetStatus;
    private Float assetValueTargetStatus;

    /**
     * Optional single listener notified when the model changes.
     */
    private IFLECOTableModelChangeListener changeEventListener;

    /**
     * Metric type used internally to simplify constraint/setting logic.
     */
    private enum MetricType {
        GENE, CATEGORY, FUNCTION, ASSET
    }

    /**
     * Constructs a new table model for the provided initial status and
     * strategic constraints.
     *
     * @param initialStatus the initial {@link Chromosome} (must not be
     * {@code null})
     * @param strategicConstraints the {@link StrategicConstraints} instance
     * (must not be {@code null})
     * @throws IllegalArgumentException if {@code initialStatus} or
     * {@code strategicConstraints} is {@code null}
     */
    public FLECOTableModel(final Chromosome initialStatus, final StrategicConstraints strategicConstraints) {
        if (initialStatus == null) {
            LOGGER.error("FLECOTableModel.<init>: initialStatus must not be null");
            throw new IllegalArgumentException("initialStatus must not be null");
        }
        if (strategicConstraints == null) {
            LOGGER.error("FLECOTableModel.<init>: strategicConstraints must not be null");
            throw new IllegalArgumentException("strategicConstraints must not be null");
        }

        this.initialStatus = initialStatus;
        this.strategicConstraints = strategicConstraints;
        this.implementationGroup = initialStatus.getImplementationGroup();
        this.targetStatus = null;
        this.changeEventListener = null;

        buildMetricsKeys();
        computeValuesForInitialStatus();
    }

    /**
     * Registers a single change listener for this model.
     *
     * <p>
     * Only one listener is allowed. Calling this method when a listener is
     * already registered will log and throw
     * {@link IllegalArgumentException}.</p>
     *
     * @param changeEventListener listener to register (must not be
     * {@code null})
     * @throws IllegalArgumentException if a listener is already registered or
     * {@code changeEventListener} is {@code null}
     */
    public void setChangeEventListener(final IFLECOTableModelChangeListener changeEventListener) {
        if (changeEventListener == null) {
            LOGGER.error("setChangeEventListener: provided listener is null");
            throw new IllegalArgumentException("changeEventListener must not be null");
        }
        if (this.changeEventListener != null) {
            LOGGER.error("setChangeEventListener: a listener has already been defined; only one is allowed");
            throw new IllegalArgumentException("A listener has already been defined for this FLECOTableModel. Only one is allowed.");
        }
        this.changeEventListener = changeEventListener;
    }

    /**
     * Sets the target status computed by FLECO and refreshes the corresponding
     * columns.
     *
     * @param targetStatus the target {@link Chromosome} (may be {@code null} to
     * clear)
     */
    public void setTargetStatus(final Chromosome targetStatus) {
        this.targetStatus = targetStatus;
        computeValuesForTargetStatus();
        fireTableColumnsUpdated(TARGET_STATUS);
        notifyChangeListenerIfPresent();
    }

    /**
     * Returns the current target status.
     *
     * @return the target {@link Chromosome} or {@code null} if none
     */
    public Chromosome getTargetStatus() {
        return targetStatus;
    }

    /**
     * Sets the strategic constraints used by the model and refreshes constraint
     * columns.
     *
     * @param strategicConstraints the {@link StrategicConstraints} instance
     * (must not be {@code null})
     * @throws IllegalArgumentException if {@code strategicConstraints} is
     * {@code null}
     */
    public void setStrategicConstraints(final StrategicConstraints strategicConstraints) {
        if (strategicConstraints == null) {
            LOGGER.error("setStrategicConstraints: strategicConstraints must not be null");
            throw new IllegalArgumentException("strategicConstraints must not be null");
        }
        this.strategicConstraints = strategicConstraints;
        fireTableColumnsUpdated(CONSTRAINT_OPERATOR, CONSTRAINT_VALUE);
        notifyChangeListenerIfPresent();
    }

    /**
     * Replaces the initial status chromosome and rebuilds the model structure
     * according to the new implementation group.
     *
     * @param initialStatus the new initial {@link Chromosome} (must not be
     * {@code null})
     * @throws IllegalArgumentException if {@code initialStatus} is {@code null}
     */
    public void setInitialStatus(final Chromosome initialStatus) {
        if (initialStatus == null) {
            LOGGER.error("setInitialStatus: initialStatus must not be null");
            throw new IllegalArgumentException("initialStatus must not be null");
        }
        this.initialStatus = initialStatus;
        this.implementationGroup = initialStatus.getImplementationGroup();
        buildMetricsKeys();
        computeValuesForInitialStatus();
        fireTableColumnsUpdated(CURRENT_STATUS);
        notifyChangeListenerIfPresent();
    }

    /**
     * Removes the target status (if any) and refreshes the model.
     */
    public void removeTargetStatus() {
        this.targetStatus = null;
        computeValuesForTargetStatus();
        fireTableColumnsUpdated(TARGET_STATUS);
        notifyChangeListenerIfPresent();
    }

    /**
     * Removes all strategic constraints from the underlying
     * {@link StrategicConstraints}.
     *
     * <p>
     * If no strategic constraints are present this method is a no-op.</p>
     */
    public void removeStrategicConstraints() {
        if (strategicConstraints != null) {
            strategicConstraints.removeAll();
            fireTableColumnsUpdated(CONSTRAINT_OPERATOR, CONSTRAINT_VALUE);
            notifyChangeListenerIfPresent();
        }
    }

    /**
     * Removes the initial status and clears the model.
     *
     * <p>
     * After calling this method {@link #getRowCount()} will return 0.</p>
     */
    public void removeInitialStatus() {
        this.initialStatus = null;
        this.metricsKeys = new String[0];
        computeValuesForInitialStatus();
        fireTableDataChanged();
        notifyChangeListenerIfPresent();
    }

    /**
     * Returns the implementation group used by this model.
     *
     * @return the {@link ImplementationGroups} instance
     */
    public ImplementationGroups getImplementationGroup() {
        return implementationGroup;
    }

    /**
     * Returns the strategic constraints used by this model.
     *
     * @return the {@link StrategicConstraints} instance
     */
    public StrategicConstraints getStrategicConstraints() {
        return strategicConstraints;
    }

    /**
     * Returns the initial status chromosome.
     *
     * @return the initial {@link Chromosome} or {@code null} if removed
     */
    public Chromosome getInitialStatus() {
        return initialStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {
        return MAX_COLUMNS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName(final int column) {
        switch (column) {
            case CYBERTOMP_METRIC_KEY:
                return "CyberTOMP® metric key";
            case CYBERTOMP_METRIC_ACRONYM:
                return "CyberTOMP® metric";
            case CYBERTOMP_METRIC_NAME:
                return "Purpose";
            case FUNCTIONAL_AREA:
                return "Leading functional area";
            case CURRENT_STATUS:
                return "Current status";
            case CONSTRAINT_OPERATOR:
                return "Constraint operator";
            case CONSTRAINT_VALUE:
                return "Constraint value";
            case TARGET_STATUS:
                return "Target status";
            default:
                return "Column name not defined";
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * All columns return {@link String} as their class in the current UI
     * usage.</p>
     */
    @Override
    public Class<?> getColumnClass(final int column) {
        return String.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowCount() {
        if (initialStatus != null && metricsKeys != null) {
            return metricsKeys.length;
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Only the CURRENT_STATUS and constraint columns are editable depending on
     * the metric type. Metric key, acronym, purpose, functional area and target
     * status are read-only.</p>
     */
    @Override
    public boolean isCellEditable(final int row, final int column) {
        if (initialStatus == null || metricsKeys == null) {
            return false;
        }
        if (column == CYBERTOMP_METRIC_KEY
                || column == CYBERTOMP_METRIC_ACRONYM
                || column == CYBERTOMP_METRIC_NAME
                || column == FUNCTIONAL_AREA
                || column == TARGET_STATUS) {
            return false;
        }
        if (column == CURRENT_STATUS) {
            for (Genes gene : Genes.getGenesFor(implementationGroup)) {
                if (gene.name().equals(metricsKeys[row])) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValueAt(final int row, final int column) {
        if (initialStatus == null || metricsKeys == null) {
            return null;
        }
        switch (column) {
            case CYBERTOMP_METRIC_KEY:
                return getCyberTOMPMetricKeyAt(row);
            case CYBERTOMP_METRIC_ACRONYM:
                return getCorrespondingMetricAcronymAt(row);
            case CYBERTOMP_METRIC_NAME:
                return getCorrespondingPurposeAt(row);
            case FUNCTIONAL_AREA:
                return getCorrespondingLeadingFunctionalAreaAt(row);
            case CURRENT_STATUS:
                return getInitialStatusAt(row);
            case CONSTRAINT_OPERATOR:
                return getConstraintOperatorAt(row);
            case CONSTRAINT_VALUE:
                return getConstraintValueAt(row);
            case TARGET_STATUS:
                return getTargetStatusAt(row);
            default:
                return null;
        }
    }

    /**
     * Returns the acronym for the metric at the given row.
     *
     * @param row row index
     * @return acronym string or "UNDEFINED" when unavailable
     */
    private Object getCorrespondingMetricAcronymAt(final int row) {
        if (initialStatus == null) {
            return "UNDEFINED";
        }
        final Object key = getCyberTOMPMetricKeyAt(row);
        if (!(key instanceof String)) {
            return "UNDEFINED";
        }
        final String keyStr = (String) key;
        try {
            final Genes gene = Genes.valueOf(keyStr);
            return "            " + gene.getAcronym();
        } catch (IllegalArgumentException e1) {
            try {
                final Categories category = Categories.valueOf(keyStr);
                return "        " + category.getAcronym();
            } catch (IllegalArgumentException e2) {
                try {
                    final Functions function = Functions.valueOf(keyStr);
                    return "    " + function.getAcronym();
                } catch (IllegalArgumentException e3) {
                    return "BUSINESS (SUB)ASSET";
                }
            }
        }
    }

    /**
     * Returns the purpose (name/description) for the metric at the given row.
     *
     * @param row row index
     * @return purpose string or "UNDEFINED" when unavailable
     */
    private Object getCorrespondingPurposeAt(final int row) {
        if (initialStatus == null) {
            return "UNDEFINED";
        }
        final Object key = getCyberTOMPMetricKeyAt(row);
        if (!(key instanceof String)) {
            return "UNDEFINED";
        }
        final String keyStr = (String) key;
        try {
            final Genes gene = Genes.valueOf(keyStr);
            return gene.getPurpose();
        } catch (IllegalArgumentException e1) {
            try {
                final Categories category = Categories.valueOf(keyStr);
                return category.getPurpose();
            } catch (IllegalArgumentException e2) {
                try {
                    final Functions function = Functions.valueOf(keyStr);
                    return function.getPurpose();
                } catch (IllegalArgumentException e3) {
                    return "---";
                }
            }
        }
    }

    /**
     * Returns the leading functional area for the metric at the given row.
     *
     * @param row row index
     * @return functional area name or "UNDEFINED" when unavailable
     */
    private Object getCorrespondingLeadingFunctionalAreaAt(final int row) {
        if (initialStatus == null) {
            return "UNDEFINED";
        }
        final Object key = getCyberTOMPMetricKeyAt(row);
        if (!(key instanceof String)) {
            return "UNDEFINED";
        }
        final String keyStr = (String) key;
        try {
            final Genes gene = Genes.valueOf(keyStr);
            return gene.getLeadingFunctionalArea().getAreaName();
        } catch (IllegalArgumentException e) {
            return FunctionalAreas.SEVERAL.getAreaName();
        }
    }

    /**
     * Returns the metric key stored for the given row.
     *
     * @param row row index
     * @return metric key string or {@code null}
     */
    private Object getCyberTOMPMetricKeyAt(final int row) {
        if (initialStatus == null || metricsKeys == null) {
            return null;
        }
        if (row < 0 || row >= metricsKeys.length) {
            return null;
        }
        return metricsKeys[row];
    }

    /**
     * No-op setter for metric key column (kept for API compatibility).
     *
     * @param value ignored
     * @param row ignored
     */
    private void setCyberTOMPMetricAt(final Object value, final int row) {
        // intentionally no-op to preserve API compatibility
    }

    /**
     * Returns the current (initial) status value for the metric at the given
     * row.
     *
     * @param row row index
     * @return numeric value or {@code null}
     */
    private Object getInitialStatusAt(final int row) {
        if (initialStatus == null || metricsKeys == null) {
            return null;
        }
        computeValuesForInitialStatus();
        final Object key = getCyberTOMPMetricKeyAt(row);
        if (!(key instanceof String)) {
            return null;
        }
        final String keyStr = (String) key;
        try {
            return genesValuesInitialStatus.get(Genes.valueOf(keyStr));
        } catch (IllegalArgumentException e1) {
            try {
                return categoriesValuesInitialStatus.get(Categories.valueOf(keyStr));
            } catch (IllegalArgumentException e2) {
                try {
                    return functionsValuesInitialStatus.get(Functions.valueOf(keyStr));
                } catch (IllegalArgumentException e3) {
                    return assetValueInitialStatus;
                }
            }
        }
    }

    /**
     * Returns the target status value for the metric at the given row.
     *
     * @param row row index
     * @return numeric value or {@code null}
     */
    private Object getTargetStatusAt(final int row) {
        if (targetStatus == null || metricsKeys == null) {
            return null;
        }
        computeValuesForTargetStatus();
        final Object key = getCyberTOMPMetricKeyAt(row);
        if (!(key instanceof String)) {
            return null;
        }
        final String keyStr = (String) key;
        try {
            return genesValuesTargetStatus.get(Genes.valueOf(keyStr));
        } catch (IllegalArgumentException e1) {
            try {
                return categoriesValuesTargetStatus.get(Categories.valueOf(keyStr));
            } catch (IllegalArgumentException e2) {
                try {
                    return functionsValuesTargetStatus.get(Functions.valueOf(keyStr));
                } catch (IllegalArgumentException e3) {
                    return assetValueTargetStatus;
                }
            }
        }
    }

    /**
     * Returns the constraint operator name for the metric at the given row.
     *
     * @param row row index
     * @return operator name or {@code null} when model is not initialized
     */
    private Object getConstraintOperatorAt(final int row) {
        if (initialStatus == null || metricsKeys == null) {
            return null;
        }
        final String key = metricsKeys[row];
        try {
            final Genes gene = Genes.valueOf(key);
            if (gene.appliesToIG(implementationGroup) && strategicConstraints.hasDefinedConstraint(gene)) {
                return strategicConstraints.getConstraint(gene).getComparisonOperator().name();
            }
        } catch (IllegalArgumentException e1) {
            try {
                final Categories category = Categories.valueOf(key);
                if (category.appliesToIG(implementationGroup) && strategicConstraints.hasDefinedConstraint(category)) {
                    return strategicConstraints.getConstraint(category).getComparisonOperator().name();
                }
            } catch (IllegalArgumentException e2) {
                try {
                    final Functions function = Functions.valueOf(key);
                    if (function.appliesToIG(implementationGroup) && strategicConstraints.hasDefinedConstraint(function)) {
                        return strategicConstraints.getConstraint(function).getComparisonOperator().name();
                    }
                } catch (IllegalArgumentException e3) {
                    if (row == ASSET_ROW && strategicConstraints.hasDefinedConstraint()) {
                        return strategicConstraints.getConstraint().getComparisonOperator().name();
                    }
                }
            }
        }
        return NO_CONSTRAINT;
    }

    /**
     * Returns the constraint threshold value for the metric at the given row.
     *
     * @param row row index
     * @return threshold value (Float) or {@code null} when model is not
     * initialized
     */
    public Object getConstraintValueAt(final int row) {
        if (initialStatus == null || metricsKeys == null) {
            return null;
        }
        final String key = metricsKeys[row];
        try {
            final Genes gene = Genes.valueOf(key);
            if (gene.appliesToIG(implementationGroup) && strategicConstraints.hasDefinedConstraint(gene)) {
                return strategicConstraints.getConstraint(gene).getThreshold();
            }
        } catch (IllegalArgumentException e1) {
            try {
                final Categories category = Categories.valueOf(key);
                if (category.appliesToIG(implementationGroup) && strategicConstraints.hasDefinedConstraint(category)) {
                    return strategicConstraints.getConstraint(category).getThreshold();
                }
            } catch (IllegalArgumentException e2) {
                try {
                    final Functions function = Functions.valueOf(key);
                    if (function.appliesToIG(implementationGroup) && strategicConstraints.hasDefinedConstraint(function)) {
                        return strategicConstraints.getConstraint(function).getThreshold();
                    }
                } catch (IllegalArgumentException e3) {
                    if (row == ASSET_ROW && strategicConstraints.hasDefinedConstraint()) {
                        return strategicConstraints.getConstraint().getThreshold();
                    }
                }
            }
        }
        return 0.0f;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Supports editing current status and constraints. After applying changes
     * the model notifies the registered listener (if any).</p>
     */
    @Override
    public void setValueAt(final Object value, final int row, final int column) {
        if (initialStatus == null || metricsKeys == null) {
            return;
        }
        switch (column) {
            case CYBERTOMP_METRIC_KEY:
                setCyberTOMPMetricAt(value, row);
                break;
            case CURRENT_STATUS:
                setInitialStatusAt(value, row);
                break;
            case CONSTRAINT_OPERATOR:
                setConstraintOperatorAt(value, row);
                break;
            case CONSTRAINT_VALUE:
                setConstraintValueAt(value, row);
                break;
            default:
                break;
        }
        notifyChangeListenerIfPresent();
    }

    /**
     * Sets the allele for the gene corresponding to the given row based on the
     * provided numeric value.
     *
     * <p>
     * If the provided value is not a {@link Float} the method is a no-op. Any
     * {@link IllegalArgumentException} (e.g. invalid gene name) is logged.</p>
     *
     * @param value expected to be a {@link Float} representing DLI
     * @param row row index
     */
    private void setInitialStatusAt(final Object value, final int row) {
        if (initialStatus == null || metricsKeys == null) {
            return;
        }
        try {
            final String key = metricsKeys[row];
            final Genes gene = Genes.valueOf(key);
            if (!(value instanceof Float)) {
                return;
            }
            final float valueFloat = (Float) value;
            Alleles allele = Alleles.DLI_0;
            if (Float.compare(valueFloat, Alleles.DLI_0.getDLI()) == 0) {
                allele = Alleles.DLI_0;
            } else if (Float.compare(valueFloat, Alleles.DLI_33.getDLI()) == 0) {
                allele = Alleles.DLI_33;
            } else if (Float.compare(valueFloat, Alleles.DLI_67.getDLI()) == 0) {
                allele = Alleles.DLI_67;
            } else if (Float.compare(valueFloat, Alleles.DLI_100.getDLI()) == 0) {
                allele = Alleles.DLI_100;
            }
            initialStatus.updateAllele(gene, allele);
            computeValuesForInitialStatus();
            // notify UI for all rows in current status column
            for (int j = 0; j < metricsKeys.length; j++) {
                fireTableCellUpdated(j, CURRENT_STATUS);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("setInitialStatusAt: invalid metric key at row {}", row, e);
        } catch (Exception e) {
            LOGGER.error("setInitialStatusAt: unexpected error at row {}", row, e);
        }
    }

    /**
     * Sets or removes the comparison operator for the metric at the given row.
     *
     * <p>
     * Accepted {@code value} is a {@link String} with either
     * {@link #NO_CONSTRAINT} or a {@link ComparisonOperators} name. The method
     * updates {@link StrategicConstraints} accordingly and refreshes constraint
     * columns.</p>
     *
     * @param value operator name or {@link #NO_CONSTRAINT}
     * @param row row index
     */
    private void setConstraintOperatorAt(final Object value, final int row) {
        if (initialStatus == null || metricsKeys == null) {
            return;
        }
        if (!(value instanceof String)) {
            return;
        }
        final String valueString = (String) value;
        final String key = metricsKeys[row];
        final MetricType type = determineMetricType(key);

        try {
            switch (type) {
                case GENE: {
                    if (NO_CONSTRAINT.equals(valueString)) {
                        strategicConstraints.removeConstraint(Genes.valueOf(key));
                    } else {
                        final ComparisonOperators comparisonOperator = ComparisonOperators.valueOf(valueString);
                        final Genes gene = Genes.valueOf(key);
                        final Constraint existing = strategicConstraints.hasDefinedConstraint(gene) ? strategicConstraints.getConstraint(gene) : null;
                        final float threshold = existing != null ? existing.getThreshold() : defaultThresholdForOperator(comparisonOperator);
                        final Constraint updated = new Constraint(comparisonOperator, threshold);
                        strategicConstraints.removeConstraint(gene);
                        strategicConstraints.addConstraint(gene, updated);
                    }
                    break;
                }
                case CATEGORY: {
                    if (NO_CONSTRAINT.equals(valueString)) {
                        strategicConstraints.removeConstraint(Categories.valueOf(key));
                    } else {
                        final ComparisonOperators comparisonOperator = ComparisonOperators.valueOf(valueString);
                        final Categories category = Categories.valueOf(key);
                        final Constraint existing = strategicConstraints.hasDefinedConstraint(category) ? strategicConstraints.getConstraint(category) : null;
                        final float threshold = existing != null ? existing.getThreshold() : defaultThresholdForOperator(comparisonOperator);
                        final Constraint updated = new Constraint(comparisonOperator, threshold);
                        strategicConstraints.removeConstraint(category);
                        strategicConstraints.addConstraint(category, updated);
                    }
                    break;
                }
                case FUNCTION: {
                    if (NO_CONSTRAINT.equals(valueString)) {
                        strategicConstraints.removeConstraint(Functions.valueOf(key));
                    } else {
                        final ComparisonOperators comparisonOperator = ComparisonOperators.valueOf(valueString);
                        final Functions function = Functions.valueOf(key);
                        final Constraint existing = strategicConstraints.hasDefinedConstraint(function) ? strategicConstraints.getConstraint(function) : null;
                        final float threshold = existing != null ? existing.getThreshold() : defaultThresholdForOperator(comparisonOperator);
                        final Constraint updated = new Constraint(comparisonOperator, threshold);
                        strategicConstraints.removeConstraint(function);
                        strategicConstraints.addConstraint(function, updated);
                    }
                    break;
                }
                case ASSET: {
                    if (NO_CONSTRAINT.equals(valueString)) {
                        strategicConstraints.removeConstraint();
                    } else {
                        final ComparisonOperators comparisonOperator = ComparisonOperators.valueOf(valueString);
                        final Constraint existing = strategicConstraints.hasDefinedConstraint() ? strategicConstraints.getConstraint() : null;
                        final float threshold = existing != null ? existing.getThreshold() : defaultThresholdForOperator(comparisonOperator);
                        final Constraint updated = new Constraint(comparisonOperator, threshold);
                        strategicConstraints.removeConstraint();
                        strategicConstraints.addConstraint(updated);
                    }
                    break;
                }
                default:
                    break;
            }
            fireTableColumnsUpdated(CONSTRAINT_OPERATOR, CONSTRAINT_VALUE);
        } catch (IllegalArgumentException e) {
            LOGGER.error("setConstraintOperatorAt: invalid operator or metric at row {} value='{}'", row, valueString, e);
        } catch (Exception e) {
            LOGGER.error("setConstraintOperatorAt: unexpected error at row {} value='{}'", row, valueString, e);
        }
    }

    /**
     * Sets the constraint threshold value for the metric at the given row.
     *
     * <p>
     * Accepted {@code value} types: {@link Float} or {@link String} parseable
     * as float. Values are clamped to [0.0, 1.0]. If the metric has no defined
     * constraint, this method will only update existing constraints (it will
     * not create new ones).</p>
     *
     * @param value numeric threshold or string representation
     * @param row row index
     */
    public void setConstraintValueAt(final Object value, final int row) {
        if (initialStatus == null || metricsKeys == null) {
            return;
        }
        float threshold = 0.0f;
        if (value instanceof Float) {
            threshold = (Float) value;
        } else if (value instanceof String) {
            try {
                threshold = Float.parseFloat((String) value);
            } catch (NumberFormatException ex) {
                LOGGER.error("setConstraintValueAt: provided value is not a float: '{}'", value);
                return;
            }
        } else {
            return;
        }
        // clamp
        if (threshold < 0.0f) {
            threshold = 0.0f;
        } else if (threshold > 1.0f) {
            threshold = 1.0f;
        }

        final String key = metricsKeys[row];
        final MetricType type = determineMetricType(key);

        try {
            switch (type) {
                case GENE: {
                    final Genes gene = Genes.valueOf(key);
                    if (strategicConstraints.hasDefinedConstraint(gene)) {
                        final Constraint existing = strategicConstraints.getConstraint(gene);
                        final Constraint updated = new Constraint(existing.getComparisonOperator(), threshold);
                        strategicConstraints.removeConstraint(gene);
                        strategicConstraints.addConstraint(gene, updated);
                    }
                    break;
                }
                case CATEGORY: {
                    final Categories category = Categories.valueOf(key);
                    if (strategicConstraints.hasDefinedConstraint(category)) {
                        final Constraint existing = strategicConstraints.getConstraint(category);
                        final Constraint updated = new Constraint(existing.getComparisonOperator(), threshold);
                        strategicConstraints.removeConstraint(category);
                        strategicConstraints.addConstraint(category, updated);
                    }
                    break;
                }
                case FUNCTION: {
                    final Functions function = Functions.valueOf(key);
                    if (strategicConstraints.hasDefinedConstraint(function)) {
                        final Constraint existing = strategicConstraints.getConstraint(function);
                        final Constraint updated = new Constraint(existing.getComparisonOperator(), threshold);
                        strategicConstraints.removeConstraint(function);
                        strategicConstraints.addConstraint(function, updated);
                    }
                    break;
                }
                case ASSET: {
                    if (strategicConstraints.hasDefinedConstraint()) {
                        final Constraint existing = strategicConstraints.getConstraint();
                        final Constraint updated = new Constraint(existing.getComparisonOperator(), threshold);
                        strategicConstraints.removeConstraint();
                        strategicConstraints.addConstraint(updated);
                    }
                    break;
                }
                default:
                    break;
            }
            fireTableColumnsUpdated(CONSTRAINT_VALUE);
        } catch (IllegalArgumentException e) {
            LOGGER.error("setConstraintValueAt: invalid metric at row {} value='{}'", row, value, e);
        } catch (Exception e) {
            LOGGER.error("setConstraintValueAt: unexpected error at row {} value='{}'", row, value, e);
        }
    }

    /**
     * Computes aggregated values for the initial status: per-gene,
     * per-category, per-function and asset-level values. Results are stored in
     * the corresponding cached maps/fields.
     */
    private void computeValuesForInitialStatus() {
        genesValuesInitialStatus = new EnumMap<>(Genes.class);
        categoriesValuesInitialStatus = new EnumMap<>(Categories.class);
        functionsValuesInitialStatus = new EnumMap<>(Functions.class);
        assetValueInitialStatus = 0.0f;

        if (initialStatus == null) {
            return;
        }

        float auxFunctionFitness;
        float auxCategoryFitness;

        for (Functions f : Functions.values()) {
            if (!f.appliesToIG(implementationGroup)) {
                continue;
            }
            auxFunctionFitness = 0.0f;
            for (Categories c : f.getCategories(implementationGroup)) {
                auxCategoryFitness = 0.0f;
                for (Genes g : c.getGenes(implementationGroup)) {
                    final float geneValue = initialStatus.getAllele(g).getDLI();
                    genesValuesInitialStatus.put(g, geneValue);
                    auxCategoryFitness += geneValue * g.getWeight(implementationGroup);
                }
                if (auxCategoryFitness >= 1.0f) {
                    auxCategoryFitness = 1.0f;
                }
                categoriesValuesInitialStatus.put(c, auxCategoryFitness);
                auxCategoryFitness *= c.getWeight(implementationGroup);
                if (auxCategoryFitness > c.getWeight(implementationGroup)) {
                    auxCategoryFitness = c.getWeight(implementationGroup);
                }
                auxFunctionFitness += auxCategoryFitness;
            }
            if (auxFunctionFitness >= 1.0f) {
                auxFunctionFitness = 1.0f;
            }
            functionsValuesInitialStatus.put(f, auxFunctionFitness);
            auxFunctionFitness *= f.getWeight(implementationGroup);
            if (auxFunctionFitness > f.getWeight(implementationGroup)) {
                auxFunctionFitness = f.getWeight(implementationGroup);
            }
            assetValueInitialStatus += auxFunctionFitness;
            if (assetValueInitialStatus >= 1.0f) {
                assetValueInitialStatus = 1.0f;
            }
        }
    }

    /**
     * Computes aggregated values for the target status: per-gene, per-category,
     * per-function and asset-level values. Results are stored in the
     * corresponding cached maps/fields.
     */
    private void computeValuesForTargetStatus() {
        genesValuesTargetStatus = new EnumMap<>(Genes.class);
        categoriesValuesTargetStatus = new EnumMap<>(Categories.class);
        functionsValuesTargetStatus = new EnumMap<>(Functions.class);
        assetValueTargetStatus = 0.0f;

        if (targetStatus == null) {
            return;
        }

        float auxFunctionFitness;
        float auxCategoryFitness;

        for (Functions f : Functions.values()) {
            if (!f.appliesToIG(implementationGroup)) {
                continue;
            }
            auxFunctionFitness = 0.0f;
            for (Categories c : f.getCategories(implementationGroup)) {
                auxCategoryFitness = 0.0f;
                for (Genes g : c.getGenes(implementationGroup)) {
                    final float geneValue = targetStatus.getAllele(g).getDLI();
                    genesValuesTargetStatus.put(g, geneValue);
                    auxCategoryFitness += geneValue * g.getWeight(implementationGroup);
                }
                if (auxCategoryFitness >= 1.0f) {
                    auxCategoryFitness = 1.0f;
                }
                categoriesValuesTargetStatus.put(c, auxCategoryFitness);
                auxCategoryFitness *= c.getWeight(implementationGroup);
                if (auxCategoryFitness > c.getWeight(implementationGroup)) {
                    auxCategoryFitness = c.getWeight(implementationGroup);
                }
                auxFunctionFitness += auxCategoryFitness;
            }
            if (auxFunctionFitness >= 1.0f) {
                auxFunctionFitness = 1.0f;
            }
            functionsValuesTargetStatus.put(f, auxFunctionFitness);
            auxFunctionFitness *= f.getWeight(implementationGroup);
            if (auxFunctionFitness > f.getWeight(implementationGroup)) {
                auxFunctionFitness = f.getWeight(implementationGroup);
            }
            assetValueTargetStatus += auxFunctionFitness;
            if (assetValueTargetStatus >= 1.0f) {
                assetValueTargetStatus = 1.0f;
            }
        }
    }

    /**
     * Builds the {@link #metricsKeys} array according to the current
     * {@link #implementationGroup}. The array contains "Asset" followed by
     * functions, categories and genes in hierarchical order.
     */
    private void buildMetricsKeys() {
        int rowCount = 1; // Asset row
        for (Functions function : Functions.getFunctionsFor(implementationGroup)) {
            rowCount++; // function
            for (Categories category : Categories.getCategoriesFor(function, implementationGroup)) {
                rowCount++; // category
                for (Genes gene : Genes.getGenesFor(category, implementationGroup)) {
                    rowCount++; // gene
                }
            }
        }
        this.metricsKeys = new String[rowCount];
        int count = 0;
        metricsKeys[count++] = "Asset";
        for (Functions function : Functions.getFunctionsFor(implementationGroup)) {
            metricsKeys[count++] = function.name();
            for (Categories category : Categories.getCategoriesFor(function, implementationGroup)) {
                metricsKeys[count++] = category.name();
                for (Genes gene : Genes.getGenesFor(category, implementationGroup)) {
                    metricsKeys[count++] = gene.name();
                }
            }
        }
    }

    /**
     * Determines the metric type (GENE, CATEGORY, FUNCTION, ASSET) for the
     * given key.
     *
     * @param key metric key string
     * @return {@link MetricType}
     */
    private MetricType determineMetricType(final String key) {
        try {
            Genes.valueOf(key);
            return MetricType.GENE;
        } catch (IllegalArgumentException e1) {
            try {
                Categories.valueOf(key);
                return MetricType.CATEGORY;
            } catch (IllegalArgumentException e2) {
                try {
                    Functions.valueOf(key);
                    return MetricType.FUNCTION;
                } catch (IllegalArgumentException e3) {
                    return MetricType.ASSET;
                }
            }
        }
    }

    /**
     * Returns a sensible default threshold for a newly created constraint based
     * on the comparison operator.
     *
     * @param op comparison operator
     * @return default threshold in range [0.0,1.0]
     */
    private float defaultThresholdForOperator(final ComparisonOperators op) {
        switch (op) {
            case LESS:
            case LESS_OR_EQUAL:
            case EQUAL:
                return 1.0f;
            case GREATER:
            case GREATER_OR_EQUAL:
            default:
                return 0.0f;
        }
    }

    /**
     * Fires {@link #fireTableCellUpdated} for all rows for the provided
     * columns.
     *
     * @param columns columns to update
     */
    private void fireTableColumnsUpdated(final int... columns) {
        if (metricsKeys == null) {
            return;
        }
        for (int j = 0; j < metricsKeys.length; j++) {
            for (int col : columns) {
                fireTableCellUpdated(j, col);
            }
        }
    }

    /**
     * Notifies the registered change listener if present.
     */
    private void notifyChangeListenerIfPresent() {
        if (changeEventListener != null) {
            try {
                changeEventListener.onFLECOTableModelChanged();
            } catch (Throwable t) {
                LOGGER.error("notifyChangeListenerIfPresent: listener threw an exception", t);
            }
        }
    }
}
