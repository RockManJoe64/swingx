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
import org.jdesktop.swingx.JXTitledPanel;

/**
 * Read only binding -- just modifies the title of a JXTitledPanel
 *
 * @author Richard
 */
public class JXTitledPanelBinding extends SwingColumnBinding {
    /**
     * The original value prior to binding
     */
    private String oldValue;
    
    /**
     */
    public JXTitledPanelBinding(JXTitledPanel comp) {
        super(comp, String.class);
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
        throw new AssertionError("never called because this binding is read only");
    }
    
    /**
     * @inheritDoc
     */
    public JXTitledPanel getComponent() {
        return (JXTitledPanel)super.getComponent();
    }

}
