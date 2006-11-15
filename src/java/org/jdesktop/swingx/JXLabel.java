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
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.PainterSupport;
import org.jdesktop.swingx.painter.PainterSupportImpl;

/**
 *
 * @author joshy
 */
public class JXLabel extends JLabel implements PainterSupport {
    
    /** Creates a new instance of JXLabel */
    public JXLabel() {
        super();
        initPainterSupport();
    }
    public JXLabel(String text) {
        super(text);
        initPainterSupport();
    }
    private void initPainterSupport() {
        setPainterSupport(new PainterSupportImpl());
        getPainterSupport().setPainter(new AbstractPainter() {
            protected void paintBackground(Graphics2D g, JComponent component, int width, int height) {
                JXLabel.super.paintComponent(g);
            }
        }, PainterSupportImpl.COMPONENT);
    }
    
    private PainterSupportImpl painterSupport;

    public Painter getBackgroundPainter() {
        return getPainterSupport().getPainter(PainterSupportImpl.BACKGROUND);
    }

    public void setBackgroundPainter(Painter backgroundPainter) {
        Painter old = this.getBackgroundPainter();
        getPainterSupport().setPainter(backgroundPainter, PainterSupportImpl.BACKGROUND);
        if (backgroundPainter != null) {
            setOpaque(false);
        }
        firePropertyChange("backgroundPainter", old, getBackgroundPainter());
        repaint();
    }
    
    public Painter getForegroundPainter() {
        return getPainterSupport().getPainter(PainterSupportImpl.FOREGROUND);
    }
    
    public void setForegroundPainter(Painter painter) {
        Painter old = this.getForegroundPainter();
        getPainterSupport().setPainter(painter, PainterSupportImpl.BACKGROUND);
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
        getPainterSupport().paint(g2, this,
                this.getWidth()  - ins.left - ins.right,
                this.getHeight() - ins.top  - ins.bottom);
        g2.dispose();
    }

    private PainterSupportImpl getPainterSupport() {
        return painterSupport;
    }

    private void setPainterSupport(PainterSupportImpl painterSet) {
        this.painterSupport = painterSet;
    }

    public void addPainter(Painter painter) {
        painterSupport.addPainter(painter);
    }

    public void addPainter(Painter painter, int level) {
        painterSupport.addPainter(painter,level);
    }

    public List<Painter> getOrderedPainters() {
        return painterSupport.getOrderedPainters();
    }

    public Painter getPainter(int level) {
        return painterSupport.getPainter(level);
    }

    public List<Painter> getPainters(int level) {
        return painterSupport.getPainters(level);
    }

    public void setPainter(Painter painter, int level) {
        painterSupport.setPainter(painter,level);
    }
    
}
