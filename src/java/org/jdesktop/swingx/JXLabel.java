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

/**
 *
 * @author rbair
 */
public class JXLabel extends JLabel /*implements DesignMode*/ {
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
//            ctx = DataBoundUtils.bind(this, dataPath);
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
