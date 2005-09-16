/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf.metal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI;

/**
 * Metal implementation of the <code>JXTaskPane</code> UI. <br>
 * 
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 */
public class MetalTaskPaneUI extends BasicTaskPaneUI {

  public static ComponentUI createUI(JComponent c) {
    return new MetalTaskPaneUI();
  }

  protected void installDefaults() {
    super.installDefaults();
    group.setOpaque(false);
  }

  protected Border createPaneBorder() {
    return new MetalPaneBorder();
  }

  /**
   * The border of the taskpane group paints the "text", the "icon",
   * the "expanded" status and the "special" type.
   *  
   */
  class MetalPaneBorder extends PaneBorder {

    protected void paintExpandedControls(JXTaskPane group, Graphics g, int x,
      int y, int width, int height) {
      ((Graphics2D)g).setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
      
      g.setColor(getPaintColor(group));
      paintRectAroundControls(group, g, x, y, width, height, g.getColor(), g
        .getColor());
      paintChevronControls(group, g, x, y, width, height);
      
      ((Graphics2D)g).setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_OFF);      
    }

    @Override
    protected boolean isMouseOverBorder() {
      return true;
    }    
  }

}