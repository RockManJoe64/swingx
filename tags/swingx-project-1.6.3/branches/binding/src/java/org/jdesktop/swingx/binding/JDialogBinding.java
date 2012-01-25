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
import javax.swing.JDialog;



/**
 * Binds the title of a JDialog to some data.
 *
 * @author Richard
 */
public class JDialogBinding extends AWTColumnBinding {
    /**
     * The original title
     */
    private String oldValue;
    
    /**
     */
    public JDialogBinding(JDialog dlg) {
        super(dlg, String.class);
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
    public JDialog getComponent() {
        return (JDialog)super.getComponent();
    }
}