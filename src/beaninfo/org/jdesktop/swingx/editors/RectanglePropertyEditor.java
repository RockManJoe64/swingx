/*
 * RectanglePropertyEditor.java
 *
 * Created on August 16, 2006, 12:13 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.editors;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyEditorSupport;

/**
 *
 * @author joshy
 */
public class RectanglePropertyEditor extends PropertyEditorSupport {

    /** Creates a new instance of Rectangle2DPropertyEditor */
    public RectanglePropertyEditor() {
    }

    public Rectangle getValue() {
        return (Rectangle)super.getValue();
    }

    public String getJavaInitializationString() {
        Rectangle rect = getValue();
        return rect == null ? "null" : "new java.awt.Rectangle(" + rect.getX() + ", " + rect.getY() + ", " + rect.getWidth() + ", " + rect.getHeight() + ")";
    }

    public void setAsText(String text) throws IllegalArgumentException {
        //the text could be in many different formats. All of the supported formats are as follows:
        //(where x and y are doubles of some form)
        //[x,y,w,h]
        //[x y w h]
        //x,y,w,h]
        //[x,y w,h
        //[ x , y w,h] or any other arbitrary whitespace
        // x , y w h] or any other arbitrary whitespace
        //[ x , y w, h or any other arbitrary whitespace
        //x,y w, h
        // x , y w,h (or any other arbitrary whitespace)
        //x y w h
        //or any other such permutation
        // (empty space)
        //null
        //[]
        //[ ]
        //any other value throws an IllegalArgumentException

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
            String x = text.substring(0, index).trim();
            text = text.substring(index).trim();
            index = text.indexOf(' ');
            String y = text.substring(0, index).trim();
            text = text.substring(index).trim();
            index = text.indexOf(' ');
            String w = text.substring(0, index).trim();
            String h = text.substring(index).trim();
            Rectangle val = new Rectangle(
                    Integer.parseInt(x),
                    Integer.parseInt(y),
                    Integer.parseInt(w),
                    Integer.parseInt(h)
                    );
            setValue(val);
        } catch (Exception e) {
            throw new IllegalArgumentException("The input value " + originalParam + " is not formatted correctly. Please " +
                    "try something of the form [x,y,w,h] or [x , y , w , h] or [x y w h]", e);
        }
    }

    public String getAsText() {
        Rectangle rect = getValue();
        return rect == null ? "[]" : "[" + rect.x + ", " + rect.y + ", " + rect.width + ", " + rect.height + "]";
    }

    public static void main(String... args) {
        test("[1.5,1.2,10,35]");
        test("1.5,1.2,10,35]");
        test("[1.5,1.2,10,35");
        test("[ 1.5 , 1.2 ,10,35]");
        test(" 1.5 , 1.2 ,10,35]");
        test("[ 1.5 , 1.2,10,35");
        test("1.5,1.2,10,35");
        test(" 1.5 , 1.2 10 35");
        test("1.5 1.2, 10 35");
        test("");
        test("null");
        test("[]");
        test("[ ]");
        test("[1.5 1.2 10 35]");
    }

    private static void test(String input) {
        System.out.print("Input '" + input + "'");
        try {
            RectanglePropertyEditor ed = new RectanglePropertyEditor();
            ed.setAsText(input);
            Rectangle rect = ed.getValue();
            System.out.println(" succeeded: " + rect);
        } catch (Exception e) {
            System.out.println(" failed: " + e.getMessage());
        }
    }
} 
