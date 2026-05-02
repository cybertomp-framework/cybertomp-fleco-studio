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
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a worker that executes FLECO algorithm from a swing GUI
 * without freezing it.
 *
 * @author Manuel Domínguez-Dorado
 */
public class FLECOSwingWorker extends SwingWorker<FLECO, FLECO> {

    private FLECO fleco;
    private IFLECOGUI gui;

    private final Logger logger = LoggerFactory.getLogger(FLECOSwingWorker.class);
    
    /**
     * This is the constructor of the class. It creates a new instance and
     * initialize its attributes with their default values.
     *
     * @param fleco The instance of FLECO algorithm to be run in background.
     * @param gui The GUI from which FLECO is launched.
     */
    public FLECOSwingWorker(FLECO fleco, IFLECOGUI gui) {
        this.fleco = fleco;
        this.gui = gui;
    }

    /**
     * This method executes the FLECO algorithm in background.
     *
     * @return The executed instance of the FLECO algorithm.
     * @throws Exception when something uncontrolled happens while computing in
     * background.
     */
    @Override
    protected FLECO doInBackground() throws Exception {
        if (!isCancelled()) {
            fleco.evolve();
        }
        return fleco;
    }

    /**
     * This method is called when the background execution is finished. It calls
     * a methoid of the GUI in order to update the corresponding components if
     * needed.
     */
    @Override
    protected void done() {
        gui.afterOnRunFLECO();
    }

}
