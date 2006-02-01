/*
 * CompoundDecorator.java
 *
 * Created on January 20, 2006, 2:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.validation;

import com.jgoodies.validation.ValidationResult;
import java.awt.Component;

/**
 * Allows two different decorators to be combined into one
 *
 * @author Richard
 */
public class CompoundDecorator implements ValidationDecorator {
    private ValidationDecorator first;
    private ValidationDecorator second;
    
    /** Creates a new instance of CompoundDecorator */
    public CompoundDecorator(ValidationDecorator first, ValidationDecorator second) {
        this.first = first;
        this.second = second;
    }

    public void decorate(Component c, ValidationResult result) {
        first.decorate(c, result);
        second.decorate(c, result);
    }
}
