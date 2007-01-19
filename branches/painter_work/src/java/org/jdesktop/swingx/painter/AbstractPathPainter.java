/*
 * AbstractPathPainter.java
 *
 * Created on August 12, 2006, 8:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import org.apache.batik.ext.awt.LinearGradientPaint;
import org.jdesktop.swingx.painter.effects.AbstractPathEffect;
import org.jdesktop.swingx.painter.effects.PathEffect;


/**
 *
 * @author joshy
 */
public abstract class AbstractPathPainter<T> extends PositionedPainter<T> {
    
    /**
     * Different available fill styles. BOTH indicates that both the outline,
     * and the fill should be painted. This is the default. FILLED indicates that
     * the shape should be filled, but no outline painted. OUTLINE specifies that
     * the shape should be outlined, but not filled
     */
    public enum Style {BOTH, FILLED, OUTLINE, NONE}
    
    private boolean snapPaint;
    private PathEffect[] pathEffect;
    
    
    private Style style = Style.BOTH;
    /**
     * The stroke width to use when painting. If null, the default Stroke for
     * the Graphics2D is used
     */
    private float borderWidth;
    
    /**
     * The paint to use when filling the shape
     */
    private Paint fillPaint;
    
    /**
     * The Paint to use when stroking the shape (drawing the outline). If null,
     * then the component foreground color is used
     */
    private Paint borderPaint;
    
    /**
     * Creates a new instance of AbstractPathPainter
     */
    public AbstractPathPainter() {
        fillPaint = Color.RED;
    }
    public AbstractPathPainter(Paint paint) {
        this.fillPaint = paint;
    }
    
    
    /**
     * @return Gets the Paint being used. May be null
     */
    public Paint getFillPaint() {
        return fillPaint;
    }
    
    /**
     * Sets the Paint to use. If null, nothing is painted
     *
     * @param p the Paint to use
     */
    public void setFillPaint(Paint p) {
        Paint old = getFillPaint();
        this.fillPaint = p;
        firePropertyChange("paint", old, getFillPaint());
    }
    
    public boolean isSnapPaint() {
        return snapPaint;
    }
    
    public void setSnapPaint(boolean snapPaint) {
        boolean old = this.isSnapPaint();
        this.snapPaint = snapPaint;
        firePropertyChange("snapPaint",old,this.snapPaint);
    }
    
    /**
     * The Paint to use for stroking the shape (painting the outline).
     * Can be a Color, GradientPaint, TexturePaint, or any other kind of Paint.
     * If null, the component foreground is used.
     *
     * @param p the Paint to use for stroking the shape. May be null.
     */
    public void setBorderPaint(Paint p) {
        Paint old = getBorderPaint();
        this.borderPaint = p;
        firePropertyChange("borderPaint", old, getBorderPaint());
    }
    
    /**
     * @return the Paint used when stroking the shape. May be null
     */
    public Paint getBorderPaint() {
        return borderPaint;
    }
    
    /**
     * The shape can be filled or simply stroked (outlined), or both. By default,
     * the shape is both filled and stroked. This property specifies the strategy to
     * use.
     *
     * @param s the Style to use. If null, Style.BOTH is used
     */
    public void setStyle(Style s) {
        Style old = getStyle();
        this.style = s == null ? Style.BOTH : s;
        firePropertyChange("style", old, getStyle());
    }
    
    /**
     * @return the Style used
     */
    public Style getStyle() {
        return style;
    }
    
    /**
     * Sets the stroke to use for painting. If null, then the default Graphics2D
     * stroke use used
     *
     *
     * @param s the Stroke to fillPaint with
     */
    public void setBorderWidth(float s) {
        float old = getBorderWidth();
        this.borderWidth = s;
        firePropertyChange("strokeWidth", old, getBorderWidth());
    }
    
    /**
     * @return the Stroke to use for painting
     */
    public float getBorderWidth() {
        return borderWidth;
    }
    
    
    
    public static Paint calculateSnappedPaint(Paint p, int width, int height) {
        if(p instanceof Color) {
            return p;
        }
        if(p instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint)p;
            Point2D start = gp.getPoint1();
            Point2D end = gp.getPoint2();
            
            Point2D[] pts = new Point2D[2];
            pts[0] = gp.getPoint1();
            pts[1] = gp.getPoint2();
            pts = adjustPoints(pts, width, height);
            
            return new GradientPaint(pts[0], gp.getColor1(), pts[1], gp.getColor2());
            
        }
        
        if(p instanceof LinearGradientPaint) {
            LinearGradientPaint mgp = (LinearGradientPaint)p;
            Point2D start = mgp.getStartPoint();
            Point2D end = mgp.getEndPoint();
            
            Point2D[] pts = new Point2D[2];
            pts[0] = mgp.getStartPoint();
            pts[1] = mgp.getEndPoint();
            pts = adjustPoints(pts, width, height);
            /*
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
             */
            
            LinearGradientPaint mgp2 = new LinearGradientPaint(
                    pts[0], pts[1],
                    mgp.getFractions(),
                    mgp.getColors());
            
            return mgp2;
        }
        return p;
    }
    
    public static boolean isNear(double angle, double target, double error) {
        if(Math.abs(target - Math.abs(angle)) < error) {
            return true;
        }
        return false;
    }
    
    public static double calcAngle(Point2D p1, Point2D p2) {
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
    
    // shape effect stuff
    public abstract Shape provideShape(T comp, int width, int height);
    
    public void setPathEffects(PathEffect... pathEffect) {
        this.pathEffect = pathEffect;
    }
    
    public PathEffect[] getPathEffects() {
        return this.pathEffect;
    }
    
    private static Point2D[] adjustPoints(Point2D[] pts, int width, int height) {
        Point2D start = pts[0];
        Point2D end = pts[1];
        
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
        
        return new Point2D[] { start, end };
    }
    
}
