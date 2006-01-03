/*
 * JXPasswordField.java
 *
 * Created on May 9, 2005, 3:19 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx;
import javax.swing.JPasswordField;
import javax.swing.text.Document;
import org.jdesktop.binding.BindingContext;

/**
 *
 * @author rbair
 */
public class JXPasswordField extends JPasswordField implements DataAware/*implements DesignMode*/ {

    /**
     * @inheritDoc
     */
    public JXPasswordField() {
        super();
    }

    /**
     * @inheritDoc
     */
    public JXPasswordField(String text) {
        super(text);
    }

    /**
     * @inheritDoc
     */ 
    public JXPasswordField(int columns) {
        super(columns);
    }

    /**
     * @inheritDoc
     */
    public JXPasswordField(String text, int columns) {
        super(text, columns);
    }

    /**
     * @inheritDoc
     */
    public JXPasswordField(Document doc, String txt, int columns) {
        super(doc, txt, columns);
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

    public void setBindingContext(BindingContext ctx) {
        if (this.ctx != null) {
            DataBoundUtils.unbind(this, this.ctx);
        }
        this.ctx = ctx;
        if (this.ctx != null) {
            if (DataBoundUtils.isValidPath(this.dataPath)) {
                ctx.bind(this, this.dataPath);
            }
        }
    }

    public BindingContext getBindingContext() {
        return ctx;
    }
    
    //PENDING
    //addNotify and removeNotify were necessary for java one, not sure if I still
    //need them or not
    public void addNotify() {
        super.addNotify();
        //if ctx does not exist, try to create one
        if (ctx == null && DataBoundUtils.isValidPath(dataPath)) {
            ctx = DataBoundUtils.bind(this, dataPath);
        }
    }

    public void removeNotify() {
        //if I had a ctx, blow it away
        if (ctx != null) {
            DataBoundUtils.unbind(this, ctx);
        }
        super.removeNotify();
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
