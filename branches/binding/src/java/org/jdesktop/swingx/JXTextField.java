/*
 * JXTextField.java
 *
 * Created on May 9, 2005, 1:03 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx;
import java.awt.Container;
import javax.swing.JTextField;
import javax.swing.text.Document;
import org.jdesktop.binding.BindingContext;
import org.jdesktop.binding.impl.ColumnBinding;
import org.jdesktop.binding.impl.ManyToOneStrategy;
import org.jdesktop.conversion.Converter;
import org.jdesktop.swingx.binding.AWTColumnBinding;
import org.jdesktop.swingx.binding.JTextComponentBinding;
import org.jdesktop.swingx.validation.ValidationDecorator;
import org.jdesktop.swingx.validation.ValidationDecoratorFactory;
import org.jdesktop.validation.Validator;
/**
 *
 * @author rbair
 */
public class JXTextField extends JTextField implements DataAware/*, DesignMode*/ {
    /**
     * @inheritDoc
     */ 
    public JXTextField() {
        super(10);
    }

    /**
     * @inheritDoc
     */ 
    public JXTextField(String text) {
        super(text);
    }

    /**
     * @inheritDoc
     */ 
    public JXTextField(int columns) {
        super(columns);
    }

    /**
     * @inheritDoc
     */ 
    public JXTextField(String text, int columns) {
        super(text, columns);
    }

    /**
     * @inheritDoc
     */ 
    public JXTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
    }
    
    /*************      Data Binding    ****************/
    private String dataPath = "";
    private BindingContext ctx = null;
    private JTextComponentBinding binding;
    private AWTColumnBinding.AutoCommit autoCommit = AWTColumnBinding.AutoCommit.NEVER;
    private Object conversionFormat = null;
    private Converter converter = null;
    private ManyToOneStrategy manyToOneStrategy = new ColumnBinding.CommonValueStrategy();
    private ValidationDecorator validationDecorator = ValidationDecoratorFactory.getSeverityBackgroundTooltipDecorator();
    private Object validationKey = null;
    private Validator validator = null;
    
    /**
     * @inheritDoc
     */
    public JTextComponentBinding getBinding() {
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
        binding = (JTextComponentBinding)DataBoundUtils.bind(ctx, this, dataPath);
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
        
    //PENDING
    //addNotify and removeNotify were necessary for java one, not sure if I still
    //need them or not
    public void addNotify() {
        super.addNotify();
        BindingContext parent = DataBoundUtils.findBindingContext(getParent());
        if (ctx == null && parent != null) {
            setBindingContext(parent);
        }
    }

    public void removeNotify() {
        BindingContext parent = DataBoundUtils.findBindingContext(getParent());
        if (ctx == parent && parent != null) {
            setBindingContext(null);
        }
        super.removeNotify();
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
