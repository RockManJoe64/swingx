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
import org.jdesktop.binding.ScalarBinding;


/**
 *
 * @author Richard
 */
public class JLabelBinding extends ScalarBinding {
    /** Creates a new instance of JLabelBinding */
    public JLabelBinding(JLabel label, String fieldName) {
        super(label, fieldName, String.class);
    }

    protected void initialize() {
        //nothing to init
    }

    public void release() {
        //nothing to release
    }

    protected void setComponentValue(Object value) {
        JLabel label = (JLabel)getComponent();
        label.setText(value == null ? "" : (String)value);
    }
    
    protected String getComponentValue() {
        //in reality, this should never be called since
        //label components are always read only!!!
        //TODO
        return ((JLabel)getComponent()).getText();
    }
}
