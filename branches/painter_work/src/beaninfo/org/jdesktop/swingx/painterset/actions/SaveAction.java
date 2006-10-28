/*
 * SaveAction.java
 *
 * Created on August 11, 2006, 9:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painterset.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jdesktop.swingx.editors.PainterUtil;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painterset.PainterEditorPanel;
import org.jdesktop.swingx.painterset.PainterSet;

/**
 *
 * @author joshy
 */
public class SaveAction extends AbstractAction {
    
    private final PainterEditorPanel painterEditorPanel;
    
    /** Creates a new instance of SaveAction */
    public SaveAction(PainterEditorPanel painterEditorPanel) {
        this.painterEditorPanel = painterEditorPanel;
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
        PainterSet set = painterEditorPanel.getSelectedPainterSet();
        if(set.file != null) {
            PainterUtil.savePainterToFile((CompoundPainter)set.model.getRoot(), set.file);
        } else {
            SaveAsAction.save(painterEditorPanel);
        }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
