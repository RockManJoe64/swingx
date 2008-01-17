/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.JWindow;
import javax.swing.RootPaneContainer;
import javax.swing.UIManager;

/**
 * Contains tests for SwingXUtilities.
 * 
 * @author Jeanette Winzenburg
 */
public class SwingXUtilitiesTest extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(SwingXUtilitiesTest.class.getName());
    public static void main(String args[]) {
        setSystemLF(true);
//        Locale.setDefault(new Locale("es"));
        SwingXUtilitiesTest test = new SwingXUtilitiesTest();
        try {
          test.runInteractiveTests();
//            test.runInteractiveTests("interactive.*Compare.*");
//            test.runInteractiveTests("interactive.*Tree.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    
    
    
    @Override
    protected void setUp() throws Exception {
        setSystemLF(true);
    }



    public void testUpdateAllComponentTreeUIs() {
        // This test will not work in a headless configuration.
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run test - headless environment");
            return;
        }
        if (isCrossPlatformLFSameAsSystem()) {
            LOG.info("cannot run test - no safe LFs to toggle");
            return;
        }
        List<RootPaneContainer> toplevels = new ArrayList<RootPaneContainer>();
        for (int i = 0; i < 10; i++) {
            JXFrame frame = new JXFrame();
            toplevels.add(frame);
            toplevels.add(new JDialog(frame));
            toplevels.add(new JWindow(frame));
        }
        // sanity
        assertTrue("assumption is to start with system ll", 
                UIManager.getLookAndFeel().isNativeLookAndFeel());
        setSystemLF(false);
        SwingXUtilities.updateAllComponentTreeUIs();
        // sanity
        for (RootPaneContainer window : toplevels) {
            JRootPane rootPane = window.getRootPane();
            assertEquals(UIManager.get(rootPane.getUIClassID()),  
                    rootPane.getUI().getClass().getName());
        }
    }



    /**
     * @return boolean indicating if system and 
     *   cross platform LF are different.
     */
    private boolean isCrossPlatformLFSameAsSystem() {
        return UIManager.getCrossPlatformLookAndFeelClassName().equals(
                UIManager.getSystemLookAndFeelClassName());
    }

}
