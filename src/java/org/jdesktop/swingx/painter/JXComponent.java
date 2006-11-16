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

/**
 *
 * @author joshy
 */
public interface JXComponent {
   
    int BACKGROUND_LAYER = -100;

    int COMPONENT_LAYER = 0;

    int FOREGROUND_LAYER = 100;

    int VALIDATION_LAYER = 200;

    int OVERLAY_LAYER = 300;
    /**
     * Add this painter at the FOREGROUND_LEVEL. If there are any 
     * painters already at that level then the new painter will be added
     * after them (ie: drawn on top of them).
     */
    void addPainter(Painter painter);

    /**
     * Add this specified painter at the specified level. If there are any
     *  painters already at that level, then the new painter will be added
     *  after them (ie: drawn on top of them)
     */
    void addPainter(Painter painter, int level);

    /**
     * Get all of the painters as a list, ordered by layer
     */
    List<Painter> getPainters();
    
    /** set all of the painters as an ordered list. All painters will be
     * placed at the COMPONENT_LEVEL. This removes any existing painters.
     * If a null value is passed in then the internal list of painters will be empty
     */
    public void setPainters(List<Painter> list);

    /**
     * Get the painter at the requested level. If there is more than one painter
     * at that level, it will return the first one.
     */
    Painter getPainter(int level);

    /**
     * Get a list of all painters at the requested level
     */
    List<Painter> getPainters(int level);

    /**
     * Replace all painters at the specified level with the new painter
     */
    void setPainter(Painter painter, int level);
    
}
