/*
 * DimensionPropertyEditor.java
 *
 * Created on August 16, 2006, 12:18 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.editors;

import java.awt.Dimension;
import java.beans.PropertyEditorSupport;

/**
 *
 * @author joshy
 */
public class DimensionPropertyEditor extends PropertyEditorSupport {
    
    public DimensionPropertyEditor() {
    }

    public Dimension getValue() {
        return (Dimension)super.getValue();
    }

    public String getJavaInitializationString() {
        Dimension point = getValue();
        return point == null ? "null" : "new java.awt.Dimension(" + point.width + ", " + point.height + ")";
    }

    public void setAsText(String text) throws IllegalArgumentException {
        String originalParam = text;

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
            String firstNumber = text.substring(0, index).trim();
            String secondNumber = text.substring(index).trim();
            Dimension val = new Dimension(
                    Integer.parseInt(firstNumber), 
                    Integer.parseInt(secondNumber));
            setValue(val);
        } catch (Exception e) {
            throw new IllegalArgumentException("The input value " + originalParam + " is not formatted correctly. Please " +
                    "try something of the form [w,h] or [w , h] or [w h]", e);
        }
    }

    public String getAsText() {
        Dimension dim = getValue();
        return dim == null ? "[]" : "[" + dim.width + ", " + dim.height + "]";
    }

} 

