/*
 * PathEffect.java
 *
 * Created on November 1, 2006, 10:10 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter.effects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

/**
 *
 * @author joshy
 */
public interface PathEffect {
    /*
     * Applies the shape effect. This effect will be drawn on top of the graphics context.
     */
    public abstract void apply(Graphics2D g, Shape clipShape, int width, int height, Color fillColor);
}
