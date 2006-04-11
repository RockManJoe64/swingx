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

package org.jdesktop.swingx.color;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * TODO May want to move to org.jdesktop.swingx.util
 *
 * @author joshua.marinacci@sun.com
 */
public class ColorUtil {
    
    /**
     * Returns a new color equal to the old one, except that there is no
     * alpha channel (transparency).
     * @param color the color to remove the alpha (transparency) from
     * @return Color
     */
    public static Color removeAlpha(Color color) {
        return new Color(color.getRed(),color.getGreen(),color.getBlue());
    }
    
    /**
     * Modifies the passed in color by setting a new alpha channel (transparency)
     * and returns the new color. 
     *
     * @param col the color to modify
     * @param alpha the new alpha (transparency) level. Must be an int between 0 and 255
     *
     * @return the new Color
     */
    public static Color setAlpha(Color col, int alpha) {
        return new Color(col.getRed(),col.getGreen(),col.getBlue(),alpha);
    }
    
    /**
     * 
     * Modifies the passed in color by changing it's brightness using HSB
     * calculations. The brightness must be a float between 0 and 1. If 0 the
     * resulting color will be black. If 1 the resulting color will be the brightest
     * possible form of the passed in color.
     * 
     * @param color the color to modify
     * @param brightness the brightness to use in the new color
     * @return the new Color
     */
    public static Color setBrightness(Color color, float brightness) {
        int alpha = color.getAlpha();
        
        float[] cols = Color.RGBtoHSB(color.getRed(),color.getGreen(),color.getBlue(),null);
        cols[2] = brightness;
        Color c2 = color.getHSBColor(cols[0],cols[1],cols[2]);
        
        return setAlpha(c2,alpha);
    }
    
    /**
     * Produces a String represeting the passed in color as a hex value
     * (including the #) suitable for use in html. It does not include 
     * the alpha (transparency) channel in the string.
     * @param color the color to convert
     * @return the hex String
     */
    public static String toHexString(Color color) {
        return "#"+(""+Integer.toHexString(color.getRGB())).substring(2);        
    }
        
    private static Paint checker_texture = null;

    
    /**
     * Obtain a <code>java.awt.Paint</code> instance which draws a checker
     * background of black and white. 
     * Note: The returned instance may be shared.
     * Note: This method should be reimplemented to not use a png resource.
     *
     * @return a Paint implementation
     */
    public static Paint getCheckerPaint() {
	if(checker_texture == null) {
            checker_texture = Color.white;
	    try {
		BufferedImage checker_image = ImageIO.read(
			ColorUtil.class.getResourceAsStream("/icons/checker8.png"));
		Rectangle rect = new Rectangle(0,0,
			checker_image.getWidth(),checker_image.getHeight());
		checker_texture = new TexturePaint(checker_image,rect);
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
        return checker_texture;           
    }
    
    /**
     * Draws an image on top of a component by doing a 3x3 grid stretch of the image
     * using the specified insets.
     */
    public static void tileStretchPaint(Graphics g, 
                JComponent comp,
                BufferedImage img,
                Insets ins) {
        
        int left = ins.left;
        int right = ins.right;
        int top = ins.top;
        int bottom = ins.bottom;
        
        // top
        g.drawImage(img,
                    0,0,left,top,
                    0,0,left,top,
                    null);
        g.drawImage(img,
                    left,                 0, 
                    comp.getWidth() - right, top, 
                    left,                 0, 
                    img.getWidth()  - right, top, 
                    null);
        g.drawImage(img,
                    comp.getWidth() - right, 0, 
                    comp.getWidth(),         top, 
                    img.getWidth()  - right, 0, 
                    img.getWidth(),          top, 
                    null);

        // middle
        g.drawImage(img,
                    0,    top, 
                    left, comp.getHeight()-bottom,
                    0,    top,   
                    left, img.getHeight()-bottom,
                    null);
        
        g.drawImage(img,
                    left,                  top, 
                    comp.getWidth()-right,      comp.getHeight()-bottom,
                    left,                  top,   
                    img.getWidth()-right,  img.getHeight()-bottom,
                    null);
         
        g.drawImage(img,
                    comp.getWidth()-right,     top, 
                    comp.getWidth(),           comp.getHeight()-bottom,
                    img.getWidth()-right, top,   
                    img.getWidth(),       img.getHeight()-bottom,
                    null);
        
        // bottom
        g.drawImage(img,
                    0,comp.getHeight()-bottom, 
                    left, comp.getHeight(),
                    0,img.getHeight()-bottom,   
                    left,img.getHeight(),
                    null);
        g.drawImage(img,
                    left,                    comp.getHeight()-bottom, 
                    comp.getWidth()-right,        comp.getHeight(),
                    left,                    img.getHeight()-bottom,   
                    img.getWidth()-right,    img.getHeight(),
                    null);
        g.drawImage(img,
                    comp.getWidth()-right,     comp.getHeight()-bottom, 
                    comp.getWidth(),           comp.getHeight(),
                    img.getWidth()-right, img.getHeight()-bottom,   
                    img.getWidth(),       img.getHeight(),
                    null);
    }

}
