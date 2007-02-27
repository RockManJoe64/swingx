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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Method;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jdesktop.swingx.JXPropertySheet2;
import org.jdesktop.swingx.color.ColorUtil;
import org.jdesktop.swingx.util.WindowUtils;

/**
 *
 * @author joshy
 */
public class PropertyValuePanel extends JPanel {
    
    private JLabel paintableEditor;
    private JComponent editor;
    private PropertyEditor propertyEditor;
    private CustomEditorButton customEditorButton;
    
    private PropertyDescriptor propertyDescriptor;
    
    private Object bean;
    
    private JXPropertySheet2 sheet;
    
    /** Creates a new instance of PropertyValuePanel */
    public PropertyValuePanel() {
        customEditorButton = new CustomEditorButton();
        customEditorButton.setFocusable(false);
        customEditorButton.setFocusPainted(false);
        customEditorButton.setMargin(new Insets(0,0,0,0));
        customEditorButton.setPreferredSize(new Dimension(20,20));
        paintableEditor = new JLabel() {
            public void paintComponent(Graphics g) {
                Paint p = ColorUtil.getCheckerPaint(Color.GRAY, Color.WHITE, 6);
                ((Graphics2D)g).setPaint(p);
                g.fillRect(0,0,getWidth(), getHeight());
                if(getPropertyEditor() != null) {
                    if(getPropertyEditor().isPaintable()) {
                        getPropertyEditor().paintValue(g, new Rectangle(0, 0, getWidth(), getHeight()));
                    }
                } else {
                    g.drawLine(0,0,getWidth(),getHeight());
                    g.drawLine(0,0,getWidth(),getHeight());
                }
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
                final JFrame frame = new JFrame("Edit");
                final PropertyChangeListener pcl = new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                        p("got a change");
                        writeValue();
                    }
                };
                if(propertyEditor != null) {
                    propertyEditor.addPropertyChangeListener(pcl);
                }
                frame.setLayout(new BorderLayout());
                frame.add(customEditor,"Center");
                JButton close = new JButton("Close");
                close.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        frame.setVisible(false);
                        frame.dispose();
                        p("property editor = " + propertyEditor);
                        p("new value = " + propertyEditor.getValue());
                        p("desc = " + propertyDescriptor);
                        writeValue();
                        if(propertyEditor != null) {
                            propertyEditor.removePropertyChangeListener(pcl);
                        }
                        p("finishing the editing");
                        if(sheet != null) {
                            sheet.fireChangeEvent();
                        }
                    }
                    
                });
                frame.add(close, "South");
                frame.pack();
                frame.setLocation(WindowUtils.getPointForCentering(frame));
                frame.setVisible(true);
            }
        }
    }
    
    public PropertyEditor getPropertyEditor() {
        return propertyEditor;
    }
    
    public void setPropertyEditor(PropertyEditor propertyEditor) {
        this.propertyEditor = propertyEditor;
    }
    
    public void setPropertyDescriptor(PropertyDescriptor desc) {
        this.propertyDescriptor = desc;
    }
    
    public void setBean(Object bean) {
        this.bean = bean;
    }
    
    public void setPropertySheet(JXPropertySheet2 sheet) {
        this.sheet = sheet;
    }
    
    private void writeValue() {
        if(propertyDescriptor != null) {
            try {
                Method meth = propertyDescriptor.getWriteMethod();
                p("method = " + meth.getName());
                p("type = " + meth.getParameterTypes());
                for(Class clss : meth.getParameterTypes()) {
                    p("class = " + clss.getName());
                }
                
                p("bean = " + bean);
                meth.invoke(bean, propertyEditor.getValue());
            } catch (Exception ex) {
                p(ex);
            }
            paintableEditor.repaint();
        }
    }
    
    private static void p(String str) {
        System.out.println(str);
    }
    private static void p(Throwable thr) {
        System.out.println(thr.getMessage());
        thr.printStackTrace();
    }
}
