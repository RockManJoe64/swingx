package org.jdesktop.swingx.painterset.actions;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.editors.PainterUtil;
import org.jdesktop.swingx.painterset.PainterEditorPanel;
import org.joshy.util.u;

public class ExportAction extends AbstractAction {
    public ExportAction(PainterEditorPanel painterEditorPanel) {
        this.painterEditorPanel = painterEditorPanel;
    }

    private final PainterEditorPanel painterEditorPanel;


    public void actionPerformed(ActionEvent e) {
        try {
            FileDialog fd = new FileDialog((Frame) SwingUtilities.windowForComponent(painterEditorPanel));
            fd.setMode(FileDialog.SAVE);
            fd.setVisible(true);
            if (fd.getFile() != null) {
                PainterUtil.savePainterToImage(this.painterEditorPanel.testPanel, 
                        this.painterEditorPanel.getRootPainter(), 
                        new File(fd.getDirectory(), fd.getFile()));
            }  else {
                u.p("save canceled");
            }
        }  catch (Exception ex) {
            u.p(ex);
        }
    }
}