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
package org.jdesktop.swingx.plaf.basic;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.JXLoginPanel;
import org.jdesktop.swingx.plaf.LoginPanelUI;
/**
 * Base implementation of the <code>JXLoginPanel</code> UI.
 *
 * @author rbair
 */
public class BasicLoginPanelUI extends LoginPanelUI {
    private JXLoginPanel dlg;
    
    /** Creates a new instance of BasicLoginDialogUI */
    public BasicLoginPanelUI(JXLoginPanel dlg) {
        this.dlg = dlg;
    }
    
  public static ComponentUI createUI(JComponent c) {
    return new BasicLoginPanelUI((JXLoginPanel)c);
  }

    public Image getBanner() {
        int w = 400;
        int h = 60;
        float loginStringX = w * .05f;
        float loginStringY = h * .75f;

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        Font font = UIManager.getFont("JXLoginPanel.bannerFont");
        g2.setFont(font);
        Graphics2D originalGraphics = g2;
        if(!dlg.getComponentOrientation().isLeftToRight()) {
            originalGraphics = (Graphics2D)g2.create();
            g2.scale(-1, 1);
            g2.translate(-w, 0);
            loginStringX = w - (((float)font.getStringBounds(dlg.getBannerText(), 
                    originalGraphics.getFontRenderContext()).getWidth()) + w * .05f);
        } 

        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        //draw a big square
        g2.setColor(UIManager.getColor("JXLoginPanel.bannerDarkBackground"));
        g2.fillRect(0, 0, w, h);

        //create the curve shape
        GeneralPath curveShape = new GeneralPath(GeneralPath.WIND_NON_ZERO);
        curveShape.moveTo(0, h * .6f);
        curveShape.curveTo(w * .167f, h * 1.2f, w * .667f, h * -.5f, w, h * .75f);
        curveShape.lineTo(w, h);
        curveShape.lineTo(0, h);
        curveShape.lineTo(0, h * .8f);
        curveShape.closePath();

        //draw into the buffer a gradient (bottom to top), and the text "Login"
        GradientPaint gp = new GradientPaint(0, h, UIManager.getColor("JXLoginPanel.bannerDarkBackground"),
                0, 0, UIManager.getColor("JXLoginPanel.bannerLightBackground"));
        g2.setPaint(gp);
        g2.fill(curveShape);

        originalGraphics.setColor(UIManager.getColor("JXLoginPanel.bannerForeground"));
        originalGraphics.drawString(dlg.getBannerText(), loginStringX, loginStringY);
        return img;
    }
}
