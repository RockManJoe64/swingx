/*
 * JXCheckBox.java
 *
 * Created on May 9, 2005, 3:17 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import org.jdesktop.binding.BindingContext;

/**
 *
 * @author rbair
 */
public class JXCheckBox extends JCheckBox /*implements DesignMode*/ {
    /**
     * Creates an initially unselected check box button with no text, no icon.
     */
    public JXCheckBox () {
        super();
    }

    /**
     * Creates an initially unselected check box with an icon.
     *
     * @param icon  the Icon image to display
     */
    public JXCheckBox(Icon icon) {
        super(icon);
    }
    
    /**
     * Creates a check box with an icon and specifies whether
     * or not it is initially selected.
     *
     * @param icon  the Icon image to display
     * @param selected a boolean value indicating the initial selection
     *        state. If <code>true</code> the check box is selected
     */
    public JXCheckBox(Icon icon, boolean selected) {
        super(icon, selected);
    }
    
    /**
     * Creates an initially unselected check box with text.
     *
     * @param text the text of the check box.
     */
    public JXCheckBox (String text) {
        super(text);
    }

    /**
     * Creates a check box where properties are taken from the 
     * Action supplied.
     *
     * @since 1.3
     */
    public JXCheckBox(Action a) {
        super(a);
    }


    /**
     * Creates a check box with text and specifies whether 
     * or not it is initially selected.
     *
     * @param text the text of the check box.
     * @param selected a boolean value indicating the initial selection
     *        state. If <code>true</code> the check box is selected
     */
    public JXCheckBox (String text, boolean selected) {
        super(text, selected);
    }

    /**
     * Creates an initially unselected check box with 
     * the specified text and icon.
     *
     * @param text the text of the check box.
     * @param icon  the Icon image to display
     */
    public JXCheckBox(String text, Icon icon) {
        super(text, icon);
    }

    /**
     * Creates a check box with text and icon,
     * and specifies whether or not it is initially selected.
     *
     * @param text the text of the check box.
     * @param icon  the Icon image to display
     * @param selected a boolean value indicating the initial selection
     *        state. If <code>true</code> the check box is selected
     */
    public JXCheckBox (String text, Icon icon, boolean selected) {
        super(text, icon, selected);
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

    //BEANS SPECIFIC CODE:
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
