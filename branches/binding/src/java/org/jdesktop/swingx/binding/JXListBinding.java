/*
 * JListBinding.java
 *
 * Created on August 12, 2005, 10:50 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.binding;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.jdesktop.swingx.JXList;
/**
 *
 * @author rbair
 */
public class JXListBinding extends JListBinding {
    
    /** Creates a new instance of JListBinding */
    public JXListBinding(JXList list) {
        super(list, list.getDisplayName());
        setSelectionModelName(list.getSelectionModelName());
        list.addPropertyChangeListener("displayName", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setDisplayName((String)evt.getNewValue());
            }
        });
    }
}
