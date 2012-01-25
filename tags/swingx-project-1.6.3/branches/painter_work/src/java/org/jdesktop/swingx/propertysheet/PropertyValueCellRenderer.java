package org.jdesktop.swingx.propertysheet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.apache.batik.ext.awt.LinearGradientPaint;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPropertySheet;
import org.jdesktop.swingx.color.ColorUtil;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.RectanglePainter;
import org.jdesktop.swingx.propertysheet.BeanProvider;
import org.netbeans.swing.outline.DefaultOutlineCellRenderer;
import org.netbeans.swing.outline.Outline;


public class PropertyValueCellRenderer extends DefaultOutlineCellRenderer {
    private Object bean;
    private BeanProvider provider;
    private PropertyDescriptor pd = null;
    private PropertyValuePanel customEditorPanel;
    
    public PropertyValueCellRenderer(BeanProvider provider) {
        this.provider = provider;
        customEditorPanel = new PropertyValuePanel();
    }
    
    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        this.bean = provider.getBean();
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
        /*
        label.setPreferredSize(new Dimension(50,50));
        label.setMinimumSize(new Dimension(50,50));
        label.setMaximumSize(new Dimension(50,50));
         
         */
        label.setSize(50,50);
        label.setBackground(Color.WHITE);
        label.setForeground(Color.BLACK);
        if(isSelected) {
            label.setBackground(ColorUtil.setSaturation(Color.BLUE,0.2f));
        }
        
        if(value instanceof Category) {
            label.setIcon(null);
            label.setBackground(Color.GRAY);
            label.setForeground(Color.WHITE);
            if(column == 0) {
                label.setText(((Category)value).name);
                label.setBorder(
                        BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.WHITE,0),
                        BorderFactory.createMatteBorder(1,1,1,0,Color.DARK_GRAY)
                        ));
            } else {
                label.setText("");
                label.setBorder(BorderFactory.createMatteBorder(1,0,1,1,Color.DARK_GRAY));
            }
            return label;
        }
        
        if(column == 0) {
            if(value == provider.getBean()) {
                label.setText("The Bean");
                return label;
            }
            
            
            if(value instanceof PropertyDescriptor) {
                PropertyDescriptor prop = (PropertyDescriptor) value;
                PropertyEditor pe = BeanUtils.getPE(prop, provider.getBean());
                label.setText(prop.getDisplayName());
                label.setIcon(null);
                return label;
            }
        }
        
        customEditorPanel.setPropertyEditor(null);
        this.pd = null;
        if (value instanceof PropertyDescriptor) {
            PropertyDescriptor prop = (PropertyDescriptor) value;
            Class type = prop.getPropertyType();
            
            
            // handle booleans
            if (boolean.class.equals(type)  || Boolean.class.equals(type)) {
                Boolean val = (Boolean)BeanUtils.getPropertyValue(prop, bean);
                JCheckBox cb = new JCheckBox("",val.booleanValue());
                cb.setBackground(label.getBackground());
                cb.setOpaque(true);
                customEditorPanel.setEditorComponent(cb);
                return customEditorPanel;
            }
            
            
            PropertyEditor pe = BeanUtils.getPE(prop,bean);
            
            // handle paintable property editors
            if (pe != null && pe.isPaintable()) {
                Object o = BeanUtils.getPropertyValue(prop,bean);
                if(o != null) {
                    pe.setValue(o);
                    customEditorPanel.setPropertyEditor(pe);
                }
                customEditorPanel.setEditorComponent(null); // reset to paintable
                this.pd = prop;
                return customEditorPanel;
            } else {
                // handle normal property editors
                customEditorPanel.setPropertyEditor(null);
                String text = BeanUtils.calculateText(prop, bean);
                JLabel label2 = new JLabel();
                label2.setText(text);
                label2.setBackground(label.getBackground());
                if(!isSelected) {
                    label2.setOpaque(false);
                }
                label2.setOpaque(true);
                customEditorPanel.setEditorComponent(label2);
                return customEditorPanel;
            }
        } else {
            label.setText(""+value);
        }
        return label;
    }
    
}