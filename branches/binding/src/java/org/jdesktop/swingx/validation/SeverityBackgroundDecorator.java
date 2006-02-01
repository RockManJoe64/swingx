/*
 * MandatoryAndBlankBackgroundDecorator.java
 *
 * Created on January 20, 2006, 5:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.validation;

import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.view.ValidationComponentUtils;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JComponent;

/**
 * A decorator that will set the background color of the component based on the
 * severity of the ValidationResult.
 *
 * @author Richard
 */
public class SeverityBackgroundDecorator extends AbstractDecorator {    
    /** Creates a new instance of MandatoryAndBlankBackgroundDecorator */
    public SeverityBackgroundDecorator() {
    }
    
    /**
     * Sets the component background according to the associated 
     * validation result: default, error, warning.
     * 
     * @param component  the component to be decorated
     * @param result the ValidationResult. All of the messages within this
     *               ValidationResult are guaranteed to be related to the given
     *               component.
     */
    public void decorate(Component c, ValidationResult result) {
        if (c instanceof JComponent) {
            JComponent component = (JComponent) c;
            if ((result == null) || result.isEmpty()) {
                restoreBackground(component);
            } else if (result.hasErrors()) {
                setErrorBackground(component);
            } else if (result.hasWarnings()) {
                setWarningBackground(component);
            }
        } else {
            throw new AssertionError("not implemented");
        }
    }
}
