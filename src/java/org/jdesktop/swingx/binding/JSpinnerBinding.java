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

/**
 *
 * @author Richard
 */
public class JSpinnerBinding extends SwingColumnBinding {
    private Object oldValue;
    
    public JSpinnerBinding(JSpinner cbox) {
        super(cbox, Object.class);
    }

    protected void doInitialize() {
        oldValue = getComponent().getValue();
    }

    public void doRelease() {
        if (oldValue != null) {
            getComponent().setValue(oldValue);
        }
    }
    
    protected Object getComponentValue() {
        return getComponent().getValue();
    }

    protected void setComponentValue(Object obj) {
        getComponent().setValue(obj);
    }
    
    public JSpinner getComponent() {
        return (JSpinner)super.getComponent();
    }

    protected void setComponentEditable(boolean editable) {
        getComponent().setEnabled(editable);
    }
}
