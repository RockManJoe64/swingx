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
package org.jdesktop.swingx.action;

import java.awt.event.ItemEvent;
import javax.swing.Icon;
import org.jdesktop.swingx.action.AbstractActionExt;

/**
 * @author Jeanette Winzenburg
 * @author rbair
 */
public abstract class LinkAction extends AbstractActionExt {
    public static final String VISITED = "visited";
 
    /**
     * Copy constuctor copies the state.
     */
    public LinkAction(LinkAction action) {
        super(action);
        setVisited(false);
    }

    public LinkAction() {
        this("");
    }
    
    /**
     * @inheritDoc
     */
    public LinkAction(String name) {
        super(name);
        setVisited(false);
    }

    /**
     * @inheritDoc
     */
    public LinkAction(String name, Icon icon) {
        super(name, icon);
        setVisited(false);
    }

    /**
     * Constructs an Action with the label and command
     *
     * @param name name of the action usually used as a label
     * @param command command key of the action
     */
    public LinkAction(String name, String command) {
        this(name);
        setActionCommand(command);
        setVisited(false);
    }

    /**
     * @param name display name of the action
     * @param command the value of the action command key
     * @param icon icon to display
     */
    public LinkAction(String name, String command, Icon icon) {
        super(name, icon);
        setActionCommand(command);
        setVisited(false);
    }

    //various setter/getter methods
    /**
     * @return the Visited state of the Link
     */
    public boolean isVisited() {
        Boolean b = (Boolean)getValue(VISITED);
        return b == null ? false : b.booleanValue();
    }
    
    /**
     * Sets a flag to indicate if the link has been visited. The state of this
     * flag can be used to render the color of the link.
     * @param visited flag
     */
    public void setVisited(boolean visited) {
        putValue(VISITED, visited);
    }

    /**
     * Do nothing
     */
    public void itemStateChanged(ItemEvent e) {
    }
}