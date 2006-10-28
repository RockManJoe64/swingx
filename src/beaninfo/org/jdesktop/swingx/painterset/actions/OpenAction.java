package org.jdesktop.swingx.painterset.actions;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.editors.PainterUtil;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painterset.PainterEditorPanel;
import org.jdesktop.swingx.painterset.PainterSet;

public class OpenAction extends AbstractAction {
    public OpenAction(PainterEditorPanel painterEditorPanel) {
        this.painterEditorPanel = painterEditorPanel;
    }
    
    private final PainterEditorPanel painterEditorPanel;
    
    
    public void actionPerformed(ActionEvent e) {
        try {
            FileDialog fd = new FileDialog((Frame) SwingUtilities.windowForComponent(painterEditorPanel));
            fd.setMode(FileDialog.LOAD);
            fd.setVisible(true);
            if (fd.getFile() != null) {
                File file = new File(fd.getDirectory(), fd.getFile());
                Painter painter =  PainterUtil.loadPainter(file);
                if(! (painter instanceof CompoundPainter)) {
                    painter = new CompoundPainter(painter);
                }
                PainterSet set = painterEditorPanel.setupNewPainter((CompoundPainter)painter);
                set.name = file.getName();
                set.file = file;
                painterEditorPanel.addPainterSet(set);
            }
        }  catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}