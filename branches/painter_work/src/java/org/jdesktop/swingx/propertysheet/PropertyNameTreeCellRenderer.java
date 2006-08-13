/*
 * PropertyNameTreeCellRenderer.java
 *
 * Created on August 12, 2006, 6:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.propertysheet;

import java.awt.Component;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import org.jdesktop.swingx.JXPropertySheet;

/**
 *
 * @author joshy
 */
public class PropertyNameTreeCellRenderer extends DefaultTreeCellRenderer {
    JXPropertySheet sheet;
    
    /** Creates a new instance of PropertyNameTreeCellRenderer */
    public PropertyNameTreeCellRenderer(JXPropertySheet sheet) {
        this.sheet = sheet;
    }

    public Component getTreeCellRendererComponent(JTree jTree, Object object, boolean b, boolean b0, boolean b1, int i, boolean b2) {
        JLabel label = (JLabel) super.getTreeCellRendererComponent(jTree, object, b, b0, b1, i, b2);
        if(object == sheet.bean) {
            label.setText("The Bean");
        }
        if(object instanceof Category) {
            label.setText(((Category)object).name);
        }
        if(object instanceof PropertyDescriptor) {
            PropertyDescriptor prop = (PropertyDescriptor) object;
            PropertyEditor pe = BeanUtils.getPE(prop, sheet.bean);
            label.setText(prop.getDisplayName());
        }
        return label;
    }
    
}
