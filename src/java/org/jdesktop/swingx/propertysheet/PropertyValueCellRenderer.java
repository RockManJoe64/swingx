package org.jdesktop.swingx.propertysheet;

import java.awt.Component;
import java.awt.Graphics;
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
        
        if (value instanceof PropertyDescriptor) {
            PropertyDescriptor prop = (PropertyDescriptor) value;
            Class type = prop.getPropertyType();
            if (boolean.class.equals(type)  || Boolean.class.equals(type)) {
                u.p("it's a boolean");
                Object val = BeanUtils.getPropertyValue(prop,bean);
                return boolRend.getTableCellRendererComponent(table, val, isSelected, hasFocus, row, column);
            }
            
            
            PropertyEditor pe = BeanUtils.getPE(prop,bean);
            if (pe != null && pe.isPaintable()) {
                //u.p("doing painting instead");
                doPaint = true;
                ed = pe;
            } else {
                doPaint = false;
                ed = null;
                String text = BeanUtils.calculateText(prop, bean);
                label.setText(text);
            }
        }  else {
            label.setText(""+value);
        }
        return label;
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(doPaint) {
            //u.p("doing painting");
            ed.paintValue(g, this.getBounds());
        }
    }
    
    
    
}