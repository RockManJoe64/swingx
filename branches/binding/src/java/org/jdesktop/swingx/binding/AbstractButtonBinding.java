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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.AbstractButton;
import org.jdesktop.binding.BindingException;
import org.jdesktop.swingx.binding.AWTColumnBinding.AutoCommit;

/**
 * Basic binding for subclasses of AbstractButton. This Binding is concerned
 * with the selected state of the AbstractButton.
 *
 * @author Richard
 */
public class AbstractButtonBinding extends SwingColumnBinding {
    private boolean oldValue;
    private boolean modelValue;
    private ButtonStateListener stateListener;
    
    /**
     * Creates a new AbstractButtonBinding to bind to the given button.
     */
    public AbstractButtonBinding(AbstractButton button) {
        super(button, Boolean.class);
        stateListener = new ButtonStateListener();
    }
    
    /**
     * @inheritDoc
     */
    protected void doInitialize() {
        oldValue = getComponent().isSelected();
        getComponent().addItemListener(stateListener);
    }

    /**
     * @inheritDoc
     */
    public void doRelease() {
        getComponent().removeItemListener(stateListener);
        getComponent().setSelected(oldValue);
    }

    /**
     * @inheritDoc
     */
    protected void setComponentValue(Object value) {
        modelValue = value == null ? Boolean.FALSE : (Boolean)value;
        stateListener.ignoreEvent = true;
        getComponent().setSelected(modelValue);
        stateListener.ignoreEvent = false;
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

    private final class ButtonStateListener implements ItemListener {
        private boolean ignoreEvent = false;
        
        public void itemStateChanged(ItemEvent e) {
            if (!ignoreEvent) {
                //update the edited state
                setEdited(!getComponentValue().equals(modelValue));
                if (getAutoCommit() == AutoCommit.ON_CHANGE) {
                    save();
                }
            }
        }
    }
}