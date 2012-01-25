/*
 * ValidationDecoratorFactory.java
 *
 * Created on January 21, 2006, 2:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.validation;

/**
 *
 * @author Richard
 */
public class ValidationDecoratorFactory {
    private static final SeverityBackgroundDecorator SEVERITY_BACKGROUND_INSTANCE = new SeverityBackgroundDecorator();
    private static final TooltipDecorator TOOLTIP_INSTANCE = new TooltipDecorator();
    
    private ValidationDecoratorFactory() {
    }
    
    public static ValidationDecorator getSeverityBackgroundDecorator() {
        return SEVERITY_BACKGROUND_INSTANCE;
    }

    public static ValidationDecorator getTooltipDecorator() {
        return TOOLTIP_INSTANCE;
    }
    
    public static ValidationDecorator getSeverityBackgroundTooltipDecorator() {
        return new CompoundDecorator(SEVERITY_BACKGROUND_INSTANCE, TOOLTIP_INSTANCE);
    }
}
