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
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import org.jdesktop.swingx.painter.effects.PathEffect;



/**
 *
 * @author joshua.marinacci@sun.com
 *
 */

public class RectanglePainter<T> extends AbstractPathPainter<T> {
    private boolean rounded = false;
    //private Insets insets = new Insets(0,0,0,0);
    private int roundWidth = 20;
    private int roundHeight = 20;
    private int width = -1;
    private int height = -1;
    //private double strokeWidth = 1;
    
    /** Creates a new instance of RectanglePainter */
    public RectanglePainter() {
    }
    
    public RectanglePainter(Color fillPaint, Color borderPaint) {
        this(0,0,0,0,0,0,false,fillPaint,1f,borderPaint);
    }
    public RectanglePainter(int top, int left, int bottom, int right) {
        this(top, left, bottom, right, 0, 0, false, Color.RED, 1f, Color.BLACK);
    }
    public RectanglePainter(int top, int left, int bottom, int right,
            int roundWidth, int roundHeight) {
        this(top,left,bottom,right,roundWidth, roundHeight, true, Color.RED, 1f, Color.BLACK);
    }
    
    public RectanglePainter(int width, int height, int cornerRadius, Paint fillPaint) {
        this(new Insets(0,0,0,0), width,height, 
                cornerRadius, cornerRadius, true, 
                fillPaint, 1f, Color.BLACK);
    }
    
    public RectanglePainter(Insets insets,
            int width, int height,
            int roundWidth, int roundHeight, boolean rounded, Paint fillPaint,
            float strokeWidth, Paint borderPaint) {
        this();
        this.width = width;
        this.height = height;
        setHorizontalStretch(false);
        setVerticalStretch(false);
        setInsets(insets);
        this.roundWidth = roundWidth;
        this.roundHeight = roundHeight;
        this.rounded = rounded;
        this.setFillPaint(fillPaint);
        this.setBorderWidth(strokeWidth);
        this.setBorderPaint(borderPaint);
    }
    
    public RectanglePainter(int top, int left, int bottom, int right,
            int roundWidth, int roundHeight, boolean rounded, Paint fillPaint,
            float strokeWidth, Paint borderPaint) {
        this();
        this.setInsets(new Insets(top,left,bottom,right));
        setVerticalStretch(true);
        setHorizontalStretch(true);
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
    protected Shape calculateShape(T component, int width, int height) {
        Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top;
        
        // use the position calcs from the super class
        Rectangle bounds = calculatePosition(this.width, this.height, width, height);
        if(this.width != -1 && !isHorizontalStretch()) {
            width = this.width;
            x = bounds.x;
        } 
        if(this.height != -1 && !isVerticalStretch()) {
            height = this.height;
            y = bounds.y;
        }
        
        if(isHorizontalStretch()) {
            width = width - insets.left - insets.right;
        }
        if(isVerticalStretch()) {
            height = height - insets.top - insets.bottom;
        }
        
        
        Shape shape = new Rectangle2D.Double(x, y, width, height);
        if(rounded) {
            shape = new RoundRectangle2D.Double(x, y, width, height, roundWidth, roundHeight);
        }
        return shape;
    }
    
    
    
    public void paintBackground(Graphics2D g, T component, int width, int height) {
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
        
        g.fill(shape);
        if(getPathEffects() != null) {
            for(PathEffect ef : getPathEffects()) {
                ef.apply(g, shape, width, height);
            }
        }
    }
    
    public Shape provideShape(T comp, int width, int height) {
        return calculateShape(comp,width,height);
    }
}

