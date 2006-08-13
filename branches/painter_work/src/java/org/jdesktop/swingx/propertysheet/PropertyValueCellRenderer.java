package org.jdesktop.swingx.propertysheet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXPropertySheet;
import org.joshy.util.u;


public class PropertyValueCellRenderer extends DefaultTableCellRenderer {
    private final JXPropertySheet jXPropertySheet;

    private Object bean;
    private boolean doPaint = false;
    private PropertyEditor ed = null;
    private PropertyDescriptor pd = null;
    TableCellRenderer boolRend;
    
    public PropertyValueCellRenderer(JXPropertySheet jXPropertySheet) {
        this.jXPropertySheet = jXPropertySheet;
        boolRend = this.jXPropertySheet.getDefaultRenderer(Boolean.class);
    }
    
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
        
        JXPropertySheet sheet = (JXPropertySheet) table;
        bean = sheet.bean;
        
        doPaint = false;
        ed = null;
        this.pd = null;
        if (value instanceof PropertyDescriptor) {
            PropertyDescriptor prop = (PropertyDescriptor) value;
            Class type = prop.getPropertyType();
            if (boolean.class.equals(type)  || Boolean.class.equals(type)) {
                Object val = BeanUtils.getPropertyValue(prop,bean);
                return boolRend.getTableCellRendererComponent(table, val, isSelected, hasFocus, row, column);
            }
            
            
            PropertyEditor pe = BeanUtils.getPE(prop,bean);
            if (pe != null && pe.isPaintable()) {
                doPaint = true;
                ed = pe;
                this.pd = prop;
            } else {
                doPaint = false;
                ed = null;
                String text = BeanUtils.calculateText(prop, bean);
                label.setText(text);
            }
        } else if (value instanceof Category) {
            if(column == 1) {
                label.setText("");
            } else {
                label.setText(((Category)value).name);
            }
        } else {
            label.setText(""+value);
        }
        return label;
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(doPaint) {
            Dimension dim = getSize();
            ed.paintValue(g, new Rectangle(0,0,dim.width,dim.height));
            g.setColor(Color.GREEN);
            g.drawLine(0,0,50,50);
        }
    }
    
    
    
}