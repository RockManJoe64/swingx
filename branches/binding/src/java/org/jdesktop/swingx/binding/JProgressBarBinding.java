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
 * Binding implementation for JProgressBar and subclasses of JProgressBar.
 * This binding is read-only.
 *
 * @author Richard
 */
public class JProgressBarBinding extends SwingColumnBinding {
    private int oldValue;
    
    /**
     */
    public JProgressBarBinding(JProgressBar pb) {
        super(pb, int.class);
    }

    /**
     * @inheritDoc
     */
    protected void doInitialize() {
        oldValue = getComponent().getValue();
    }

    /**
     * @inheritDoc
     */
    public void doRelease() {
        getComponent().setValue(oldValue);
    }
    
    /**
     * @inheritDoc
     */
    protected void setComponentValue(Object obj) {
        getComponent().setValue((Integer)obj);
    }
    
    /**
     * @inheritDoc
     */
    protected Object getComponentValue() {
        throw new AssertionError("This method should never be called because the binding is readonly");
    }

    /**
     * @inheritDoc
     */
    public JProgressBar getComponent() {
        return (JProgressBar)super.getComponent();
    }
}
