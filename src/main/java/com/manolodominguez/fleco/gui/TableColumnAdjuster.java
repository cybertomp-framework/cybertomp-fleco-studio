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

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility that adjusts {@link JTable} column widths based on header and cell
 * content. Designed to be used with tables configured with
 * {@code AUTO_RESIZE_OFF}, although it will operate with other modes as well.
 *
 * <p>
 * The adjuster can include/exclude header and/or data when computing widths,
 * optionally only enlarge columns (never shrink), and optionally react to model
 * changes dynamically.
 * </p>
 *
 * <p>
 * This implementation is conservative: it preserves the original public API and
 * behavior while adding defensive checks, compact logging and small readability
 * improvements.
 * </p>
 *
 * <p>
 * Compatibility: Java 11, no additional dependencies.</p>
 *
 * @author Rob Camick
 * @author Manuel Domínguez-Dorado
 */
public class TableColumnAdjuster implements PropertyChangeListener, TableModelListener {

    /**
     * Default extra spacing added to computed widths (pixels).
     */
    private static final int DEFAULT_SPACING = 6;

    /**
     * Default: include column header when computing width.
     */
    private static final boolean DEFAULT_COLUMN_HEADER_INCLUDED = true;

    /**
     * Default: include column data when computing width.
     */
    private static final boolean DEFAULT_COLUMN_DATA_INCLUDED = true;

    /**
     * Default: allow shrinking columns (false means allow shrink).
     */
    private static final boolean DEFAULT_IS_ONLY_ENLARGE_COLUMN = false;

    /**
     * Default: enable dynamic adjustment on model changes.
     */
    private static final boolean DEFAULT_DYNAMIC_ADJUSTMENT = true;

    /**
     * Backing table whose columns are managed. Never null.
     */
    private final JTable table;

    /**
     * Extra spacing added to computed widths.
     */
    private int spacing;

    /**
     * Whether header is included in width computation.
     */
    private boolean isColumnHeaderIncluded;

    /**
     * Whether data is included in width computation.
     */
    private boolean isColumnDataIncluded;

    /**
     * Whether columns should only be enlarged (never shrunk).
     */
    private boolean isOnlyEnlargeColumn;

    /**
     * Whether dynamic adjustment is enabled.
     */
    private boolean isDynamicAdjustment;

    /**
     * Stores previous widths for restore operations.
     */
    private final Map<TableColumn, Integer> columnSizes = new HashMap<>();

    /**
     * Logger for diagnostic messages.
     */
    private final Logger logger = LoggerFactory.getLogger(TableColumnAdjuster.class);

    /**
     * Constructs a TableColumnAdjuster for the provided table using the default
     * spacing.
     *
     * @param table the table whose columns will be managed; must not be null
     * @throws NullPointerException if {@code table} is null
     */
    public TableColumnAdjuster(final JTable table) {
        this(table, DEFAULT_SPACING);
    }

    /**
     * Constructs a TableColumnAdjuster for the provided table using the
     * specified spacing.
     *
     * @param table the table whose columns will be managed; must not be null
     * @param spacing extra spacing (pixels) to add to computed widths
     * @throws NullPointerException if {@code table} is null
     */
    public TableColumnAdjuster(final JTable table, final int spacing) {
        this.table = Objects.requireNonNull(table, "table must not be null");
        this.spacing = spacing;
        // Initialize preferred widths defensively: if column model is available set to current width
        final TableColumnModel columnModel = this.table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            try {
                columnModel.getColumn(i).setPreferredWidth(0);
            } catch (Exception ex) {
                logger.debug("Failed to set preferred width for column {}: {}", i, ex.getMessage());
            }
        }
        setColumnHeaderIncluded(DEFAULT_COLUMN_HEADER_INCLUDED);
        setColumnDataIncluded(DEFAULT_COLUMN_DATA_INCLUDED);
        setOnlyEnlargeColumn(DEFAULT_IS_ONLY_ENLARGE_COLUMN);
        setDynamicAdjustment(DEFAULT_DYNAMIC_ADJUSTMENT);
    }

    /**
     * Adjusts every column in the table by computing an appropriate width and
     * applying it.
     */
    public final void adjustColumns() {
        final TableColumnModel tableColumnModel = table.getColumnModel();
        for (int i = 0; i < tableColumnModel.getColumnCount(); i++) {
            adjustColumn(i);
        }
    }

    /**
     * Adjusts the width of the column at the given view index.
     *
     * @param column the view index of the column to adjust
     */
    public final void adjustColumn(final int column) {
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (!tableColumn.getResizable()) {
            return;
        }
        final int columnHeaderWidth = getColumnHeaderWidth(column);
        final int columnDataWidth = getColumnDataWidth(column);
        final int preferredWidth = Math.max(columnHeaderWidth, columnDataWidth);
        updateTableColumn(column, preferredWidth);
    }

    /**
     * Computes the width required to render the column header at the given view
     * index.
     *
     * @param column the view index of the column header
     * @return the preferred width in pixels (0 if header excluded)
     */
    private int getColumnHeaderWidth(final int column) {
        if (!isColumnHeaderIncluded) {
            return 0;
        }
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);
        final Object value = tableColumn.getHeaderValue();
        TableCellRenderer renderer = tableColumn.getHeaderRenderer();
        if (renderer == null && table.getTableHeader() != null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }
        if (renderer == null) {
            return 0;
        }
        final Component component = renderer.getTableCellRendererComponent(table, value, false, false, -1, column);
        return component == null ? 0 : component.getPreferredSize().width;
    }

    /**
     * Computes the width required to render the data cells of the column at the
     * given view index.
     *
     * @param column the view index of the column
     * @return the preferred width in pixels (0 if data excluded)
     */
    private int getColumnDataWidth(final int column) {
        if (!isColumnDataIncluded) {
            return 0;
        }
        int preferredWidth = 0;
        final TableColumn columnObj = table.getColumnModel().getColumn(column);
        final int maxWidth = columnObj.getMaxWidth();
        final int rowCount = table.getRowCount();
        for (int row = 0; row < rowCount; row++) {
            preferredWidth = Math.max(preferredWidth, getCellDataWidth(row, column));
            if (preferredWidth >= maxWidth) {
                break;
            }
        }
        return preferredWidth;
    }

    /**
     * Computes the width required to render a specific cell.
     *
     * @param row the row index
     * @param column the view column index
     * @return the preferred width in pixels for the cell (including intercell
     * spacing)
     */
    private int getCellDataWidth(final int row, final int column) {
        try {
            final TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
            final Component component = table.prepareRenderer(cellRenderer, row, column);
            final int width = component.getPreferredSize().width + table.getIntercellSpacing().width;
            return width;
        } catch (Exception ex) {
            // Defensive: if a renderer misbehaves, log and return a conservative width
            logger.debug("Error computing cell width at row {}, col {}: {}", row, column, ex.getMessage());
            return 0;
        }
    }

    /**
     * Updates the width of the column at the given view index applying spacing
     * and respecting the {@code isOnlyEnlargeColumn} policy.
     *
     * @param column the view index of the column to update
     * @param width the computed width (without spacing)
     */
    private void updateTableColumn(final int column, int width) {
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (!tableColumn.getResizable()) {
            return;
        }
        width += spacing;
        if (isOnlyEnlargeColumn) {
            width = Math.max(width, tableColumn.getPreferredWidth());
        }
        columnSizes.put(tableColumn, tableColumn.getWidth());
        try {
            table.getTableHeader().setResizingColumn(tableColumn);
        } catch (Exception ex) {
            // Some LAFs or states may not have a table header; ignore safely
            logger.debug("Unable to set resizing column: {}", ex.getMessage());
        }
        tableColumn.setWidth(width);
    }

    /**
     * Restores every column to the previously stored width (if available).
     */
    public final void restoreColumns() {
        final TableColumnModel tableColumnModel = table.getColumnModel();
        for (int i = 0; i < tableColumnModel.getColumnCount(); i++) {
            restoreColumn(i);
        }
    }

    /**
     * Restores the width of the column at the given view index to its previous
     * value if it was recorded.
     *
     * @param column the view index of the column to restore
     */
    private void restoreColumn(final int column) {
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);
        final Integer width = columnSizes.get(tableColumn);
        if (width != null) {
            try {
                table.getTableHeader().setResizingColumn(tableColumn);
            } catch (Exception ex) {
                logger.debug("Unable to set resizing column for restore: {}", ex.getMessage());
            }
            tableColumn.setWidth(width);
        }
    }

    /**
     * Sets whether the column header is included when computing widths.
     *
     * @param isColumnHeaderIncluded true to include header width, false
     * otherwise
     */
    public final void setColumnHeaderIncluded(final boolean isColumnHeaderIncluded) {
        this.isColumnHeaderIncluded = isColumnHeaderIncluded;
    }

    /**
     * Sets whether column data is included when computing widths.
     *
     * @param isColumnDataIncluded true to include data width, false otherwise
     */
    public final void setColumnDataIncluded(final boolean isColumnDataIncluded) {
        this.isColumnDataIncluded = isColumnDataIncluded;
    }

    /**
     * Sets whether columns should only be enlarged (never shrunk).
     *
     * @param isOnlyEnlargeColumn true to only enlarge columns, false to allow
     * shrink
     */
    public final void setOnlyEnlargeColumn(final boolean isOnlyEnlargeColumn) {
        this.isOnlyEnlargeColumn = isOnlyEnlargeColumn;
    }

    /**
     * Enables or disables dynamic adjustment. When enabled, the adjuster will
     * listen to table property changes and model events to recalculate widths.
     *
     * @param isDynamicAdjustment true to enable dynamic adjustment, false to
     * disable
     */
    public final void setDynamicAdjustment(final boolean isDynamicAdjustment) {
        if (this.isDynamicAdjustment == isDynamicAdjustment) {
            return;
        }
        // Toggle listeners safely, guarding against null model
        if (isDynamicAdjustment) {
            table.addPropertyChangeListener(this);
            final TableModel model = table.getModel();
            if (model != null) {
                model.addTableModelListener(this);
            } else {
                logger.debug("setDynamicAdjustment: table model is null when enabling dynamic adjustment");
            }
        } else {
            try {
                table.removePropertyChangeListener(this);
            } catch (Exception ex) {
                logger.debug("Error removing property change listener: {}", ex.getMessage());
            }
            final TableModel model = table.getModel();
            if (model != null) {
                model.removeTableModelListener(this);
            }
        }
        this.isDynamicAdjustment = isDynamicAdjustment;
    }

    /**
     * Called when a bound property changes on the table. Specifically reacts to
     * changes of the "model" property to update listeners and recalculate
     * column widths.
     *
     * @param e the property change event
     */
    @Override
    public final void propertyChange(final PropertyChangeEvent e) {
        if (!"model".equals(e.getPropertyName())) {
            return;
        }
        final Object oldVal = e.getOldValue();
        final Object newVal = e.getNewValue();
        if (oldVal instanceof TableModel) {
            try {
                ((TableModel) oldVal).removeTableModelListener(this);
            } catch (Exception ex) {
                logger.debug("Error removing old table model listener: {}", ex.getMessage());
            }
        }
        if (newVal instanceof TableModel) {
            try {
                ((TableModel) newVal).addTableModelListener(this);
            } catch (Exception ex) {
                logger.debug("Error adding new table model listener: {}", ex.getMessage());
            }
        }
        adjustColumns();
    }

    /**
     * Called when the table model changes. If data inclusion is disabled this
     * method returns immediately. Otherwise it schedules a column adjustment on
     * the EDT.
     *
     * <p>
     * Behavior mirrors the original implementation: when a single column is
     * updated it adjusts that column (or only the changed row if
     * {@code isOnlyEnlargeColumn} is set), otherwise it adjusts all
     * columns.</p>
     *
     * @param e the table model event describing the change
     */
    @Override
    public final void tableChanged(final TableModelEvent e) {
        if (!isColumnDataIncluded) {
            return;
        }
        // Schedule on EDT to avoid concurrency issues with Swing components
        SwingUtilities.invokeLater(() -> {
            if (e == null) {
                adjustColumns();
                return;
            }
            final int eventType = e.getType();
            final int modelColumn = e.getColumn();
            final int viewColumn = (modelColumn == TableModelEvent.ALL_COLUMNS) ? -1 : table.convertColumnIndexToView(modelColumn);

            if (eventType == TableModelEvent.UPDATE && viewColumn != -1) {
                if (isOnlyEnlargeColumn) {
                    final int row = e.getFirstRow();
                    if (row >= 0 && row < table.getRowCount()) {
                        final TableColumn tableColumn = table.getColumnModel().getColumn(viewColumn);
                        if (tableColumn.getResizable()) {
                            final int width = getCellDataWidth(row, viewColumn);
                            updateTableColumn(viewColumn, width);
                        }
                    } else {
                        // If row is invalid, fallback to adjusting the whole column
                        adjustColumn(viewColumn);
                    }
                } else {
                    adjustColumn(viewColumn);
                }
            } else {
                // For inserts, deletes or structural changes, adjust all columns
                adjustColumns();
            }
        });
    }
}
