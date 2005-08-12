/*
 * SwingBinding.java
 *
 * Created on August 12, 2005, 10:30 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.binding;

import javax.swing.JComponent;
import org.jdesktop.binding.Binding;

/**
 * Abstract implementation of Binding for Swing components
 *
 * @author rbair
 */
public abstract class SwingBinding extends Binding {
    
    /** Creates a new instance of SwingBinding */
    public SwingBinding(JComponent component) {
        super(component);
    }
}
