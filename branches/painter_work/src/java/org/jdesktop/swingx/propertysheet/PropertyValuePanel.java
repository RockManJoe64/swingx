/*
 * PropertyValuePanel.java
 *
 * Created on August 13, 2006, 1:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.propertysheet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jdesktop.swingx.util.WindowUtils;
import org.joshy.util.u;

/**
 *
 * @author joshy
 */
public class PropertyValuePanel extends JPanel {
    
    private JLabel paintableEditor;
    private JComponent editor;
    public PropertyEditor ed;
    public CustomEditorButton customEditorButton;
    
    /** Creates a new instance of PropertyValuePanel */
    public PropertyValuePanel() {
        customEditorButton = new CustomEditorButton();
        paintableEditor = new JLabel() {
            public void paintComponent(Graphics g) {
                ed.paintValue(g, new Rectangle(0, 0, getWidth(), getHeight()));
                super.paintComponent(g);
            }
        };
        setEditorComponent(paintableEditor);
    }
    
    public void setEditorComponent(JComponent comp) {
        this.editor = comp;
        removeAll();
        setLayout(new BorderLayout());
        if(editor == null) {
            add(paintableEditor);
        } else {
            add(editor,"Center");
        }
        add(customEditorButton,"East");
    }
    
    public void setCustomEditor(Component comp) {
        customEditorButton.setCustomEditor(comp);
    }
    
    private class CustomEditorButton extends JButton implements ActionListener {
        private Component customEditor;
        
        public CustomEditorButton() {
            super("..");
            addActionListener(this);
        }
        
        public void setCustomEditor(Component component) {
            this.customEditor = component;
        }
        
        public void actionPerformed(ActionEvent e) {
            if(customEditor != null) {
                JFrame frame = new JFrame("Edit");
                frame.setLayout(new BorderLayout());
                frame.add(customEditor,"Center");
                frame.add(new JButton("Close"),"South");
                frame.pack();
                frame.setLocation(WindowUtils.getPointForCentering(frame));
                frame.setVisible(true);
            }
        }
    }
}
