/*
 * SwingFieldBinding.java
 *
 * Created on September 28, 2005, 3:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import org.jdesktop.binding.BindingException;
import org.jdesktop.binding.impl.ColumnBinding;

/**
 * This is the base class for all of the AWT/Swing binding implementations that
 * are associated with a single Column, f.i. JTextComponentBinding or AbstractButtonBinding.
 * It is meant to be subclassed (hence, it is abstract).
 *
 * @author rbair
 */
public abstract class AWTColumnBinding extends ColumnBinding {
    /**
     * An enum indicating various times to commit automatically in the
     * Swing binding
     */
    public static enum AutoCommit {ON_CHANGE, ON_FOCUS_LOST, NEVER};
    
    /**
     * Indicates when to perform autocommit
     */
    private AutoCommit autoCommit = AutoCommit.NEVER;
    
    /**
     * Listens for focus lost events and, if autoCommit is set to ON_FOCUS_LOST,
     * will initiate a save operation
     */
    private FocusLostListener focusListener;
    
    /**
     * Basic constructor, creates a new AWTColumnBinding
     *
     * @param c the Component that this Binding is for
     * @param type the class that the bound value should be converted into
     *             for use with this Binding. For example, the JTextComponentBinding
     *             specifies that it wants a String. If you don't want automatic
     *             conversion, then specify Object.class or null.
     */
    public AWTColumnBinding(Component c, Class type) {
        super(c, type);
    }
    
    /**
     * Sets the component to be enabled based on the given param
     */
    protected void setComponentEnabled(boolean enabled) {
        getComponent().setEnabled(enabled);
    }
    
    /**
     * Not all Swing/AWT components have an editable property, so it is unimplemented
     * by default. Each component should override this method if they want to support
     * editability changes. JLabel doesn't need this method, JTextField does.
     */
    protected void setComponentEditable(boolean editable) {
        //no-op
    }
    
    /**
     * @inheritDoc
     * overridden for convenience (covariant return type)
     */
    public Component getComponent() {
        return (Component)super.getComponent();
    }
    
    /**
     * Sets when to perform autocommit. Can be turned off by passing in
     * AutoCommit.NEVER.
     *
     * @param mode The AutoCommit mode
     */
    public void setAutoCommit(AutoCommit mode) {
        if (this.autoCommit != mode) {
            AutoCommit oldValue = this.autoCommit;
            this.autoCommit = mode;
            doConfigureAutoCommit();
            pcs.firePropertyChange("autoCommit", oldValue, mode);
        }
    }
    
    /**
     * @return the autocommit mode
     */
    public AutoCommit getAutoCommit() {
        return autoCommit;
    }
    
    /**
     * Override this method to change any logic in the Binding necesary for
     * a different AutoCommit mode
     */
    protected void doConfigureAutoCommit() {
        if (autoCommit == AutoCommit.ON_FOCUS_LOST) {
            if(focusListener == null) {
                focusListener = new FocusLostListener();
                getComponent().addFocusListener(focusListener);
            }
        } else if (focusListener != null) {
            getComponent().removeFocusListener(focusListener);
        }
    }
    
    /**
     * Handles the chores for when autocommit is set to ON_FOCUS_LOST
     */
    private final class FocusLostListener extends FocusAdapter {
        public void focusLost(FocusEvent e) {
            if (autoCommit == AutoCommit.ON_FOCUS_LOST) {
                save();
            }
        }
    }
}