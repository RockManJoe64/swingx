package org.jdesktop.swingx.painterset.actions;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.editors.PainterUtil;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painterset.PainterEditorPanel;
import org.jdesktop.swingx.painterset.PainterSet;

public class SaveAsAction extends AbstractAction {
    public SaveAsAction(PainterEditorPanel painterEditorPanel) {
        this.painterEditorPanel = painterEditorPanel;
    }

    private final PainterEditorPanel painterEditorPanel;


    public void actionPerformed(ActionEvent e) {
        save(painterEditorPanel);
    }
    
    public static void save(PainterEditorPanel painterEditorPanel) {
        try {
            FileDialog fd = new FileDialog((Frame) SwingUtilities.windowForComponent(painterEditorPanel));
            fd.setMode(FileDialog.SAVE);
            fd.setVisible(true);
            if (fd.getFile() != null) {
                PainterSet set = painterEditorPanel.getSelectedPainterSet();
                File file = new File(fd.getDirectory(), fd.getFile());
                set.file = file;
                set.name = file.getName();
                PainterUtil.savePainterToFile((CompoundPainter)set.model.getRoot(), file);
                painterEditorPanel.updateFromPainterSet(set);
            }  else {
            }
        }  catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}