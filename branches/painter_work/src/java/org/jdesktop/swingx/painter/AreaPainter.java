/*
 * AreaPainter.java
 *
 * Created on August 12, 2006, 8:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;
import org.apache.batik.ext.awt.LinearGradientPaint;

/**
 *
 * @author joshy
 */
public abstract class AreaPainter extends AbstractPainter {
    /**
     * The paint to use
     */
    private Paint paint;
    private boolean snapPaint;
    
    /** Creates a new instance of AreaPainter */
    public AreaPainter() {
        paint = Color.RED;
    }
    public AreaPainter(Paint paint) {
        this.paint = paint;
    }

    
    /**
     * @return Gets the Paint being used. May be null
     */
    public Paint getPaint() {
        return paint;
    }

    
    public boolean isSnapPaint() {
        return snapPaint;
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

    
    public void setSnapPaint(boolean snapPaint) {
        boolean old = this.isSnapPaint();
        this.snapPaint = snapPaint;
        firePropertyChange("snapPaint",old,this.snapPaint);
    }
    
    protected Paint calculateSnappedPaint(Paint p, int width, int height) {
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
