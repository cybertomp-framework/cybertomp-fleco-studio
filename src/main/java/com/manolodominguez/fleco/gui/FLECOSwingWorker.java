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
 * Swing {@link SwingWorker} that executes a {@link FLECO} algorithm instance in
 * a background thread to avoid freezing the GUI.
 *
 * <p>
 * This worker runs {@link FLECO#evolve()} in {@link #doInBackground()} and,
 * when finished (regardless of success, cancellation or failure), invokes
 * {@link IFLECOGUI#afterOnRunFLECO()} on the configured GUI from the EDT via
 * {@link SwingWorker#done()} (Swing guarantees {@code done()} runs on the
 * EDT).</p>
 *
 * <p>
 * <b>Validation</b>: constructor parameters are validated. If {@code fleco} or
 * {@code gui} is {@code null} an {@link IllegalArgumentException} is logged and
 * thrown.</p>
 *
 * <p>
 * <b>Threading</b>: the worker executes {@code FLECO.evolve()} off the EDT. The
 * {@code done()} callback is executed on the EDT as per SwingWorker
 * contract.</p>
 *
 * <p>
 * Instances are single-use: a {@code SwingWorker} must not be reused after
 * execution.</p>
 *
 * @author Manuel Domínguez-Dorado
 */
public final class FLECOSwingWorker extends SwingWorker<FLECO, FLECO> {

    /**
     * Logger for diagnostics and validation failures.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FLECOSwingWorker.class);

    /**
     * The FLECO algorithm instance to execute in background. Guaranteed
     * non-null after construction.
     */
    private final FLECO fleco;

    /**
     * GUI callback used to update UI after the worker finishes. Guaranteed
     * non-null after construction.
     */
    private final IFLECOGUI gui;

    /**
     * Creates a new worker that will execute the provided {@code fleco}
     * instance and notify the provided {@code gui} when finished.
     *
     * @param fleco the {@link FLECO} instance to run in background (must not be
     * {@code null})
     * @param gui the GUI callback used to update UI after execution (must not
     * be {@code null})
     * @throws IllegalArgumentException if {@code fleco} or {@code gui} is
     * {@code null}
     */
    public FLECOSwingWorker(final FLECO fleco, final IFLECOGUI gui) {
        if (fleco == null) {
            LOGGER.error("FLECOSwingWorker.<init>: fleco must not be null");
            throw new IllegalArgumentException("fleco must not be null");
        }
        if (gui == null) {
            LOGGER.error("FLECOSwingWorker.<init>: gui must not be null");
            throw new IllegalArgumentException("gui must not be null");
        }
        this.fleco = fleco;
        this.gui = gui;
    }

    /**
     * Executes {@link FLECO#evolve()} in a background thread.
     *
     * <p>
     * If the worker is cancelled before or during execution, the method returns
     * the {@link FLECO} instance without attempting further work. Any
     * {@link Throwable} thrown by {@code fleco.evolve()} is logged and rethrown
     * so SwingWorker's exception handling can surface it to callers of
     * {@link #get()}.</p>
     *
     * @return the executed {@link FLECO} instance
     * @throws Exception if an unexpected error occurs during execution
     */
    @Override
    protected FLECO doInBackground() throws Exception {
        if (isCancelled()) {
            LOGGER.debug("doInBackground: task was cancelled before start");
            return fleco;
        }

        try {
            fleco.evolve();
        } catch (Throwable t) {
            LOGGER.error("doInBackground: unexpected error while evolving FLECO instance", t);
            // Rethrow to allow SwingWorker to propagate the exception to callers of get()
            if (t instanceof Exception) {
                throw (Exception) t;
            }
            throw new Exception("Unexpected error while evolving FLECO", t);
        }
        return fleco;
    }

    /**
     * Called on the Event Dispatch Thread when background execution finishes.
     *
     * <p>
     * This implementation always invokes {@link IFLECOGUI#afterOnRunFLECO()} to
     * allow the GUI to update its state. Any runtime exception thrown by the
     * GUI callback is caught and logged to avoid crashing the EDT.</p>
     */
    @Override
    protected void done() {
        try {
            gui.afterOnRunFLECO();
        } catch (Throwable t) {
            LOGGER.error("done: exception while notifying GUI in afterOnRunFLECO()", t);
            // Do not rethrow: done() runs on EDT and throwing would be harmful.
        }
    }

    /**
     * Returns the {@link FLECO} instance associated with this worker.
     *
     * @return the non-null {@link FLECO} instance provided at construction time
     */
    public FLECO getFleco() {
        return fleco;
    }

    /**
     * Returns the GUI callback associated with this worker.
     *
     * @return the non-null {@link IFLECOGUI} instance provided at construction
     * time
     */
    public IFLECOGUI getGui() {
        return gui;
    }
}
