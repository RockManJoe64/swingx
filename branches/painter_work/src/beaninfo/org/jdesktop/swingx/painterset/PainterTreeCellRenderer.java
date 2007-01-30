package org.jdesktop.swingx.painterset;

import java.awt.Component;
import java.awt.image.BufferedImageOp;
import javax.swing.JLabel;
import javax.swing.JTree;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painterset.dndtree.DnDTree;
import org.jdesktop.swingx.painterset.dndtree.DnDTreeCellRenderer;


class PainterTreeCellRenderer extends DnDTreeCellRenderer {
    private final PainterEditorPanel painterEditorPanel;


    public PainterTreeCellRenderer(PainterEditorPanel painterEditorPanel, DnDTree dnDTree) {
        super(dnDTree);
        this.painterEditorPanel = painterEditorPanel;
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        label.setText(value.getClass().getSimpleName());
        if (value instanceof BufferedImageOp) {
            label.setIcon(this.painterEditorPanel.effectsIcon);
        }
        if (value instanceof AbstractPainter && !(value instanceof CompoundPainter)) {
            label.setIcon(this.painterEditorPanel.painterIcon);
        }
        return label;
    }
}