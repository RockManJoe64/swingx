/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;


/**
 * Responsible for showing the default PopupMenu.
 * 
 * @author Jeanette Winzenburg
 */
public class ContextMenuHandler extends MouseAdapter {

    private ActionMap actionMap;

    private ContextMenuSource contextMenuSource;

    private JPopupMenu popup;

    /**
     * creates a context handler for TextContextMenuSource.
     *
     */
    public ContextMenuHandler() {
        this(null);
    }
    
    /**
     * creates a context handler for the given ContextMenuSource.
     * Defaults to TextContextMenuSource if source == null.
     * 
     * @param source
     */
    public ContextMenuHandler(ContextMenuSource source) {
        contextMenuSource = source;
    }

    // --------------------- MouseListener
    
    public void mousePressed(MouseEvent e) {
        maybeShowContext(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowContext(e);
    }

    private void maybeShowContext(final MouseEvent e) {
        if (!e.isPopupTrigger() || !e.getComponent().isEnabled())
            return;
        if (e.getComponent().hasFocus()) {
            showContextPopup(e);
        } else {
            ((JComponent) e.getComponent()).grabFocus();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    showContextPopup(e);
                }
            });
        }

    }

    private void showContextPopup(MouseEvent e) {
        showContextPopup((JComponent) e.getComponent(), e.getX(), e
                .getY());

    }

    private void showContextPopup(JComponent component, int x, int y) {
        JPopupMenu popup = getPopupMenu(component, true);
        popup.show(component, x, y);
    }

    /**
     * @param component
     * @return
     */
    private JPopupMenu getPopupMenu(JComponent component, boolean synchEnabled) {
        if (popup == null) {
            popup = new JPopupMenu();
            ActionMap map = getActionMap(component);
            String[] keys = getContextMenuSource().getKeys();
            for (int i = 0; i < keys.length; i++) {
                if (keys[i] != null) {
                    popup.add(map.get(keys[i]));
                } else {
                    popup.addSeparator();
                }
            }
        }  
        if (synchEnabled) {
            getContextMenuSource().updateActionEnabled(component, actionMap);
        }

        return popup;
    }

    private ActionMap getActionMap(JComponent component) {
        if (actionMap == null) {
            actionMap = getContextMenuSource().createActionMap(component);
        } else {
            // todo: replace actions with components?
        }
        return actionMap;
    }

    private ContextMenuSource getContextMenuSource() {
        if (contextMenuSource == null) {
            contextMenuSource = new TextContextMenuSource();
        }
        return contextMenuSource;
    }



}
