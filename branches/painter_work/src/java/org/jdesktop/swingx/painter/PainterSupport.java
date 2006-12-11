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
public class PainterSupport<T extends JComponent> extends AbstractPainter implements JXComponent {
    
    private Map<Integer, List<Painter>> layers = new HashMap<Integer, List<Painter>>();
    
    /**
     *implementation detail */
    
    public void paintBackground(Graphics2D g, JComponent component, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        Set<Integer> layerSet = layers.keySet();
        List<Integer> layerList = new ArrayList(layerSet);
        Collections.sort(layerList);
        for(Integer n : layerList) {
            List<Painter> layer = layers.get(n);
            for(Painter p : layer) {
                p.paint(g2,component,width,height);
            }
        }
        g2.dispose();
    }
    
    
    /** Replace all painters at the specified level with the new painter
     */
    public void setPainter(Painter painter, int level) {
        List<Painter> list = new ArrayList<Painter>();
        list.add(painter);
        layers.put(level,list);
    }
    
    public void setPainters(Map<Integer, List<Painter>> painters) {
        layers = painters;
    }

    public void setBackgroundPainter(Painter painter) {
        setPainter(JXComponent.BACKGROUND_LAYER, painter);
    }

    public void setForegroundPainter(Painter painter) {
        setPainter(JXComponent.FOREGROUND_LAYER, painter);
    }

    public Painter getBackgroundPainter() {
        return getPainter(JXComponent.BACKGROUND_LAYER);
    }

    public Painter getForegroundPainter() {
        return getPainter(JXComponent.FOREGROUND_LAYER);
    }

    public Map<Integer, List<Painter>> getPainters() {
        return layers;
    }

    public Painter getPainter(Integer layer) {
        if(!layers.containsKey(layer)) {
            return null;
        }
        return layers.get(layer).get(0);
    }
    
    public void setPainter(Integer layer, Painter painter) {
        if(!layers.containsKey(layer)) {
            List<Painter> list = new ArrayList<Painter>();
            layers.put(layer,list);
        }
        layers.get(layer).add(0,painter);
    }

    
}
