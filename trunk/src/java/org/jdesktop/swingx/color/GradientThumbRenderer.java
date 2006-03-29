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
import java.awt.Graphics2D;
import java.awt.Image;
import javax.imageio.ImageIO;
import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.JXGradientChooser;

public class GradientThumbRenderer implements JXMultiThumbSlider.ThumbRenderer {
    private final JXGradientChooser gradientPicker;
    private JXMultiThumbSlider slider;
    private Image thumb_black;
    private Image thumb_gray;

    public GradientThumbRenderer(JXGradientChooser gradientPicker, JXMultiThumbSlider slider) {
	super();
	this.gradientPicker = gradientPicker;
	this.slider = slider;

	try {
	    thumb_black = ImageIO.read(this.getClass().getResourceAsStream("/icons/thumb_black.png"));
	    thumb_gray = ImageIO.read(this.getClass().getResourceAsStream("/icons/thumb_gray.png"));
	} catch (Exception ex)		{
	    ex.printStackTrace();
	}
    }

    public void paintThumb(Graphics2D g, JXMultiThumbSlider.ThumbComp thumb, int index, boolean selected) {
	int w = thumb.getWidth();
	Color c = (Color)gradientPicker.getSlider().getModel().getThumbAt(index).getObject();
	c = ColorUtil.removeAlpha(c);
	g.setColor(c);
	g.fillRect(0, 0, w - 1, w - 1);
	if (selected) {
	    g.drawImage(thumb_black, 0, 0, null);
	} else {
	    g.drawImage(thumb_gray, 0, 0, null);
	}
    }
}
