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


/**
 * Basic binding implementation for JLabel and subclasses of JLabel
 *
 * @author Richard
 */
public class JLabelBinding extends SwingColumnBinding {
    private String oldValue;

    /**
     */
    public JLabelBinding(JLabel label) {
        super(label, String.class);
    }
    
    /**
     * @inheritDoc
     */
    protected void doInitialize() {
        oldValue = getComponent().getText();
    }

    /**
     * @inheritDoc
     */
    public void doRelease() {
        getComponent().setText(oldValue);
    }

    /**
     * @inheritDoc
     */
    protected void setComponentValue(Object value) {
        getComponent().setText(value == null ? "" : (String)value);
    }
    
    /**
     * @inheritDoc
     */
    protected String getComponentValue() {
        throw new AssertionError("This method should never be called because the title is readonly");
    }

    /**
     * @inheritDoc
     */
    public JLabel getComponent() {
        return (JLabel)super.getComponent();
    }
}
