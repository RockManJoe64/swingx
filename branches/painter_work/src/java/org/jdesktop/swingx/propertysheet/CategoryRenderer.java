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
import java.awt.Paint;
import java.awt.Point;
import org.apache.batik.ext.awt.LinearGradientPaint;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.painter.RectanglePainter;

/**
 *
 *
 *http://www.walkersoftware.net/2005/09/09/tiger-trees/
 *
 * @author joshy
 */
public class CategoryRenderer extends JXLabel {
    public static final boolean doBlue = false;
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
}
