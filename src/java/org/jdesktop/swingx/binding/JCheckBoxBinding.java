/*
 * JCheckBoxBinding.java
 *
 * Created on September 13, 2005, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;

import javax.swing.JCheckBox;

/**
 *
 * @author Richard
 */
public class JCheckBoxBinding extends SwingColumnBinding {
    private boolean oldValue;
    
    /** Creates a new instance of JCheckBoxBinding */
    public JCheckBoxBinding(JCheckBox cbox) {
        super(cbox, Boolean.class);
    }

    protected void doInitialize() {
        oldValue = getComponent().isSelected();
    }

    public void doRelease() {
        getComponent().setSelected(oldValue);
    }
    
    protected Object getComponentValue() {
        return getComponent().isSelected();
    }

    protected void setComponentValue(Object obj) {
        getComponent().setSelected((Boolean)obj);
    }
    
    public JCheckBox getComponent() {
        return (JCheckBox)super.getComponent();
    }

    protected void setComponentEditable(boolean editable) {
        getComponent().setEnabled(editable);
    }
}
