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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.apache.batik.ext.awt.LinearGradientPaint;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPropertySheet;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.RectanglePainter;
import org.joshy.util.u;


public class PropertyValueCellRenderer extends DefaultTableCellRenderer {
    private final JXPropertySheet jXPropertySheet;
    
    private Object bean;
    private PropertyDescriptor pd = null;
    //TableCellRenderer boolRend;
    private PropertyValuePanel customEditorPanel;
    private JXLabel categoryRend;
    
    public PropertyValueCellRenderer(JXPropertySheet jXPropertySheet) {
        this.jXPropertySheet = jXPropertySheet;
        //boolRend = this.jXPropertySheet.getDefaultRenderer(Boolean.class);
        customEditorPanel = new PropertyValuePanel();
        categoryRend = new CategoryRenderer();
    }
    
    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
        JXPropertySheet sheet = (JXPropertySheet) table;
        bean = sheet.bean;
        
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
                customEditorPanel.setPropertyEditor(pe);
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
        } else if (value instanceof Category) {
            categoryRend.setText("");
            if(isSelected) {
                //categoryRend.setBackground(UIManager.getColor("Table.selectionBackground"));
                categoryRend.setOpaque(false);
            } else {
                categoryRend.setOpaque(false);
            }
            return categoryRend;
        } else {
            label.setText(""+value);
        }
        return label;
    }
    
}