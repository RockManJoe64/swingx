/*
 * RectanglePainterBeanInfo.java
 *
 * Created on July 18, 2006, 3:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import org.jdesktop.swingx.EnumerationValue;
import org.jdesktop.swingx.editors.EnumPropertyEditor;
import org.jdesktop.swingx.editors.EnumerationValuePropertyEditor;
import org.jdesktop.swingx.editors.Paint2PropertyEditor;
import org.netbeans.beaninfo.editors.InsetsEditor;

/**
 *
 * @author joshy
 */
public class RectanglePainterBeanInfo extends AbstractPainterBeanInfo {
    
    /** Creates a new instance of RectanglePainterBeanInfo */
    public RectanglePainterBeanInfo() {
        super(RectanglePainter.class);
    }
    
    protected void initialize() {
        super.initialize();
        setPropertyEditor(Paint2PropertyEditor.class, "paint", "borderPaint");
        setPropertyEditor(InsetsEditor.class,"insets");
        setPropertyEditor(StylePropertyEditor.class,"style");
        setHidden(true,"clip","fractionalMetrics","rendering",
                "interpolation","textAntialiasing",
                "dithering","colorRendering");
    }
    
    public static final class StylePropertyEditor extends EnumPropertyEditor {
        public StylePropertyEditor() {
            super(RectanglePainter.Style.class);
        }
    }
    
    
}
