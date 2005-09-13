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
import javax.swing.JTextField;
import org.jdesktop.binding.ScalarBinding;


/**
 *
 * @author Richard
 */
public class JTextFieldBinding extends ScalarBinding {
    private String oldValue;
    
    public JTextFieldBinding(JTextField tf) {
        super(tf, String.class);
    }
    
    /** Creates a new instance of JTextFieldBinding */
    public JTextFieldBinding(JTextField tf, String fieldName) {
        super(tf, fieldName, String.class);
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
    
    public JTextField getComponent() {
        return (JTextField)super.getComponent();
    }
}
