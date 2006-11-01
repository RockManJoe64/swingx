/*
 * ShadowPathEffect.java
 *
 * Created on October 19, 2006, 2:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter.effects;

import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author joshy
 */
public class ShadowPathEffect extends AbstractPathEffect {
    
    /** Creates a new instance of ShadowPathEffect */
    public ShadowPathEffect() {
        super();
        setBrushColor(Color.BLACK);
        setRenderInsideShape(false);
        setShouldFillShape(true);
        setOffset(new Point(3,3));
    }
    
}
