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
import org.jdesktop.swingx.editors.InsetsPropertyEditor;
import org.jdesktop.swingx.editors.Point2DPropertyEditor;
import org.netbeans.beaninfo.editors.InsetsEditor;
import org.netbeans.beaninfo.editors.URLEditor;

/**
 *
 * @author joshy
 */
public class ImagePainterBeanInfo extends PositionedPainterBeanInfo {
    
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

    
}
