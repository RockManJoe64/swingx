/*
 * Test.java
 *
 * Created on August 19, 2006, 5:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painterset;


import javax.swing.*;

import javax.swing.plaf.basic.*;

import java.awt.*;

import java.awt.event.*;



public class Test {
    
    public static void main(String[] args)
    
    {
        
        JTabbedPane pane = new JTabbedPane();
        
        pane.setUI(new TestPlaf());
        
        pane.add("Panel 1",new JLabel("Content of Panel 1"));
        
        pane.add("Panel 2",new JLabel("Content of Panel 2"));
        
        pane.add("Panel 3",new JLabel("Content of Panel 3"));
        
        pane.add("Panel 4",new JLabel("Content of Panel 4"));
        
        JFrame frame = new JFrame();
        frame.getContentPane().add(pane);
        frame.setSize(400,100);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    
    
    
}	// End of main class
