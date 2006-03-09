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
package org.jdesktop.swingx.table;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.JXHyperlink;
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
public class TableLinkRenderer extends JXHyperlink implements TableCellRenderer {
    private JTable table;
    private Object identifier;
    private Point lastMousePoint = new Point(0, 0);
    /**
     * The default cursor
     */
    private Cursor defaultCursor = Cursor.getDefaultCursor();
    /**
     * The mouse cursor to show when the mouse is hovering over a hyperlink
     * TODO: Should get this from the UI manager
     */
    private Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    /**
     * Provided by the developer to indicate what should be done when something
     * is executed, as well as the text to display, etc
     */
    private LinkAction linkAction;

    /**
     * Create a do-nothing LinkRenderer. The items renderer will look like
     * hyperlinks, but nothing will happen when they are clicked.
     */
    public TableLinkRenderer(JTable table, Object colId) {
        this(table, colId, null);
    }

    /**
     * Create a LinkRenderer that gets the action to perform (and potentially
     * other attributes) from the given LinkAction
     */
    public TableLinkRenderer(JTable table, Object colId, LinkAction visitingDelegate) {
        this.table = table;
        this.identifier = colId;
        linkAction = visitingDelegate;
        LinkListener ll = new LinkListener();
        table.addMouseListener(ll);
        table.addMouseMotionListener(ll);
    }

    public void setVisitingDelegate(LinkAction visitingDelegate) {
        linkAction = visitingDelegate;
        setAction(linkAction);
    }
    
    public LinkAction getVisitingDelegate() {
        return linkAction;
    }

//------------------------ TableCellRenderer
    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        updateLinkAction(value);
        Point p = lastMousePoint;
        int colX = table.columnAtPoint(p);
        int rowY = table.rowAtPoint(p);
        if (/*hasFocus || */(p != null && (p.x >= 0) && (colX == column) && (rowY == row))) {
            getModel().setRollover(true);
        } else {
            getModel().setRollover(false);
        }
        TableCellRenderer renderer = table.getCellRenderer(rowY, colX);
        if (renderer instanceof TableLinkRenderer) {
            table.setCursor(handCursor);
        } else {
            table.setCursor(defaultCursor);
        }
        updateSelectionColors(table, isSelected);
//        updateFocusBorder(cellHasFocus);
        return this;
    }

    private void updateSelectionColors(JTable table, boolean isSelected) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }

    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        setBorderPainted(true);
        setOpaque(true);
    }

    private void updateLinkAction(Object value) {
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

    private final class LinkListener extends MouseInputAdapter {
        /* (non-Javadoc)
         * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
         */
        public void mouseMoved(MouseEvent e) {
            lastMousePoint = e.getPoint();
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e) {
            lastMousePoint = e.getPoint();
            int clickedIndex = table.columnAtPoint(lastMousePoint);
            TableColumn clickedCol = table.getColumn(identifier);
            if (clickedCol != null && clickedCol.getModelIndex() == clickedIndex) {
                ActionEvent ae = new ActionEvent(table, lastMousePoint.y / table.getRowHeight(), "hyperlink clicked");
                if (linkAction != null) {
                    linkAction.actionPerformed(ae);
                }
            }
        }
    }
}
