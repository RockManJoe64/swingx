/*
 * JXButton.java
 *
 * Created on July 25, 2006, 3:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JButton;
import org.jdesktop.swingx.painter.Painter;

/**
 *
 * @author jm158417
 */
public class JXButton extends JButton {
    
    /** Creates a new instance of JXButton */
    public JXButton() {
    }
    
    private Painter backgroundPainter;
    private Painter foregroundPainter;

    public Painter getBackgroundPainter() {
        return backgroundPainter;
    }

    public void setBackgroundPainter(Painter backgroundPainter) {
        Painter old = this.getBackgroundPainter();
        this.backgroundPainter = backgroundPainter;
        if (backgroundPainter != null) {
            setOpaque(false);
            setContentAreaFilled(false);
            setBorder(null);
        }
        firePropertyChange("backgroundPainter", old, backgroundPainter);
        repaint();
    }
    
    public Painter getForegroundPainter() {
        return foregroundPainter;
    }
    
    public void setForegroundPainter(Painter painter) {
        Painter old = this.getForegroundPainter();
        this.foregroundPainter = painter;
        if (foregroundPainter != null) {
            setOpaque(false);
        }
        firePropertyChange("foregroundPainter", old, foregroundPainter);
        repaint();
    }
    
    protected void paintComponent(Graphics g) {
        if (backgroundPainter != null) {
            Graphics2D g2 = (Graphics2D)g.create();
            //Insets ins = this.getBorder().getBorderInsets(this);
            Insets ins = this.getInsets();
            //g2.translate(ins.left, ins.top);
            backgroundPainter.paint(g2, this, this.getWidth(), this.getHeight());
            //backgroundPainter.paint(g2, this, 
            //        this.getWidth()  - ins.left - ins.right,
            //        this.getHeight() - ins.top  - ins.bottom);
            g2.dispose();
        }
        
        if (foregroundPainter != null) {
            Graphics2D g2 = (Graphics2D)g.create();
            Insets ins = this.getInsets();
            g2.translate(ins.left, ins.top);
            foregroundPainter.paint(g2, this, 
                    this.getWidth()  - ins.left - ins.right,
                    this.getHeight() - ins.top  - ins.bottom);
            g2.dispose();
        } else {
            //super.paintComponent(g);
        }        
    }
    
}
