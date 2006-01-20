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
}
