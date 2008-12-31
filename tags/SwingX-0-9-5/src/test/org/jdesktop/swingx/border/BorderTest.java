/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.swingx.border;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.icon.SortArrowIcon;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;


/**
 * Tests related to SwingX Borders.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class BorderTest extends InteractiveTestCase {

    /**
     * Issue ??-swingx: IconBorder must handle null icon.
     *
     */
    @Test
    public void testIconBorderNullIcon() {
        IconBorder border = new IconBorder();
        JLabel label = new JLabel("dummy");
        border.getBorderInsets(label);
    }
    
    @Test
    public void testIconBorderSetNullIcon() {
        IconBorder border = new IconBorder(new SortArrowIcon(true));
        border.setIcon(null);
        JLabel label = new JLabel("dummy");
        border.getBorderInsets(label);
    }
    /**
     * test new padding api.
     *
     */
    @Test
    public void testPadding() {
        IconBorder border = new IconBorder(new SortArrowIcon(true));
        int oldPadding = border.getPadding();
        int padding = oldPadding + 10;
        border.setPadding(padding);
        assertEquals(padding, border.getPadding());
    }
    
    @Test
    public void testNotNegativePadding() {
        IconBorder border = new IconBorder(new SortArrowIcon(true),
                SwingConstants.EAST, - 10);
        assertTrue(border.getPadding() >= 0);
        border.setPadding(-10);
        assertTrue(border.getPadding() >= 0);
    }
    
    /**
     * test default padding for backward compatibility. 
     * Was hardcoded to 2*4.
     *
     */
    @Test
    public void testDefaultPadding() {
        Icon icon = new SortArrowIcon(true);
        IconBorder border = new IconBorder(icon, SwingConstants.WEST);
        JLabel label = new JLabel("dummy");
        label.setBorder(border);
        Insets insets = border.getBorderInsets(label);
        // internal knowledge: default padding has been 2 * 4
        assertEquals(insets.left, icon.getIconWidth() + 8);
    }
    
    
//------------------- visual checks
    
    /**
     * Visuals: null border, component orientation, padding.
     * 
     */
    public void interactiveIconBorderNullIcon() {
        final Icon icon = new SortArrowIcon(true);
        final IconBorder border = new IconBorder(icon);
        final JLabel label = new JLabel("...dummy............ with icon border");
        Border lineBorder = BorderFactory.createLineBorder(Color.RED, 2);
        label.setBorder(BorderFactory.createCompoundBorder(lineBorder, border));
        Action setIcon = new AbstractActionExt("null icon") {
            public void actionPerformed(ActionEvent e) {
                border.setIcon(null);
                label.repaint();
                setEnabled(false);
            }
            
        };
        Action setPadding = new AbstractActionExt("set padding") {

            public void actionPerformed(ActionEvent e) {
                border.setPadding(border.getPadding() + 10);
                label.repaint();
                
            }
            
        };
        final JXFrame frame = wrapInFrame(label, "IconBorder");
        Action toggleComponentOrientation = new AbstractActionExt("toggle orientation") {
            
            public void actionPerformed(ActionEvent e) {
                ComponentOrientation current = frame.getComponentOrientation();
                if (current == ComponentOrientation.LEFT_TO_RIGHT) {
                    frame.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                } else {
                    frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                    
                }
                frame.validate();
                frame.repaint();
                
            }
            
        };
        addAction(frame, toggleComponentOrientation);
        addAction(frame, setIcon);
        addAction(frame, setPadding);
        frame.setSize(400, 200);
        frame.setVisible(true);

    }

    public static void main(String args[]) {
        BorderTest test = new BorderTest();
        try {
          test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*Mark.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }


}
