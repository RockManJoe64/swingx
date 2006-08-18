/*
 * RectanglePainter.java
 *
 * Created on July 12, 2006, 6:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.jdesktop.swingx.painter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import org.joshy.util.u;


/**
 *
 * @author joshua.marinacci@sun.com
 *
 */

public class RectanglePainter extends AreaPainter {
    private Paint borderPaint = Color.BLACK;
    private boolean rounded = false;
    private Insets insets = new Insets(0,0,0,0);
    private int roundWidth = 20;
    private int roundHeight = 20;
    private double strokeWidth = 1;
    /**
     * Indicates whether the shape should be filled or outlined, or both
     */
    private Style style = Style.BOTH;
    
    
    /** Creates a new instance of RectanglePainter */
    public RectanglePainter() {
    }
    
    
    
    public RectanglePainter(int top, int left, int bottom, int right,
            int roundWidth, int roundHeight, boolean rounded, Paint fillPaint,
            double strokeWidth, Paint borderPaint) {
        this();
        insets = new Insets(top,left,bottom,right);
        this.roundWidth = roundWidth;
        this.roundHeight = roundHeight;
        this.rounded = rounded;
        this.setPaint(fillPaint);
        this.strokeWidth = strokeWidth;
        this.borderPaint = borderPaint;
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
    
    protected Shape calculateShape(JComponent component, int width, int height) {
        Shape shape = new Rectangle2D.Double(insets.left, insets.top,
                width-insets.left-insets.right,
                height-insets.top-insets.bottom);
        if(rounded) {
            shape = new RoundRectangle2D.Double(insets.left, insets.top,
                    width-insets.left-insets.right,
                    height-insets.top-insets.bottom,
                    roundWidth, roundHeight);
        }
        return shape;
    }
    
    
    
    public void paintBackground(Graphics2D g, JComponent component, int width, int height) {
        Shape shape = calculateShape(component, width, height);
        switch (getStyle()) {
            case BOTH:
                drawBackground(g,shape,width,height);
                drawBorder(g,shape,width,height);
                break;
            case FILLED:
                drawBackground(g,shape,width,height);
                break;
            case OUTLINE:
                drawBorder(g,shape,width,height);
                break;
            case NONE:
                break;
        }
        
        // background
        // border
        // leave the clip to support masking other painters
        g.setClip(shape);
    }
    
    private void drawBorder(Graphics2D g, Shape shape, int width, int height) {
        g.setPaint(borderPaint);
        g.setStroke(new BasicStroke((float)strokeWidth));
        g.draw(shape);
    }
    private void drawBackground(Graphics2D g, Shape shape, int width, int height) {
        Paint p = getPaint();
        if(isSnapPaint()) {
            p = calculateSnappedPaint(p, width, height);
        }
        
        g.setPaint(p);
        g.fill(shape);
    }
    
    public Paint getBorderPaint() {
        return borderPaint;
    }
    
    public void setBorderPaint(Paint borderPaint) {
        Paint oldBorderPaint = getBorderPaint();
        this.borderPaint = borderPaint;
        firePropertyChange("fillPaint",oldBorderPaint,borderPaint);
    }
    
    public boolean isRounded() {
        return rounded;
    }
    
    public void setRounded(boolean rounded) {
        boolean oldRounded = isRounded();
        this.rounded = rounded;
        firePropertyChange("rounded",oldRounded,rounded);
    }
    
    public Insets getInsets() {
        return insets;
    }
    
    public void setInsets(Insets insets) {
        Insets oldInsets = getInsets();
        this.insets = insets;
        firePropertyChange("insets",oldInsets,insets);
    }
    
    public int getRoundWidth() {
        return roundWidth;
    }
    
    public void setRoundWidth(int roundWidth) {
        int oldRoundWidth = getRoundWidth();
        this.roundWidth = roundWidth;
        firePropertyChange("roundWidth",oldRoundWidth,roundWidth);
    }
    
    public int getRoundHeight() {
        return roundHeight;
    }
    
    public void setRoundHeight(int roundHeight) {
        int oldRoundHeight = getRoundHeight();
        this.roundHeight = roundHeight;
        firePropertyChange("roundHeight",oldRoundHeight,roundHeight);
    }
    
    public double getStrokeWidth() {
        return strokeWidth;
    }
    
    public void setStrokeWidth(double strokeWidth) {
        double oldStrokeWidth = getStrokeWidth();
        this.strokeWidth = strokeWidth;
        firePropertyChange("strokeWidth",oldStrokeWidth,strokeWidth);
    }
    
}

