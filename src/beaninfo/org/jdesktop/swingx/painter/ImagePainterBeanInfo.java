/*
 * ImagePainterBeanInfo.java
 *
 * Created on July 20, 2006, 1:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import org.jdesktop.swingx.EnumerationValue;
import org.jdesktop.swingx.editors.EnumerationValuePropertyEditor;
import org.jdesktop.swingx.editors.ImageEditor;
import org.jdesktop.swingx.editors.Point2DPropertyEditor;
import org.netbeans.beaninfo.editors.URLEditor;

/**
 *
 * @author joshy
 */
public class ImagePainterBeanInfo extends AbstractPainterBeanInfo {
    
    /** Creates a new instance of ImagePainterBeanInfo */
    public ImagePainterBeanInfo() {
        super(ImagePainter.class);
    }
    
    protected void initialize() {
        super.initialize();
        //setPropertyEditor(ImageEditor.class,"image");
        //setPropertyEditor(URLEditor.class,"imageURL");
        //setPropertyEditor(StylePropertyEditor.class,"style");
        //setPropertyEditor(Point2DPropertyEditor.class,"imagePosition");
    }
    
    public static final class StylePropertyEditor extends EnumerationValuePropertyEditor {
        public StylePropertyEditor() {
            super(null, new EnumerationValue[] {
                new EnumerationValue("Centered", ImagePainter.Style.CENTERED, "Style.CENTERED"),
                new EnumerationValue("CSS Positioned", ImagePainter.Style.CSS_POSITIONED, "Style.CSS_POSITIONED"),
                new EnumerationValue("Positioned", ImagePainter.Style.POSITIONED, "Style.POSITIONED"),
                new EnumerationValue("Scaled", ImagePainter.Style.SCALED, "Style.SCALED"),
                new EnumerationValue("Tiled", ImagePainter.Style.TILED, "Style.TILED"),
            });
        }
    }
    
}
