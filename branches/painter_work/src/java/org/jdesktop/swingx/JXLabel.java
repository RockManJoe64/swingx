/*
 * JXLabel.java
 *
 * Created on July 25, 2006, 1:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.lang.reflect.Method;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.PainterSet;

/**
 *
 * @author joshy
 */
public class JXLabel extends JLabel {
    
    /** Creates a new instance of JXLabel */
    public JXLabel() {
        super();
        initPainterSet();
    }
    public JXLabel(String text) {
        super(text);
        initPainterSet();
    }
    private void initPainterSet() {
        setPainterSet(new PainterSet());
        getPainterSet().setPainter(new AbstractPainter() {
            protected void paintBackground(Graphics2D g, JComponent component, int width, int height) {
                JXLabel.super.paintComponent(g);
            }
        }, PainterSet.COMPONENT);
    }
    
    //private Painter backgroundPainter;
    //private Painter foregroundPainter;
    private PainterSet painterSet;

    public Painter getBackgroundPainter() {
        return getPainterSet().getPainter(PainterSet.BACKGROUND);
    }

    public void setBackgroundPainter(Painter backgroundPainter) {
        Painter old = this.getBackgroundPainter();
        getPainterSet().setPainter(backgroundPainter, PainterSet.BACKGROUND);
        if (backgroundPainter != null) {
            setOpaque(false);
        }
        firePropertyChange("backgroundPainter", old, getBackgroundPainter());
        repaint();
    }
    
    public Painter getForegroundPainter() {
        return getPainterSet().getPainter(PainterSet.FOREGROUND);
    }
    
    public void setForegroundPainter(Painter painter) {
        Painter old = this.getForegroundPainter();
        getPainterSet().setPainter(painter, PainterSet.BACKGROUND);
        if (painter != null) {
            setOpaque(false);
        }
        firePropertyChange("foregroundPainter", old, getForegroundPainter());
        repaint();
    }
    
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g.create();
        Insets ins = this.getInsets();
        g2.translate(ins.left, ins.top);
        getPainterSet().paint(g2, this,
                this.getWidth()  - ins.left - ins.right,
                this.getHeight() - ins.top  - ins.bottom);
        g2.dispose();
    }

    public PainterSet getPainterSet() {
        return painterSet;
    }

    private void setPainterSet(PainterSet painterSet) {
        this.painterSet = painterSet;
    }
    
}
