/*
 * NeonBorderEffectBeanInfo.java
 *
 * Created on October 30, 2006, 1:14 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter.effects;

import org.jdesktop.swingx.editors.Paint2PropertyEditor;
import org.jdesktop.swingx.painter.AbstractPainterBeanInfo;

/**
 *
 * @author joshy
 */
public class NeonBorderEffectBeanInfo extends AbstractPainterBeanInfo {
    
    /** Creates a new instance of NeonBorderEffectBeanInfo */
    public NeonBorderEffectBeanInfo() {
        super(NeonBorderEffect.class);
    }
    
    protected void initialize() {
        super.initialize();
        setPropertyEditor(Paint2PropertyEditor.class, "edgeColor", "centerColor", "brushColor");
        //setPropertyEditor(InsetsPropertyEditor.class,"insets");
        //setPropertyEditor(StylePropertyEditor.class,"style");
    }
    
    /*
    public static final class StylePropertyEditor extends EnumPropertyEditor {
        public StylePropertyEditor() {
            super(RectanglePainter.Style.class);
        }
    }
     */

}
