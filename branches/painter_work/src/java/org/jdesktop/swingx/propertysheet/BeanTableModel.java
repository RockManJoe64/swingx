package org.jdesktop.swingx.propertysheet;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.jdesktop.swingx.JXPropertySheet;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.CompoundPainterBeanInfo;
import org.joshy.util.u;


public class BeanTableModel extends AbstractTableModel {
    public Object bean;
    private static final boolean debug = false;
    private BeanInfo info;
    public List<PropertyDescriptor> props = new ArrayList<PropertyDescriptor>();
    private JXPropertySheet sheet;
    
    private static void d(Object o) {
        if(debug) u.p(o);
    }
    
    public BeanTableModel(JXPropertySheet sheet, Object bean, Class stopClass) {
        super();
        this.sheet = sheet;
        this.bean = bean;
        try {
            d(Introspector.getBeanInfoSearchPath());
            d("looking for bean infos for class: " + bean.getClass());
            String[] paths = { "org.jdesktop.swingx.painter" };
            Introspector.setBeanInfoSearchPath(paths);
            if(stopClass != null) {
                info = Introspector.getBeanInfo(bean.getClass(), stopClass);
            } else {
                info = Introspector.getBeanInfo(bean.getClass());
                //info = Introspector.getBeanInfo(CompoundPainter.class);
            }
            d("bean info = " + info);
            //u.p("other infos");
            //u.p(info.getAdditionalBeanInfo());
            PropertyDescriptor[] props2;
            props2 = info.getPropertyDescriptors();
            for(PropertyDescriptor p : props2) {
                d("prop desc: " + p);
                d("ed : " + p.getPropertyEditorClass());
                if(p.isHidden() && !sheet.isHiddenShown()) continue;
                if(p.isExpert() && !sheet.isExpertShown()) continue;
                props.add(p);
            }
            
        }  catch (Exception ex) {
            u.p(ex);
        }
    }
    
    public int getColumnCount() {
        return 2;
    }
    
    public int getRowCount() {
        return props.size();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            return props.get(rowIndex).getName();
        }
        if (columnIndex == 1) {
            PropertyDescriptor prop = props.get(rowIndex);
            return prop;
        }
        return "blah";
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            return false;
        }
        PropertyDescriptor prop = props.get(rowIndex);
        //u.p("prop desc = " + prop);
        if(prop.getPropertyType() == null) return false;
        PropertyEditor ed = BeanUtils.getPE(prop,bean);//PropertyEditorManager.findEditor(prop.getPropertyType());
        //u.p("prop ed = " + prop.getName() + " " + ed);
        if(ed == null) return false;
        if(ed.isPaintable()) return true;
        return true;
    }
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(columnIndex != 1) return;
        PropertyDescriptor prop = props.get(rowIndex);
        PropertyEditor ed = BeanUtils.getPE(prop, bean);
        //PropertyEditor ed = PropertyEditorManager.findEditor(prop.getPropertyType());
        try {
            ed.setAsText((String)aValue);
        } catch (IllegalArgumentException arg) {
            u.p("not a valid value: " + aValue);
            return;
        }
        Method meth = prop.getWriteMethod();
        if(meth != null) {
            try {
                meth.invoke(bean, ed.getValue());
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

    

    
    
}