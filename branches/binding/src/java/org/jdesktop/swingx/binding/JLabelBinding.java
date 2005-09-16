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

import javax.swing.JLabel;
import org.jdesktop.binding.FieldBinding;


/**
 *
 * @author Richard
 */
public class JLabelBinding extends FieldBinding {
    private String oldValue;
    
    public JLabelBinding(JLabel label) {
        super(label, String.class);
    }
    
    public JLabelBinding(JLabel label, String fieldName) {
        super(label, fieldName, String.class);
    }

    protected void initialize() {
        oldValue = getComponent().getText();
    }

    public void release() {
        getComponent().setText(oldValue);
    }

    protected void setComponentValue(Object value) {
        getComponent().setText(value == null ? "" : (String)value);
    }
    
    protected String getComponentValue() {
        //in reality, this should never be called since
        //label components are always read only!!!
        assert false : "getComponentValue was called although the JLabelBinding is read only";
        return null;
    }
    
    public JLabel getComponent() {
        return (JLabel)super.getComponent();
    }
}
