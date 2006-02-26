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
import java.util.ArrayList;
import org.jdesktop.binding.impl.AbstractBindingContext;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;
import org.jdesktop.binding.BindingContext;
import org.jdesktop.binding.impl.BindingFactory;
import org.jdesktop.binding.impl.BindingFactoryImpl;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXRadioGroup;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;

/**
 *
 * @author rbair
 */
public class BindingContextSupport extends AbstractBindingContext {
    private static Map<Container, BindingContext> CONTEXTS = new HashMap<Container, BindingContext>();
    
    static {
        //initalize Swing bindings for the factory
        BindingFactoryImpl factory = (BindingFactoryImpl)BindingFactory.getInstance();
        factory.addMapping(AbstractButton.class, AbstractButtonBinding.class);
        factory.addMapping(JComboBox.class, JComboBoxBinding.class);
        factory.addMapping(DefaultComboBoxModel.class, JComboBoxListBinding.class);
        factory.addMapping(JXComboBox.JXComboBoxList.class, JXComboBoxListBinding.class);
        factory.addMapping(JDialog.class, JDialogBinding.class);
        factory.addMapping(JFrame.class, JFrameBinding.class);
        factory.addMapping(JLabel.class, JLabelBinding.class);
        factory.addMapping(JList.class, JListBinding.class);
        factory.addMapping(JProgressBar.class, JProgressBarBinding.class);
        factory.addMapping(JSlider.class, JSliderBinding.class);
        factory.addMapping(JSpinner.class, JSpinnerBinding.class);
        factory.addMapping(JTable.class, JTableBinding.class);
        factory.addMapping(JTextComponent.class, JTextComponentBinding.class);
        factory.addMapping(JTree.class, JTreeBinding.class);
        factory.addMapping(JXDatePicker.class, JXDatePickerBinding.class);
        factory.addMapping(JXImagePanel.class, JXImagePanelBinding.class);
        factory.addMapping(JXRadioGroup.class, JXRadioGroupBinding.class);
        factory.addMapping(JXTable.class, JXTableBinding.class);
        factory.addMapping(JXTitledPanel.class, JXTitledPanelBinding.class);
        factory.addMapping(JXTree.class, JXTreeBinding.class);
        factory.addMapping(JXTreeTable.class, JXTreeTableBinding.class);
        factory.addMapping(JXList.class, JXListBinding.class);
    }
    
    private Container container;
    private List<BindingContext> childrenContexts;
    private AbstractBindingContext parentContext;
    
    public Container getContainer() {
        return container;
    }
    
    public BindingContextSupport() {
    }
    
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
    }

    public AbstractBindingContext getParentContext() {
        if (container == null) {
            return null;
        }
        //walk up the containment hierarchy looking for an AbstractBindingContext.
        Container parent = container.getParent();
        while (parent != null && parentContext == null) {
            if (CONTEXTS.get(parent) != null) {
                parentContext = (AbstractBindingContext)CONTEXTS.get(parent);
            } else {
                parent = parent.getParent();
            }
        }
        return parentContext;
    }

    public List<BindingContext> getChildrenContexts() {
        //walk down the containment hierarchy looking for all children
        //but only the FIRST LEVEL of all children. That is, after finding
        //a BindingContext child, stop recursing on that child
        childrenContexts = new ArrayList<BindingContext>();
        getChildrenContexts(childrenContexts, container);
        return childrenContexts;
    }
    
    private void getChildrenContexts(List<BindingContext> childrenContexts, Container parent) {
        if (parent != null) {
            for (Component child : parent.getComponents()) {
                if (child instanceof Container) {
                    BindingContext b = CONTEXTS.get((Container)child);
                    if (b == null) {
                        getChildrenContexts(childrenContexts, (Container)child);
                    } else {
                        childrenContexts.add(b);
                    }
                }
            }
        }
    }
}