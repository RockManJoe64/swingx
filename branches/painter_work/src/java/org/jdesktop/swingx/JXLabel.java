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
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.JXComponent;
import org.jdesktop.swingx.painter.PainterSupport;

/**
 *
 * @author joshy
 */
public class JXLabel extends JLabel implements JXComponent {
    
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
        setPainterSupport(new PainterSupport());
        getPainterSupport().setPainter(PainterSupport.COMPONENT_LAYER,
                new AbstractPainter() {
            protected void paintBackground(Graphics2D g, JComponent component, int width, int height) {
                JXLabel.super.paintComponent(g);
            }
        });
    }
    
    private PainterSupport painterSupport;
    
    public Painter getBackgroundPainter() {
        return getPainterSupport().getBackgroundPainter();
    }
    
    public void setBackgroundPainter(Painter backgroundPainter) {
        Painter old = this.getBackgroundPainter();
        getPainterSupport().setBackgroundPainter(backgroundPainter);
        if (backgroundPainter != null) {
            setOpaque(false);
        }
        firePropertyChange("backgroundPainter", old, getBackgroundPainter());
        repaint();
    }
    
    public Painter getForegroundPainter() {
        return getPainterSupport().getForegroundPainter();
    }
    
    public void setForegroundPainter(Painter painter) {
        Painter old = this.getForegroundPainter();
        getPainterSupport().setForegroundPainter(painter);
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
    
    private PainterSupport getPainterSupport() {
        return painterSupport;
    }
    
    private void setPainterSupport(PainterSupport painterSet) {
        this.painterSupport = painterSet;
    }
    
    public Map<Integer, List<Painter>> getPainters() {
        return getPainterSupport().getPainters();
    }
    
    public void setPainters(Map<Integer, List<Painter>> painters) {
        getPainterSupport().setPainters(painters);
    }
    
    public void setPainter(Integer layer, Painter painter) {
        getPainterSupport().setPainter(layer,painter);
    }
    
    public Painter getPainter(Integer layer) {
        return getPainterSupport().getPainter(layer);
    }
    
}
