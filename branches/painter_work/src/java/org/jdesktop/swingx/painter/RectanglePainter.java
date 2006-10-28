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

public class RectanglePainter extends AbstractPathPainter {
    private boolean rounded = false;
    //private Insets insets = new Insets(0,0,0,0);
    private int roundWidth = 20;
    private int roundHeight = 20;
    //private double strokeWidth = 1;
    
    /** Creates a new instance of RectanglePainter */
    public RectanglePainter() {
    }
    
    
    public RectanglePainter(int top, int left, int bottom, int right) {
        this(top, left, bottom, right, 0, 0, false, Color.RED, 1f, Color.BLACK);
    }
    public RectanglePainter(int top, int left, int bottom, int right,
            int roundWidth, int roundHeight) {
        this(top,left,bottom,right,roundWidth, roundHeight, true, Color.RED, 1f, Color.BLACK);
    }
    
    public RectanglePainter(int top, int left, int bottom, int right,
            int roundWidth, int roundHeight, boolean rounded, Paint fillPaint,
            float strokeWidth, Paint borderPaint) {
        this();
        this.setInsets(new Insets(top,left,bottom,right));
        this.roundWidth = roundWidth;
        this.roundHeight = roundHeight;
        this.rounded = rounded;
        this.setFillPaint(fillPaint);
        this.setBorderWidth(strokeWidth);
        this.setBorderPaint(borderPaint);
    }
    
    
    
    
    public boolean isRounded() {
        return rounded;
    }
    
    public void setRounded(boolean rounded) {
        boolean oldRounded = isRounded();
        this.rounded = rounded;
        firePropertyChange("rounded",oldRounded,rounded);
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
    
    
    /* ======== drawing code ============ */
    protected Shape calculateShape(JComponent component, int width, int height) {
        Insets insets = getInsets();
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
        g.setPaint(getBorderPaint());
        g.setStroke(new BasicStroke(getBorderWidth()));
        g.draw(shape);
    }
    
    private void drawBackground(Graphics2D g, Shape shape, int width, int height) {
        Paint p = getFillPaint();
        if(isSnapPaint()) {
            p = calculateSnappedPaint(p, width, height);
        }
        
        g.setPaint(p);
        if(!(p instanceof Color)) {
            p = Color.BLUE;
        }
        
        g.fill(shape);
        if(getPathEffect() != null) {
            getPathEffect().apply(g, shape, width, height, (Color)p);
        }
    }
    
    public Shape provideShape(JComponent comp, int width, int height) {
        return calculateShape(comp,width,height);
    }
}

