/*
 * ReflectionBinding.java
 *
 * Created on September 6, 2005, 12:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import org.jdesktop.binding.DataModel;
import org.jdesktop.binding.impl.ColumnBinding;

/**
 * General binding that uses reflection to interface a DataModel field to a
 * property on a bean.
 *
 * @author rbair
 */
public class ReflectionBinding extends ColumnBinding {
    private String propertyName;
    private Method getterMethod;
    private Method setterMethod;
    
    /** Creates a new instance of ReflectionBinding */
    public ReflectionBinding(Object target, String propertyName) {
        super(target, getPropertyType(target, propertyName));
        this.propertyName = propertyName;
        
        //get the getter/setter methods
        try {
            BeanInfo info = Introspector.getBeanInfo(target.getClass());
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if (pd.getName().equals(propertyName)) {
                    getterMethod = pd.getReadMethod();
                    setterMethod = pd.getWriteMethod();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ReflectionBinding(Object target, String propertyName, DataModel model, String fieldName) {
        this(target, propertyName);
        setColumnName(fieldName);
        setDataModel(model);
    }
    
    private static Class getPropertyType(Object target, String propertyName) {
        try {
            BeanInfo info = Introspector.getBeanInfo(target.getClass());
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if (pd.getName().equals(propertyName)) {
                    return pd.getPropertyType();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Object.class;
    }

    protected Object getComponentValue() {
        try {
            return getterMethod.invoke(getComponent());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void setComponentValue(Object obj) {
        try {
            setterMethod.invoke(getComponent(), obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doInitialize() {
    }

    public void doRelease() {
    }

    protected void setComponentEditable(boolean editable) {
        //no-op
    }

    protected void setComponentEnabled(boolean enabled) {
        //no-op
    }
}
