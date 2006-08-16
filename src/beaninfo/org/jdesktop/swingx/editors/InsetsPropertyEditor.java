/*
 * InsetsPropertyEditor.java
 *
 * Created on July 20, 2006, 12:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.editors;

import java.awt.Insets;
import java.beans.PropertyEditorSupport;

/**
 *
 * @author joshy
 */
public class InsetsPropertyEditor extends PropertyEditorSupport {
    
    /** Creates a new instance of InsetsPropertyEditor */
    public InsetsPropertyEditor() {
    }
    
    public Insets getValue() {
        return (Insets)super.getValue();
    }
    
    public void setAsText(String text) {
        String originalParam = text;
        
        // replace formatting chars w/ spaces
        if (text != null) {
            //remove any opening or closing brackets
            text = text.replace('[', ' ');
            text = text.replace(']', ' ');
            text = text.replace(',', ' ');
            //trim whitespace
            text = text.trim();
        }
        
        //test for the simple case
        if (text == null || text.equals("") || text.equals("null")) {
            setValue(null);
            return;
        }
        
        //the first sequence of characters must now be a number. So, parse it out
        //ending at the first whitespace. Then trim and the remaining value must
        //be the second number. If there are any problems, throw and IllegalArgumentException
        try {
            int index = text.indexOf(' ');
            String x = text.substring(0, index).trim();
            text = text.substring(index).trim();
            index = text.indexOf(' ');
            String y = text.substring(0, index).trim();
            text = text.substring(index).trim();
            index = text.indexOf(' ');
            String w = text.substring(0, index).trim();
            String h = text.substring(index).trim();
            Insets val = new Insets(
                    Integer.parseInt(x),
                    Integer.parseInt(y),
                    Integer.parseInt(w),
                    Integer.parseInt(h)
                    );
            setValue(val);
        } catch (Exception e) {
            throw new IllegalArgumentException("The input value " + originalParam + " is not formatted correctly. Please " +
                    "try something of the form [top,left,bottom,right] or [top , left , bottom , right] or [top left bottom right]", e);
        }
    }
    
    public String getAsText() {
        Insets val = getValue();
        return val == null ? "[]" : "[" + val.top + ", " + val.left + ", " + 
                val.bottom + ", " + val.right + "]";
    }
    
}
