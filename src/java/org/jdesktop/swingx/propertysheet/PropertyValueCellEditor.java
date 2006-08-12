package org.jdesktop.swingx.propertysheet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.jdesktop.swingx.JXPropertySheet;
import org.jdesktop.swingx.util.WindowUtils;
import org.joshy.util.u;


public class PropertyValueCellEditor extends DefaultCellEditor {
    private final JXPropertySheet jXPropertySheet;
    private CustomEditorButton customEditorButton;
    private JPanel customEditorPanel;
    private DefaultCellEditor boolEd;
    
    public PropertyValueCellEditor(JXPropertySheet jXPropertySheet) {
        super(new JTextField());
        this.jXPropertySheet = jXPropertySheet;
        boolEd = new DefaultCellEditor(new JCheckBox());
        customEditorButton = new CustomEditorButton();
        customEditorPanel = new JPanel();
        customEditorPanel.setLayout(new BorderLayout());
        customEditorPanel.add(new JLabel("----"),"Center");
        customEditorPanel.add(customEditorButton,"East");
    }
    
    class CustomEditorButton extends JButton implements ActionListener {
        private Component customEditor;
        
        public CustomEditorButton() {
            super("..");
            addActionListener(this);
        }

        private void setCustomEditor(Component component) {
            this.customEditor = component;
        }

        public void actionPerformed(ActionEvent e) {
            if(customEditor != null) {
                JFrame frame = new JFrame("Edit");
                frame.add(customEditor);
                frame.pack();
                frame.setLocation(WindowUtils.getPointForCentering(frame));
                frame.setVisible(true);
            }
        }
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
        if(ed.supportsCustomEditor()) {
            u.p("there's a custom editor on: " + pd.getName());
            customEditorButton.setCustomEditor(ed.getCustomEditor());
            return customEditorPanel;
        }
        //            ed.setValue()
        tf.setText(BeanUtils.calculateText(pd, jXPropertySheet.bean));//ed.getAsText())
        return tf;
    }
    /*
    public Object getCellEditorValue() {
        
    } 
     */  
    /*
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    }
     
     
    public Object getCellEditorValue() {
    }
     
    public boolean isCellEditable(EventObject anEvent) {
    }
     
    public boolean shouldSelectCell(EventObject anEvent) {
    }
     
    public boolean stopCellEditing() {
    }
     
    public void cancelCellEditing() {
    }
     
    public void addCellEditorListener(CellEditorListener l) {
    }
     
    public void removeCellEditorListener(CellEditorListener l) {
    }
     */
    
}