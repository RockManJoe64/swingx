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

package org.jdesktop.swingx;
import java.awt.Component;
import org.jdesktop.binding.Binding;
import org.jdesktop.binding.BindingContext;

/**
 * @author rbair
 */
public class DataBoundUtils {
    
    /** Creates a new instance of DataBoundUtils */
    private DataBoundUtils() {
    }
    
    /**
     * Walks up the containment hierarchy looking for the first BindingContext.
     * Returns null if one cannot be found
     */
    public static BindingContext findBindingContext(Component component) {
       BindingContext ctx = null;
       while (ctx == null && component != null) {
           if (component instanceof BindingContext) {
               ctx = (BindingContext)component;
           } else {
               component = component.getParent();
           }
       }
       return ctx;
    }
    
    public static Binding bind(BindingContext ctx, Object c, String path) {
        if (ctx != null && path != null && !path.trim().equals("")) {
            return ctx.bind(c, path);
        } else {
            return null;
        }
    }
    
    public static void unbind(BindingContext ctx, Object c) {
        if (ctx != null) {
            ctx.unbind(c);
        }
    }
    
    public static boolean isValidPath(String path) {
        //TODO if it follows the path regular language
        return path != null && !path.trim().equals("");
    }
}
