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
 * Listener contract for clients that want to be notified when a
 * {@code FLECOTableModel} has changed.
 *
 * <p>
 * Implementations receive a single callback {@link #onFLECOTableModelChanged()}
 * which is invoked by the table model after any change that should cause the UI
 * or other components to refresh. The callback is expected to be executed on
 * the Swing Event Dispatch Thread (EDT) by the caller; if an implementation
 * performs Swing updates and is invoked off the EDT it should re-dispatch work
 * to the EDT (for example using {@code SwingUtilities.invokeLater(...)}) to
 * avoid threading issues.</p>
 *
 * <p>
 * Only one method is required which makes this interface suitable for use with
 * lambda expressions where convenient.</p>
 *
 * @author Manuel Domínguez Dorado
 */
@FunctionalInterface
public interface IFLECOTableModelChangeListener extends EventListener {

    /**
     * Invoked by {@code FLECOTableModel} to advertise that at least one value
     * has changed and interested parties should refresh their view or state.
     *
     * <p>
     * Implementations should be resilient to repeated calls and should avoid
     * performing long-running work on the calling thread. Exceptions thrown by
     * implementations should be handled by the caller to avoid disrupting the
     * model's control flow.</p>
     */
    void onFLECOTableModelChanged();
}
