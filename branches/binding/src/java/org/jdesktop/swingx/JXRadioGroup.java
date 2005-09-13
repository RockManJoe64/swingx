/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JRadioButton;
import org.jdesktop.binding.BindingContext;


/**
 * @author Amy Fowler
 * @version 1.0
 */

public class JXRadioGroup extends JXPanel {
    private static final long serialVersionUID = 3257285842266567986L;
    private ButtonGroup buttonGroup;
    private List<Object> values = new ArrayList<Object>();
    private ActionSelectionListener actionHandler;
    private List<ActionListener> actionListeners;
    private int gapWidth;

    public JXRadioGroup() {
        this(0);
    }

    public JXRadioGroup(int gapWidth) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        buttonGroup = new ButtonGroup();
        this.gapWidth = gapWidth;
        
    }
    public JXRadioGroup(Object radioValues[]) {
        this();
        for(int i = 0; i < radioValues.length; i++) {
            add(radioValues[i]);
        }
    }

    public void setValues(Object[] radioValues) {
        clearAll();
        for(int i = 0; i < radioValues.length; i++) {
            add(radioValues[i]);
        }
    }
    
    private void clearAll() {
        values.clear();
        removeAll();
        buttonGroup = new ButtonGroup();
    }

    public void add(Object radioValue) {
        values.add(radioValue);
        addButton(new JRadioButton(radioValue.toString()));
    }

    public void add(AbstractButton button) {
        values.add(button.getText());
        // PENDING: mapping needs cleanup...
        addButton(button);
    }

    private void addButton(AbstractButton button) {
        buttonGroup.add(button);
        super.add(button);
        if (actionHandler == null) {
            actionHandler = new ActionSelectionListener();
//            actionHandler = new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    fireActionEvent(e);
//                }
//            };
        }
        button.addActionListener(actionHandler);
        button.addItemListener(actionHandler);
    }

    private class ActionSelectionListener implements ActionListener,
    ItemListener {

        public void actionPerformed(ActionEvent e) {
            fireActionEvent(e);
        
        }
        
        public void itemStateChanged(ItemEvent e) {
            fireActionEvent(null);
        
        }

}
   
    private void checkGap() {
        if ((getGapWidth() > 0) && (getComponentCount() > 0)) {
            add(Box.createHorizontalStrut(getGapWidth()));
        }
    }

    private int getGapWidth() {
        return gapWidth;
    }

    public AbstractButton getSelectedButton() {
        ButtonModel selectedModel = buttonGroup.getSelection();
        AbstractButton children[] = getButtonComponents();
        for(int i = 0; i < children.length; i++) {
            AbstractButton button = (AbstractButton)children[i];
            if (button.getModel() == selectedModel) {
                return button;
            }
        }
        return null;
    }

    private AbstractButton[] getButtonComponents() {
        Component[] children = getComponents();
        List buttons = new ArrayList();
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof AbstractButton) {
                buttons.add(children[i]);
            }
        }
        return (AbstractButton[]) buttons.toArray(new AbstractButton[buttons.size()]);
    }

    private int getSelectedIndex() {
        ButtonModel selectedModel = buttonGroup.getSelection();
        Component children[] = getButtonComponents();
        for (int i = 0; i < children.length; i++) {
            AbstractButton button = (AbstractButton) children[i];
            if (button.getModel() == selectedModel) {
                return i;
            }
        }
        return -1;
    }

    public Object getSelectedValue() {
        int index = getSelectedIndex();
        return (index < 0 || index >= values.size()) ? null : values.get(index);
    }

    public void setSelectedValue(Object value) {
        int index = values.indexOf(value);
        AbstractButton button = getButtonComponents()[index];
        button.setSelected(true);
    }

    public void addActionListener(ActionListener l) {
        if (actionListeners == null) {
            actionListeners = new ArrayList<ActionListener>();
        }
        actionListeners.add(l);
    }

    public void removeActionListener(ActionListener l) {
        if (actionListeners != null) {
            actionListeners.remove(l);
        }
    }

    public ActionListener[] getActionListeners() {
        if (actionListeners != null) {
            return (ActionListener[])actionListeners.toArray(new ActionListener[0]);
        }
        return new ActionListener[0];
    }

    protected void fireActionEvent(ActionEvent e) {
        if (actionListeners != null) {
            for (int i = 0; i < actionListeners.size(); i++) {
                ActionListener l = (ActionListener) actionListeners.get(i);
                l.actionPerformed(e);
            }
        }
    }
    
    /*************      Data Binding    ****************/
    private String dataPath = "";
    private BindingContext ctx = null;
    
    /**
     * @param path
     */
    public void setDataPath(String path) {
        path = path == null ? "" : path;
        if (!this.dataPath.equals(path)) {
            DataBoundUtils.unbind(this, ctx);
            String oldPath = this.dataPath;
            this.dataPath = path;
            if (DataBoundUtils.isValidPath(this.dataPath)) {
                ctx = DataBoundUtils.bind(this, this.dataPath);
            }
            firePropertyChange("dataPath", oldPath, this.dataPath);
        }
    }
    
    public String getDataPath() {
        return dataPath;
    }

    //PENDING
    //addNotify and removeNotify were necessary for java one, not sure if I still
    //need them or not
//    public void addNotify() {
//        super.addNotify();
//        //if ctx does not exist, try to create one
//        if (ctx == null && DataBoundUtils.isValidPath(dataPath)) {
//            ctx = DataBoundUtils.bind(JXEditorPane.this, dataPath);
//        }
//    }
//
//    public void removeNotify() {
//        //if I had a ctx, blow it away
//        if (ctx != null) {
//            DataBoundUtils.unbind(this, ctx);
//        }
//        super.removeNotify();
//    }
//
//    //BEANS SPECIFIC CODE:
//    private boolean designTime = false;
//    public void setDesignTime(boolean designTime) {
//        this.designTime = designTime;
//    }
//    public boolean isDesignTime() {
//        return designTime;
//    }
//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        if (designTime && dataPath != null && !dataPath.equals("")) {
//            //draw the binding icon
//            ImageIcon ii = new ImageIcon(getClass().getResource("icon/chain.png"));
//            g.drawImage(ii.getImage(), getWidth() - ii.getIconWidth(), 0, ii.getIconWidth(), ii.getIconHeight(), ii.getImageObserver());
//        }
//    }
}