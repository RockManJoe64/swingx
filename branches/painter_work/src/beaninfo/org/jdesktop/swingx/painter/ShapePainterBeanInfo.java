/*
 * ShapePainterBeanInfo.java
 *
 * Created on August 1, 2006, 5:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import org.jdesktop.swingx.editors.Paint2PropertyEditor;
import org.netbeans.beaninfo.editors.InsetsEditor;

/**
 *
 * @author joshy
 */
public class ShapePainterBeanInfo extends PositionedPainterBeanInfo {
    
    /** Creates a new instance of ShapePainterBeanInfo */
    public ShapePainterBeanInfo() {
        super(ShapePainter.class);
    }
    
    protected void initialize() {
        super.initialize();
        setPropertyEditor(Paint2PropertyEditor.class, "fillPaint", "strokePaint");
        setPropertyEditor(InsetsEditor.class,"insets");
    }
}
