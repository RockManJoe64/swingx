/*
 * BindingContextSupport.java
 *
 * Created on May 9, 2005, 2:01 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.binding;

import java.awt.Container;
import java.awt.Component;
import org.jdesktop.binding.impl.AbstractBindingContext;
import java.util.Map;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.jdesktop.binding.impl.BindingFactory;
import org.jdesktop.binding.impl.BindingFactoryImpl;

/**
 *
 * @author rbair
 */
public class BindingContextSupport extends AbstractBindingContext {
    private static Map<Container, AbstractBindingContext> CONTEXTS = new HashMap<Container, AbstractBindingContext>();
    
    static {
        //initalize Swing bindings for the factory
        BindingFactoryImpl factory = (BindingFactoryImpl)BindingFactory.getInstance();
        factory.addMapping(JLabel.class, JLabelBinding.class);
        factory.addMapping(JList.class, JListBinding.class);
        factory.addMapping(JTable.class, JTableBinding.class);
        factory.addMapping(JTextField.class, JTextFieldBinding.class);
    }
    
    private Container container;
    private AbstractBindingContext parentContext;
    private AbstractBindingContext[] childrenContexts;
    
    /**
     * Creates a new instance of BindingContextSupport 
     */
    public BindingContextSupport(Container c) {
        this.container = c;

        CONTEXTS.put(c, this);
        
        //construct the parent AbstractBindingContext and the
        //children binding context. Note that this is a first and
        //very rash attempt
        Container parent = c.getParent();
        Component[] children = c.getComponents();
        
        parentContext = CONTEXTS.get(parent);
        if (parentContext == null && parent != null) {
            parentContext = new BindingContextSupport(parent);
            CONTEXTS.put(parent, parentContext);
        }

        int count = 0;
        for (Component child : children) {
            if (child instanceof Container) {
                count++;
            }
        }

        childrenContexts = new AbstractBindingContext[count];
        count = 0;
        for (Component child : children) {
            if (child instanceof Container) {
                childrenContexts[count] = new BindingContextSupport((Container)child);
                CONTEXTS.put((Container)child, childrenContexts[count]);
                count++;
            }
        }
    }

    public AbstractBindingContext getParent() {
        return parentContext;
    }

    public AbstractBindingContext[] getChildren() {
        return childrenContexts;
    }
}