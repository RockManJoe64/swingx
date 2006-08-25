/*
 * PositionedPainter.java
 *
 * Created on July 31, 2006, 3:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.Insets;
import java.awt.Rectangle;

/**
 *
 * @author joshy
 */
public abstract class PositionedPainter extends AbstractPainter {
    
    /**
     * Specifies how to draw the image, i.e. what kind of Style to use
     * when drawing
     */
    private VerticalAlignment vertical = VerticalAlignment.CENTER;
    private HorizontalAlignment horizontal = HorizontalAlignment.CENTER;
    private Insets insets = new Insets(0,0,0,0);
    private boolean verticalStretch = false;
    private boolean horizontalStretch = false;

    /**
     * Creates a new instance of PositionedPainter
     */
    public PositionedPainter() {
    }

    public static enum HorizontalAlignment { LEFT, CENTER, RIGHT }

    
    public static enum VerticalAlignment { TOP, CENTER, BOTTOM }

    
    public HorizontalAlignment getHorizontal() {
        return horizontal;
    }

    
    public Insets getInsets() {
        return insets;
    }

    
    public VerticalAlignment getVertical() {
        return vertical;
    }

    
    public boolean isHorizontalStretch() {
        return horizontalStretch;
    }

    
    public boolean isVerticalStretch() {
        return verticalStretch;
    }

    
    public void setHorizontal(HorizontalAlignment horizontal) {
        HorizontalAlignment old = this.getHorizontal();
        this.horizontal = horizontal;
        firePropertyChange("horizontal",old,this.horizontal);
    }

    
    public void setHorizontalStretch(boolean horizontalStretch) {
        boolean old = this.isHorizontalStretch();
        this.horizontalStretch = horizontalStretch;
        firePropertyChange("horizontalStretch",old,this.horizontalStretch);
    }

    
    public void setInsets(Insets insets) {
        Insets old = this.getInsets();
        this.insets = insets;
        firePropertyChange("insets",old,this.insets);
    }

    
    
    public void setVertical(VerticalAlignment vertical) {
        VerticalAlignment old = this.getVertical();
        this.vertical = vertical;
        firePropertyChange("vertical",old,this.vertical);
    }

    
    public void setVerticalStretch(boolean verticalStretch) {
        boolean old = this.isVerticalStretch();
        this.verticalStretch = verticalStretch;
        firePropertyChange("verticalStretch",old,this.verticalStretch);
    }
    
    protected Rectangle calculatePosition(final int contentWidth, final int contentHeight, 
            final int width, final int height) {
        
        Rectangle rect = new Rectangle();
        rect.width = contentWidth;
        rect.height = contentHeight;
        
        if(isHorizontalStretch()) {
            rect.width = width - insets.left - insets.right;
        }
        
        if(isVerticalStretch()) {
            rect.height = height - insets.top - insets.bottom;
        }
        rect.x = calculateX(rect.width, width);
        rect.y = calculateY(rect.height, height);
        return rect;
    }

    private int calculateY(final int imgHeight, final int height) {
        int y = 0;
        if(getVertical() == VerticalAlignment.TOP) {
            y = 0;
            y+= insets.top;
        }
        if(getVertical() == VerticalAlignment.CENTER) {
            y = (height-imgHeight)/2;
            y += insets.top;
        }
        if(getVertical() == VerticalAlignment.BOTTOM) {
            y = height-imgHeight;
            y-= insets.bottom;
        }
        return y;
    }

    private int calculateX(final int imgWidth, final int width) {
        int x = 0;
        if(getHorizontal() == HorizontalAlignment.LEFT) {
            x = 0;
            x+= insets.left;
        }
        if(getHorizontal() == HorizontalAlignment.CENTER) {
            x = (width-imgWidth)/2;
            x += insets.left;
        }
        if(getHorizontal() == HorizontalAlignment.RIGHT) {
            x = width-imgWidth;
            x-= insets.right;
        }
        return x;
    }
}
