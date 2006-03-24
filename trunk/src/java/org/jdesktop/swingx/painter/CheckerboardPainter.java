/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jdesktop.swingx.painter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 * <p>A Painter implementation that paints a checkerboard pattern. The light
 * and dark colors (Paint instances) are configurable, as are the size of the
 * squares (squareLength).</p>
 * 
 * <p>To configure a checkerboard pattern that used a gradient for the dark
 * tiles and Color.WHITE for the light tiles, you could:
 * <pre><code>
 *  GradientPaint gp = new GradientPaint(
 *      new Point2D.Double(0, 0),
 *      Color.BLACK,
 *      new Point2D.Double(0, 32),
 *      Color.GRAY);
 *  CheckerboardPainter p = new CheckerboardPainter();
 *  p.setDarkPaint(gp);
 *  p.setLightPaint(Color.WHITE);
 *  p.setSquareLength(32);
 *  panel.seBackgroundPainter(p);
 * </code></pre></p>
 * 
 * <p>Note that in this example, the "32" in the GradientPaint matches the "32"
 * set for the squareLength. This is necessary because GradientPaints don't
 * readjust themselves for the size of the square. They are fixed and immutable
 * at the time of creation.</p>
 * 
 * 
 * 
 * @author rbair
 */
public class CheckerboardPainter extends AbstractPainter {
    private Paint checkerPaint;
    
    private Paint darkColor = new Color(204, 204, 204);
    private Paint lightColor = Color.WHITE;
    private int squareLength = 8;
    
    /**
     * Create a new CheckerboardPainter. By default the light color is Color.WHITE,
     * the dark color is a light gray, and the square length is 8.
     */
    public CheckerboardPainter() {
    }
    
    /**
     * Create a new CheckerboardPainter with the specified light and dark paints.
     * By default the square length is 8.
     *
     * @param darkPaint the paint used to draw the dark squares
     * @param lightPaint the paint used to draw the light squares
     */
    public CheckerboardPainter(Paint darkPaint, Paint lightPaint) {
        this(darkPaint, lightPaint, 8);
    }
    
    /**
     * Create a new CheckerboardPainter with the specified light and dark paints
     * and the specified square size.
     *
     * @param darkPaint the paint used to draw the dark squares
     * @param lightPaint the paint used to draw the light squares
     * @param length the length of the checker board squares
     */
    public CheckerboardPainter(Paint darkPaint, Paint lightPaint, int length) {
        this.darkColor = darkPaint;
        this.lightColor = lightPaint;
        this.squareLength = length;
    }
    
    
    /**
     * Specifies the length of the squares. By default, it is 8. A length of <=
     * 0 will cause an IllegalArgumentException to be thrown.
     *
     * @param length the length of one side of a square tile. Must be > 0.
     */
    public void setSquareLength(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be > 0");
        }
        
        int old = getSquareLength();
        this.squareLength = length;
        checkerPaint = null;
        firePropertyChange("squareLength", old, getSquareLength());
    }
    
    /**
     * @param return the squareLength. Will be > 0
     */
    public int getSquareLength() {
        return squareLength;
    }
    
    /**
     * Specifies the paint to use for dark tiles. This is a Paint and
     * may be anything, including a TexturePaint for painting images. If null,
     * the background color of the component is used.
     *
     * @param color the Paint to use for painting the "dark" tiles. May be null.
     */
    public void setDarkPaint(Paint color) {
        Paint old = getDarkPaint();
        this.darkColor = color;
        checkerPaint = null;
        firePropertyChange("darkColor", old, getDarkPaint());
    }
    
    /**
     * @returns the Paint used for painting the "dark" tiles. May be null
     */
    public Paint getDarkPaint() {
        return darkColor;
    }
    
    /**
     * Specifies the paint to use for light tiles. This is a Paint and
     * may be anything, including a TexturePaint for painting images. If null,
     * the foreground color of the component is used.
     *
     * @param color the Paint to use for painting the "light" tiles. May be null.
     */
    public void setLightPaint(Paint color) {
        Paint old = getLightPaint();
        this.lightColor = color;
        checkerPaint = null;
        firePropertyChange("lightColor", old, getLightPaint());
    }
    
    /**
     * @returns the Paint used for painting the "light" tiles. May be null
     */
    public Paint getLightPaint() {
        return lightColor;
    }
    
    /**
     * Helper method that creates and returns the Paint that incorporates the
     * sizes and light and dark Paints in one TexturePaint. I may want to cache
     * this value in the future for performance reasons
     */
    private Paint getCheckerPaint(JComponent c) {
        if (checkerPaint == null) {
            int sqlength = getSquareLength();
            int length = sqlength * 2;
            BufferedImage image = new BufferedImage(length, length, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gfx = image.createGraphics();
            Paint p = getLightPaint();
            if (p == null) {
                p = c.getForeground();
            }
            gfx.setPaint(p);
            gfx.fillRect(0, 0, length, length);
            p = getDarkPaint();
            if (p == null) {
                p = c.getBackground();
            }
            gfx.setPaint(p);
            gfx.fillRect(0, 0, sqlength - 1, sqlength - 1);
            gfx.fillRect(sqlength, sqlength, sqlength - 1, sqlength - 1);
            gfx.dispose();

            checkerPaint = new TexturePaint(image, new Rectangle(0, 0, image.getWidth(), image.getHeight()));
        }
        return checkerPaint;
    }
    
    /**
     * @inheritDoc
     */
    public void paintBackground(Graphics2D g, JComponent component) {
        g.setPaint(getCheckerPaint(component));
        g.fillRect(0, 0, component.getWidth(), component.getHeight());
    }
}
