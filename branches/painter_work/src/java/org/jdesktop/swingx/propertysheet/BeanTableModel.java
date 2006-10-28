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
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.CompoundPainterBeanInfo;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;


public class BeanTableModel extends AbstractTreeTableModel {
    public Object bean;
    private static final boolean debug = false;
    private BeanInfo info;
    public List<PropertyDescriptor> props = new ArrayList<PropertyDescriptor>();
    public List<Category> categories = new ArrayList();
    public Map<String,Category> categoryMap = new HashMap<String,Category>();
    
    private static void d(Object o) {
        if(debug) System.out.println(""+o);
    }
    
    
    public BeanTableModel(Object bean, Class stopClass,
            boolean included, List categoryFilter, 
            boolean isExpertOnly, boolean isExpertShown, boolean isHiddenShown) {
        super(bean);
        this.bean = bean;
        
        Category catchall = new Category();
        catchall.name = "Other Properties";
        catchall.props = new ArrayList<PropertyDescriptor>();
        
        if(bean == null) {
            return;
        }
        try {
            
            // get the bean info
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
            
            // loop through all of the property descriptors
            PropertyDescriptor[] props2 = info.getPropertyDescriptors();
            for(PropertyDescriptor p : props2) {
                d("prop desc: " + p);
                d("ed : " + p.getPropertyEditorClass());
                if(isExpertOnly && !p.isExpert()) {
                    continue;
                }
                if(p.isHidden() && !isHiddenShown) continue;
                if(p.isExpert() && !isExpertShown) continue;
                
                
                String catname = findCategory(p);
                d("category = " + catname);
                if(included) {
                    // without a category
                    if(catname == null) {
                        //props.add(p);
                        //catchall.props.add(p);
                    } else { // with a category
                        //only add property if it's category is on the list'
                        if(categoryFilter.contains(catname)) {
                            Category catg = getCategory(catname);
                            props.add(p);
                            catg.props.add(p);
                        }
                    }
                } else {
                    // without a category
                    if(catname == null) {
                        props.add(p);
                        catchall.props.add(p);
                    } else { // with a category
                        //only add property if it's category is on the list'
                        if(!categoryFilter.contains(catname)) {
                            Category catg = getCategory(catname);
                            props.add(p);
                            catg.props.add(p);
                        }
                    }
                }
            }
        }  catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        
        // only use the catchall category if there is at least one property in it.
        if(catchall.props.size() > 0) {
            categoryMap.put(catchall.name,catchall);
            this.categories.add(catchall);
        }
        d("catchall = " + catchall.props.size());
    }
    
    private Category getCategory(final String catname) {
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
        return catg;
    }
    
    private String findCategory(final PropertyDescriptor p) {
        
        // find the current category
        String catname = null;
        Enumeration en = p.attributeNames();
        while(en.hasMoreElements()) {
            String att = (String) en.nextElement();
            if("category".equals(att)) {
                catname = (String) p.getValue(att);
            }
        }
        return catname;
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
            PropertyDescriptor prop = (PropertyDescriptor)node;
            if(prop.getPropertyType() == null) return false;
            if(prop.getWriteMethod() == null) return false;
            PropertyEditor ed = BeanUtils.getPE(prop,bean);
            if(ed == null) return false;
            if(ed.isPaintable()) return true;
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
            return prop;
        }
        return "blah";
    }
    /*
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        u.p("is cell editiable");
        if(columnIndex == 0) {
            return false;
        }
        PropertyDescriptor prop = props.get(rowIndex);
        if(prop.getPropertyType() == null) return false;
        PropertyEditor ed = BeanUtils.getPE(prop,bean);
        if(ed == null) return false;
        if(ed.isPaintable()) return true;
        return true;
    }*/
    
    public void setValueAt(Object newValue, Object node, int column) {
        // you can't set the root bean or categories
        if(newValue == bean) return;
        if(newValue instanceof Category) return;
        
        // don't call this anymore because the editors will set the value on
        // the bean directly, so we don't need to set it here any more.
        //setValueAt(newValue, props.indexOf(node), column);
    }
    
    public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
        if(columnIndex != 1) return;
        PropertyDescriptor prop = props.get(rowIndex);
        PropertyEditor ed = BeanUtils.getPE(prop, bean);
        try {
            ed.setAsText((String)newValue);
        } catch (IllegalArgumentException arg) {
            System.out.println("not a valid value: " + newValue);
            return;
        }
        Method meth = prop.getWriteMethod();
        if(meth != null) {
            try {
                System.out.println("invoking: " + ed.getValue() + " on " + meth.getName());
                meth.invoke(bean, ed.getValue());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
}