package org.jdesktop.swingx.painterset.actions;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImageOp;
import javax.swing.AbstractAction;
import javax.swing.JTree;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painterset.PainterEditorPanel;
import org.jdesktop.swingx.painterset.PainterTreeModel;


public class DeleteAction extends AbstractAction {
    public DeleteAction(PainterEditorPanel painterEditorPanel) {
        this.painterEditorPanel = painterEditorPanel;
    }

    private final PainterEditorPanel painterEditorPanel;

    
    public void actionPerformed(ActionEvent e) {
        JTree painterTree = painterEditorPanel.getSelectedTree();
        Object node = painterTree.getSelectionPath().getLastPathComponent();
        Object parent = painterTree.getSelectionPath().getParentPath().getLastPathComponent();
        PainterTreeModel painterTreeModel = (PainterTreeModel) painterTree.getModel();
        if (node instanceof Painter) {
            painterTreeModel.removePainter((CompoundPainter) parent, (Painter) node);
        }
        if (node instanceof BufferedImageOp) {
            painterTreeModel.removeEffect((AbstractPainter) parent, (BufferedImageOp) node);
        }
    }
}