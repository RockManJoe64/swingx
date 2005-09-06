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
import java.awt.Graphics;
import java.beans.DesignMode;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.text.Document;
import org.jdesktop.binding.BindingContext;

/**
 *
 * @author rbair
 */
public class JXTextField extends JTextField /*, DesignMode*/ {
    /**
     * For data binding
     */
    private String dataPath = "";
    private BindingContext ctx = null;
    
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

    public void addNotify() {
        super.addNotify();
        //if ctx does not exist, try to create one
        if (ctx == null && DataBoundUtils.isValidPath(dataPath)) {
            ctx = DataBoundUtils.bind(JXTextField.this, dataPath);
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
//            g.drawImage(ii.getImage(), getWidth() - 13, 0, 13, 13, ii.getImageObserver());
//        }
//    }
}
