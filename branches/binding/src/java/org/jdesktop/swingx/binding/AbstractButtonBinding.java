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
import javax.swing.AbstractButton;
import org.jdesktop.binding.FieldBinding;


/**
 * You may specify two field names for this binding, one for the name and
 * one for the selection state...<br>
 * PENDING/TODO: The current ScalarBinding architecture is adapted for a single
 * field on the master, but this is bogus for this component, which wants potentially
 * two different field values specified
 *
 * @author Richard
 */
public class AbstractButtonBinding extends FieldBinding {
    private String oldValue;
    
    public AbstractButtonBinding(AbstractButton button) {
        super(button, String.class);
    }
    
    public AbstractButtonBinding(AbstractButton button, String fieldName) {
        super(button, fieldName, String.class);
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
        assert false : "getComponentValue was called although the AbstractButtonBinding is read only";
        return null;
    }
    
    public AbstractButton getComponent() {
        return (AbstractButton)super.getComponent();
    }
}
