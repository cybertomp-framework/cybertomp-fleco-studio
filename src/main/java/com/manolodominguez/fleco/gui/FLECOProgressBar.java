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

import com.manolodominguez.fleco.events.ProgressEvent;
import javax.swing.JProgressBar;
import com.manolodominguez.fleco.events.IFLECOProgressEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a progress bar that also is able to receive
 * progressevents from an instance of FLECO algorithm.
 *
 * @author Manuel Domínguez-Dorado
 */
public class FLECOProgressBar extends JProgressBar implements IFLECOProgressEventListener {

    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(FLECOProgressBar.class);
    
    /**
     * This is the constructor of the class. it creates a new instance of
     * FLECOProgressBar and initialize its attributes.
     *
     * @author Manuel Domínguez-Dorado
     */
    public FLECOProgressBar() {
        super();
    }

    /**
     * This class is called when an instance of FLECO algorithm this progress
     * bar is subscribed to, send a progress event.
     *
     * @param progressEvent the progress event sent by amn instance of FLECO
     * algorithm.
     * @author Manuel Domínguez-Dorado
     */
    @Override
    public void onProgressEventReceived(ProgressEvent progressEvent) {
        setValue((int) (progressEvent.getProgressPercentage() * 100));
    }

}
