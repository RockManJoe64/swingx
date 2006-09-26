package org.jdesktop.swingx.painterset;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import java.awt.RenderingHints;


public class AAEditor extends ComboBoxPropertyEditor {
    public AAEditor() {
        super();
        setAvailableValues(new Object[]  {
            RenderingHints.VALUE_ANTIALIAS_DEFAULT, 
            RenderingHints.VALUE_ANTIALIAS_OFF, 
            RenderingHints.VALUE_ANTIALIAS_ON});
    }
}