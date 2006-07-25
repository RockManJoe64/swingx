/*
 * AffineTransformPainter.java
 *
 * Created on July 19, 2006, 6:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.Graphics2D;
import javax.swing.JComponent;

/**
 *
 * @author joshy
 */
public class AffineTransformPainter extends AbstractPainter {
    private double angle = 0;
    private double scaleX = 1;
    private double scaleY = 1;
    private double shearX = 0;
    private double shearY = 0;
    private double translateX = 0;
    private double translateY = 0;
    
    /** Creates a new instance of AffineTransformPainter */
    public AffineTransformPainter() {
        super.setShouldRestoreState(false);
    }
    protected void paintBackground(Graphics2D g, JComponent component, int width, int height) {
        g.translate(getTranslateX(), getTranslateY());
        g.scale(scaleX,scaleY);
        g.shear(shearX,shearY);
        g.rotate(angle);
    }

    public double getTranslateX() {
        return translateX;
    }

    public void setTranslateX(double translateX) {
        double old = this.translateX;
        this.translateX = translateX;
        firePropertyChange("translateX",old,translateX);
    }

    public double getTranslateY() {
        return translateY;
    }

    public void setTranslateY(double translateY) {
        double old = this.translateY;
        this.translateY = translateY;
        firePropertyChange("translateY",old,translateY);
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        double old = this.getAngle();
        this.angle = angle;
        firePropertyChange("angle",old,this.angle);
    }

    public double getScaleX() {
        return scaleX;
    }

    public void setScaleX(double scaleX) {
        double old = this.getScaleX();
        this.scaleX = scaleX;
        firePropertyChange("scaleX",old,this.scaleX);
    }

    public double getScaleY() {
        return scaleY;
    }

    public void setScaleY(double scaleY) {
        double old = this.getScaleY();
        this.scaleY = scaleY;
        firePropertyChange("scaleY",old,this.scaleY);
    }

    public double getShearX() {
        return shearX;
    }

    public void setShearX(double shearX) {
        double old = this.getShearX();
        this.shearX = shearX;
        firePropertyChange("shearX",old,this.shearX);
    }

    public double getShearY() {
        return shearY;
    }

    public void setShearY(double shearY) {
        double old = this.getShearY();
        this.shearY = shearY;
        firePropertyChange("shearY",old,this.shearY);
    }
    
}
