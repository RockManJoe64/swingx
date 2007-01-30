/*
 * ShapeTest.java
 *
 * Created on August 20, 2006, 6:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painters;

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
import org.jdesktop.swingx.painter.effects.NeonBorderEffect;
import org.jdesktop.swingx.painter.effects.AbstractAreaEffect;
import org.jdesktop.swingx.painter.ShapePainter;
import org.jdesktop.swingx.painterset.*;

/**
 *
 * @author joshy
 */
public class ShapeTest extends JXPanel {
    public ShapePainter sp;
    public AbstractAreaEffect effect;
    /** Creates a new instance of ShapeTest */
    public ShapeTest() {
        
        
        Shape shape = PainterEditorPanel.stringToShape("S",new Font("Arial",Font.BOLD,160));
        sp = new ShapePainter(shape,Color.RED);
        sp.setBorderWidth(5);
        sp.setBorderPaint(Color.RED.darker());
        sp.setAntialiasing(AbstractPainter.Antialiasing.On);
        sp.setStyle(ShapePainter.Style.FILLED);
        effect = new NeonBorderEffect();
        sp.setPathEffects(effect);
        MattePainter mp = new MattePainter(Color.BLUE);
        
        final ShapeTestPanel tp = new ShapeTestPanel(this);
        ((JXPanel)tp.preview).setBackgroundPainter(new CompoundPainter(mp,sp));
        
        JXPropertySheet2 sheet = (JXPropertySheet2)tp.sheet;
        sheet.setBean(effect);
        sheet.setExpertOnly(false);
        sheet.setHiddenShown(false);
        sheet.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
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
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new ShapeTest());
                frame.pack();
                //frame.setSize(300,300);
                frame.setVisible(true);
            }
        });
    }

    
    boolean isEffectEnabled() {
        if(sp.getPathEffects() == null) {
            return false;
        }
        return true;
    }

    void removeEffect() {
        sp.setPathEffects(null);
    }

    void addEffect() {
        sp.setPathEffects(effect);
        
    }
    
}
