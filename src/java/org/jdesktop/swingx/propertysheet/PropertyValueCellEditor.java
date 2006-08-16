package org.jdesktop.swingx.propertysheet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.EventObject;
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
    private JTextField editorTextField;
    private PropertyDescriptor pd;
    private Object bean;

    //override to enable single click editing
    public boolean isCellEditable(EventObject evt) {
        if(evt instanceof MouseEvent) {
            if(((MouseEvent)evt).getClickCount() >= 1) {
                return true;
            }
        }
        return super.isCellEditable(evt);
    }
    

    public PropertyValueCellEditor(JXPropertySheet jXPropertySheet) {
        super(new JTextField());
        this.jXPropertySheet = jXPropertySheet;
        editorTextField = new JTextField();
        
        customEditorPanel = new PropertyValuePanel();
    }
    
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        u.p("creatying an editor");
        JComponent tf = (JComponent) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        pd = (PropertyDescriptor) value;
        Class type = pd.getPropertyType();
        
        // use checkboxes for booleans
        if (boolean.class.equals(type)  || Boolean.class.equals(type)) {
            return new BooleanTCE(this, pd, jXPropertySheet.bean, tf);
        }
        
        
        PropertyEditor ed = BeanUtils.getPE(pd, jXPropertySheet.bean);
        // handle paintable PEs and PEs with custom editors
        if(ed != null) {
            if(ed.isPaintable()) {
                customEditorPanel.setPropertyEditor(ed);
                customEditorPanel.setEditorComponent(null);
            }
            if(ed.supportsCustomEditor()) {
                return new CustomEditorTCE(this, ed, tf);
            }
            if(ed.getTags() != null) {
                u.p("prop : '" + pd.getName() + "' supports tags");
                return new ComboBoxTCE(this, pd, ed, jXPropertySheet.bean, tf);
            }
        }
        
        return new PropertyEditorTCE(this, pd, jXPropertySheet.bean, tf);
    }
}

class ComboBoxTCE extends PropertyValuePanel implements CellEditorListener {
    private Object bean;
    private PropertyValueCellEditor pvce;
    private JComboBox cb;
    private PropertyEditor ed;
    private PropertyDescriptor pd;
    
    public ComboBoxTCE(PropertyValueCellEditor fpvce, PropertyDescriptor pd, PropertyEditor ed, Object bean, JComponent prototype) {
        this.pvce = fpvce;
        String[] vals = ed.getTags();
        cb = new JComboBox(vals);
        this.ed = ed;
        this.pd = pd;
        this.bean = bean;
        setEditorComponent(cb);
        pvce.addCellEditorListener(this);
    }
    
    public void editingStopped(ChangeEvent e) {
        u.p("editing stopped");
        String value = (String)cb.getSelectedItem();
        BeanUtils.setPropertyFromText(pd, bean, value);
        cleanup();
    }
    
    
    public void editingCanceled(ChangeEvent e) {
        u.p("editing canceled");
        cleanup();
    }
    
    private void cleanup() {
        bean = null;
        pd = null;
        pvce.removeCellEditorListener(this);
    }
}

class BooleanTCE extends PropertyValuePanel implements CellEditorListener {
    private JCheckBox cb;
    private PropertyDescriptor pd;
    private Object bean;
    private PropertyValueCellEditor pvce;
    public BooleanTCE(PropertyValueCellEditor fpvce, PropertyDescriptor pd, Object bean, JComponent prototype) {
        this.pvce = fpvce;
        cb = new JCheckBox("",false);
        cb.setOpaque(true);
        //cb.setForeground(prototype.getForeground());
        cb.setBackground(prototype.getBackground());
        this.pd = pd;
        this.bean = bean;
        setEditorComponent(cb);
        pvce.addCellEditorListener(this);
    }
    
    public void editingCanceled(ChangeEvent changeEvent) {
        cleanup();
    }
    
    public void editingStopped(ChangeEvent changeEvent) {
        u.p("editing stopped");
        String text = Boolean.toString(cb.isSelected());
        BeanUtils.setPropertyFromText(pd, bean, text);
        cleanup();
    }
    private void cleanup() {
        bean = null;
        pd = null;
        pvce.removeCellEditorListener(this);
    }
}



class PropertyEditorTCE extends PropertyValuePanel {
    
    private PropertyValueCellEditor pvce;
    private JTextField editorTextField;
    private PropertyDescriptor pd;
    private Object bean;
    
    public PropertyEditorTCE(final PropertyValueCellEditor fpvce,
            final PropertyDescriptor fpd, final Object fbean, JComponent prototype) {
        this.pvce = fpvce;
        this.pd = fpd;
        this.bean = fbean;
        
        editorTextField = new JTextField();
        editorTextField.setText(BeanUtils.calculateText(pd, bean));
        setEditorComponent(editorTextField);
        pvce.addCellEditorListener(new CellEditorListener() {
            public void editingCanceled(ChangeEvent changeEvent) {
                u.p("editing canceled");
                cleanup();
            }
            public void editingStopped(ChangeEvent changeEvent) {
                u.p("editing stopped");
                String text = editorTextField.getText();
                //u.p("text = " + text + " bean = " + bean);
                BeanUtils.setPropertyFromText(pd,bean,text);
                cleanup();
                //u.p("done === ");
            }
            private void cleanup() {
                bean = null;
                pd = null;
                pvce.removeCellEditorListener(this);
            }
        });
    }
}

class CustomEditorTCE extends PropertyValuePanel {
    
    private PropertyValueCellEditor pvce;
    
    public CustomEditorTCE(PropertyValueCellEditor pvce, PropertyEditor ed, JComponent prototype) {
        this.pvce = pvce;
        this.setPropertyEditor(ed);
        setEditorComponent(null);
        setCustomEditor(ed.getCustomEditor());
    }
}



