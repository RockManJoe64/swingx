package org.jdesktop.swingx.painterset.dndtree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class DnDTreeCellRenderer extends DefaultTreeCellRenderer {
    private final DnDTree dnDTree;

    boolean isTargetNode;
    boolean isTargetNodeLeaf;
    boolean isLastItem;
    int BOTTOM_PAD = 30;
    public DnDTreeCellRenderer(DnDTree dnDTree) {
        super();
        this.dnDTree = dnDTree;
    }
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean isExpanded, boolean isLeaf, int row, boolean hasFocus) {
        isTargetNode = (value == this.dnDTree.dropTargetNode);
        isTargetNodeLeaf = (isTargetNode && tree.getModel().isLeaf(value));        
        //                            ((TreeNode)value).isLeaf());
                // isLastItem = (index == list.getModel().getSize()-1);
                boolean showSelected = isSelected &
                              (dnDTree.dropTargetNode == null);
        return super.getTreeCellRendererComponent (tree, value,
                                                   isSelected, isExpanded,
                                                   isLeaf, row, hasFocus);

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isTargetNode) {
            g.setColor(Color.black);
            if (isTargetNodeLeaf) {
                g.drawLine (0, 0, getSize().width, 0);
            } else {
                g.drawRect (0, 0, getSize().width-1, getSize().height-1);
            }
        }
    }}