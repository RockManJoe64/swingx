/*
 * DataBoundUtils.java
 *
 * Created on May 9, 2005, 1:09 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx;
import java.awt.Component;
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
    
    public static BindingContext bind(Component target, String path) {
        BindingContext ctx = findBindingContext(target);
        if (ctx != null) {
            ctx.bind(target, path);
        }
        return ctx;
    }
    
    public static BindingContext bind(Component comp, Object target, String path) {
        BindingContext ctx = findBindingContext(comp);
        if (ctx != null) {
            ctx.bind(target, path);
        }
        return ctx;
    }
    
    public static void unbind(Component target, BindingContext ctx) {
        if (ctx != null) {
            ctx.unbind(target);
        }
    }
    
    public static void unbind(Object target, BindingContext ctx) {
        if (ctx != null) {
            ctx.unbind(target);
        }
    }
    
    public static boolean isValidPath(String path) {
        //TODO if it follows the path regular language
        return path != null && !path.trim().equals("");
    }
}
