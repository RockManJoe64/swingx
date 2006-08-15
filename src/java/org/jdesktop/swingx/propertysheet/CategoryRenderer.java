/*
 * CategoryRenderer.java
 *
 * Created on August 13, 2006, 11:16 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.propertysheet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Point;
import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTreeUI;
import org.apache.batik.ext.awt.LinearGradientPaint;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.RectanglePainter;
import org.joshy.util.u;

/**
 *
 *
 *
 * @author joshy
 */
public class CategoryRenderer extends JXLabel {
    public static final boolean doBlue = true;
    /** Creates a new instance of CategoryRenderer */
    public CategoryRenderer() {
        Paint bg = Color.BLUE;
        float f[] = new float[] { 0.0f, 0.5f, 1.0f };
        Color colors[] = new Color[] {
            new Color(0x6e6eff),
            new Color(0x0000ff),
            new Color(0x0000e3) };
        bg = new LinearGradientPaint(new Point(0,0), new Point(0,10), f, colors);
        Paint border = new Color(0,0,0,0);
        RectanglePainter p = new RectanglePainter(0,0,0,0, 10,10, false, bg, 0, border);
        p.setSnapPaint(true);
        if(doBlue) {
            setBackgroundPainter(p);
            setForeground(Color.WHITE);
        }
    }

    public void configure(JTree tree, boolean expanded, Category cat) {
        // add these icons back since they will be overriden by my indent hacks
        BasicTreeUI ui = (BasicTreeUI)tree.getUI();
        if(expanded) {
            setIcon(ui.getExpandedIcon());
        } else {
            setIcon(ui.getCollapsedIcon());
        }
        setText(cat.name);
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(200,20);
    }
    
    
    public void paint(Graphics g) {
        u.p("pref size = " + getPreferredSize());
        this.setLocation(0,this.getY());
        u.p("me = " + this);
        u.p("parent = " + getParent());
        u.p("parent's parent = " + getParent().getParent());
        super.paint(g);
    }
}
