/*
 * BeanUtils.java
 *
 * Created on August 12, 2006, 3:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.propertysheet;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author joshy
 */
public class BeanUtils {
    // a cache of editors so we don't have to recreate them, when they might be
    // expensive to create
    private static Map<Class,PropertyEditor> editorCache = new HashMap<Class,PropertyEditor>();
    
    public static PropertyEditor getPE(PropertyDescriptor pd, Object bean) {
        PropertyEditor ed = null;
        
        Class clzz = pd.getPropertyEditorClass();
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
    
    
    public static String calculateText(PropertyDescriptor pd, Object bean) {
        PropertyEditor ed = getPE(pd,bean);
        //u.p("ed = " + ed);
        
        Method meth = pd.getReadMethod();
        if(meth == null) {
            return "???";
        }
        
        
        try {
            Object obj = meth.invoke(bean);
            if(ed != null) {
                ed.setValue(obj);
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
    
}
