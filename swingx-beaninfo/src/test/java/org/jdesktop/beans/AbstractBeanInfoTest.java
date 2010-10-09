package org.jdesktop.beans;

import static org.hamcrest.CoreMatchers.is;
import static org.jdesktop.test.matchers.Matchers.property;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.awt.Insets;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public abstract class AbstractBeanInfoTest<T> {
    protected T instance;
    private BeanInfo beanInfo;
    private Map<Class<?>, Object> listeners;
    
    @Before
    public void setUp() throws Exception {
        instance = createInstance();
        beanInfo = Introspector.getBeanInfo(instance.getClass());
        listeners = new HashMap<Class<?>, Object>();
        
        for (EventSetDescriptor descriptor : beanInfo.getEventSetDescriptors()) {
            Class<?> eventClass = descriptor.getListenerType();
            Object listener = mock(eventClass);
            
            descriptor.getAddListenerMethod().invoke(instance, listener);
            listeners.put(eventClass, listener);
        }
    }
    
    protected abstract T createInstance();
    
    @Test
    public final void testBoundProperties() throws Exception {
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            if (descriptor.isBound()) {
                if (descriptor.isHidden()) {
                    continue;
                }
                
                if (descriptor.getWriteMethod() == null) {
                    fail("bound read-only property: " + descriptor.getName());
                }
                
                Class<?> propertyType = descriptor.getPropertyType();
                
                if (isUnhandledType(propertyType)) {
                    //TODO log?
                    continue;
                }
                
                Object defaultValue = descriptor.getReadMethod().invoke(instance);
                Object newValue = getNewValue(propertyType, defaultValue);
                
                descriptor.getWriteMethod().invoke(instance, newValue);
                
                PropertyChangeListener pcl = (PropertyChangeListener) listeners.get(PropertyChangeListener.class);
                verify(pcl).propertyChange(argThat(is(property(descriptor.getName(), defaultValue, newValue))));
                reset(pcl);
            }
        }
    }
    
    private boolean isUnhandledType(Class<?> type) {
        return type == null;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Object getNewValue(Class<?> propertyType, Object defaultValue) {
        Object result = null;
        
        if (propertyType.isArray()) {
            int length = defaultValue == null ? 1 : ((Object[]) defaultValue).length + 1;
            result = Array.newInstance(propertyType.getComponentType(), length);
        } else if (propertyType.isEnum()) {
            EnumSet set = EnumSet.allOf((Class<? extends Enum>) propertyType);
            int size = set.size();
            
            if (size == 1) {
                result = defaultValue == null ? set.iterator().next() : null;
            } else {
                int ordinal = ((Enum) defaultValue).ordinal();
                ordinal = ordinal == size - 1 ? 0 : ordinal + 1;
                Iterator iter = set.iterator();
                
                for (int i = 0; i < ordinal + 1; i++) {
                    result = iter.next();
                }
            }
        } else if (propertyType == boolean.class) {
            result = Boolean.FALSE.equals(defaultValue);
        } else if (propertyType == float.class) {
            result = ((Float) defaultValue) + 1f;
        } else if (propertyType == String.class) {
            result = "original string: " + defaultValue;
        } else if (propertyType == Insets.class) {
            if (new Insets(0, 0, 0, 0).equals(defaultValue)) {
                result = new Insets(1, 1, 1, 1);
            } else {
                result = mock(propertyType);
            }
        } else {
            result = mock(propertyType);
        }
        
        return result;
    }
    
    @After
    public void tearDown() {
        for (Object listener : listeners.values()) {
            verifyNoMoreInteractions(listener);
        }
    }
}
