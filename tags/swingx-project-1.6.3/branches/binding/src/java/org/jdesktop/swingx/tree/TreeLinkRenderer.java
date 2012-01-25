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
package org.jdesktop.swingx.tree;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.WeakHashMap;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.action.LinkAction;

/**
 * A Renderer/Editor for Links. This can be reused for JLists or JTables
 * (TODO what about JTrees?).
 * 
 * internally uses JXHyperlink for both (Note: don't reuse the same
 * instance as both the editor and renderer).
 * 
 * PENDING: make renderer respect selected cell state.
 * 
 * @author Jeanette Winzenburg
 */
public class TreeLinkRenderer extends DefaultTreeCellRenderer {

    private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
    
    /**
     * The JLists that this renderer will work with. This is needed to register
     * the mouse motion listener.
     *
     * <p>Design point: The ListCellRenderer API allows a single Renderer to be
     * reused among multiple Lists. However, in order for the Hyperlink to look
     * and feel right (changing the cursor to indicate that it can be clicked,
     * providing the underline and possibly changing the text color on mouse over)
     * a mouse listener must be placed on the hosting component. This leaves three
     * general approaches:</p>
     * 
     * <p>First, we can mandate that a ListLinkRenderer works with only one JList.
     * It is then trivial to code the ListLinkRenderer to register a mouse
     * listener on the JList.</p>
     *
     * <p>Second, we can add logic to the JXList so that it handles the mouse motion
     * logic for the renderer. The JXList would also then be responsible for changing
     * the pointer etc. The benefit is that the Renderer could be shared among
     * multiple JLists. The problem is that coding a dependency in JXList for
     * a specific renderer is ugly. Also, the renderer won't work with ordinary
     * JLists.</p>
     *
     * <p>The third approach is to maintain a WeakHashMap of JLists in the
     * renderer. Whenever "getListCellRendererComponent" is called, if the JList
     * isn't in the HashMap, then attach a mouse listener and add it. Whenever a
     * reference needs to be garbage collected, then get rid of the mouse listener
     * (which could happen automatically if done correctly). In this way, the
     * LinkRenderer can work with multiple JLists, doesn't require JXList, and
     * doesn't require an explicit reference to the list when constructed. The
     * downside is complexity of implementation.</p>
     *
     * <p>This class takes this final approach. While more difficult to implement,
     * it maintains all use cases and is very flexible.
     */
    private WeakHashMap<JTree,State> lists = new WeakHashMap<JTree,State>();
    private HoverListener hoverListener = new HoverListener();

    /**
     * Created and managed internally to give the "hyperlink" look
     */
    private JXHyperlink hyperlink;
    /**
     * Provided by the developer to indicate what should be done when something
     * is executed, as well as the text to display, etc
     */
    private LinkAction linkAction;
    
    /**
     * Create a do-nothing LinkRenderer. The items renderer will look like
     * hyperlinks, but nothing will happen when they are clicked.
     */
    public TreeLinkRenderer() {
        this(null);
    }

    /**
     * Create a LinkRenderer that gets the action to perform (and potentially
     * other attributes) from the given LinkAction
     */
    public TreeLinkRenderer(LinkAction visitingDelegate) {
        linkAction = visitingDelegate;
        hyperlink = new JXHyperlink(linkAction) {
            @Override
            public void updateUI() {
                super.updateUI();
                setBorderPainted(true);
                setOpaque(true);
            }
        };
    }

    /**
     */
    public void setVisitingDelegate(LinkAction visitingDelegate) {
        linkAction = visitingDelegate;
        hyperlink.setAction(linkAction);
    }
    
    /**
     */
    public LinkAction getVisitingDelegate() {
        return linkAction;
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, 
            boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        //if I've never seen this list before, add it to the set of lists
        if (!lists.containsKey(tree)) {
            lists.put(tree, new State());
            tree.addMouseListener(hoverListener);
            tree.addMouseMotionListener(hoverListener);
        }

        hyperlink.setOpaque(tree.isOpaque());
        updateLinkAction(value);
        updateSelectionColors(tree, selected);
        State s = lists.get(tree);
        if (s != null) {
            if (s.mouseOverValue == null) {
                tree.setCursor(s.defaultCursor);
                hyperlink.getModel().setRollover(false);
            } else if (s.mouseOverValue == value) {
                hyperlink.getModel().setRollover(true);
                tree.setCursor(State.handCursor);
            } else {
                hyperlink.getModel().setRollover(false);
            }
        }
        
        Icon icon = super.getLeafIcon();
        if (!leaf) {
            icon = expanded ? super.getOpenIcon() : super.getClosedIcon();
        }
        hyperlink.setIcon(icon);
//        updateFocusBorder(cellHasFocus);
        return hyperlink;
    }
    
    private void updateSelectionColors(JTree tree, boolean isSelected) {
        if (isSelected) {
            // linkButton.setForeground(table.getSelectionForeground());
//            hyperlink.setBackground(super.getBackgroundSelectionColor());
        } else {
            // linkButton.setForeground(table.getForeground());
            hyperlink.setBackground(super.getBackgroundNonSelectionColor());
        }
    }
    
    private void updateLinkAction(Object value) {
        if (linkAction == null) {
            linkAction = new LinkAction(value == null ? "" : value.toString()) {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Broken Link");
                }
            };
            hyperlink.setAction(linkAction);
        } else {
            linkAction.setName(value == null ? "" : value.toString());
        }
    }

    private static final class State {
        /**
         * The last mouse point. Used to decide whether the underline should be drawn
         * under the hyperlink at a given cell
         */
        private Point lastMousePoint = new Point(0, 0);
        private Object mouseOverValue = null;
        /**
         * The default cursor
         */
        private Cursor defaultCursor = Cursor.getDefaultCursor();
        /**
         * The mouse cursor to show when the mouse is hovering over a hyperlink
         * TODO: Should get this from the UI manager
         */
        private static final Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    }

    private final class HoverListener extends MouseInputAdapter {
        /* (non-Javadoc)
         * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
         */
        public void mouseMoved(MouseEvent e) {
            JTree tree = (JTree)e.getSource();
            State s = lists.get(tree);
            if (s != null) {
                s.lastMousePoint = e.getPoint();
                TreePath path = tree.getPathForLocation(s.lastMousePoint.x, s.lastMousePoint.y);
                if (path != null) {
                    s.mouseOverValue = path.getLastPathComponent();
                    tree.repaint();
                } else {
                    s.mouseOverValue = null;
                }
            }
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e) {
            JTree tree = (JTree)e.getSource();
            ActionEvent ae = new ActionEvent(tree, ActionEvent.ACTION_PERFORMED, "click_event");
            if (linkAction != null) {
                linkAction.actionPerformed(ae);
            }
        }

        public void mouseExited(MouseEvent e) {
            JTree tree = (JTree)e.getSource();
            //cause the list to be repainted so that the underlined
            //hyperlink cell is removed
            State s = lists.get(tree);
            if (s != null) {
                s.lastMousePoint = e.getPoint();
                s.mouseOverValue = null;
                tree.repaint();
            }
        }
    }
}
