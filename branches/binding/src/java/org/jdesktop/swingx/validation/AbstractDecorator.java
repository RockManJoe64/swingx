/*
 * AbstractValidationDecorator.java
 *
 * Created on January 21, 2006, 2:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.validation;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.UIManager;

/**
 *
 * @author Richard
 */
public abstract class AbstractDecorator implements ValidationDecorator {
    public static final String ERROR_COLOR_KEY = "AbstractDecorator.errorColor";
    public static final String WARNING_COLOR_KEY = "AbstractDecorator.warningColor";
    //the same key as in com.jgoodies.validation.view.ValidationComponentUtils
    private static final String STORED_BACKGROUND_KEY = "validation.storedBackground";
    private static final String STORED_FOREGROUND_KEY = "validation.storedForeground";

    static {
        //store the UIManager defaults that may not have already been loaded
        //Don't blow away anything that has already been set
        Color c = UIManager.getColor(ERROR_COLOR_KEY);
        if (c == null) {
            UIManager.put(ERROR_COLOR_KEY, new Color(255, 215, 215));
        }
        c = UIManager.getColor(WARNING_COLOR_KEY);
        if (c == null) {
            UIManager.put(WARNING_COLOR_KEY, new Color(255, 235, 205));
        }
    }
   
    /** Creates a new instance of AbstractValidationDecorator */
    public AbstractDecorator() {
    }
    
    protected static void setErrorBackground(JComponent comp) {
        storeBackground(comp);
        comp.setBackground(UIManager.getColor(ERROR_COLOR_KEY));
    }
    
    protected static void setWarningBackground(JComponent comp) {
        storeBackground(comp);
        comp.setBackground(UIManager.getColor(WARNING_COLOR_KEY));
    }
    
    /**
     * Provides the same functionality as the JGoodies ValidationComponentUtils
     * method of the same name
     */
    protected static Color getStoredBackground(JComponent comp) {
        return (Color) comp.getClientProperty(STORED_BACKGROUND_KEY);
    }

    protected static void storeBackground(JComponent comp) {
        restoreBackground(comp);
        comp.putClientProperty(STORED_BACKGROUND_KEY, comp.getBackground());
    }
    
    /**
     * Provides the same functionality as the JGoodies ValidationComponentUtils
     * method of the same name
     */
    protected static void restoreBackground(JComponent comp) {
        Color c = getStoredBackground(comp);
        if (c != null) {
            comp.setBackground(c);
        }
    }
    
    /**
     */
    protected static Color getStoredForeground(JComponent comp) {
        return (Color) comp.getClientProperty(STORED_FOREGROUND_KEY);
    }
    
    protected static void storeForeground(JComponent comp) {
        restoreForeground(comp);
        comp.putClientProperty(STORED_FOREGROUND_KEY, comp.getForeground());
    }
    
    /**
     */
    protected static void restoreForeground(JComponent comp) {
        Color c = getStoredForeground(comp);
        if (c != null) {
            comp.setForeground(c);
        }
    }
    
    /**
     * When JComponent has been decorated, its background color, foreground color,
     * border, or other properties may have changed by a Decorator. If any
     * subsequent changes to the background color occur, I want to be notified of
     * them so that I can flush the "old" value from the client properties where
     * they are stored. That way, when asked to restore a background, for instance,
     * I can realize that I shouldn't restore the background and move on. Further,
     * if the component has changed from editable to non-editable or from enabled
     * to disable, I want to know so I can change the background color properly.
     */
    private static final class ComponentObserver implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            //TODO
            throw new AssertionError("not implemented");
        }
    }
}
