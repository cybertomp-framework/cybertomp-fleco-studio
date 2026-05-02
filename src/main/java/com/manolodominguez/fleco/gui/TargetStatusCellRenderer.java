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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cell renderer for the "Target status" column in the FLECO table.
 *
 * <p>
 * This renderer highlights target values that differ from the current status: -
 * If the target value differs from the current value the text is shown in
 * <b>bold</b> and colored {@link Color#RED} (or {@link Color#YELLOW} when the
 * row is selected). - If the values are equal the text is shown in plain style
 * and colored {@link Color#BLACK} (or {@link Color#WHITE} when selected).
 * </p>
 *
 * <p>
 * The implementation is defensive: it tolerates unexpected input types and
 * missing table/model values by logging a compact diagnostic and falling back
 * to the default rendering behavior. This avoids throwing exceptions from the
 * renderer which would break Swing painting.
 * </p>
 *
 * <p>
 * Compatibility: Java 11.</p>
 *
 * @author Manuel Domínguez-Dorado
 */
public class TargetStatusCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    /**
     * Logger for compact diagnostics.
     */
    private static final Logger logger = LoggerFactory.getLogger(TargetStatusCellRenderer.class);

    /**
     * Creates a new TargetStatusCellRenderer instance.
     */
    public TargetStatusCellRenderer() {
        super();
    }

    /**
     * Returns the component used for drawing the cell. This implementation
     * compares the target value (the {@code value} parameter) with the current
     * status value stored in the table model at column
     * {@link FLECOTableModel#CURRENT_STATUS} and adjusts font and foreground
     * color accordingly.
     *
     * <p>
     * Defensive behavior:
     * <ul>
     * <li>If {@code table} is null the method logs an error and returns the
     * default renderer component without modifications.</li>
     * <li>If the model value or the provided {@code value} cannot be
     * interpreted as a numeric value, the method logs a debug message and falls
     * back to default rendering.</li>
     * </ul>
     * </p>
     *
     * @param table the {@link JTable} that uses this renderer; may be null in
     * erroneous calls (handled defensively)
     * @param value the value to assign to the cell (expected to be a
     * {@link Float} or other {@link Number})
     * @param isSelected true if the cell is selected
     * @param hasFocus true if the cell has focus
     * @param row the row index of the cell being rendered (view index)
     * @param column the column index of the cell being rendered (view index)
     * @return the component used for rendering the cell
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value,
            final boolean isSelected, final boolean hasFocus, final int row, final int column) {

        // Defensive: ensure table is available
        if (table == null) {
            logger.error("getTableCellRendererComponent called with null table");
            return super.getTableCellRendererComponent(null, value, isSelected, hasFocus, row, column);
        }

        // Obtain default rendering first
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // If value is null, keep default rendering (no text)
        if (value == null) {
            setText(null);
            return this;
        }

        // Try to interpret target value as a float
        final Float targetValue = toFloat(value);
        if (targetValue == null) {
            logger.debug("TargetStatusCellRenderer: value at row {}, col {} is not numeric: {}", row, column, value);
            // Fallback: show string representation
            setText(String.valueOf(value));
            return this;
        }

        // Try to read current status from model safely
        Float initialValue = null;
        try {
            final Object modelValue = table.getModel().getValueAt(row, FLECOTableModel.CURRENT_STATUS);
            initialValue = toFloat(modelValue);
            if (initialValue == null) {
                logger.debug("TargetStatusCellRenderer: current status at row {} is not numeric: {}", row, modelValue);
            }
        } catch (Exception ex) {
            // Defensive: log and continue with null initialValue
            logger.debug("TargetStatusCellRenderer: error reading current status at row {}: {}", row, ex.getMessage());
        }

        // Compare values: if initialValue is null treat as equal (no highlight)
        final boolean differs = (initialValue != null) && (Float.compare(initialValue.floatValue(), targetValue.floatValue()) != 0);

        // Prepare font: only change if style differs to avoid unnecessary repaints
        final Font baseFont = getFont();
        final int baseStyle = (baseFont == null) ? Font.PLAIN : baseFont.getStyle();
        final int desiredStyle = differs ? (baseStyle | Font.BOLD) : (baseStyle & ~Font.BOLD);
        if (baseFont == null || baseFont.getStyle() != desiredStyle) {
            setFont((baseFont == null) ? new Font(Font.SANS_SERIF, desiredStyle, 12) : baseFont.deriveFont(desiredStyle));
        }

        // Foreground color depending on selection and difference
        if (differs) {
            setForeground(isSelected ? Color.YELLOW : Color.RED);
        } else {
            setForeground(isSelected ? Color.WHITE : Color.BLACK);
        }

        // Set textual representation consistently (use Float.toString for numeric values)
        setText(targetValue.toString());

        return this;
    }

    /**
     * Attempts to convert an arbitrary object to a {@link Float}.
     *
     * <p>
     * Supported inputs:
     * <ul>
     * <li>{@link Float}</li>
     * <li>{@link Double}</li>
     * <li>{@link Number} (uses {@link Number#floatValue})</li>
     * <li>{@link String} parsable as a float</li>
     * </ul>
     * If conversion fails this method returns {@code null}.
     * </p>
     *
     * @param obj the object to convert
     * @return a {@link Float} instance or {@code null} if conversion is not
     * possible
     */
    private static Float toFloat(final Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Float) {
            return (Float) obj;
        }
        if (obj instanceof Number) {
            return Float.valueOf(((Number) obj).floatValue());
        }
        if (obj instanceof String) {
            final String s = ((String) obj).trim();
            if (s.isEmpty()) {
                return null;
            }
            try {
                return Float.valueOf(Float.parseFloat(s));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }
}
