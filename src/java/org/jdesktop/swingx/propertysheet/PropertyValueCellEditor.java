package org.jdesktop.swingx.propertysheet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import org.jdesktop.swingx.JXPropertySheet;
import org.jdesktop.swingx.util.WindowUtils;
import org.joshy.util.u;


public class PropertyValueCellEditor extends DefaultCellEditor {
    private final JXPropertySheet jXPropertySheet;
    private PropertyValuePanel customEditorPanel;
    
    public PropertyValueCellEditor(JXPropertySheet jXPropertySheet) {
        super(new JTextField());
        this.jXPropertySheet = jXPropertySheet;
        
        customEditorPanel = new PropertyValuePanel();
        
        this.addCellEditorListener(new CellEditorListener() {
            public void editingCanceled(ChangeEvent changeEvent) {
                u.p("editing canceled");
            }
            public void editingStopped(ChangeEvent changeEvent) {
                u.p("editing stopped");
            }
        });
    }
    
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JTextField tf = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        PropertyDescriptor pd = (PropertyDescriptor) value;
        Class type = pd.getPropertyType();
        // use checkboxes for booleans
        
        if (boolean.class.equals(type)  || Boolean.class.equals(type)) {
            JCheckBox cb = new JCheckBox("",false);
            cb.setBackground(tf.getBackground());
            cb.setOpaque(true);
            customEditorPanel.setEditorComponent(cb);
            return customEditorPanel;
        }
        
        PropertyEditor ed = BeanUtils.getPE(pd, jXPropertySheet.bean);
        if(ed != null) {
            if(ed.isPaintable()) {
                customEditorPanel.ed = ed;
                customEditorPanel.setEditorComponent(null);
            }
            if(ed.supportsCustomEditor()) {
                customEditorPanel.setEditorComponent(null);
                customEditorPanel.setCustomEditor(ed.getCustomEditor());
                return customEditorPanel;
            }
        }
        //customEditorPanel.setCustomEditor(null);
        tf.setText(BeanUtils.calculateText(pd, jXPropertySheet.bean));//ed.getAsText())
        customEditorPanel.setEditorComponent(tf);
        return customEditorPanel;
    }
    
}