/*
 * ValidationDecorator.java
 *
 * Created on January 20, 2006, 2:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.validation;

import com.jgoodies.validation.ValidationResult;
import java.awt.Component;

/**
 * NOTE: This interface and it's associated implementations could very well be part
 * of the jgoodies Validation library. Like the Swing Border class, ValidationDecorators
 * are designed to be reusable among multiple validations. There is a ValidationDecoratorFactory
 * (much like a BorderFactory) for vending commonly used ValidationDecorators.
 *
 * @author Richard
 */
public interface ValidationDecorator {
    public void decorate(Component c, ValidationResult result);
}
