/*
 * JCheckBoxBinding.java
 *
 * Created on September 13, 2005, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A read/write binding for JSlider and subclasses of JSlider
 *
 * @author Richard
 */
public class JSliderBinding extends SwingColumnBinding {
    /**
     * The original value (prior to binding)
     */
    private int oldValue;
    /**
     * Whether or not the JSlider is editable. This, together with the
     * enabled flag, indicate what state the JSlider should be in
     * (JSlider only has one state by default, enabled)
     */
    private boolean editable;
    /**
     * Whether or not the JSlider is enabled. This, together with the
     * editable flag, indicate the state of the JSlider
     */
    private boolean enabled;
    /**
     * Listens for changes in the value of the JSlider, and updates the
     * edited state accordingly.
     */
    private ValueListener listener;
    /**
     * The value in the model -- used to calculate whether the state has changed
     * for the JSlider
     */
    private int modelValue;
    
    /**
     */
    public JSliderBinding(JSlider slider) {
        super(slider, Integer.class);
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
        getComponent().setValue(oldValue);
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
        modelValue = (Integer)obj;
        listener.ignoreEvent = true;
        getComponent().setValue(obj == null ? 0 : (Integer)obj);
        listener.ignoreEvent = false;
    }
    
    /**
     * @inheritDoc
     */
    public JSlider getComponent() {
        return (JSlider)super.getComponent();
    }

    
    protected void setComponentEditable(boolean editable) {
        this.editable = editable;
        updateComponentEnabledState();
    }
    
    protected void setComponentEnabled(boolean enabled) {
        this.enabled = enabled;
        updateComponentEnabledState();
    }
    
    private void updateComponentEnabledState() {
        getComponent().setEnabled(enabled && editable);
    }
    
    /**
     */
    private class ValueListener implements ChangeListener {
        private boolean ignoreEvent = false;
        public void stateChanged(ChangeEvent e) {
            if (!ignoreEvent) {
                setEdited(getComponent().getValue() != modelValue);
                if (getAutoCommit() == AutoCommit.ON_CHANGE) {
                    save();
                }
            }
        }
    }
}
