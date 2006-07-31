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
import java.awt.geom.Point2D;
import javax.swing.JComponent;
import org.apache.batik.ext.awt.LinearGradientPaint;
import org.apache.batik.ext.awt.MultipleGradientPaint;

/**
 * A Painter implementation that uses a Paint to fill the entire background
 * area using that Paint. For example, if I wanted to paint the entire background
 * in Color.GREEN, I would:
 * <pre><code>
 *  MattePainter p = new MattePainter(Color.GREEN);
 *  panel.setBackgroundPainter(p);
 * </code></pre></p>
 *
 * <p>Since it accepts a Paint, it is also possible to paint a texture or use other
 * more exotic Paint implementations. To paint a BufferedImage texture as the
 * background:
 * <pre><code>
 *  TexturePaint paint = new TexturePaint(bufferedImage,
 *      new Rectangle2D.Double(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight()));
 *  MattePainter p = new MattePainter(paint);
 *  panel.setBackgroundPainter(p);
 * </code></pre></p>
 *
 * <p>If no paint is specified, then nothing is painted</p>
 *
 * @author rbair
 */
public class MattePainter extends AbstractPainter {
    /**
     * The paint to use
     */
    private Paint paint;
    private boolean snapPaint;
    
    /**
     * Creates a new MattePainter with "null" as the paint used
     */
    public MattePainter() {
    }
    
    /**
     * Create a new MattePainter that uses the given color. This is only
     * a convenience constructor since Color is a Paint, and thus the
     * other constructor is perfectly suited for specify a color as well
     *
     * @param color Color to fill with
     */
    public MattePainter(Color color) {
        this((Paint)color);
    }
    
    /**
     * Create a new MattePainter for the given Paint. This can be a GradientPaint
     * (though not recommended because the gradient will not grow when the
     * component becomes larger), TexturePaint, Color, or other Paint instance.
     *
     * @param paint Paint to fill with
     */
    public MattePainter(Paint paint) {
        super();
        this.paint = paint;
    }
    
    /**
     * Sets the Paint to use. If null, nothing is painted
     *
     * @param p the Paint to use
     */
    public void setPaint(Paint p) {
        Paint old = getPaint();
        this.paint = p;
        firePropertyChange("paint", old, getPaint());
    }
    
    /**
     * @return Gets the Paint being used. May be null
     */
    public Paint getPaint() {
        return paint;
    }
    
    /**
     * @inheritDoc
     */
    public void paintBackground(Graphics2D g, JComponent component, int width, int height) {
        Paint p = getPaint();
        if (p != null) {
            if(isSnapPaint()) {
                p = calculateSnappedPaint(p,width,height);
            }
            g.setPaint(p);
            g.fillRect(0, 0, width, height);
        }
    }
    
    public boolean isSnapPaint() {
        return snapPaint;
    }
    
    public void setSnapPaint(boolean snapPaint) {
        boolean old = this.isSnapPaint();
        this.snapPaint = snapPaint;
        firePropertyChange("snapPaint",old,this.snapPaint);
    }
    
    private Paint calculateSnappedPaint(Paint p, int width, int height) {
        if(p instanceof Color) {
            return p;
        }
        
        if(p instanceof LinearGradientPaint) {
            LinearGradientPaint mgp = (LinearGradientPaint)p;
            Point2D start = mgp.getStartPoint();
            Point2D end = mgp.getEndPoint();
            double angle = calcAngle(start,end);
            double a2 = Math.toDegrees(angle);
            double e = 10;
            
            // if it is near 0 degrees
            if(Math.abs(angle) < Math.toRadians(e) ||
               Math.abs(angle) > Math.toRadians(360-e)) {
                start = new Point2D.Float(0,0);
                end = new Point2D.Float(width,0);
            }
            
            // near 45 
            if(isNear(a2, 45, e)) {
                start = new Point2D.Float(0,0);
                end = new Point2D.Float(width,height);                
            }
            
            // near 90
            if(isNear(a2,  90, e)) {
                start = new Point2D.Float(0,0);
                end = new Point2D.Float(0,height);
            }
            
            // near 135
            if(isNear(a2, 135, e)) {
                start = new Point2D.Float(width,0);
                end = new Point2D.Float(0,height);                
            }
            
            // near 180
            if(isNear(a2, 180, e)) {
                start = new Point2D.Float(width,0);
                end = new Point2D.Float(0,0);
            }
            
            // near 225
            if(isNear(a2, 225, e)) {
                start = new Point2D.Float(width,height);
                end = new Point2D.Float(0,0);
            }
            
            // near 270
            if(isNear(a2, 270, e)) {
                start = new Point2D.Float(0,height);
                end = new Point2D.Float(0,0);
            }
            
            // near 315
            if(isNear(a2, 315, e)) {
                start = new Point2D.Float(0,height);
                end = new Point2D.Float(width,0);
            }
            
            LinearGradientPaint mgp2 = new LinearGradientPaint(
                    start, end,
                    mgp.getFractions(),
                    mgp.getColors());
            
            return mgp2;
        }
        return p;
    }
    
    private static boolean isNear(double angle, double target, double error) {
        if(Math.abs(target - Math.abs(angle)) < error) {
            return true;
        }
        return false;
    }
    
    private static double calcAngle(Point2D p1, Point2D p2) {
        double x_off = p2.getX() - p1.getX();
        double y_off = p2.getY() - p1.getY();
        double angle = Math.atan(y_off / x_off);
        if (x_off < 0) {
            angle = angle + Math.PI;
        }

        if(angle < 0) { angle+= 2*Math.PI; }
        if(angle > 2*Math.PI) { angle -= 2*Math.PI; }
        return angle;
    }
}
