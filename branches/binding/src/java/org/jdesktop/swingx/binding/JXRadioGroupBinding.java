/*
 * JCheckBoxBinding.java
 *
 * Created on September 13, 2005, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;
import org.jdesktop.swingx.JXRadioGroup;
/**
 *
 * @author Richard
 */
public class JXRadioGroupBinding extends SwingColumnBinding {
    private Object oldValue;
    
    public JXRadioGroupBinding(JXRadioGroup group) {
        super(group, Object.class);
    }

    protected void doInitialize() {
        oldValue = getComponent().getSelectedValue();
    }

    public void doRelease() {
        getComponent().setSelectedValue(oldValue);
    }
    
    protected Object getComponentValue() {
        return getComponent().getSelectedValue();
    }

    protected void setComponentValue(Object obj) {
        getComponent().setSelectedValue(obj);
    }
    
    public JXRadioGroup getComponent() {
        return (JXRadioGroup)super.getComponent();
    }
    
    protected void setComponentEditable(boolean editable) {
        getComponent().setEnabled(editable);
    }
}
