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
 *
 * @author Richard
 */
public class JSliderBinding extends SwingColumnBinding {
    private int oldValue;
    private boolean editable;
    private boolean enabled;
    private ValueListener listener;
    private int cachedValue;
    
    public JSliderBinding(JSlider slider) {
        super(slider, Integer.class);
        listener = new ValueListener();
    }

    protected void doInitialize() {
        oldValue = getComponent().getValue();
        getComponent().addChangeListener(listener);
    }

    public void doRelease() {
        getComponent().setValue(oldValue);
        getComponent().removeChangeListener(listener);
    }
    
    protected Object getComponentValue() {
        return getComponent().getValue();
    }

    protected void setComponentValue(Object obj) {
        listener.changing = true;
        getComponent().setValue((Integer)obj);
        cachedValue = (Integer)obj;
        listener.changing = false;
    }
    
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
    
    private class ValueListener implements ChangeListener {
        private boolean changing = false;
        public void stateChanged(ChangeEvent e) {
            if (!changing) {
                setEdited(getComponent().getValue() != cachedValue);
                save();
            }
        }
    }
}
