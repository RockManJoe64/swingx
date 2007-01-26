package org.jdesktop.swingx.painterset.actions;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImageOp;
import javax.swing.AbstractAction;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.effects.ImageEffect;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.effects.AbstractPathEffect;
import org.jdesktop.swingx.painter.ShapePainter;
import org.jdesktop.swingx.painterset.PainterEditorPanel;
import org.jdesktop.swingx.painterset.PainterTreeModel;

public class AddEffectAction extends AbstractAction {
    public AddEffectAction(PainterEditorPanel painterEditorPanel) {
        this.painterEditorPanel = painterEditorPanel;
    }
    
    private final PainterEditorPanel painterEditorPanel;
    
    
    public void actionPerformed(ActionEvent e) {
        if (this.painterEditorPanel.getSelectedPainter()  instanceof AbstractPainter) {
            try {
                PainterTreeModel painterTreeModel = (PainterTreeModel) painterEditorPanel.getSelectedTree().getModel();
                
                Class clazz = (Class) painterEditorPanel.newEffectCombo.getSelectedItem();
                Object obj = clazz.newInstance();
                if (obj instanceof ImageEffect) {
                    ImageEffect eff = null;
                    eff = (ImageEffect) obj;
                    painterTreeModel.addEffect((AbstractPainter) painterEditorPanel.getSelectedPainter(), eff);
                }
                if (obj instanceof BufferedImageOp) {
                    ImageEffect eff = null;
                    eff = new ImageEffect((BufferedImageOp) obj);
                    painterTreeModel.addEffect((AbstractPainter) painterEditorPanel.getSelectedPainter(), eff);
                }
                if (obj instanceof AbstractPathEffect) {
                    AbstractPathEffect eff = (AbstractPathEffect) obj;
                    Painter ptr = (Painter) painterEditorPanel.getSelectedPainter();
                    if(ptr instanceof ShapePainter) {
                        painterTreeModel.addShapeEffect((ShapePainter)ptr,eff);
                    }
                }
            }  catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}