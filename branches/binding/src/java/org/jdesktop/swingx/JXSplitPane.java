/*
 * JXSplitPane.java
 *
 * Created on November 29, 2005, 8:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 *
 * @author Richard
 */
public class JXSplitPane extends JSplitPane {
    /**
     * A flag specifying whether the divider should be painted. This is a
     * hint which may be ignored by the look and feel
     */
    private boolean dividerPainted = true;
    /**
     * This is the divider Border, specified only if dividerPainted is true.
     * I cache this border so I can restore it if the user toggles setDividerPainted
     * back to true
     */
    private Border dividerBorder = null;
    
    /** Creates a new instance of JXSplitPane */
    public JXSplitPane() {
    }
    
    public void setDividerPainted(boolean b) {
        if (dividerPainted != b) {
            // if possible, toggle whether a border is part of the divider
            if (getUI() instanceof BasicSplitPaneUI) {
                BasicSplitPaneUI ui = (BasicSplitPaneUI)getUI();
                if (b) {
                    dividerBorder = ui.getDivider().getBorder();
                    ui.getDivider().setBorder(BorderFactory.createEmptyBorder());
                } else {
                    ui.getDivider().setBorder(dividerBorder);
                }
            }
            firePropertyChange("dividerPainted", !b, b);
        }
    }
    
    public boolean isDividerPainted() {
        return dividerPainted;
    }
}