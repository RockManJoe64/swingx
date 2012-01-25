package org.jdesktop.swingx;

import java.awt.Dimension;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.propertysheet.BeanProvider;
import org.jdesktop.swingx.propertysheet.BeanTableModel;
import org.jdesktop.swingx.propertysheet.PropertyValueCellEditor;
import org.jdesktop.swingx.propertysheet.PropertyValueCellRenderer;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RowModel;


public class JXPropertySheet2 extends Outline implements BeanProvider {
    private Object bean;
    private List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
    private List<String> categoryFilter;
    private boolean included = true;
    
    public JXPropertySheet2() {
        this(null);
    }
    public JXPropertySheet2(Object bean) {
        super();
        expertOnly = false;
        expertShown = true;
        hiddenShown = false;
        buildModel(bean);
        this.getTableHeader().setReorderingAllowed(false);
        this.setDefaultRenderer(Object.class, new PropertyValueCellRenderer(this));
        this.setDefaultEditor(Object.class, new PropertyValueCellEditor(this));
        this.setIntercellSpacing(new Dimension(0,1));
        this.setRowHeight(40);
    }
    
    public void setModel(TableModel model) {
        
    }
    
    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }
    
    public void fireChangeEvent() {
        ChangeEvent e = new ChangeEvent(bean);
        for(ChangeListener l : changeListeners) {
            l.stateChanged(e);
        }
    }
    
    private void buildModel(Object bean) {
        this.bean = bean;
        final SuperBeanModel mod = new SuperBeanModel(bean, null, false,
                new ArrayList(), isExpertOnly(), isExpertShown(), isHiddenShown());
        OutlineModel model = DefaultOutlineModel.createOutlineModel(mod, mod);
        super.setModel(model);
        this.setRootVisible(false);
    }
    
    class SuperBeanModel extends BeanTableModel implements RowModel {
        public SuperBeanModel(Object bean, Class stopClass,
                boolean included, List categoryFilter,
                boolean isExpertOnly, boolean isExpertShown, boolean isHiddenShown) {
            super(bean,stopClass,included,categoryFilter,isExpertOnly,isExpertShown, isHiddenShown);
        }
        public Class getColumnClass(int column) {
            return String.class;
        }
        public int getColumnCount() {
            return 1;
        }
        public String getColumnName(int column) {
            return super.getColumnName(column+1);
        }
        public Object getValueFor(Object node, int column) {
            return super.getValueAt(node,column+1);
        }
        public boolean isCellEditable(Object node, int column) {
            return node instanceof PropertyDescriptor && column == 0;
        }
        public void setValueFor(Object node, int column, Object value) {
        }
    }
    
    public Object getBean() {
        return this.bean;
    }
    
    public void setBean(Object bean) {
        buildModel(bean);
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
        buildModel(bean);
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
        buildModel(getBean());
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
        buildModel(getBean());
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
        buildModel(getBean());
        firePropertyChange("expertOnly", new Boolean(oldExpertOnly), new Boolean(expertOnly));
    }
    
    public void setIncludedCategories(String ... cats) {
        included = true;
        for(String cat : cats) {
            categoryFilter.add(cat);
        }
        buildModel(getBean());
    }
    
    public void setExcludedCategories(String ... cats) {
        included = false;
        for(String cat : cats) {
            categoryFilter.add(cat);
        }
        buildModel(getBean());
    }

       
}
