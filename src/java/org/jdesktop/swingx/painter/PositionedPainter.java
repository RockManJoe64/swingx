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
 * An abstract base class for any painter which can be positioned. This means
 * the painter has some intrinsic size to what it is drawing and
 * can be stretched or aligned both horizontally and vertically.  The
 *
 * 
 * The PositionedPainter class provides the following configuraable properties:
 * 
 * <ul>
 * <li>horizonal - the horizonal alignment (left, center, and right)</li>
 * <li>vertical - the vertical alignment (top, center, and bottom)</li>
 * <li>insets - whitespace on the top, bottom, left, and right.
 * </ul>
 * 
 * @author joshy
 */
public abstract class PositionedPainter<T> extends AbstractPainter<T> {
    
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

    /**
     * An enum which controls horizontal alignment
     */
    public static enum HorizontalAlignment { LEFT, CENTER, RIGHT }

    
    /**
     * An enum which controls vertical alignment
     */
    public static enum VerticalAlignment { TOP, CENTER, BOTTOM }

    
    /**
     * Gets the current horizontal alignment.
     * @return the current horizontal alignment
     */
    public HorizontalAlignment getHorizontal() {
        return horizontal;
    }

    
    /**
     * Gets the current whitespace insets.
     * @return the current insets
     */
    public Insets getInsets() {
        return insets;
    }

    
    /**
     * gets the current vertical alignment
     * @return current vertical alignment
     */
    public VerticalAlignment getVertical() {
        return vertical;
    }

    
    /**
     * indicates if the painter content is stretched horizontally
     * @return the current horizontal stretch value
     */
    public boolean isHorizontalStretch() {
        return horizontalStretch;
    }

    
    /**
     * indicates if the painter content is stretched vertically
     * @return the current vertical stretch value
     */
    public boolean isVerticalStretch() {
        return verticalStretch;
    }

    
    /**
     * Sets a new horizontal alignment. Used to position the content at the left, right, or center.
     * @param horizontal new horizontal alignment
     */
    public void setHorizontal(HorizontalAlignment horizontal) {
        HorizontalAlignment old = this.getHorizontal();
        this.horizontal = horizontal;
        firePropertyChange("horizontal",old,this.horizontal);
    }

    
    /**
     * Sets if the content should be stretched horizontally to fill all available horizontal
     * space (minus the left and right insets).
     * @param horizontalStretch new horizonal stretch value
     */
    public void setHorizontalStretch(boolean horizontalStretch) {
        boolean old = this.isHorizontalStretch();
        this.horizontalStretch = horizontalStretch;
        firePropertyChange("horizontalStretch",old,this.horizontalStretch);
    }

    
    /**
     * Sets the current whitespace insets.
     * @param insets new insets
     */
    public void setInsets(Insets insets) {
        Insets old = this.getInsets();
        this.insets = insets;
        firePropertyChange("insets",old,this.insets);
    }

    
    
    /**
     * Sets a new vertical alignment. Used to position the content at the top, bottom, or center.
     * @param vertical new vertical alignment
     */
    public void setVertical(VerticalAlignment vertical) {
        VerticalAlignment old = this.getVertical();
        this.vertical = vertical;
        firePropertyChange("vertical",old,this.vertical);
    }

    
    /**
     * Sets if the content should be stretched vertically to fill all available vertical
     * space (minus the top and bottom insets).
     * @param verticalStretch new vertical stretch value
     */
    public void setVerticalStretch(boolean verticalStretch) {
        boolean old = this.isVerticalStretch();
        this.verticalStretch = verticalStretch;
        firePropertyChange("verticalStretch",old,this.verticalStretch);
    }
    
    /**
     * a protected method used by subclasses to calculate the final position of the
     * content. Subclasses usually don't need to override this.
     * @param contentWidth The width of the content to be painted
     * @param contentHeight The height of the content to be painted
     * @param width the width of the area that the content will be positioned in
     * @param height the height of the area that the content will be positioned in
     * @return the rectangle for the content to be painted in
     */
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
