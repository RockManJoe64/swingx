package org.jdesktop.swingx.validation;

import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.view.ValidationComponentUtils;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JComponent;

/**
 * Changes the tooltip of the Component to contain the validation message
 *
 * @author Richard
 */
public class TooltipDecorator extends AbstractDecorator {    
    public TooltipDecorator() {
    }
    
    /**
     */
    public void decorate(Component c, ValidationResult result) {
        if (c instanceof JComponent) {
            JComponent component = (JComponent) c;
            if ((result == null) || result.isEmpty()) {
                //TODO should restore -- see AbstractDecorator
                component.setToolTipText(null);
            } else if (result.hasErrors()) {
                component.setToolTipText(result.getMessagesText());
            } else if (result.hasWarnings()) {
                component.setToolTipText(result.getMessagesText());
            }
        } else {
            throw new AssertionError("not implemented");
        }
    }
}
