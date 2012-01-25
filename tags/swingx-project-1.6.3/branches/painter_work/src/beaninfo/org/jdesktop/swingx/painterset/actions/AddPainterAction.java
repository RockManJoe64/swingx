package org.jdesktop.swingx.painterset.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painterset.PainterEditorPanel;
import org.jdesktop.swingx.painterset.PainterTreeModel;

public class AddPainterAction extends AbstractAction {
    public AddPainterAction(PainterEditorPanel painterEditorPanel) {
        this.painterEditorPanel = painterEditorPanel;
    }

    private final PainterEditorPanel painterEditorPanel;

    
    public void actionPerformed(ActionEvent e) {
        try {
            Class clazz = (Class) painterEditorPanel.newPainterCombo.getSelectedItem();
            Painter pt = (Painter) clazz.newInstance();
            PainterTreeModel painterTreeModel = (PainterTreeModel) painterEditorPanel.getSelectedTree().getModel();
            painterTreeModel.insertNodeInto(pt, this.painterEditorPanel.getRootPainter(), this.painterEditorPanel.getRootPainter().getPainters().length);
        }  catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        } 
    }
}