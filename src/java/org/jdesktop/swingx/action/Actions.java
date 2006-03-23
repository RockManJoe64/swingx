/*
 * Actions.java
 *
 * Created on March 16, 2006, 4:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.action;

import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * A factory/utility class for Action objects. It is conceptually similar to
 * the Collections and Arrays factory/utility classes in the java.util package.
 *
 * @author rbair
 */
public class Actions {
    
    /** Creates a new instance of Actions */
    private Actions() {
    }
  
    /**
     * Copies the state out of the source action into the dest action
     */
    public static <T extends Action> void copy(T source, T dest) {
        if (source == null) {
            return;
        }
        
        if (source instanceof AbstractAction) {
            Object[] keys = ((AbstractAction)source).getKeys();
            if (keys != null) {
                for (Object key : keys) {
                    String k = "" + key;
                    dest.putValue(k, source.getValue(k));
                }
            }
            dest.setEnabled(source.isEnabled());
            
            // Copy change listeners.
            for (PropertyChangeListener l : ((AbstractAction)source).getPropertyChangeListeners()) {
                dest.addPropertyChangeListener(l);
            }
        } else {
            //copy over only the bare essentials
            dest.putValue(Action.ACCELERATOR_KEY, source.getValue(Action.ACCELERATOR_KEY));
            dest.putValue(Action.ACTION_COMMAND_KEY, source.getValue(Action.ACTION_COMMAND_KEY));
            dest.putValue(Action.DEFAULT, source.getValue(Action.DEFAULT));
            dest.putValue(Action.LONG_DESCRIPTION, source.getValue(Action.LONG_DESCRIPTION));
            dest.putValue(Action.MNEMONIC_KEY, source.getValue(Action.MNEMONIC_KEY));
            dest.putValue(Action.NAME, source.getValue(Action.NAME));
            dest.putValue(Action.SHORT_DESCRIPTION, source.getValue(Action.SHORT_DESCRIPTION));
            dest.putValue(Action.SMALL_ICON, source.getValue(Action.SMALL_ICON));
        }
    }
}
