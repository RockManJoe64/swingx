/*
 * BeanUtils.java
 *
 * Created on August 12, 2006, 3:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.propertysheet;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.swing.plaf.InsetsUIResource;
import org.jdesktop.swingx.editors.DimensionPropertyEditor;
import org.jdesktop.swingx.editors.InsetsPropertyEditor;
import org.jdesktop.swingx.editors.Point2DPropertyEditor;
import org.jdesktop.swingx.editors.PointPropertyEditor;
import org.jdesktop.swingx.editors.Rectangle2DPropertyEditor;
import org.jdesktop.swingx.editors.RectanglePropertyEditor;

/**
 *
 * @author joshy
 */
public class BeanUtils {
    // a cache of editors so we don't have to recreate them, when they might be
    // expensive to create
    private static Map<Class,PropertyEditor> editorCache = new HashMap<Class,PropertyEditor>();
    
    static {
        //String[] paths = { "org.jdesktop.swingx.editors" };
        PropertyEditorManager.registerEditor(Point.class, PointPropertyEditor.class);
        PropertyEditorManager.registerEditor(Point2D.class, Point2DPropertyEditor.class);
        PropertyEditorManager.registerEditor(Rectangle2D.class, Rectangle2DPropertyEditor.class);
        PropertyEditorManager.registerEditor(Rectangle.class, RectanglePropertyEditor.class);
        PropertyEditorManager.registerEditor(Insets.class, InsetsPropertyEditor.class);
        PropertyEditorManager.registerEditor(Dimension.class, DimensionPropertyEditor.class);
        //PropertyEditorManager.setEditorSearchPath(paths);        
    }
    
    public static PropertyEditor getPE(PropertyDescriptor pd, Object bean) {
        PropertyEditor ed = null;
        //u.p("getting an editor for: " + pd.getName());
        Class clzz = pd.getPropertyEditorClass();
        //u.p("clazz = " + clzz);
        if(clzz != null) {
            if(editorCache.containsKey(clzz)) {
                ed = editorCache.get(clzz);
            } else {
                ed = pd.createPropertyEditor(bean);
                editorCache.put(clzz, ed);
            }
        } else {
            if(pd.getPropertyType() != null) {
                ed = PropertyEditorManager.findEditor(pd.getPropertyType());
                //u.p("found an editor: " + ed);
            }
        }
        return ed;
    }
    
    
    public static Object getPropertyValue(PropertyDescriptor pd, Object bean) {
        PropertyEditor ed = getPE(pd,bean);
        Method meth = pd.getReadMethod();
        if(meth == null) {
            return null;
        }
        try {
            Object obj = meth.invoke(bean);
            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static void setPropertyFromText(PropertyDescriptor pd, Object bean, String text) {
        PropertyEditor ed = getPE(pd,bean);
        Method meth = pd.getWriteMethod();
        if(meth == null) {
            p("couldn't write to this bean!!!!");
            return;
        }
        try {
            //u.p("converting text: " + text);
            ed.setAsText(text);
            Object value = ed.getValue();
            //u.p("got value: " + value);
            meth.invoke(bean,value);
        } catch (Exception ex) {
            p("error writing back to the object");
        }
        
    }
    
    public static String calculateText(PropertyDescriptor pd, Object bean) {
        PropertyEditor ed = getPE(pd,bean);
        //u.p("ed = " + ed);
        
        Method meth = pd.getReadMethod();
        if(meth == null) {
            return "???";
        }
        
        
        try {
            Object obj = meth.invoke(bean);
            //u.p("got out the object: " + obj);
            if(ed != null) {
                ed.setValue(obj);
                //u.p("got out the text: " + ed.getAsText());
                return ed.getAsText();
            } else {
                return ""+obj;
            }
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            //ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            //ex.printStackTrace();
        }
        
        return "???--";
    }
    
    private static void p(String str) {
        System.out.println(str);
    }
    private static void p(Throwable thr) {
        System.out.println(thr.getMessage());
        thr.printStackTrace();
    }
}
