/*
 * TestUtils.java
 *
 * Created on October 31, 2006, 9:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.test;

import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import junit.framework.Assert;

/**
 * Extends assert to get all the ease-of-use assert methods
 * @author rbair
 */
public final class TestUtils extends Assert {
    private TestUtils() {}
    
    public static void assertPropertyChangeNotification(
            Object bean, String propertyName, Object expected) throws Exception {
        
        //add the property change listener
        Method m = bean.getClass().getMethod("addPropertyChangeListener", PropertyChangeListener.class);
        PropertyChangeReport rpt = new PropertyChangeReport();
        m.invoke(bean, rpt);
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, bean.getClass());
        
        //the original bean value, before being set to 'expected'
        Object originalValue = pd.getReadMethod().invoke(bean);
        
        //set the bean value to 'expected'
        m = pd.getWriteMethod();
        m.invoke(bean, expected);
        
        //get the new bean value, after being set to 'expected'
        Object newValue = pd.getReadMethod().invoke(bean);
        
        if (newValue == originalValue || 
                (newValue != null && newValue.equals(originalValue))) {
            //assert that we don't get an event, because newValue was the same
            //as old value (should only get an event if they differ)
            assertEquals(0, rpt.getEventCount());
        } else {
            //assert bean's property is newValue
            assertEquals(expected, newValue);
            //assert that there is exactly one event
            assertEquals(1, rpt.getEventCount());
            //assert that the event's property name is correct
            assertEquals(propertyName, rpt.getLastEvent().getPropertyName());
            //assert that the original value is the old value of the event
            assertEquals(originalValue, rpt.getLastOldValue());
            //assert that the expected value is the new value of the event
            assertEquals(expected, rpt.getLastNewValue());
        }
    }
}
