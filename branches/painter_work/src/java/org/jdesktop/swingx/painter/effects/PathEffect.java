/*
 * PathEffect.java
 *
 * Created on August 20, 2006, 7:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter.effects;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import org.jdesktop.swingx.color.ColorUtil;
import org.joshy.util.u;

/**
 *
 * @author joshy
 */
public class PathEffect {
    
    /**
     * Creates a new instance of PathEffect
     */
    public PathEffect() {
        setBrushColor(Color.BLACK);
        setBrushSteps(10);
        setEffectWidth(8);
        setRenderInsideShape(false);
        setOffset(new Point(5,5));
        setShouldFillShape(true);
    }
    
    
    
    public void apply(Graphics2D g, Shape shape, int width, int height, Color fillColor) {
        
        // create a shape for clipping
        //Shape clipShape = createClipShape(width, height);
        //Shape clipShape = new Ellipse2D.Float(width/4, height/4, width/2, height/2);
        Shape clipShape = shape;
        
        // Clear the background to white
        g.setColor(Color.WHITE);
        //g.fillRect(0, 0, width, height);
        
        // Set the clip shape onto a buffer image
        BufferedImage clipImage = createClipImage(clipShape, g, width, height);
        Graphics2D g2 = clipImage.createGraphics();
        
        // Fill the shape with a gradient
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.SrcAtop);
        Color clrHi;
        clrHi = ColorUtil.setBrightness(fillColor, 1f);
        Color clrLo;
        clrLo = ColorUtil.setBrightness(fillColor, 0.5f);
        //g2.setPaint(new GradientPaint(0, 0, clrHi, 0, height, clrLo));
        // use fill color instead
        g2.setPaint(fillColor);
        g2.fill(clipShape);
        
        // Apply the border glow effect
        paintBorderGlow(g2, 8, clipShape, width, height, fillColor, fillColor);
        
        g2.dispose();
        
        g.drawImage(clipImage, 0, 0, null);
        
    }
    private Shape createClipShape(int width, int height) {
        float border = 20.0f;
        float x1 = border;
        float y1 = border;
        float x2 = width - border;
        float y2 = height - border;
        
        float adj = 3.0f; // helps round out the sharp corners
        float arc = 8.0f;
        float dcx = 0.18f * width;
        float cx1 = x1-dcx;
        float cy1 = 0.40f * height;
        float cx2 = x1+dcx;
        float cy2 = 0.50f * height;
        
        GeneralPath gp = new GeneralPath();
        gp.moveTo(x1-adj, y1+adj);
        gp.quadTo(x1, y1, x1+adj, y1);
        gp.lineTo(x2-arc, y1);
        gp.quadTo(x2, y1, x2, y1+arc);
        gp.lineTo(x2, y2-arc);
        gp.quadTo(x2, y2, x2-arc, y2);
        gp.lineTo(x1+adj, y2);
        gp.quadTo(x1, y2, x1, y2-adj);
        gp.curveTo(cx2, cy2, cx1, cy1, x1-adj, y1+adj);
        gp.closePath();
        return gp;
    }
    private BufferedImage createClipImage(Shape s, Graphics2D g, int width, int height) {
        // Create a translucent intermediate image in which we can perform
        // the soft clipping
        
        GraphicsConfiguration gc = g.getDeviceConfiguration();
        BufferedImage img = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        Graphics2D g2 = img.createGraphics();
        
        // Clear the image so all pixels have zero alpha
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, width, height);
        
        // Render our clip shape into the image.  Note that we enable
        // antialiasing to achieve the soft clipping effect.  Try
        // commenting out the line that enables antialiasing, and
        // you will see that you end up with the usual hard clipping.
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(s);
        g2.dispose();
        
        return img;
    }
    private static Color getMixedColor(Color c1, float pct1, Color c2, float pct2) {
        float[] clr1 = c1.getComponents(null);
        float[] clr2 = c2.getComponents(null);
        for (int i = 0; i < clr1.length; i++) {
            clr1[i] = (clr1[i] * pct1) + (clr2[i] * pct2);
        }
        return new Color(clr1[0], clr1[1], clr1[2], clr1[3]);
    }
    
    private void paintBorderGlow(Graphics2D g2, int glowWidth, Shape clipShape, int width, int height,
            Color clrHi, Color clrLo) {
        //Color brushColor = Color.WHITE;
        //int steps = 10;
        int steps = getBrushSteps();
        float brushAlpha = 1f/steps;
        //int effectWidth = 8;
        
        //boolean inside = true;
        boolean inside = isRenderInsideShape();
        
        g2.setPaint(getBrushColor());
        //u.p("brush color = " + getBrushColor());
        
        //Point2D offset = new Point(5,5);
        g2.translate(offset.getX(), offset.getY());
        
        if(isShouldFillShape()) {
            // set the inside/outside mode
            if(inside) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1f));
                Area a1 = new Area(new Rectangle(
                        (int)-offset.getX()-20,
                        (int)-offset.getY()-20,
                        width+40,height+40));
                Area a2 = new Area(clipShape);
                a1.subtract(a2);
                g2.fill(a1);
            } else {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, 1f));
                g2.fill(clipShape);
            }
            
        }
        
        // set the inside/outside mode
        if(inside) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, brushAlpha));
        } else {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, brushAlpha));
        }
        
        // draw the effect
        for(float i=0; i<steps; i=i+1f) {
            float brushWidth = i * effectWidth/steps;
            g2.setStroke(new BasicStroke(brushWidth));
            g2.draw(clipShape);
        }
        g2.translate(-offset.getX(), -offset.getY());
        
    }
    
    /**
     * Holds value of property brushColor.
     */
    private Color brushColor;
    
    /**
     * Utility field used by bound properties.
     */
    private java.beans.PropertyChangeSupport propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);
    
    /**
     * Adds a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }
    
    /**
     * Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }
    
    /**
     * Getter for property brushColor.
     * @return Value of property brushColor.
     */
    public Color getBrushColor() {
        return this.brushColor;
    }
    
    /**
     * Setter for property brushColor.
     * @param brushColor New value of property brushColor.
     */
    public void setBrushColor(Color brushColor) {
        Color oldBrushColor = this.brushColor;
        this.brushColor = brushColor;
        propertyChangeSupport.firePropertyChange("brushColor", oldBrushColor, brushColor);
    }
    
    /**
     * Holds value of property brushSteps.
     */
    private int brushSteps;
    
    /**
     * Getter for property brushSteps.
     * @return Value of property brushSteps.
     */
    public int getBrushSteps() {
        return this.brushSteps;
    }
    
    /**
     * Setter for property brushSteps.
     * @param brushSteps New value of property brushSteps.
     */
    public void setBrushSteps(int brushSteps) {
        int oldBrushSteps = this.brushSteps;
        this.brushSteps = brushSteps;
        propertyChangeSupport.firePropertyChange("brushSteps", new Integer(oldBrushSteps), new Integer(brushSteps));
    }
    
    /**
     * Holds value of property effectWidth.
     */
    private int effectWidth;
    
    /**
     * Getter for property effectWidth.
     * @return Value of property effectWidth.
     */
    public int getEffectWidth() {
        return this.effectWidth;
    }
    
    /**
     * Setter for property effectWidth.
     * @param effectWidth New value of property effectWidth.
     */
    public void setEffectWidth(int effectWidth) {
        int oldEffectWidth = this.effectWidth;
        this.effectWidth = effectWidth;
        propertyChangeSupport.firePropertyChange("effectWidth", new Integer(oldEffectWidth), new Integer(effectWidth));
    }
    
    /**
     * Holds value of property renderInsideShape.
     */
    private boolean renderInsideShape;
    
    /**
     * Getter for property renderInsideShape.
     * @return Value of property renderInsideShape.
     */
    public boolean isRenderInsideShape() {
        return this.renderInsideShape;
    }
    
    /**
     * Setter for property renderInsideShape.
     * @param renderInsideShape New value of property renderInsideShape.
     */
    public void setRenderInsideShape(boolean renderInsideShape) {
        boolean oldRenderInsideShape = this.renderInsideShape;
        this.renderInsideShape = renderInsideShape;
        propertyChangeSupport.firePropertyChange("renderInsideShape", new Boolean(oldRenderInsideShape), new Boolean(renderInsideShape));
    }
    
    /**
     * Holds value of property offset.
     */
    private Point2D offset;
    
    /**
     * Getter for property offset.
     * @return Value of property offset.
     */
    public Point2D getOffset() {
        return this.offset;
    }
    
    /**
     * Setter for property offset.
     * @param offset New value of property offset.
     */
    public void setOffset(Point2D offset) {
        Point2D oldOffset = this.offset;
        this.offset = offset;
        propertyChangeSupport.firePropertyChange("offset", oldOffset, offset);
    }
    
    /**
     * Holds value of property shouldFillShape.
     */
    private boolean shouldFillShape;
    
    /**
     * Getter for property shouldFillShape.
     * @return Value of property shouldFillShape.
     */
    public boolean isShouldFillShape() {
        return this.shouldFillShape;
    }
    
    /**
     * Setter for property shouldFillShape.
     * @param shouldFillShape New value of property shouldFillShape.
     */
    public void setShouldFillShape(boolean shouldFillShape) {
        boolean oldShouldFillShape = this.shouldFillShape;
        this.shouldFillShape = shouldFillShape;
        propertyChangeSupport.firePropertyChange("shouldFillShape", new Boolean(oldShouldFillShape), new Boolean(shouldFillShape));
    }
    
}
