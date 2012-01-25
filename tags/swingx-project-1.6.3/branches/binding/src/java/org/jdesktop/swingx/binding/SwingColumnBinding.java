/*
 * SwingFieldBinding.java
 *
 * Created on September 28, 2005, 3:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;
import com.jgoodies.validation.ValidationResult;
import javax.swing.JComponent;
import org.jdesktop.swingx.validation.ValidationDecorator;
import org.jdesktop.swingx.validation.ValidationDecoratorFactory;

/**
 * This is the base class for all of the Swing binding implementations that
 * are associated with a single Column, f.i. JTextComponentBinding or AbstractButtonBinding.
 * It is meant to be subclassed (hence, it is abstract).
 *
 * All subclasses must be intended for use with subclasses of JComponent.
 *
 * @author rbair
 */
public abstract class SwingColumnBinding extends AWTColumnBinding { 
    /**
     * The ValidationDecorator knows how to decorate the UI component in response to validation events.
     * Can be null.
     */
    private ValidationDecorator validationDecorator = ValidationDecoratorFactory.getSeverityBackgroundTooltipDecorator();
    
    /**
     * Basic constructor, creates a new SwingColumnBinding
     * 
     * @param c the JComponent that this Binding is for
     * @param type the class that the bound value should be converted into
     *             for use with this Binding. For example, the JTextComponentBinding
     *             specifies that it wants a String. If you don't want automatic
     *             conversion, then specify Object.class or null.
     */
    public SwingColumnBinding(JComponent c, Class type) {
        super(c, type);
    }

    /**
     * @inheritDoc
     * overridden for convenience (covariant return type)
     */
    public JComponent getComponent() {
        return (JComponent)super.getComponent();
    }

    /**
     * @inheritDoc
     * Default implementation of doValidationResult that relies on JGoodies Validation
     * to decorate the component properly
     */
    protected void doValidationResult(ValidationResult result) {
        JComponent c = getComponent();
        if (validationDecorator != null) {
            validationDecorator.decorate(c, result);
        }
    }
    
    
    /**
     * Sets the ValidationDecorator to use to decorate the UI component
     * when validation events occur. If null, then no visual clue will be given
     * to the user via this Binding tha a validation error has occured
     */
    public void setValidationDecorator(ValidationDecorator decorator) {
        ValidationDecorator old = this.validationDecorator;
        this.validationDecorator = decorator;
        pcs.firePropertyChange("validationDecorator", old, decorator);
    }
    
    /**
     * @return the validation decorator
     */
    public ValidationDecorator getValidationDecorator() {
        return this.validationDecorator;
    }    
}
