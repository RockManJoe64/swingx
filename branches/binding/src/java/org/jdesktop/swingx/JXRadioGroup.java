/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
import org.jdesktop.binding.impl.ColumnBinding;
import org.jdesktop.binding.impl.ManyToOneStrategy;
import org.jdesktop.conversion.Converter;
import org.jdesktop.swingx.binding.AWTColumnBinding;
import org.jdesktop.swingx.binding.JXRadioGroupBinding;
import org.jdesktop.swingx.validation.ValidationDecorator;
import org.jdesktop.swingx.validation.ValidationDecoratorFactory;
import org.jdesktop.validation.Validator;


/**
 * @author Amy Fowler
 * @version 1.0
 */

public class JXRadioGroup extends JXPanel implements DataAware {
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
    private JXRadioGroupBinding binding;
    private AWTColumnBinding.AutoCommit autoCommit = AWTColumnBinding.AutoCommit.NEVER;
    private Object conversionFormat = null;
    private Converter converter = null;
    private ManyToOneStrategy manyToOneStrategy = null;
    private ValidationDecorator validationDecorator = ValidationDecoratorFactory.getSeverityBackgroundTooltipDecorator();
    private Object validationKey = null;
    private Validator validator = null;
    
    /**
     * @inheritDoc
     */
    public JXRadioGroupBinding getBinding() {
        return binding;
    }
    
    /**
     * @inheritDoc
     */
    public Object getDomainData() {
        return binding == null ? null : binding.getDomainData();
    }
    
    /**
     * @param path
     */
    public void setDataPath(String path) {
        path = path == null ? "" : path;
        if (!this.dataPath.equals(path)) {
            String oldPath = this.dataPath;
            this.dataPath = path;
            firePropertyChange("dataPath", oldPath, this.dataPath);
            DataBoundUtils.unbind(ctx, this);
            bind();
        }
    }
    
    public String getDataPath() {
        return dataPath;
    }

    public void setBindingContext(BindingContext ctx) {
        if (this.ctx != ctx) {
            BindingContext old = this.ctx;
            this.ctx = ctx;
            firePropertyChange("bindingContext", old, ctx);
            DataBoundUtils.unbind(old, this);
            bind();
        }
    }
    
    private void bind() {
        binding = (JXRadioGroupBinding)DataBoundUtils.bind(ctx, this, dataPath);
        if (binding != null) {
            binding.setAutoCommit(autoCommit);
            binding.setConversionFormat(conversionFormat);
            binding.setConverter(converter);
            binding.setManyToOneStrategy(manyToOneStrategy);
            binding.setValidationDecorator(validationDecorator);
            binding.setValidationKey(validationKey);
            binding.setValidator(validator);
        }
    }

    public BindingContext getBindingContext() {
        return ctx;
    }
    
    public AWTColumnBinding.AutoCommit getAutoCommit() {
        return autoCommit;
    }
    
    public void setAutoCommit(AWTColumnBinding.AutoCommit autoCommit) {
        Object old = this.autoCommit;
        this.autoCommit = autoCommit;
        if (binding != null) {
            binding.setAutoCommit(autoCommit);
        }
        firePropertyChange("autoCommit", old, autoCommit);
    }
    
    public Object getConversionFormat() {
        return conversionFormat;
    }
    
    public void setConversionFormat(Object conversionFormat) {
        Object old = this.conversionFormat;
        this.conversionFormat = conversionFormat;
        if (binding != null) {
            binding.setConversionFormat(conversionFormat);
        }
        firePropertyChange("conversionFormat", old, conversionFormat);
    }
    
    public Converter getConverter() {
        return converter;
    }
    
    public void setConverter(Converter converter) {
        Object old = this.converter;
        this.converter = converter;
        if (binding != null) {
            binding.setConverter(converter);
        }
        firePropertyChange("converter", old, converter);
    }
    
    public ManyToOneStrategy getManyToOneStrategy() {
        return manyToOneStrategy;
    }
    
    public void setManyToOneStrategy(ManyToOneStrategy manyToOneStrategy) {
        Object old = this.manyToOneStrategy;
        this.manyToOneStrategy = manyToOneStrategy;
        if (binding != null) {
            binding.setManyToOneStrategy(manyToOneStrategy);
        }
        firePropertyChange("manyToOneStrategy", old, manyToOneStrategy);
    }
    
    public ValidationDecorator getValidationDecorator() {
        return validationDecorator;
    }
    
    public void setValidationDecorator(ValidationDecorator validationDecorator) {
        Object old = this.validationDecorator;
        this.validationDecorator = validationDecorator;
        if (binding != null) {
            binding.setValidationDecorator(validationDecorator);
        }
        firePropertyChange("validationDecorator", old, validationDecorator);
    }
    
    public String getValidationKey() {
        return (String)validationKey;
    }
    
    public void setValidationKey(String validationKey) {
        Object old = this.validationKey;
        this.validationKey = validationKey;
        if (binding != null) {
            binding.setValidationKey(validationKey);
        }
        firePropertyChange("validationKey", old, validationKey);
    }
    
    public Validator getValidator() {
        return validator;
    }
    
    public void setValidator(Validator validator) {
        Object old = this.validator;
        this.validator = validator;
        if (binding != null) {
            binding.setValidator(validator);
        }
        firePropertyChange("validator", old, validator);
    }
        
    /**
     * @inheritDoc
     * Overridden so that if no binding context has been specified for this
     * component by this point, then we'll try to locate a BindingContext
     * somewhere in the containment heirarchy.
     */
    public void addNotify() {
        super.addNotify();
        if (ctx == null && DataBoundUtils.isValidPath(dataPath)) {
            setBindingContext(DataBoundUtils.findBindingContext(this));
        }
    }
    
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
