/*
 * SwingFieldBinding.java
 *
 * Created on September 28, 2005, 3:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.view.ValidationComponentUtils;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import org.jdesktop.binding.impl.ColumnBinding;

/**
 * This is the base class for all of the Swing binding implementations that
 * are associated with a single Column, f.i. JTextComponentBinding or AbstractButtonBinding.
 * It is meant to be subclassed (hence, it is abstract).
 *
 * All subclasses must be intended for use with subclasses of Component. Thus, this
 * could also be the parent binding for all AWT bindings.
 *
 * @author rbair
 */
public abstract class SwingColumnBinding extends ColumnBinding {
    /**
     * Basic constructor, creates a new SwingColumnBinding
     * 
     * @param c the Component that this Binding is for
     * @param type the class that the bound value should be converted into
     *             for use with this Binding. For example, the JTextComponentBinding
     *             specifies that it wants a String. If you don't want automatic
     *             conversion, then specify Object.class or null.
     */
    public SwingColumnBinding(Component c, Class type) {
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
     * @inheritDoc
     * overridden to perform validation.
     */
    protected ValidationResult doValidation() {
        ValidationResult result = super.doValidation();
        //In JGoodies binding, there are keys associated with the components
        //(I believe they are in the client properties). The JGoodies
        //ValidationComponentUtils contains the logic for getting these keys
        
        //further, since I cannot have a "key" associated with the component on
        //a deeper level, I have to associate the ValidationResult message with
        //the components key here, by creating a new ValidationResult associated
        //with the components key
        Component c = getComponent();
        if (c instanceof JComponent) {
            final Object msgKey = ValidationComponentUtils.getMessageKey((JComponent)c);
            if (msgKey != null) {
                ValidationResult newResults = new ValidationResult();
                for (final Object m : result.getMessages()) {
                    ValidationMessage msg = new ValidationMessage() {
                        public Severity severity() {
                            return ((ValidationMessage)m).severity();
                        }
                        public String formattedText() {
                            return ((ValidationMessage)m).formattedText();
                        }
                        public Object key() {
                            Object key = ((ValidationMessage)m).key();
                            return key == null ? msgKey : key;
                        }
                    };
                    newResults.add(msg);
                }
                result = newResults;
            }
        }
        doValidationResult(result);
        return result;
    }
    
    /**
     * Modifies the UI component in some way to indicate the results of Validation
     */
    protected void doValidationResult(ValidationResult result) {
        //TODO
    }
}
