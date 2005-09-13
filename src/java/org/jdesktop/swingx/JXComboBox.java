/*
 * JXComboBox.java
 *
 * Created on May 9, 2005, 3:18 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import org.jdesktop.binding.BindingContext;

/**
 *
 * @author rbair
 */
public class JXComboBox extends JComboBox /*implements DesignMode*/ {
    /**
     * @inheritDoc
     */
    public JXComboBox(ComboBoxModel aModel) {
        super(aModel);
    }

    /** 
     * @inheritDoc
     */
    public JXComboBox(final Object items[]) {
        super(items);
    }

    /**
     * @inheritDoc
     */
    public JXComboBox(Vector<?> items) {
        super(items);
    }

    /**
     * @inheritDoc
     */
    public JXComboBox() {
        super();
    }

    /*************      Data Binding    ****************/
    private String dataPath = "";
    private String listDataPath = "";
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

    /**
     * @param path
     */
    public void setListDataPath(String path) {
        path = path == null ? "" : path;
        if (!this.listDataPath.equals(path)) {
            DataBoundUtils.unbind(this, ctx);
            String oldPath = this.listDataPath;
            this.listDataPath = path;
            if (DataBoundUtils.isValidPath(this.listDataPath)) {
                ctx = DataBoundUtils.bind(this, this.getModel(), this.listDataPath);
            }
            firePropertyChange("listDataPath", oldPath, this.listDataPath);
        }
    }
    
    public String getListDataPath() {
        return listDataPath;
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
