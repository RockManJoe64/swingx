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
 * @author Richard
 */
public class JXTitledPanelBinding extends SwingColumnBinding {
    private String oldValue;
    
    public JXTitledPanelBinding(JXTitledPanel comp) {
        super(comp, String.class);
    }
    
    protected void doInitialize() {
        oldValue = getComponent().getTitle();
    }

    public void doRelease() {
        getComponent().setTitle(oldValue);
    }

    protected void setComponentValue(Object value) {
        getComponent().setTitle(value == null ? "" : (String)value);
    }
    
    protected String getComponentValue() {
        return getComponent().getTitle();
    }
    
    public JXTitledPanel getComponent() {
        return (JXTitledPanel)super.getComponent();
    }
}
