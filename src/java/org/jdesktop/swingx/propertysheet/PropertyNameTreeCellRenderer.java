/*
 * PropertyNameTreeCellRenderer.java
 *
 * Created on August 12, 2006, 6:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.propertysheet;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPropertySheet;
import org.joshy.util.u;

/**
 *
 * @author joshy
 */
public class PropertyNameTreeCellRenderer extends DefaultTreeCellRenderer {
    JXPropertySheet sheet;
    CategoryRenderer categoryRend;
    
    /** Creates a new instance of PropertyNameTreeCellRenderer */
    public PropertyNameTreeCellRenderer(JXPropertySheet sheet) {
        this.sheet = sheet;
        categoryRend = new CategoryRenderer();
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object object,
            boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, object,
                selected, expanded, leaf, row, hasFocus);
        label.setIcon(null);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        if(object == sheet.bean) {
            label.setText("The Bean");
        }
        if(object instanceof Category) {
            categoryRend.configure(tree, expanded, (Category)object);
            return categoryRend;
        }
        if(object instanceof PropertyDescriptor) {
            PropertyDescriptor prop = (PropertyDescriptor) object;
            PropertyEditor pe = BeanUtils.getPE(prop, sheet.bean);
            //label.setBackground(Color.RED);
            //label.setOpaque(true);
            label.setText(prop.getDisplayName());
        }
        return label;
    }
    
}
