/*
 * JTableBinding.java
 *
 * Created on August 12, 2005, 10:34 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.binding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.jdesktop.swingx.JXTree;

/**
 * A binding implementation that binds a JTable to a DataModel.
 *
 * @author rbair
 */
public class JXTreeBinding extends JTreeBinding {
    /** Creates a new instance of JTreeBinding */
    public JXTreeBinding(JXTree tree) {
        super(tree);
        setSelectionModelName(tree.getSelectionModelName());
        super.setNodeDescriptor(tree.getDescriptor());
        tree.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("selectionModelName")) {
                    setSelectionModelName((String)evt.getNewValue());
                } else if (evt.getPropertyName().equals("descriptor")) {
                    setNodeDescriptor((NodeDescriptor)evt.getNewValue());
                }
            }
        });
    }
    
    protected void doInitialize() {
        super.doInitialize();
    }
}
