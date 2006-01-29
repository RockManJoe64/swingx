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
import javax.swing.AbstractButton;


/**
 * Basic binding for subclasses of AbstractButton. This Binding is concerned
 * with the selected state of the AbstractButton.
 *
 * @author Richard
 */
public class AbstractButtonBinding extends SwingColumnBinding {
    private boolean oldValue;
    
    /**
     * Creates a new AbstractButtonBinding to bind to the given button.
     */
    public AbstractButtonBinding(AbstractButton button) {
        super(button, Boolean.class);
    }
    
    /**
     * @inheritDoc
     */
    protected void doInitialize() {
        oldValue = getComponent().isSelected();
    }

    /**
     * @inheritDoc
     */
    public void doRelease() {
        getComponent().setSelected(oldValue);
    }

    /**
     * @inheritDoc
     */
    protected void setComponentValue(Object value) {
        getComponent().setSelected(value == null ? Boolean.FALSE : (Boolean)value);
    }
    
    /**
     * @inheritDoc
     */
    protected Boolean getComponentValue() {
        return getComponent().isSelected();
    }
 
    /**
     * @inheritDoc
     */
    public AbstractButton getComponent() {
        return (AbstractButton)super.getComponent();
    }
}