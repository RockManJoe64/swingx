/*
 * JCheckBoxBinding.java
 *
 * Created on September 13, 2005, 9:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;
import javax.swing.JProgressBar;

/**
 *
 * @author Richard
 */
public class JProgressBarBinding extends SwingColumnBinding {
    private int oldValue;
    
    public JProgressBarBinding(JProgressBar pb) {
        super(pb, int.class);
    }

    protected void doInitialize() {
        oldValue = getComponent().getValue();
    }

    public void doRelease() {
        getComponent().setValue(oldValue);
    }
    
    protected Object getComponentValue() {
        return getComponent().getValue();
    }

    protected void setComponentValue(Object obj) {
        getComponent().setValue((Integer)obj);
    }
    
    public JProgressBar getComponent() {
        return (JProgressBar)super.getComponent();
    }
}
