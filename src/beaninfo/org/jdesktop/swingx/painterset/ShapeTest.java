/*
 * ShapeTest.java
 *
 * Created on August 20, 2006, 6:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painterset;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXPropertySheet2;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.ShapeEffect;
import org.jdesktop.swingx.painter.ShapePainter;
import org.joshy.util.u;

/**
 *
 * @author joshy
 */
public class ShapeTest extends JXPanel {
    
    /** Creates a new instance of ShapeTest */
    public ShapeTest() {
        
        
        Shape shape = PainterEditorPanel.stringToShape("S",new Font("Arial",Font.BOLD,160));
        ShapePainter sp = new ShapePainter(shape,Color.RED);
        sp.setStrokeWidth(5);//new BasicStroke(5f));
        sp.setStrokePaint(Color.RED);
        sp.setAntialiasing(AbstractPainter.Antialiasing.On);
        sp.setStyle(ShapePainter.Style.FILLED);
        ShapeEffect se = new ShapeEffect();
        sp.setShapeEffect(se);
        MattePainter mp = new MattePainter(Color.BLUE);
        
        final ShapeTestPanel tp = new ShapeTestPanel();
        ((JXPanel)tp.preview).setBackgroundPainter(new CompoundPainter(mp,sp));
        
        JXPropertySheet2 sheet = (JXPropertySheet2)tp.sheet;
        sheet.setBean(se);
        sheet.setExpertOnly(false);
        sheet.setHiddenShown(false);
        sheet.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                u.p("got a change");
                tp.preview.repaint();
            }
        });
        
        setLayout(new BorderLayout());
        add(tp,"Center");
    }
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.add(new ShapeTest());
                frame.pack();
                //frame.setSize(300,300);
                frame.setVisible(true);
            }
        });
    }
    
}
