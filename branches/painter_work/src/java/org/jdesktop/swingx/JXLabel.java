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
/**
 *
 * @author joshy
 */
public class JXLabel extends JLabel {
    private Painter foregroundPainter;
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
        foregroundPainter = new AbstractPainter<JXLabel>() {
            protected void paintBackground(Graphics2D g, JXLabel component, int width, int height) {
                JXLabel.super.paintComponent(g);
            }
        };
    }
    
    public Painter getForegroundPainter() {
        return foregroundPainter;
    }
    
    public void setForegroundPainter(Painter painter) {
        Painter old = this.getForegroundPainter();
        this.foregroundPainter = painter;
        if (painter != null) {
            setOpaque(false);
        }
        firePropertyChange("foregroundPainter", old, getForegroundPainter());
        repaint();
    }
    
    protected void paintComponent(Graphics g) {
        if(foregroundPainter != null) {
            Graphics2D g2 = (Graphics2D)g.create();
            Insets ins = this.getInsets();
            g2.translate(ins.left, ins.top);
            foregroundPainter.paint(g2, this,
                    this.getWidth()  - ins.left - ins.right,
                    this.getHeight() - ins.top  - ins.bottom);
            g2.dispose();
        }
    }
}
