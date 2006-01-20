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
 *
 * @author rbair
 */
public abstract class SwingColumnBinding extends ColumnBinding {
    
    public SwingColumnBinding(Component c, Class type) {
        super(c, type);
    }

    protected void setComponentEnabled(boolean enabled) {
        getComponent().setEnabled(enabled);
    }
    
    protected void setComponentEditable(boolean editable) {
        //no-op
    }
    
    public Component getComponent() {
        return (Component)super.getComponent();
    }

    protected ValidationResult doValidation() {
        ValidationResult result = super.doValidation();
        //update the keys, if necessary
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
        Component c = getComponent();
        if (c instanceof JComponent) {
//            ValidationComponentUtils.updateComponentTreeSeverity((JComponent)c, result);
//            ValidationComponentUtils.updateComponentTreeValidationBackground((JComponent)c, result);
            if (result.getSeverity() == Severity.ERROR) {
                ValidationComponentUtils.setErrorBackground((JTextComponent)c);
            } else if (result.getSeverity() == Severity.WARNING) {
                ValidationComponentUtils.setWarningBackground((JTextComponent)c);
            } else {
                c.setBackground(Color.WHITE);//normal background....
            }
        }
    }
}
