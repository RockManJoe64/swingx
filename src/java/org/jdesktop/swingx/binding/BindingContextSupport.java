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
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
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
import org.jdesktop.binding.impl.BindingFactory;
import org.jdesktop.binding.impl.BindingFactoryImpl;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXRadioGroup;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.JXTreeTable;

/**
 *
 * @author rbair
 */
public class BindingContextSupport extends AbstractBindingContext {
    private static Map<Container, AbstractBindingContext> CONTEXTS = new HashMap<Container, AbstractBindingContext>();
    
    static {
        //initalize Swing bindings for the factory
        BindingFactoryImpl factory = (BindingFactoryImpl)BindingFactory.getInstance();
        factory.addMapping(AbstractButton.class, AbstractButtonBinding.class);
        factory.addMapping(JCheckBox.class, JCheckBoxBinding.class);
        factory.addMapping(JComboBox.class, JComboBoxBinding.class);
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
        factory.addMapping(JXTitledPanel.class, JXTitledPanelBinding.class);
        factory.addMapping(JXTreeTable.class, JXTreeTableBinding.class);
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

    public AbstractBindingContext getParentContext() {
        return parentContext;
    }

    public AbstractBindingContext[] getChildrenContexts() {
        return childrenContexts;
    }
}