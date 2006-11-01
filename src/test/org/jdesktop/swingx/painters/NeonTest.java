/*
 * NeonTest.java
 *
 * Created on October 20, 2006, 11:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painters;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author joshy
 */
public class NeonTest extends JPanel {
    
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JPanel panel = new NeonTest();
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(panel);
                frame.pack();
                frame.setSize(300,300);
                frame.setVisible(true);
            }
        });
    }
    
    
    Color edgeColor = Color.RED;
    Color centerColor = Color.BLUE;
    float effectWidth = 20;
    
    /** Creates a new instance of NeonTest */
    public NeonTest() {        
    }
    
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D)graphics;
        int width = 100;
        int height = 100;
        
        Shape clipShape = new RoundRectangle2D.Double(10,10,100,100,20,20);
        Rectangle effectBounds = new Rectangle(0,0,
                width + (int)effectWidth,
                height + (int)effectWidth);
        
        // Set the clip shape onto a buffer image
        //BufferedImage clipImage = createClipImage(clipShape, g,
        //        width + (int)effectWidth,
        //        height + (int)effectWidth);
        BufferedImage clipImage = new BufferedImage(
                effectBounds.width,
                effectBounds.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = clipImage.createGraphics();
        
        // clear the buffer
        g2.setPaint(Color.BLACK);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        g2.fillRect(0,0, effectBounds.width, effectBounds.height);
        
        // turn on AA
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Apply the border glow effect
        g2.setComposite(AlphaComposite.SrcOver);
        paintBorderGlow(g2, clipShape, width, height);
        
        // clip out the parts we don't want
        g2.setComposite(AlphaComposite.Clear);
        g2.setColor(Color.WHITE);
        if(true) {
            Area area = new Area(effectBounds);//new Rectangle(0,0,50,50));
            area.subtract(new Area(clipShape));            
            g2.fill(area);
        } else {
            g2.fill(clipShape);
        }
        
        g2.dispose();
        // draw the final image
        g.drawImage(clipImage, 0, 0, null);
    }
    
    private BufferedImage createClipImage(Shape s, Graphics2D g, int width, int height) {
        // Create a translucent intermediate image in which we can perform
        // the soft clipping
        
        GraphicsConfiguration gc = g.getDeviceConfiguration();
        BufferedImage img = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        Graphics2D g2 = img.createGraphics();
        
        // Clear the image so all pixels have zero alpha
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, width, height);
        
        // Render our clip shape into the image.  Note that we enable
        // antialiasing to achieve the soft clipping effect.  Try
        // commenting out the line that enables antialiasing, and
        // you will see that you end up with the usual hard clipping.
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(s);
        g2.dispose();
        
        return img;
    }
    protected void paintBorderGlow(Graphics2D gfx, Shape clipShape, int width, int height) {
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int steps = 10;
        float brushAlpha = 1f/steps;
        
        //gfx.setPaint(getBrushColor());
        //Point2D offset = getOffset();
        //gfx.translate(offset.getX(), offset.getY());
        
        
        // draw the effect
        for(float i=steps-1; i>=0; i=i-1f) {
            float brushWidth = i * effectWidth/steps;
            gfx.setPaint(interpolateColor(i/steps,edgeColor,centerColor));
            gfx.setStroke(new BasicStroke(brushWidth,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            gfx.draw(clipShape);
        }
        
        /* // an interesting outline effect. stroke the shape with a wide brush
         * // then stroke again with slightly less wide one, then don't fill the middle
        for(int i=0; i<2; i++) {
            float brushWidth = (2-i)*5;
            p("widdth = " + brushWidth);
            gfx.setPaint(interpolateColor((float)(1-i), Color.BLACK, Color.WHITE));
            gfx.setStroke(new BasicStroke(brushWidth));
            gfx.draw(clipShape);
        }
         */
        /*
        for(int i=10; i>=0; i=i-3) {
            float brushWidth = (float)i;
            //p("widdth = " + brushWidth);
            gfx.setPaint(interpolateColor((float)(10-i)/10, Color.BLACK, Color.WHITE));
            gfx.setStroke(new BasicStroke(brushWidth));
            gfx.draw(clipShape);
        }*/
        
        //gfx.translate(-offset.getX(), -offset.getY());
        
    }
    
    public static void p(String str) {
        System.out.println(str);
    }
    
    protected Color interpolateColor(float t, Color start, Color end) {
        //System.out.println("t = " + t + " " + start + " -> " + end);
        float[] partsS = start.getRGBComponents(null);
        float[] partsE = end.getRGBComponents(null);
        float[] partsR = new float[3];
        for(int i=0; i<3; i++) {
            partsR[i] = (partsS[i] - partsE[i])*t + partsE[i];
            //System.out.println("i = " + i + " " + partsS[i] + " - " +
            //        partsE[i] + " * " + t + " = " + partsR[i]);
        }
        Color c = new Color(partsR[0],partsR[1],partsR[2]);
        System.out.println("c = " + c);
        return c;
        //return start;
    }

    
}
