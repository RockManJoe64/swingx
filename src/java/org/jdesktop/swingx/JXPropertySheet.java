package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Graphics;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.joshy.util.u;
import org.jdesktop.swingx.propertysheet.*;


public class JXPropertySheet extends JTable {
    public Object bean;
    
    public JXPropertySheet(Object bean) {
        this.bean = bean;
        this.hiddenShown = false;
        this.expertShown = false;
        setAutoCreateColumnsFromModel(true);
        generateModel();
        setDefaultRenderer(Object.class, new PropertyValueCellRenderer(this));
        setDefaultEditor(Object.class, new PropertyValueCellEditor(this));
    }
    
    private void generateModel() {
        this.setModel(new BeanTableModel(this,bean,stopClass));
    }
    
    /**
     * Holds value of property expertShown.
     */
    private boolean expertShown;
    
    
    /**
     * Getter for property expertShown.
     * @return Value of property expertShown.
     */
    public boolean isExpertShown() {
        return this.expertShown;
    }
    
    /**
     * Setter for property expertShown.
     * @param expertShown New value of property expertShown.
     */
    public void setExpertShown(boolean expertShown) {
        boolean oldExpertShown = this.expertShown;
        this.expertShown = expertShown;
        generateModel();
        firePropertyChange("expertShown", new Boolean(oldExpertShown), new Boolean(expertShown));
    }
    
    /**
     * Holds value of property hiddenShown.
     */
    private boolean hiddenShown;
    
    /**
     * Getter for property hiddenShown.
     * @return Value of property hiddenShown.
     */
    public boolean isHiddenShown() {
        return this.hiddenShown;
    }
    
    /**
     * Setter for property hiddenShown.
     * @param hiddenShown New value of property hiddenShown.
     */
    public void setHiddenShown(boolean hiddenShown) {
        boolean oldHiddenShown = this.hiddenShown;
        this.hiddenShown = hiddenShown;
        generateModel();
        firePropertyChange("hiddenShown", new Boolean(oldHiddenShown), new Boolean(hiddenShown));
    }
    
    /**
     * Holds value of property stopClass.
     */
    private Class stopClass;
    
    /**
     * Getter for property stopClass.
     * @return Value of property stopClass.
     */
    public Class getStopClass() {
        return this.stopClass;
    }
    
    /**
     * Setter for property stopClass.
     * @param stopClass New value of property stopClass.
     */
    public void setStopClass(Class stopClass) {
        Class oldStopClass = this.stopClass;
        this.stopClass = stopClass;
        generateModel();
        firePropertyChange("stopClass", oldStopClass, stopClass);
    }

    
    
}