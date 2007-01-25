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
 * A JLabel subclass which supports Painters with the foregroundPainter property. By default the
 * foregroundPainter is set to a special painter which will draw the label normally, as specified
 * by the current look and feel. Setting a new foregroundPainter will replace the existing one.
 * To modify the standard drawing behavior developers may wrap the standard painter with a
 * CompoundPainter.  
 * 
 * Ex:
 * 
 * JXLabel label = new JXLabel();
 * Painter standardPainter = label.getForegroundPainter();
 * MattePainter blue = new MattePainter(Color.BLUE);
 * CompoundPainter compound = new CompoundPainter(blue,standardPainter);
 * label.setForegroundPainter(label);
 * @author joshua.marinacci@sun.com
 */
public class JXLabel extends JLabel {
    private Painter foregroundPainter;
    /**
     * 
     */
    public JXLabel() {
        super();
        initPainterSupport();
    }
    /**
     * 
     * @param text 
     */
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
    
    /**
     * Returns the current foregroundPainter. This is a bound property.  By default the foregroundPainter
     * will be an internal painter which executes the standard painting code (paintComponent()).
     * @return 
     */
    public Painter getForegroundPainter() {
        return foregroundPainter;
    }
    
    /**
     * Sets a new foregroundPainter on the label. This will replace the existing foreground painter.
     * Existing painters can be wrapped by using a CompoundPainter.
     * @param painter 
     */
    public void setForegroundPainter(Painter painter) {
        Painter old = this.getForegroundPainter();
        this.foregroundPainter = painter;
        if (painter != null) {
            setOpaque(false);
        }
        firePropertyChange("foregroundPainter", old, getForegroundPainter());
        repaint();
    }
    
    /**
     * 
     * @param g 
     */
    protected void paintComponent(Graphics g) {
        if(foregroundPainter != null) {
            Graphics2D g2 = (Graphics2D)g.create();
            foregroundPainter.paint(g2, this,
                    this.getWidth(),
                    this.getHeight());
            //Insets ins = this.getInsets();
            //g2.translate(ins.left, ins.top);
            //foregroundPainter.paint(g2, this,
            //        this.getWidth()  - ins.left - ins.right,
            //        this.getHeight() - ins.top  - ins.bottom);
            g2.dispose();
        }
    }
}
