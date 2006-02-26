/*
 * JXLabel.java
 *
 * Created on May 9, 2005, 3:16 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx;
import javax.swing.Icon;
import javax.swing.JLabel;
import org.jdesktop.binding.BindingContext;
import org.jdesktop.binding.impl.ColumnBinding;
import org.jdesktop.binding.impl.ManyToOneStrategy;
import org.jdesktop.conversion.Converter;
import org.jdesktop.swingx.binding.AWTColumnBinding;
import org.jdesktop.swingx.binding.JLabelBinding;
import org.jdesktop.swingx.validation.ValidationDecorator;
import org.jdesktop.swingx.validation.ValidationDecoratorFactory;
import org.jdesktop.validation.Validator;

/**
 *
 * @author rbair
 */
public class JXLabel extends JLabel implements DataAware/*implements DesignMode*/ {
    /**
     * @inheritDoc
     */
    public JXLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }
            
    /**
     * @inheritDoc
     */
    public JXLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    /**
     * @inheritDoc
     */
    public JXLabel(String text) {
        super(text);
    }

    /**
     * @inheritDoc
     */
    public JXLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
    }

    /**
     * @inheritDoc
     */
    public JXLabel(Icon image) {
        super(image);
    }

    /**
     * @inheritDoc
     */
    public JXLabel() {
        super();
    }
    
    /*************      Data Binding    ****************/
    private String dataPath = "";
    private BindingContext ctx = null;
    private JLabelBinding binding;
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
    public JLabelBinding getBinding() {
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
        binding = (JLabelBinding)DataBoundUtils.bind(ctx, this, dataPath);
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

    public String getText() {
        //PENDING former implementation that used functors via expressions to
        //decide what should be rendered in the label
//        if (rendererExpression != null) {
//            parser.bindThis(row.getTable().getDataSet());
//            parser.setUndecoratedDecimal(true);
//
//            RendererExpression exp = xlist.getRendererExpression();
//            if (exp != null) {
//                //execute each expression
//                List<String> expressions = exp.getExpressions();
//                Object[] values = new Object[expressions.size()];
//                for (int i=0; i<values.length; i++) {
//                    String e = expressions.get(i);
//                    UnaryFunctor<DataRow,?> expImpl =  null;
//                    try {
//                        expImpl = parser.parseComputedColumn(row.getTable(), e);
//                    } catch (ParseException pe) {
//                        pe.printStackTrace();
//                    }
//                    values[i] = expImpl == null ? e : expImpl.fn(row);
//                }
//                value = String.format(exp.getFormat(), values);
//            }
            return super.getText();
//        } else {
//            return super.getText();
//        }
    }

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
