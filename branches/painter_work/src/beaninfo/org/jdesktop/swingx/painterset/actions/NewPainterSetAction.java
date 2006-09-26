package org.jdesktop.swingx.painterset.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jdesktop.swingx.painterset.PainterEditorPanel;
import org.jdesktop.swingx.painterset.PainterSet;


public class NewPainterSetAction extends AbstractAction {
    public NewPainterSetAction(PainterEditorPanel painterEditorPanel) {
        this.painterEditorPanel = painterEditorPanel;
    }

    private final PainterEditorPanel painterEditorPanel;


    public void actionPerformed(ActionEvent e) {
        PainterSet set = painterEditorPanel.setupNewPainter(null);
        set.name = "Untitled 1";
        painterEditorPanel.painterTabs.addTab(set.name, set.tree);
    }
}