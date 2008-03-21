/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jdesktop.swingx.editors;

import java.awt.geom.Point2D;
import java.beans.PropertyEditorSupport;

/**
 *
 * @author rbair
 */
public class Point2DPropertyEditor extends PropertyEditorSupport {

    /** Creates a new instance of Point2DPropertyEditor */
    public Point2DPropertyEditor() {
    }

    public Point2D getValue() {
        return (Point2D.Double)super.getValue();
    }

    public String getJavaInitializationString() {
        Point2D point = getValue();
        return point == null ? "null" : "new java.awt.geom.Point2D.Double(" + point.getX() + ", " + point.getY() + ")";
    }

    public void setAsText(String text) throws IllegalArgumentException {
        //the text could be in many different formats. All of the supported formats are as follows:
        //(where x and y are doubles of some form)
        //[x,y]
        //[x y]
        //x,y]
        //[x,y
        //[ x , y ] or any other arbitrary whitespace
        // x , y ] or any other arbitrary whitespace
        //[ x , y  or any other arbitrary whitespace
        //x,y
        // x , y (or any other arbitrary whitespace)
        //x y
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
            String firstNumber = text.substring(0, index).trim();
            String secondNumber = text.substring(index).trim();
            Point2D.Double val = new Point2D.Double(Double.parseDouble(firstNumber), Double.parseDouble(secondNumber));
            setValue(val);
        } catch (Exception e) {
            throw new IllegalArgumentException("The input value " + originalParam + " is not formatted correctly. Please " +
                    "try something of the form [x,y] or [x , y] or [x y]", e);
        }
    }

    public String getAsText() {
        Point2D point = getValue();
        return point == null ? "[]" : "[" + point.getX() + ", " + point.getY() + "]";
    }

    public static void main(String... args) {
        test("[1.5,1.2]");
        test("1.5,1.2]");
        test("[1.5,1.2");
        test("[ 1.5 , 1.2 ]");
        test(" 1.5 , 1.2 ]");
        test("[ 1.5 , 1.2");
        test("1.5,1.2");
        test(" 1.5 , 1.2 ");
        test("1.5 1.2");
        test("");
        test("null");
        test("[]");
        test("[ ]");
        test("[1.5 1.2]");
    }

    private static void test(String input) {
        System.out.print("Input '" + input + "'");
        try {
            Point2DPropertyEditor ed = new Point2DPropertyEditor();
            ed.setAsText(input);
            Point2D point = ed.getValue();
            System.out.println(" succeeded: " + point);
        } catch (Exception e) {
            System.out.println(" failed: " + e.getMessage());
        }
    }
} 