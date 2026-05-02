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

import java.awt.Image;
import java.net.URL;
import java.util.EnumMap;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Image broker that preloads and serves application icons.
 *
 * <p>
 * This class implements a thread-safe lazy singleton that preloads two sets of
 * {@link ImageIcon}s (16x16 and 32x32) for all {@link AvailableImages} values.
 * Callers may request either an {@link Image} or an {@link ImageIcon} for a
 * given {@link AvailableImages} identifier.</p>
 *
 * <p>
 * Loading is defensive: missing resources are logged and replaced with a safe
 * empty {@link ImageIcon} placeholder. Public methods validate their arguments
 * and log errors before throwing when appropriate.</p>
 *
 * <p>
 * <b>Threading</b>: the singleton uses double-checked locking with a
 * {@code volatile} instance reference. The internal maps are populated once at
 * construction and are safe for concurrent reads after construction.</p>
 *
 * <p>
 * Compatibility: Java 11, no additional dependencies.</p>
 *
 * @author Manuel Domínguez-Dorado
 */
public final class ImageBroker {

    /**
     * Serial version identifier (not used for serialization here but kept for
     * consistency with other Swing components).
     */
    private static final long serialVersionUID = 1L;

    /**
     * Singleton instance reference (volatile for double-checked locking).
     */
    private static volatile ImageBroker instance;

    /**
     * Preloaded 16x16 icons keyed by {@link AvailableImages}.
     */
    private final EnumMap<AvailableImages, ImageIcon> imageIcons16x16;

    /**
     * Preloaded 32x32 icons keyed by {@link AvailableImages}.
     */
    private final EnumMap<AvailableImages, ImageIcon> imageIcons32x32;

    /**
     * Logger for diagnostics and resource-loading failures.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageBroker.class);

    /**
     * Private constructor that preloads icons for all {@link AvailableImages}.
     *
     * <p>
     * Any missing resource is logged and replaced with an empty
     * {@link ImageIcon} placeholder to avoid returning {@code null} to
     * callers.</p>
     */
    private ImageBroker() {
        this.imageIcons16x16 = new EnumMap<>(AvailableImages.class);
        this.imageIcons32x32 = new EnumMap<>(AvailableImages.class);

        // Prepopulate with a safe default placeholder to ensure NOT_FOUND is always present.
        final ImageIcon placeholder = new ImageIcon();
        imageIcons16x16.put(AvailableImages.NOT_FOUND, placeholder);
        imageIcons32x32.put(AvailableImages.NOT_FOUND, placeholder);

        for (AvailableImages availableImage : AvailableImages.values()) {
            // Load 16x16
            try {
                final String path16 = availableImage.getPath16x16();
                final URL url16 = path16 == null ? null : getClass().getResource(path16);
                final ImageIcon icon16 = url16 == null ? placeholder : new ImageIcon(url16);
                imageIcons16x16.put(availableImage, icon16);
            } catch (Throwable t) {
                LOGGER.error("ImageBroker.<init>: error loading 16x16 icon for {} - using placeholder", availableImage, t);
                imageIcons16x16.put(availableImage, placeholder);
            }

            // Load 32x32
            try {
                final String path32 = availableImage.getPath32x32();
                final URL url32 = path32 == null ? null : getClass().getResource(path32);
                final ImageIcon icon32 = url32 == null ? placeholder : new ImageIcon(url32);
                imageIcons32x32.put(availableImage, icon32);
            } catch (Throwable t) {
                LOGGER.error("ImageBroker.<init>: error loading 32x32 icon for {} - using placeholder", availableImage, t);
                imageIcons32x32.put(availableImage, placeholder);
            }
        }
    }

    /**
     * Returns the singleton {@link ImageBroker} instance, creating it if
     * necessary.
     *
     * @return the singleton instance (never {@code null})
     */
    public static ImageBroker getInstance() {
        ImageBroker local = instance;
        if (local == null) {
            synchronized (ImageBroker.class) {
                local = instance;
                if (local == null) {
                    instance = local = new ImageBroker();
                }
            }
        }
        return local;
    }

    /**
     * Returns the 16x16 {@link Image} for the given {@link AvailableImages} id.
     *
     * @param imageID image identifier (must not be {@code null})
     * @return the requested {@link Image}; never {@code null} (placeholder if
     * missing)
     * @throws IllegalArgumentException if {@code imageID} is {@code null}
     */
    public Image getImage16x16(final AvailableImages imageID) {
        final ImageIcon icon = getImageIcon16x16(imageID);
        return icon.getImage();
    }

    /**
     * Returns the 32x32 {@link Image} for the given {@link AvailableImages} id.
     *
     * @param imageID image identifier (must not be {@code null})
     * @return the requested {@link Image}; never {@code null} (placeholder if
     * missing)
     * @throws IllegalArgumentException if {@code imageID} is {@code null}
     */
    public Image getImage32x32(final AvailableImages imageID) {
        final ImageIcon icon = getImageIcon32x32(imageID);
        return icon.getImage();
    }

    /**
     * Returns the 16x16 {@link ImageIcon} for the given {@link AvailableImages}
     * id.
     *
     * @param imageID image identifier (must not be {@code null})
     * @return the requested {@link ImageIcon}; never {@code null} (placeholder
     * if missing)
     * @throws IllegalArgumentException if {@code imageID} is {@code null}
     */
    public ImageIcon getImageIcon16x16(final AvailableImages imageID) {
        if (imageID == null) {
            LOGGER.error("getImageIcon16x16: imageID cannot be null");
            throw new IllegalArgumentException("imageID cannot be null");
        }
        ImageIcon icon = imageIcons16x16.get(imageID);
        if (icon == null) {
            icon = imageIcons16x16.get(AvailableImages.NOT_FOUND);
            if (icon == null) {
                // Fallback safe placeholder
                icon = new ImageIcon();
            }
        }
        return icon;
    }

    /**
     * Returns the 32x32 {@link ImageIcon} for the given {@link AvailableImages}
     * id.
     *
     * @param imageID image identifier (must not be {@code null})
     * @return the requested {@link ImageIcon}; never {@code null} (placeholder
     * if missing)
     * @throws IllegalArgumentException if {@code imageID} is {@code null}
     */
    public ImageIcon getImageIcon32x32(final AvailableImages imageID) {
        if (imageID == null) {
            LOGGER.error("getImageIcon32x32: imageID cannot be null");
            throw new IllegalArgumentException("imageID cannot be null");
        }
        ImageIcon icon = imageIcons32x32.get(imageID);
        if (icon == null) {
            icon = imageIcons32x32.get(AvailableImages.NOT_FOUND);
            if (icon == null) {
                // Fallback safe placeholder
                icon = new ImageIcon();
            }
        }
        return icon;
    }
}
