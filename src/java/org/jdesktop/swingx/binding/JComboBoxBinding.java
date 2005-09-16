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
import org.jdesktop.binding.FieldBinding;

/**
 * This single binding manages binding the list portion of the ComboBox as well
 * as the editable portion. Two data models may be required to achieve this,
 * which will result in...
 *
 * @author Richard
 */
public class JComboBoxBinding extends FieldBinding {
    private Object oldValue;
    
    public JComboBoxBinding(JComboBox cbox) {
        super(cbox, Object.class);
    }

    protected void initialize() {
        oldValue = getComponent().getSelectedItem();
    }

    public void release() {
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
}
