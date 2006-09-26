/*
 * TextAAEditor.java
 *
 * Created on July 12, 2006, 5:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painterset;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import java.awt.RenderingHints;

/**
 *
 * @author jm158417
 */
public class TextAAEditor extends ComboBoxPropertyEditor {
    
    /** Creates a new instance of TextAAEditor */
    public TextAAEditor() {
        super();
        setAvailableValues(new Object[]  {
            RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, 
            RenderingHints.VALUE_TEXT_ANTIALIAS_OFF,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON});
    }
}
