/*
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * California 95054, U.S.A. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jdesktop.swingx;

import java.util.Arrays;

import javax.swing.JFrame;

import org.jdesktop.swingx.JXLoginPane.SaveMode;

/**
 * Simple demo to show Login Pane..
 * 
 * @author rah003
 */
public class JXLoginPaneDemo  {

    public static void main(String[] args) throws Exception {
        try {
            JXLoginPane panel = new JXLoginPane();
            JFrame frame = JXLoginPane.showLoginFrame(panel);
            panel.setSaveMode(SaveMode.BOTH);
            
            frame.pack();
            frame.setVisible(true);
            
            // server combo
            panel = new JXLoginPane();
            panel.setServers(Arrays.asList(new String[] {"server1","server2"}));
            frame = JXLoginPane.showLoginFrame(panel);
            panel.setSaveMode(SaveMode.BOTH);
            
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    
    
    
}
