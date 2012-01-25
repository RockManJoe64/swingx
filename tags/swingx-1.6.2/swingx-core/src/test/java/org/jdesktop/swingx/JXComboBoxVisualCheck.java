/*
 * $Id$
 *
 * Copyright 2010 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 *
 * @author kschaefer
 */
public class JXComboBoxVisualCheck extends InteractiveTestCase {
    private ComboBoxModel model;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp() {
        model = new DefaultComboBoxModel(new JComboBox().getActionMap().allKeys());
    }
    
    public static void main(String[] args) {
        setSystemLF(true);
        
        JXComboBoxVisualCheck test = new JXComboBoxVisualCheck();
        
        try {
          test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }

    public void testDummy() { }

    public void interactiveTestComboBoxAlternateHighlighter1() {
        JXComboBox combo = new JXComboBox(model);
        combo.addHighlighter(HighlighterFactory.createSimpleStriping(HighlighterFactory.LINE_PRINTER));

        showInFrame(combo, "AlternateRowHighlighter - lineprinter");
    }
}
