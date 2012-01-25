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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import org.jdesktop.swingx.action.LinkAction;

/**
 * 
 * @author rbair
 */
public class LinkRenderer extends JXHyperlink 
        implements ListCellRenderer,
                   TreeCellRenderer,
                   TableCellRenderer {
    /**
     * <p>A map that contains all of the components that have been involved in
     * a rendering operation. The associated LinkRendererDelegate keeps track of
     * several things, including the lastMousePosition, component cursor,
     * and other state. It also handles all component specific logic, such
     * as figuring out the selected cell for a table or row for a tree.</p>
     * 
     * <p>Because the LinkRenderer is never notified when it is removed as
     * a renderer on the component, it is <strong>very important</strong> that
     * no strong reference is ever kept to the JComponent within this class.
     * Doing so could lead to major memory leaks. This WeakHashMap will not
     * keep a strong reference, and will be garbage collected when appropriate.</p>
     */
    private Map<JComponent, LinkRendererDelegate> delegates = new WeakHashMap<JComponent, LinkRendererDelegate>();
    
    /**
     * Handles all of the mouse events for all of the components that this
     * renderer is being used with. The MouseHandler keeps track of the
     * mouse position within the component, and causes repaints to occur
     * when necessary.
     */
    private final MouseHandler mouseHandler;
    
    /**
     * Create a do-nothing LinkRenderer. The items renderer will look like
     * hyperlinks, but nothing will happen when they are clicked.
     */
    public LinkRenderer() {
        mouseHandler = new MouseHandler();
    }
    
    /**
     * @inheritDoc
     */
    @Override
    public void updateUI() {
        super.updateUI();
        setBorderPainted(true);
        setOpaque(true);
    }

    /**
     * @return a LinkRendererDelegate instance for the given component.
     *         This method first consults the cache of delegates. If a proper
     *         delegate cannot be found, one will be created.
     */
    private LinkRendererDelegate getDelegate(JComponent component, Class delegateClass) {
        LinkRendererDelegate delegate = delegates.get(component);
        if (delegate == null) {
            try {
                delegate = (LinkRendererDelegate)delegateClass.newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Could not create a new link delegate " +
                        "based on the class " + delegateClass, e);
            }
            delegates.put(component, delegate);
            //remove the mouseHandler from the component, just in case it was
            //previously installed to avoid installing duplicates
            component.removeMouseListener(mouseHandler);
            component.removeMouseMotionListener(mouseHandler);
            //install the mouseHandler
            component.addMouseListener(mouseHandler);
            component.addMouseMotionListener(mouseHandler);
        }
        
        return delegate;
    }
    
    /**
     * Updates the LinkAction associated with this renderer. This method will
     * be called many times, every time the cell is re-rendered
     */
    private void updateLinkAction(LinkRendererDelegate delegate, Object value) {
        LinkAction linkAction = getAction();
        if (linkAction == null) {
            linkAction = new LinkAction(value == null ? "" : value.toString()) {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Broken Link");
                }
            };
        } else {
            linkAction.setName(value == null ? "" : value.toString());
        }
        setAction(linkAction);
    }

    public Component getListCellRendererComponent(JList list, Object value,
            int row, boolean isSelected, boolean hasFocus) {
        
        ListLinkRendererDelegate d = (ListLinkRendererDelegate)getDelegate(list, ListLinkRendererDelegate.class);
        updateLinkAction(d, value);
        Point p = d.lastMousePoint;
        if (/*hasFocus || */(p != null && (p.x >= 0) && (d.mouseOverRow == row))) {
            getModel().setRollover(true);
            list.setCursor(d.handCursor);
        } else {
            getModel().setRollover(false);
            list.setCursor(d.defaultCursor);
        }
        d.updateSelectionColors(this, list, isSelected);
//        updateFocusBorder(cellHasFocus);
        return this;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        TableLinkRendererDelegate d = (TableLinkRendererDelegate)getDelegate(table, TableLinkRendererDelegate.class);
        updateLinkAction(d, value);
        Point p = d.lastMousePoint;
        int colX = table.columnAtPoint(p);
        int rowY = table.rowAtPoint(p);
        if (/*hasFocus || */(p != null && (p.x >= 0) && (colX == column) && (rowY == row))) {
            getModel().setRollover(true);
        } else {
            getModel().setRollover(false);
        }
        TableCellRenderer renderer = table.getCellRenderer(rowY, colX);
        if (renderer instanceof LinkRenderer) {
            table.setCursor(d.handCursor);
        } else {
            table.setCursor(d.defaultCursor);
        }
        d.updateSelectionColors(this, table, isSelected);
//        updateFocusBorder(cellHasFocus);
        return this;
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        TreeLinkRendererDelegate d = (TreeLinkRendererDelegate)getDelegate(tree, TreeLinkRendererDelegate.class);
        updateLinkAction(d, value);
        Point p = d.lastMousePoint;
        if (/*hasFocus || */(p != null && (p.x >= 0) && (d.mouseOverRow == row))) {
            getModel().setRollover(true);
            tree.setCursor(d.handCursor);
        } else {
            getModel().setRollover(false);
            tree.setCursor(d.defaultCursor);
        }
        d.updateSelectionColors(this, tree, isSelected);
//        updateFocusBorder(cellHasFocus);
        return this;
    }
    
    /**
     * Intercepts mouse moved & clicked events and routes them to the
     * delegates for handling
     */
    private final class MouseHandler extends MouseInputAdapter {
        /**
         * @inheritDoc
         */
        public void mouseMoved(MouseEvent e) {
            if (e.getSource() instanceof JComponent) {
                JComponent c = (JComponent)e.getSource();
                LinkRendererDelegate d = delegates.get(c);
                if (d != null) {
                    d.mouseMoved(LinkRenderer.this, c, e);
                }
            }
        }

        /**
         * @inheritDoc
         */
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() instanceof JComponent) {
                JComponent c = (JComponent)e.getSource();
                LinkRendererDelegate d = delegates.get(c);
                if (d != null) {
                    d.mouseClicked(LinkRenderer.this, c, e);
                }
            }
        }
    }
    
    public static abstract class LinkRendererDelegate<T extends JComponent> {
        /**
         * The last mouse point gathered from the mouse handler
         */
        Point lastMousePoint = new Point(0, 0);
        /**
         * The default cursor
         */
        Cursor defaultCursor = Cursor.getDefaultCursor();
        /**
         * The mouse cursor to show when the mouse is hovering over a hyperlink
         * TODO: Should get this from the UI manager
         */
        Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        
        abstract void updateSelectionColors(LinkRenderer r, T component, boolean isSelected);
        abstract void mouseMoved(LinkRenderer r, T component, MouseEvent e);
        abstract void mouseClicked(LinkRenderer r, T component, MouseEvent e);
    }

    public static final class ListLinkRendererDelegate extends LinkRendererDelegate<JList> {
        /**
         * The row over which the mouse is currently positioned
         */
        int mouseOverRow = -1;

        void updateSelectionColors(LinkRenderer r, JList list, boolean isSelected) {
            if (isSelected) {
                r.setBackground(list.getSelectionBackground());
            } else {
                r.setBackground(list.getBackground());
            }
        }
        
        void mouseMoved(LinkRenderer renderer, JList list, MouseEvent e) {
            Point oldLocation = lastMousePoint;
            lastMousePoint = e.getPoint();
            Point newLocation = lastMousePoint;
            
            if (oldLocation != null) {
                mouseOverRow = list.locationToIndex(oldLocation);
                Rectangle r = list.getCellBounds(mouseOverRow, mouseOverRow);
                r.x = 0;
                r.width = list.getWidth();
                mouseOverRow = r.contains(oldLocation) ? mouseOverRow : -1;
                list.repaint(r);
            }
            if (newLocation != null) {
                mouseOverRow = list.locationToIndex(newLocation);
                Rectangle r = list.getCellBounds(mouseOverRow, mouseOverRow);
                r.x = 0;
                r.width = list.getWidth();
                mouseOverRow = r.contains(newLocation) ? mouseOverRow : -1;
                list.repaint(r);
            }
        }

        void mouseClicked(LinkRenderer renderer, JList list, MouseEvent e) {
            lastMousePoint = e.getPoint();
            int clickedIndex = list.locationToIndex(lastMousePoint);
            Rectangle r = list.getCellBounds(clickedIndex, clickedIndex);
            r.x = 0;
            r.width = list.getWidth();
            clickedIndex = r.contains(lastMousePoint) ? clickedIndex : -1;
            if (clickedIndex >= 0) {
                ActionEvent ae = new ActionEvent(list, clickedIndex, "hyperlink clicked");
                LinkAction linkAction = renderer.getAction();
                if (linkAction != null) {
                    linkAction.actionPerformed(ae);
                }
            }
        }
    }

    public static final class TableLinkRendererDelegate extends LinkRendererDelegate<JTable> {
        void updateSelectionColors(LinkRenderer r, JTable table, boolean isSelected) {
            if (isSelected) {
                r.setBackground(table.getSelectionBackground());
            } else {
                r.setBackground(table.getBackground());
            }
        }

        void mouseMoved(LinkRenderer renderer, JTable table, MouseEvent e) {
            Point oldLocation = lastMousePoint;
            lastMousePoint = e.getPoint();
            Point newLocation = lastMousePoint;
            
            if (oldLocation != null) {
                Rectangle r = table.getCellRect(
                        table.rowAtPoint(oldLocation), 
                        table.columnAtPoint(oldLocation), false);
                r.x = 0;
                r.width = table.getWidth();
                table.repaint(r);
            }
            if (newLocation != null) {
                Rectangle r = table.getCellRect(
                        table.rowAtPoint(newLocation), 
                        table.columnAtPoint(newLocation), false);
                r.x = 0;
                r.width = table.getWidth();
                table.repaint(r);
            }
        }

        void mouseClicked(LinkRenderer renderer, JTable table, MouseEvent e) {
            lastMousePoint = e.getPoint();
            int colX = table.columnAtPoint(lastMousePoint);
            int rowY = table.rowAtPoint(lastMousePoint);
            if (table.getCellRenderer(rowY, colX) == renderer) {
                ActionEvent ae = new ActionEvent(table, lastMousePoint.y / table.getRowHeight(), "hyperlink clicked");
                LinkAction linkAction = renderer.getAction();
                if (linkAction != null) {
                    linkAction.actionPerformed(ae);
                }
            }
        }
    }

    public static final class TreeLinkRendererDelegate extends LinkRendererDelegate<JTree> {
        /**
         * The row over which the mouse is currently positioned
         */
        int mouseOverRow = -1;

        void updateSelectionColors(LinkRenderer r, JTree tree, boolean isSelected) {
            if (isSelected) {
                //hmmm....
                r.setBackground(tree.getBackground());
            } else {
                r.setBackground(tree.getBackground());
            }
        }

        void mouseMoved(LinkRenderer renderer, JTree tree, MouseEvent e) {
            Point oldLocation = lastMousePoint;
            lastMousePoint = e.getPoint();
            Point newLocation = lastMousePoint;
            
            if (oldLocation != null) {
                mouseOverRow = tree.getRowForLocation(oldLocation.x, oldLocation.y);
                Rectangle r = tree.getRowBounds(mouseOverRow);
                if (r != null) {
                    r.x = 0;
                    r.width = tree.getWidth();
                    mouseOverRow = r.contains(oldLocation) ? mouseOverRow : -1;
                    tree.repaint(r);
                }
            }
            if (newLocation != null) {
                mouseOverRow = tree.getRowForLocation(newLocation.x, newLocation.y);
                Rectangle r = tree.getRowBounds(mouseOverRow);
                if (r != null) {
                    r.x = 0;
                    r.width = tree.getWidth();
                    mouseOverRow = r.contains(newLocation) ? mouseOverRow : -1;
                    tree.repaint(r);
                }
            }
        }

        void mouseClicked(LinkRenderer renderer, JTree tree, MouseEvent e) {
            lastMousePoint = e.getPoint();
            int clickedIndex = tree.getRowForLocation(lastMousePoint.x, lastMousePoint.y);
            if (clickedIndex >= 0) {
                ActionEvent ae = new ActionEvent(tree, clickedIndex, "hyperlink clicked");
                LinkAction linkAction = renderer.getAction();
                if (linkAction != null) {
                    linkAction.actionPerformed(ae);
                }
            }
        }
    }

//    private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
//    private void updateSelectionColors(JList table, boolean isSelected) {
//        if (isSelected) {
//            // linkButton.setForeground(table.getSelectionForeground());
//            linkButton.setBackground(table.getSelectionBackground());
//        } else {
//            // linkButton.setForeground(table.getForeground());
//            linkButton.setBackground(table.getBackground());
//        }
//    }
//    private void updateSelectionColors(JTable table, boolean isSelected) {
//            if (isSelected) {
////                linkButton.setForeground(table.getSelectionForeground());
//                linkButton.setBackground(table.getSelectionBackground());
//            }
//            else {
////                linkButton.setForeground(table.getForeground());
//                linkButton.setBackground(table.getBackground());
//            }
//    }
//    private void updateFocusBorder(boolean hasFocus) {
//        if (hasFocus) {
//            linkButton.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
//        } else {
//            linkButton.setBorder(noFocusBorder);
//        }
//    }
    
    
}
