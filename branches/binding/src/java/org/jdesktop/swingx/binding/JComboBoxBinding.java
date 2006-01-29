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

/**
 * Binds the "value" of the JComboBox to some domain data. This is a read/write
 * binding.
 *
 * @author Richard
 */
public class JComboBoxBinding extends SwingColumnBinding {
    private Object oldValue;
    
    public JComboBoxBinding(JComboBox cbox) {
        super(cbox, Object.class);
    }

    protected void doInitialize() {
        oldValue = getComponent().getSelectedItem();
    }

    public void doRelease() {
        getComponent().setSelectedItem(oldValue);
    }
    
    public JComboBox getComponent() {
        return (JComboBox)super.getComponent();
    }

    protected Object getComponentValue() {
        return getComponent().getSelectedItem();
    }

    protected void setComponentValue(Object obj) {
        getComponent().setSelectedItem(obj);
    }

    protected void setComponentEditable(boolean editable) {
        getComponent().setEditable(editable);
    }
}
