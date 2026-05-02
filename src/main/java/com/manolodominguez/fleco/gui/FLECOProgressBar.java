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
import com.manolodominguez.fleco.events.IFLECOProgressEventListener;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Swing progress bar that listens to {@link ProgressEvent} notifications
 * emitted by the FLECO algorithm and updates its value accordingly.
 *
 * <p>
 * This component expects {@link ProgressEvent#getProgressPercentage()} to
 * return a double value in the range [0.0, 1.0]. Received values are validated,
 * clamped to the valid range and converted to an integer percentage (0-100)
 * before updating the UI.</p>
 *
 * <p>
 * <b>Threading</b>: UI updates are always performed on the Swing Event Dispatch
 * Thread (EDT). If an event arrives on a non-EDT thread the update is scheduled
 * via {@link SwingUtilities#invokeLater(Runnable)}.</p>
 *
 * <p>
 * Null or invalid events are rejected: the listener logs the problem and throws
 * an appropriate runtime exception to make failures visible to callers.</p>
 *
 * <p>
 * Instances are mutable and not thread-safe for concurrent mutation; however,
 * event handling ensures safe UI updates.</p>
 *
 * @author Manuel Domínguez-Dorado
 */
public class FLECOProgressBar extends JProgressBar implements IFLECOProgressEventListener {

    /**
     * Serialization identifier for this Swing component.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger for diagnostics and validation failures.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FLECOProgressBar.class);

    /**
     * Creates a new {@code FLECOProgressBar} with default configuration.
     *
     * <p>
     * The progress value range is 0..100 as per {@link JProgressBar} default
     * model.</p>
     */
    public FLECOProgressBar() {
        super();
    }

    /**
     * Called when a {@link ProgressEvent} is received from the FLECO algorithm.
     *
     * <p>
     * The method validates the incoming event, clamps the progress percentage
     * to the [0.0, 1.0] range, converts it to an integer percentage (0-100) and
     * updates the progress bar value on the Swing Event Dispatch Thread.</p>
     *
     * <p>
     * Validation policy:
     * <ul>
     * <li>If {@code progressEvent} is {@code null} a
     * {@link NullPointerException} is thrown.</li>
     * <li>If the reported percentage is {@code NaN} or infinite it is treated
     * as 0.0 and a warning is logged.</li>
     * </ul>
     * </p>
     *
     * @param progressEvent the progress event sent by an instance of FLECO
     * algorithm
     * @throws NullPointerException if {@code progressEvent} is {@code null}
     */
    @Override
    public void onProgressEventReceived(final ProgressEvent progressEvent) {
        if (progressEvent == null) {
            LOGGER.error("onProgressEventReceived: progressEvent cannot be null");
            throw new NullPointerException("progressEvent cannot be null");
        }

        double pct = progressEvent.getProgressPercentage();

        if (Double.isNaN(pct) || Double.isInfinite(pct)) {
            LOGGER.warn("onProgressEventReceived: received invalid progress percentage (NaN/Infinite). Treating as 0.0");
            pct = 0.0;
        }

        if (pct < 0.0) {
            LOGGER.debug("onProgressEventReceived: progress percentage {} below 0.0, clamping to 0.0", pct);
            pct = 0.0;
        } else if (pct > 1.0) {
            LOGGER.debug("onProgressEventReceived: progress percentage {} above 1.0, clamping to 1.0", pct);
            pct = 1.0;
        }

        final int value = (int) Math.round(pct * 100.0);

        if (SwingUtilities.isEventDispatchThread()) {
            setValue(value);
        } else {
            SwingUtilities.invokeLater(() -> setValue(value));
        }
    }
}
