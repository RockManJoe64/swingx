package org.jdesktop.swingx.propertysheet;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import org.jdesktop.swingx.JXPropertySheet;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.CompoundPainterBeanInfo;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.joshy.util.u;


public class BeanTableModel extends AbstractTreeTableModel {
    public Object bean;
    private static final boolean debug = false;
    private BeanInfo info;
    public List<PropertyDescriptor> props = new ArrayList<PropertyDescriptor>();
    public List<Category> categories = new ArrayList();
    public Map<String,Category> categoryMap = new HashMap<String,Category>();
    private JXPropertySheet sheet;
    
    private static void d(Object o) {
        if(debug) u.p(o);
    }
    
    
    public BeanTableModel(JXPropertySheet sheet, Object bean, Class stopClass) {
        super(bean);
        this.sheet = sheet;
        this.bean = bean;
        
        Category cat = new Category();
        cat.name = "Other Properties";
        cat.props = new ArrayList<PropertyDescriptor>();
        categoryMap.put(cat.name,cat);
        this.categories.add(cat);
        
        
        try {
            d(Introspector.getBeanInfoSearchPath());
            d("looking for bean infos for class: " + bean.getClass());
            String[] paths = { "org.jdesktop.swingx.painter" };
            Introspector.setBeanInfoSearchPath(paths);
            if(stopClass != null) {
                info = Introspector.getBeanInfo(bean.getClass(), stopClass);
            } else {
                info = Introspector.getBeanInfo(bean.getClass());
            }
            d("bean info = " + info);
            PropertyDescriptor[] props2;
            props2 = info.getPropertyDescriptors();
            for(PropertyDescriptor p : props2) {
                d("prop desc: " + p);
                d("ed : " + p.getPropertyEditorClass());
                if(sheet.isExpertOnly() && !p.isExpert()) {
                    continue;
                }
                if(p.isHidden() && !sheet.isHiddenShown()) continue;
                if(p.isExpert() && !sheet.isExpertShown()) continue;
                
                // find the current category
                String catname = null;
                Enumeration en = p.attributeNames();
                while(en.hasMoreElements()) {
                    String att = (String) en.nextElement();
                    if("category".equals(att)) {
                        catname = (String) p.getValue(att);
                    }
                }
                
                // without a category
                if(catname == null) {
                    props.add(p);
                    cat.props.add(p);
                } else { // with a category
                    Category catg = null;
                    // get category if already there
                    if(categoryMap.containsKey(catname)) {
                        catg = categoryMap.get(catname);
                    } else { // else create a new category
                        catg = new Category();
                        catg.name = catname;
                        catg.props = new ArrayList<PropertyDescriptor>();
                        categoryMap.put(catg.name, catg);
                        this.categories.add(catg);
                    }
                    props.add(p);
                    catg.props.add(p);
                }
            }
            
        }  catch (Exception ex) {
            u.p(ex);
        }
    }
    
    
    /* == tree parts of the model == */
    public Object getChild(Object parent, int index) {
        if(parent == bean) {
            return categories.get(index);
        }
        if(parent instanceof Category) {
            return ((Category)parent).props.get(index);
            //return props.get(index);
        }
        return "?????";
    }
    
    public int getChildCount(Object parent) {
        if(parent == bean) {
            return categories.size();
        }
        if(parent instanceof Category) {
            return ((Category)parent).props.size();
        }
        return 0;
    }
    
    public boolean isLeaf(Object node) {
        if(node == bean) {
            return false;
        }
        if(node instanceof Category) {
            return false;
        }
        return true;
    }
    
    
    
    
    /* == table parts of the model == */
    // only the 2nd column of prop descriptors are editable
    public boolean isCellEditable(Object node, int column) {
        if(node instanceof PropertyDescriptor && column != 0) {
            return true;
        }
        return false;
    }
    
    // we only have the name and value columns right now
    public String getColumnName(int column) {
        if(column == 0) return "Name";
        if(column == 1) return "Value";
        return "???";
    }
    
    public int getColumnCount() {
        return 2;
    }
    
    // return a dummy value for the bean. for props go to the other getValueAt method
    public Object getValueAt(Object node, int column) {
        if(node instanceof PropertyDescriptor) {
            return getValueAt(props.indexOf(node),column);
        }
        if(node instanceof Category) {
            return node;
            //return "a category name";
        }
        if(node == bean) {
            return "All Properties";
        }
        return "???";
    }
    
    /* == the stuff i haven't gone through yet == */
    
    public int getRowCount() {
        int total = 0;
        for(Category c : categories) {
            total += c.props.size();
        }
        return total;
    }
    
    
    public Object getValueAt(int propIndex, int columnIndex) {
        if(columnIndex == 0) {
            return "---";
            //return props.get(rowIndex).getName();
        }
        if (columnIndex == 1) {
            PropertyDescriptor prop = props.get(propIndex);
            return prop;//.getName();
            //return "===";
        }
        return "blah";
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            return false;
        }
        PropertyDescriptor prop = props.get(rowIndex);
        if(prop.getPropertyType() == null) return false;
        PropertyEditor ed = BeanUtils.getPE(prop,bean);
        if(ed == null) return false;
        if(ed.isPaintable()) return true;
        return true;
    }
    
    public void setValueAt(Object newValue, Object node, int column) {
        // you can't set the root bean or categories
        if(newValue == bean) return;
        if(newValue instanceof Category) return;
        
        setValueAt(newValue, props.indexOf(node), column);
    }
    
    public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
        if(columnIndex != 1) return;
        
        PropertyDescriptor prop = props.get(rowIndex);
        PropertyEditor ed = BeanUtils.getPE(prop, bean);
        try {
            ed.setAsText((String)newValue);
        } catch (IllegalArgumentException arg) {
            u.p("not a valid value: " + newValue);
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