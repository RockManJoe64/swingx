/*
 * JCheckBoxBinding.java
 *
 * Created on September 13, 2005, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;
import javax.swing.JComboBox;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.jdesktop.swingx.binding.AWTColumnBinding.AutoCommit;

/**
 * Binds the "value" of the JComboBox to some domain data. This is a read/write
 * binding.
 *
 * @author Richard
 */
public class JComboBoxBinding extends SwingColumnBinding {
    /**
     * The original value (prior to binding)
     */
    private Object oldValue;
    /**
     * Listens for changes in the value of the JComboBox, and updates the
     * edited state accordingly.
     */
    private ValueListener listener;
    /**
     * The value in the data model -- used to calculate whether the state has changed
     * for the JComboBox
     */
    private Object modelValue;
    
    /**
     */
    public JComboBoxBinding(JComboBox cbox) {
        super(cbox, Object.class);
        listener = new ValueListener();
    }

    /**
     * @inheritDoc
     */
    protected void doInitialize() {
        oldValue = getComponent().getSelectedItem();
        getComponent().getModel().addListDataListener(listener);
    }

    /**
     * @inheritDoc
     */
    public void doRelease() {
        getComponent().getModel().removeListDataListener(listener);
        getComponent().setSelectedItem(oldValue);
    }
    
    /**
     * @inheritDoc
     */
    public JComboBox getComponent() {
        return (JComboBox)super.getComponent();
    }

    /**
     * @inheritDoc
     */
    protected Object getComponentValue() {
        return getComponent().getSelectedItem();
    }

    /**
     * @inheritDoc
     */
    protected void setComponentValue(Object obj) {
        modelValue = obj;
        listener.ignoreEvent = true;
        getComponent().setSelectedItem(obj);
        listener.ignoreEvent = false;
    }

    /**
     */
    private class ValueListener implements ListDataListener {
        private boolean ignoreEvent = false;

        public void intervalAdded(ListDataEvent e) {}
        public void intervalRemoved(ListDataEvent e) {}

        public void contentsChanged(ListDataEvent e) {
            if (!ignoreEvent) {
                Object selectedItem = getComponent().getSelectedItem();
                if (selectedItem != null && modelValue != null) {
                    setEdited(!selectedItem.equals(modelValue));
                } else if (selectedItem == modelValue) {
                    setEdited(false);
                } else {
                    setEdited(true);
                }
                
                if (getAutoCommit() == AutoCommit.ON_CHANGE) {
                    save();
                }
            }
        }
    }
}