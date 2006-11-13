/*
 * LayerPainter.java
 *
 * Created on November 12, 2006, 10:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;

/**
 *
 * @author joshy
 * 
 */
public class PainterSet extends CompoundPainter {
   
    public static final int BACKGROUND = -100;
    public static final int COMPONENT = 0;
    public static final int FOREGROUND = 100;
    public static final int VALIDATION = 200;
    public static final int OVERLAY = 300;

    private Map<Integer, List<Painter>> layers = new HashMap<Integer, List<Painter>>();

    /**
     *implementation detail */
    
    public void paintBackground(Graphics2D g, JComponent component, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        if(getTransform() != null) {
            g2.setTransform(getTransform());
        }
        for(Painter p : getOrderedPainters()) {
            p.paint(g2,component,width,height);
        }
        g2.dispose();
    }
    
   
    /** Get the painter at the requested level. If there is more than one painter
     * at that level, it will return the first one.
     */
    public Painter getPainter(int level) {
        if(layers.containsKey(level)) {
            return layers.get(level).get(0);
        } else {
            return null;
        }
    }
    
    /** Get a list of all painters at the requested level
     */
    public List<Painter> getPainters(int level) {
        if(layers.containsKey(level)) {
            return layers.get(level);
        } else {
            return new ArrayList<Painter>();
        }
    }

    /** Replace all painters at the specified level with the new painter
     */
    public void setPainter(Painter painter, int level) {
        List<Painter> list = new ArrayList<Painter>();
        list.add(painter);
        layers.put(level,list);
    }
    
    /** Add this specified painter at the specified level. If there are any
     *  painters already at that level, then the new painter will be added
     *  after them (ie: drawn on top of them)
     */
    public void addPainter(Painter painter, int level) {
        if(!layers.containsKey(level)) {
            List<Painter> list = new ArrayList<Painter>();
            layers.put(level,list);
        }
        layers.get(level).add(painter);
    }
    
    /** Add this painter at the FOREGROUND level. If there are any 
     * painters already at that level then the new painter will be added
     * after them (ie: drawn on top of them).
     */
    public void addPainter(Painter painter) {
        addPainter(painter,FOREGROUND);
    }
    
    /** Get all of the painters as a list, ordered by layer
     */
    public List<Painter> getOrderedPainters() {
        List<Integer> set = new ArrayList(layers.keySet());
        Collections.sort(set);
        
        List<Painter> list2 = new ArrayList<Painter>();
        for(Integer i : set) {
            for(Painter pt : layers.get(i)) {
                list2.add(pt);
            }
        }
        return list2;
    }
    
    /*
    private class Layer {
        public int layer;
        public Painter painter;
        public Layer(int layer, Painter painter) {
            this.layer = layer;
            this.painter = painter;
        }
    }
     */
    
}
