/*
 * NeonBorderEffect.java
 *
 * Created on October 20, 2006, 1:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter.effects;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 *
 * @author joshy
 */
public class NeonBorderEffect extends PathEffect {
    
    private Color edgeColor;
    private Color centerColor;
    
    
    /** Creates a new instance of NeonBorderEffect */
    public NeonBorderEffect(Color edgeColor, Color centerColor, int effectWidth) {
        super();
        setEffectWidth(effectWidth);
        this.edgeColor = edgeColor;
        this.centerColor = centerColor;
        this.setRenderInsideShape(false);
        this.setShouldFillShape(false);
        //setShapeMasked(false);
    }
    
    protected void paintBorderGlow(Graphics2D gfx, Shape clipShape, int width, int height) {
        //gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int steps = getBrushSteps();
        float brushAlpha = 1f/steps;
        
        //gfx.setPaint(getBrushColor());
        //Point2D offset = getOffset();
        //gfx.translate(offset.getX(), offset.getY());
        
        /*
        // draw the effect
        for(float i=steps-1; i>=0; i=i-1f) {
            float brushWidth = i * getEffectWidth()/steps;
            gfx.setPaint(interpolateColor(i/steps,edgeColor,centerColor));
            gfx.setStroke(new BasicStroke(brushWidth,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            gfx.draw(clipShape);
        }*/
        
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
        gfx.setComposite(AlphaComposite.SrcOver);
        for(int i=10; i>=0; i=i-3) {
            float brushWidth = (float)i;
            p("width = " + brushWidth);
            gfx.setPaint(interpolateColor((float)(10-i)/10, edgeColor, centerColor));
            gfx.setStroke(new BasicStroke(brushWidth));
            gfx.draw(clipShape);
        }
        
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
