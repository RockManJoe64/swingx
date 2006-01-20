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
 *
 * @author Richard
 */
public class JDialogBinding extends SwingColumnBinding {
    private String oldValue;
    
    public JDialogBinding(JDialog dlg) {
        super(dlg, String.class);
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
        //in reality, this should never be called since
        //label components are always read only!!!
        assert false : "getComponentValue was called although the JDialogBinding is read only";
        return null;
    }
    
    public JDialog getComponent() {
        return (JDialog)super.getComponent();
    }
}
