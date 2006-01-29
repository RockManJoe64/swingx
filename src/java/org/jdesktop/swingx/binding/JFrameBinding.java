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
import javax.swing.JFrame;

/**
 * Binds the title of the JFrame to some data
 * @author Richard
 */
public class JFrameBinding extends AWTColumnBinding {
    private String oldValue;

    /**
     */
    public JFrameBinding(JFrame frm) {
        super(frm, String.class);
    }
    
    /**
     * @inheritDoc
     */
    protected void doInitialize() {
        oldValue = getComponent().getTitle();
    }

    /**
     * @inheritDoc
     */
    public void doRelease() {
        getComponent().setTitle(oldValue);
    }

    /**
     * @inheritDoc
     */
    protected void setComponentValue(Object value) {
        getComponent().setTitle(value == null ? "" : (String)value);
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
    public JFrame getComponent() {
        return (JFrame)super.getComponent();
    }
}
