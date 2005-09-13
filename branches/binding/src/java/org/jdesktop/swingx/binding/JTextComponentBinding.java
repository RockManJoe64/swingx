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
import javax.swing.JFormattedTextField;
import javax.swing.text.JTextComponent;
import org.jdesktop.binding.ScalarBinding;


/**
 * Base class for all Swing Binding implementations that extends JTextComponent
 * @author Richard
 */
public class JTextComponentBinding extends ScalarBinding {
    private String oldValue;
    
    public JTextComponentBinding(JTextComponent comp) {
        super(comp, String.class);
    }
    
    public JTextComponentBinding(JTextComponent comp, String fieldName) {
        super(comp, fieldName, String.class);
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
        return getComponent().getText();
    }
    
    public JTextComponent getComponent() {
        return (JTextComponent)super.getComponent();
    }
}
