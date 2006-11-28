/*
 * NewInterface.java
 *
 * Created on November 15, 2006, 9:58 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.util.List;
import java.util.Map;

/**
 *
 * @author joshy
 */
public interface JXComponent {
   
    public static final Integer BACKGROUND_LAYER = new Integer(-100);

    public static final Integer COMPONENT_LAYER = new Integer(0);

    public static final Integer FOREGROUND_LAYER = new Integer(100);

    public static final Integer VALIDATION_LAYER = new Integer(200);

    public static final Integer OVERLAY_LAYER = new Integer(300);
    
    
    /**
     * Get all of the painters as a list, ordered by layer
     */
    public Map<Integer,List<Painter>> getPainters();
    
    /** set all of the painters as an ordered list. All painters will be
     * placed at the COMPONENT_LEVEL. This removes any existing painters.
     * If a null value is passed in then the internal list of painters will be empty
     */
    public void setPainters(Map<Integer,List<Painter>> painters);
    
    public void setPainter(Integer layer, Painter painter);
    public Painter getPainter(Integer layer);

    public void setBackgroundPainter(Painter painter);
    public void setForegroundPainter(Painter painter);
    public Painter getBackgroundPainter();
    public Painter getForegroundPainter();
    
}
