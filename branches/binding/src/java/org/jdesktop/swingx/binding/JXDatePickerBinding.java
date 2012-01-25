/*
 * JLabelBinding.java
 *
 * Created on August 24, 2005, 5:14 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.binding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.binding.AWTColumnBinding.AutoCommit;

/**
 * Binding for the JXDatePicker component
 * @author Richard
 */
public class JXDatePickerBinding extends SwingColumnBinding {
    /**
     * The original value (prior to binding)
     */
    private Date oldValue;
    /**
     * Listens for changes in the value of the JXDatePicker, and updates the
     * edited state accordingly.
     */
    private ValueListener listener;
    /**
     * The value in the model -- used to calculate whether the state has changed
     * for the JXDatePicker
     */
    private Date modelValue;
    
    /**
     */
    public JXDatePickerBinding(JXDatePicker datePicker) {
        super(datePicker, Date.class);
        listener = new ValueListener();
    }
    
    /**
     * @inheritDoc
     */
    protected void doInitialize() {
        oldValue = getComponent().getDate();
        getComponent().addPropertyChangeListener("value", listener);
    }

    /**
     * @inheritDoc
     */
    public void doRelease() {
        getComponent().removePropertyChangeListener("value", listener);
        getComponent().setDate(oldValue);
    }

    /**
     * @inheritDoc
     */
    protected void setComponentValue(Object value) {
        modelValue = (Date)value;
        listener.ignoreEvent = true;
        getComponent().setDate(value == null ? new Date() : modelValue);
        listener.ignoreEvent = false;
    }
    
    /**
     * @inheritDoc
     */
    protected Date getComponentValue() {
        return getComponent().getDate();
    }
    
    /**
     * @inheritDoc
     */
    public JXDatePicker getComponent() {
        return (JXDatePicker)super.getComponent();
    }

    /**
     */
    private class ValueListener implements PropertyChangeListener {
        private boolean ignoreEvent = false;
        public void propertyChange(PropertyChangeEvent evt) {
            if (!ignoreEvent) {
                setEdited(!getComponent().getDate().equals(modelValue));
                if (getAutoCommit() == AutoCommit.ON_CHANGE) {
                    save();
                }
            }
        }
    }
}
