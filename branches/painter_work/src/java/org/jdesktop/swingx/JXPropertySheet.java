package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.propertysheet.*;


public class JXPropertySheet extends JXTreeTable implements BeanProvider {
    public Object bean;
    private List<String> categoryFilter;
    
    private boolean included = true;
    
    public JXPropertySheet(Object bean) {
        this.bean = bean;
        categoryFilter = new ArrayList<String>();
        this.hiddenShown = false;
        this.expertShown = false;
        this.expertOnly = false;
        setAutoCreateColumnsFromModel(true);
        generateModel();
        //setDefaultRenderer(Object.class, new PropertyValueCellRenderer(this));
        //setTreeCellRenderer(new PropertyNameTreeCellRenderer(this));
        //setDefaultEditor(Object.class, new PropertyValueCellEditor(this));
        
        //setRootVisible(true);
        setShowsRootHandles(true);
        setShowHorizontalLines(true);
        setShowVerticalLines(false);
        setSortable(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //this.setColumnMargin(2);
        //this.setFillsViewportHeight(true);
        this.setGridColor(new Color(240,240,255));
        this.setIntercellSpacing(new Dimension(0,0));
        this.setSurrendersFocusOnKeystroke(true);
        getTableHeader().setReorderingAllowed(false);
        BasicTreeUI ui = (BasicTreeUI)this.renderer.getUI();
        //u.p("indents = " + ui.getLeftChildIndent() + " " + ui.getRightChildIndent());
        // space from the left of this node's parent's nodes triangle
        ((BasicTreeUI)this.renderer.getUI()).setLeftChildIndent(0);
        // space from the right of this node's parent's nodes triangle
        ((BasicTreeUI)this.renderer.getUI()).setRightChildIndent(0);
    }
    
    private void generateModel() {
        this.setTreeTableModel(
                new BeanTableModel(bean, stopClass,
                included, categoryFilter, isExpertOnly(), isExpertShown(), isHiddenShown()));
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
    
    /**
     * Holds value of property expertOnly.
     */
    private boolean expertOnly;
    
    /**
     * Getter for property expertOnly.
     * @return Value of property expertOnly.
     */
    public boolean isExpertOnly() {
        return this.expertOnly;
    }
    
    /**
     * Setter for property expertOnly.
     * @param expertOnly New value of property expertOnly.
     */
    public void setExpertOnly(boolean expertOnly) {
        boolean oldExpertOnly = this.expertOnly;
        this.expertOnly = expertOnly;
        generateModel();
        firePropertyChange("expertOnly", new Boolean(oldExpertOnly), new Boolean(expertOnly));
    }
    
    public void setIncludedCategories(String ... cats) {
        included = true;
        for(String cat : cats) {
            categoryFilter.add(cat);
        }
        generateModel();
    }
    
    public void setExcludedCategories(String ... cats) {
        included = false;
        for(String cat : cats) {
            categoryFilter.add(cat);
        }
        generateModel();
    }

    public Object getBean() {
        return this.bean;
    }    
    
}