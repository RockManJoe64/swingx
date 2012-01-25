/*
 * DefaultLinkAction.java
 *
 * Created on March 16, 2006, 5:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.EventListenerList;

/**
 * This LinkAction will fire action events when an event occurs. Seems odd, but
 * this is how you do it in the GUI Builder
 *
 * @author Richard
 */
public class DefaultLinkAction extends LinkAction {
    private EventListenerList listeners = new EventListenerList();
    
    /** Creates a new instance of DefaultLinkAction */
    public DefaultLinkAction() {
    }
    
    public void actionPerformed(ActionEvent ae) {
        for (ActionListener listener : getActionListeners()) {
            listener.actionPerformed(ae);
        }
    }
    
    public void addActionListener(ActionListener listener) {
        listeners.add(ActionListener.class, listener);
    }
    
    public void removeActionListener(ActionListener listener) {
        listeners.remove(ActionListener.class, listener);
    }
    
    public ActionListener[] getActionListeners() {
        return listeners.getListeners(ActionListener.class);
    }
}
