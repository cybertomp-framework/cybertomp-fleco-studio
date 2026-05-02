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

import java.util.EventListener;

/**
 * GUI callback contract used by FLECO-related Swing workers and components.
 *
 * <p>
 * Implementations of this interface receive a single lifecycle callback invoked
 * by the UI layer once a FLECO execution has finished. The method is intended
 * to be executed on the Swing Event Dispatch Thread (EDT) — callers should
 * ensure they invoke it on the EDT when updating Swing components.</p>
 *
 * <p>
 * Only one method is required which makes this interface suitable for use with
 * lambda expressions where convenient.</p>
 *
 * <p>
 * <b>Threading</b>: callers must ensure {@link #afterOnRunFLECO()} is invoked
 * on the EDT if the implementation performs Swing updates. The interface itself
 * does not impose threading semantics.</p>
 *
 * @author Manuel Domínguez Dorado
 */
@FunctionalInterface
public interface IFLECOGUI extends EventListener {

    /**
     * Called by the UI or worker once a FLECO run has completed.
     *
     * <p>
     * Implementations should perform any UI updates or follow-up actions
     * required after FLECO finishes. Because most implementations will update
     * Swing components, this method is expected to run on the Swing Event
     * Dispatch Thread (EDT). If it is invoked from a background thread, the
     * implementation should re-dispatch work to the EDT using
     * {@code SwingUtilities.invokeLater(...)} or equivalent.</p>
     */
    void afterOnRunFLECO();
}
