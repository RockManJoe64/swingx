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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;
import org.jdesktop.swingx.util.Resize;
import org.joshy.util.u;

/**
 * "Paints" text at the given location. The location should be a point where
 * the x and y values are in the range of 0 to 1. Similar to the CSS background
 * positioning algorithm, each value will be scaled along each axis of the component
 * this TextPainter is painting, and then used to position the text. A value of
 * 0,0 would put the text in the upper lefthand corner. 0.5,0.5 would position the
 * text in the center and 1.0,1.0 would be at the lower righthand corner. For a more
 * complete defintion of the positioning algorithm see the
 * <a href="http://www.w3.org/TR/CSS21/colors.html#propdef-background-position">CSS 2.1 spec</a>.
 *
 * @author rbair
 */
public class TextPainter extends PositionedPainter {
    private String text = "";
    private Font font = null;
    private Paint paint;
    private boolean snapPaint;
    
    /** Creates a new instance of TextPainter */
    public TextPainter() {
        this("");
    }
    
    public TextPainter(String text) {
        this(text, null);
    }
    
    public TextPainter(String text, Font font) {
        this(text, font, null);
    }
    
    public TextPainter(String text, Font font, Paint paint) {
        this.text = text;
        this.font = font;
        this.paint = paint;
    }
    
    public void setFont(Font f) {
        Font old = getFont();
        this.font = f;
        firePropertyChange("font", old, getFont());
    }
    
    public Font getFont() {
        return font;
    }
    
    public void setText(String text) {
        String old = getText();
        this.text = text == null ? "" : text;
        firePropertyChange("text", old, getText());
    }
    
    public String getText() {
        return text;
    }
    
    public void setPaint(Paint paint) {
        Paint old = getPaint();
        this.paint = paint;
        firePropertyChange("paint", old, getPaint());
    }
    
    public Paint getPaint() {
        return paint;
    }
    
    public boolean isSnapPaint() {
        return snapPaint;
    }
    
    public void setSnapPaint(boolean snapPaint) {
        boolean old = this.isSnapPaint();
        this.snapPaint = snapPaint;
        firePropertyChange("snapPaint",old,this.snapPaint);
    }
    
    protected void paintBackground(Graphics2D g, JComponent component, int width, int height) {
        // prep the various text attributes
        Font font = getFont();
        if (font == null) {
            font = component.getFont();
        }
        if (font == null) {
            font = new Font("Dialog", Font.PLAIN, 18);
        }
        
        if (font != null) {
            g.setFont(font);
        }
        
        Paint paint = getPaint();
        if(paint == null) {
            paint = component.getForeground();
        }
        
        
        
        // prep the text
        String text = getText();
        //make components take priority if(text == null || text.trim().equals("")) {
        if(component instanceof JTextComponent) {
            text = ((JTextComponent)component).getText();
        }
        if(component instanceof JLabel) {
            text = ((JLabel)component).getText();
        }
        if(component instanceof AbstractButton) {
            text = ((AbstractButton)component).getText();
        }
        //}
        
        // get the font metrics
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        Rectangle2D rect = metrics.getStringBounds(text,g);
        //Rectangle rect = calculatePosition(imgWidth, imgHeight, width, height);
        //g.drawImage(img, rect.x, rect.y, rect.width, rect.height, null);
        
        int tw = metrics.stringWidth(text);
        int th = metrics.getHeight();
        Rectangle res = calculatePosition(tw, th, width, height);
        
        g.translate(res.x, res.y);
        
        if(isSnapPaint()) {
            paint = AreaPainter.calculateSnappedPaint(paint, res.width, res.height);//width, height);
        }
        
        if (paint != null) {
            g.setPaint(paint);
        }
        
        
        
        //double x = location.getX() * (width  - tw);
        //double y = location.getY() * (height - th);
        //y += metrics.getAscent();
        
        //double stringWidth = SwingUtilities.computeStringWidth(metrics, text);
        //x -= stringWidth/2;
        g.drawString(text, 0, 0 + metrics.getAscent());
        g.translate(-res.x,-res.y);
    }
}
