package org.jdesktop.swingx.propertysheet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import org.jdesktop.swingx.JXPropertySheet2;
import org.jdesktop.swingx.util.WindowUtils;


public class PropertyValueCellEditor extends DefaultCellEditor {
    private final BeanProvider jXPropertySheet;
    private PropertyValuePanel customEditorPanel;
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
    
    
    public PropertyValueCellEditor(BeanProvider jXPropertySheet) {
        super(new JTextField());
        this.jXPropertySheet = jXPropertySheet;
        customEditorPanel = new PropertyValuePanel();
    }
    
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // u.p("creatying an editor");
        JComponent tf = (JComponent) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        pd = (PropertyDescriptor) value;
        Class type = pd.getPropertyType();
        
        // use checkboxes for booleans
        if (boolean.class.equals(type)  || Boolean.class.equals(type)) {
            return new BooleanTCE(this, pd, jXPropertySheet.getBean(), tf,
                    (JXPropertySheet2)jXPropertySheet);
        }
        
        
        PropertyEditor ed = BeanUtils.getPE(pd, jXPropertySheet.getBean());
        // handle paintable PEs and PEs with custom editors
        if(ed != null) {
            if(ed.isPaintable()) {
                customEditorPanel.setPropertyEditor(ed);
                customEditorPanel.setEditorComponent(null);
            }
            if(ed.supportsCustomEditor()) {
//                u.p("using a custom editor for: " + pd.getName());
                return new CustomEditorTCE(this, pd, ed, jXPropertySheet.getBean(), tf,
                        (JXPropertySheet2)jXPropertySheet);
            }
            if(ed.getTags() != null) {
//                u.p("prop : '" + pd.getName() + "' supports tags");
                return new ComboBoxTCE(this, pd, ed, jXPropertySheet.getBean(), tf,
                        (JXPropertySheet2)jXPropertySheet);
            }
        }
        
        return new PropertyEditorTCE(this, pd, jXPropertySheet.getBean(), tf,
                (JXPropertySheet2)jXPropertySheet);
    }
}

class ComboBoxTCE extends PropertyValuePanel implements CellEditorListener {
    private Object bean;
    private PropertyValueCellEditor pvce;
    private JComboBox cb;
    private PropertyEditor ed;
    private PropertyDescriptor pd;
    private JXPropertySheet2 sheet;
    public ComboBoxTCE(PropertyValueCellEditor fpvce,
            PropertyDescriptor pd, PropertyEditor ed,
            Object bean, JComponent prototype,
            final JXPropertySheet2 sheet) {
        this.pvce = fpvce;
        this.sheet = sheet;
        String[] vals = ed.getTags();
        cb = new JComboBox(vals);
        this.ed = ed;
        this.pd = pd;
        this.bean = bean;
        setEditorComponent(cb);
        pvce.addCellEditorListener(this);
        //u.p("creating a combo box editor");
        cb.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent focusEvent) {
            }
            public void focusLost(FocusEvent focusEvent) {
//                u.p("lost");
                cleanup();
            }
        });
        cb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
//                u.p("action event");
                setBeanValue();
            }
        });
    }
    
    private void setBeanValue() {
        String value = (String)cb.getSelectedItem();
        BeanUtils.setPropertyFromText(pd, bean, value);
        sheet.fireChangeEvent();
    }
    
    // i don't think this is ever called
    public void editingStopped(ChangeEvent e) {
        setBeanValue();
        cleanup();
        p("editing stopped");
    }
    
    // or this!!!
    public void editingCanceled(ChangeEvent e) {
        cleanup();
    }
    
    private void cleanup() {
        bean = null;
        pd = null;
        pvce.removeCellEditorListener(this);
    }
    private static void p(String str) {
        System.out.println(str);
    }
    private static void p(Throwable thr) {
        System.out.println(thr.getMessage());
        thr.printStackTrace();
    }
}

class BooleanTCE extends PropertyValuePanel implements CellEditorListener {
    private JCheckBox cb;
    private PropertyDescriptor pd;
    private Object bean;
    private PropertyValueCellEditor pvce;
    private JXPropertySheet2 sheet;
    public BooleanTCE(PropertyValueCellEditor fpvce,
            PropertyDescriptor pd, Object bean, JComponent prototype,
            final JXPropertySheet2 sheet) {
        this.pvce = fpvce;
        this.sheet = sheet;
        cb = new JCheckBox("",false);
        Object value = BeanUtils.getPropertyValue(pd,bean);
//        u.p("value = " + value);
        if(value instanceof Boolean) {
            cb.setSelected(((Boolean)value).booleanValue());
//            u.p("setting cb to: " + cb.isSelected());
        }
        cb.setOpaque(true);
        //cb.setForeground(prototype.getForeground());
        cb.setBackground(prototype.getBackground());
        this.pd = pd;
        this.bean = bean;
        setEditorComponent(cb);
        pvce.addCellEditorListener(this);
        cb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
//                u.p("action listener for boolean tce");
                setBeanValue();
            }
        });
    }
    
    public void editingCanceled(ChangeEvent changeEvent) {
        cleanup();
    }
    
    private void setBeanValue() {
        String text = Boolean.toString(cb.isSelected());
        BeanUtils.setPropertyFromText(pd, bean, text);
        sheet.fireChangeEvent();
    }
    
    public void editingStopped(ChangeEvent changeEvent) {
//        u.p("editing stopped on boolean tce");
        setBeanValue();
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
            final PropertyDescriptor fpd, final Object fbean, JComponent prototype,
            final JXPropertySheet2 sheet) {
        this.pvce = fpvce;
        this.pd = fpd;
        this.bean = fbean;
        
        editorTextField = new JTextField();
        editorTextField.setText(BeanUtils.calculateText(pd, bean));
        setEditorComponent(editorTextField);
        pvce.addCellEditorListener(new CellEditorListener() {
            public void editingCanceled(ChangeEvent changeEvent) {
                cleanup();
            }
            public void editingStopped(ChangeEvent changeEvent) {
                String text = editorTextField.getText();
                //u.p("text = " + text + " bean = " + bean);
                BeanUtils.setPropertyFromText(pd,bean,text);
                cleanup();
                sheet.fireChangeEvent();
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
    
    public CustomEditorTCE(PropertyValueCellEditor pvce, PropertyDescriptor pd,
            PropertyEditor ed, Object bean, JComponent prototype,
            JXPropertySheet2 sheet) {
        this.pvce = pvce;
        this.setPropertyEditor(ed);
        setEditorComponent(null);
        setCustomEditor(ed.getCustomEditor());
        setPropertyDescriptor(pd);
        setBean(bean);
        setPropertySheet(sheet);
        // i don't think this is used
        pvce.addCellEditorListener(new CellEditorListener() {
            public void editingCanceled(ChangeEvent changeEvent) {
                //u.p("custom editor canceled");
            }
            public void editingStopped(ChangeEvent changeEvent) {
                //u.p("custom editor stopped");
            }
        });
    }
}



