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
 * This class implements a specific trable cell renderer used for those cells
 * that corresponds to values of the target status.
 *
 * @author Manuel Domínguez-Dorado
 */
public class TargetStatusCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(TargetStatusCellRenderer.class);

    /**
     * This is the constructor of the class. It sets the initial values of all
     * attributes and create a new instance.
     *
     * @author Manuel Domínguez-Dorado
     */
    public TargetStatusCellRenderer() {
        super();
    }

    /**
     * This method gets the rendered component that hasto be showed in a table
     * model when applicable. It compares the values of target status and
     * initial status, for all metrics and highlights the target status ones
     * when they differ from the initial status ones.
     *
     * @author Manuel Domínguez-Dorado
     * @param table The JTable this method is rendering cells for.
     * @param value the value in the corresponding cell (row and column).
     * @param isSelected true, if the correspondig cell (row, column) is
     * selected. Otherwise, false.
     * @param hasFocus true, if the correspondig cell (row, column) has the
     * focus. Otherwise, false.
     * @param row The row that determines the cell being rendered.
     * @param column The column that determines the cell being rendered.
     * @return The cell renderer.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value != null) {
            String val = null;
            Float initialValue = (Float) table.getModel().getValueAt(row, FLECOTableModel.CURRENT_STATUS);
            Float targetValue = (Float) value;
            if (initialValue.floatValue() != targetValue.floatValue()) {
                Font f = getFont();
                setFont(f.deriveFont(f.getStyle() | Font.BOLD));
                if (isSelected) {
                    this.setForeground(Color.YELLOW);
                } else {
                    this.setForeground(Color.RED);
                }
            } else {
                Font f = getFont();
                setFont(f.deriveFont(f.getStyle() | ~Font.BOLD));
                if (isSelected) {
                    this.setForeground(Color.WHITE);
                } else {
                    this.setForeground(Color.BLACK);
                }
            }
            val = ((Float) value).toString();
            setText(val);
        }
        return this;
    }
}
