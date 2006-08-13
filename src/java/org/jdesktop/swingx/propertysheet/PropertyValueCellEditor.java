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
    private DefaultCellEditor boolEd;
    
    public PropertyValueCellEditor(JXPropertySheet jXPropertySheet) {
        super(new JTextField());
        this.jXPropertySheet = jXPropertySheet;
        boolEd = new DefaultCellEditor(new JCheckBox());
        
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
        u.p("type = " + type);
        // use checkboxes for booleans
        
        if (boolean.class.equals(type)  || Boolean.class.equals(type)) {
            u.p("it's a boolean");
            return boolEd.getTableCellEditorComponent(table,value,isSelected,row,column);
        }
        
        PropertyEditor ed = BeanUtils.getPE(pd, jXPropertySheet.bean);
        u.p("using prop editor: " + ed);
        if(ed != null) {
            if(ed.isPaintable()) {
                customEditorPanel.ed = ed;
            }
            if(ed.supportsCustomEditor()) {
                u.p("there's a custom editor on: " + pd.getName());
                customEditorPanel.customEditorButton.setCustomEditor(ed.getCustomEditor());
                return customEditorPanel;
            }
        }
        tf.setText(BeanUtils.calculateText(pd, jXPropertySheet.bean));//ed.getAsText())
        return tf;
    }
    
}