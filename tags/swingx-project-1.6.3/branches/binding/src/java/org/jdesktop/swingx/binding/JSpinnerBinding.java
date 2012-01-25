/*
 * JCheckBoxBinding.java
 *
 * Created on September 13, 2005, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.swingx.binding.AWTColumnBinding.AutoCommit;

/**
 *
 * @author Richard
 */
public class JSpinnerBinding extends SwingColumnBinding {
    /**
     * The original value (prior to binding)
     */
    private Object oldValue;
    /**
     * Listens for changes in the value of the JSlider, and updates the
     * edited state accordingly.
     */
    private ValueListener listener;
    /**
     * The value in the model -- used to calculate whether the state has changed
     * for the JSlider
     */
    private Object modelValue;
    
    /**
     */
    public JSpinnerBinding(JSpinner cbox) {
        super(cbox, Object.class);
        listener = new ValueListener();
    }

    /**
     * @inheritDoc
     */
    protected void doInitialize() {
        oldValue = getComponent().getValue();
        getComponent().addChangeListener(listener);
    }

    /**
     * @inheritDoc
     */
    public void doRelease() {
        getComponent().removeChangeListener(listener);
        if (oldValue != null) {
            getComponent().setValue(oldValue);
        }
    }
    
    /**
     * @inheritDoc
     */
    protected Object getComponentValue() {
        return getComponent().getValue();
    }

    /**
     * @inheritDoc
     */
    protected void setComponentValue(Object obj) {
        modelValue = obj;
        listener.ignoreEvent = true;
        getComponent().setValue(obj);
        listener.ignoreEvent = false;
    }
    
    /**
     * @inheritDoc
     */
    public JSpinner getComponent() {
        return (JSpinner)super.getComponent();
    }

    /**
     */
    private class ValueListener implements ChangeListener {
        private boolean ignoreEvent = false;
        public void stateChanged(ChangeEvent e) {
            if (!ignoreEvent) {
                setEdited(!getComponent().getValue().equals(modelValue));
                if (getAutoCommit() == AutoCommit.ON_CHANGE) {
                    save();
                }
            }
        }
    }
}