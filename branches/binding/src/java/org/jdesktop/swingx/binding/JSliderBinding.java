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
import org.jdesktop.binding.FieldBinding;

/**
 *
 * @author Richard
 */
public class JSliderBinding extends FieldBinding {
    private int oldValue;
    
    public JSliderBinding(JSlider slider) {
        super(slider, int.class);
    }

    protected void initialize() {
        oldValue = getComponent().getValue();
    }

    public void release() {
        getComponent().setValue(oldValue);
    }
    
    protected Object getComponentValue() {
        return getComponent().getValue();
    }

    protected void setComponentValue(Object obj) {
        getComponent().setValue((Integer)obj);
    }
    
    public JSlider getComponent() {
        return (JSlider)super.getComponent();
    }
}
