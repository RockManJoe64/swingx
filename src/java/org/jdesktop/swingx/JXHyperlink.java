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

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;
import org.jdesktop.swingx.action.LinkAction;

import org.jdesktop.swingx.plaf.JXHyperlinkAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;

/**
 * A hyperlink component that derives from JButton to provide compatibility
 * mostly for binding actions enabled/disabled behavior accesilibity i18n etc...
 *
 * This component tracks its state and changes way it is being painted after
 * being clicked for the first time.
 * 
 * @author Richard Bair
 * @author Shai Almog
 * @author Jeanette Winzenburg
 */
public class JXHyperlink extends JButton {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    public static final String uiClassID = "HyperlinkUI";

    // ensure at least the default ui is registered
    static {
      LookAndFeelAddons.contribute(new JXHyperlinkAddon());
    }

    /**
     * Color for the hyper link if it has not yet been clicked. This color can
     * be set both in code, and through the UIManager with the property
     * "JXHyperlink.unclickedColor".
     */
    private Color unclickedColor = new Color(0, 0x33, 0xFF);

    /**
     * Color for the hyper link if it has already been clicked. This color can
     * be set both in code, and through the UIManager with the property
     * "JXHyperlink.clickedColor".
     */
    private Color clickedColor = new Color(0x99, 0, 0x99);

    /**
     * Creates a new instance of JXHyperlink with default parameters
     */
    public JXHyperlink() {
        super();
    }

    public JXHyperlink(LinkAction action) {
        super(action);
        init();
    }

    /**
     * @return Color for the hyper link if it has not yet been clicked.
     */
    public Color getUnclickedColor() {
        return unclickedColor;
    }

    /**
     * Sets the color for the previously not visited link. This value will override the one
     * set by the "JXHyperlink.unclickedColor" UIManager property and defaults.
     *
     * @param color Color for the hyper link if it has not yet been clicked.
     */
    public void setClickedColor(Color color) {
        Color old = getClickedColor();
        clickedColor = color;
        if (isVisited()) {
            setForeground(getClickedColor());
        }
        firePropertyChange("clickedColor", old, getClickedColor());
    }

    /**
     * @return Color for the hyper link if it has already been clicked.
     */
    public Color getClickedColor() {
        return clickedColor;
    }

    /**
     * Sets the color for the previously visited link. This value will override the one
     * set by the "JXHyperlink.clickedColor" UIManager property and defaults.
     *
     * @param color Color for the hyper link if it has already been clicked.
     */
    public void setUnclickedColor(Color color) {
        Color old = getUnclickedColor();
        unclickedColor = color;
        if (!isVisited()) {
            setForeground(getUnclickedColor());
        }
        firePropertyChange("unclickedColor", old, getUnclickedColor());
    }

    /**
     * Sets if this link has been clicked before. This will luckily affect the way
     * this component is being painted.
     *
     * @param visited If <code>true</code> marks link as visited.
     */
    protected void setVisited(boolean visited) {
        Action action = getAction();
        if (action != null) {
            //using the untypesafe approach because netbeans was throwing errors
            //due to class cast problems
            action.putValue(LinkAction.VISITED, visited);
        }
    }

    /**
     * @return <code>true</code> if hyper link has already been clicked.
     */
    protected boolean isVisited() {
        Action action = getAction();
        //using the untypesafe approach because netbeans was throwing errors
        //due to class cast problems
        if (action != null && action.getValue(LinkAction.VISITED) != null) {
            return (Boolean)action.getValue(LinkAction.VISITED);
        } else {
            return false;
        }
    }

    /**
     * Create listener that will watch the changes of the provided <code>Action</code>
     * and will update JXHyperlink's properties accordingly.
     */
    protected PropertyChangeListener createActionPropertyChangeListener(
            final Action a) {
        final PropertyChangeListener superListener = super
                .createActionPropertyChangeListener(a);
        // JW: need to do something better - only weak refs allowed!
        // no way to hook into super
        PropertyChangeListener l = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (LinkAction.VISITED.equals(evt.getPropertyName())) {
                    init();
                } else {
                    superListener.propertyChange(evt);
                }
            }
        };
        return l;
    }

    private void init() {
        setForeground(isVisited() ? getClickedColor() : getUnclickedColor());
    }

    /**
     * Returns a string that specifies the name of the L&F class
     * that renders this component.
     */
    public String getUIClassID() {
        return uiClassID;
    }

    public void setAction(Action a) {
        //This assert was failing in NB. Not sure why -- I was using a LinkAction.
        //I think it could be a classloader issue
//        assert a == null || a instanceof LinkAction : "Failed because action is of type " + a.getClass().getName();
        super.setAction(a);
        init();
    }

}
