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
package org.jdesktop.swingx.list;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.RolloverProducer;
import org.jdesktop.swingx.action.LinkAction;

/**
 * A Renderer/Editor for Links in JLists.
 * 
 * @author rbair
 */
public class ListLinkRenderer implements ListCellRenderer{

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
    private WeakHashMap<JList,State> lists = new WeakHashMap<JList,State>();
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
    public ListLinkRenderer() {
        this(null);
    }

    /**
     * Create a LinkRenderer that gets the action to perform (and potentially
     * other attributes) from the given LinkAction
     */
    public ListLinkRenderer(LinkAction visitingDelegate) {
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

    public JXHyperlink getListCellRendererComponent(JList list, Object value, 
            int index, boolean isSelected, boolean cellHasFocus) {
        //if I've never seen this list before, add it to the set of lists
        if (!lists.containsKey(list)) {
            lists.put(list, new State());
            list.addMouseListener(hoverListener);
            list.addMouseMotionListener(hoverListener);
        }
        hyperlink.setOpaque(list.isOpaque());
        updateLinkAction(value);
        updateSelectionColors(list, isSelected);
        State s = lists.get(list);
        if (s != null) {
            if (s.mouseOverIndex == -1) {
                list.setCursor(s.defaultCursor);
                hyperlink.getModel().setRollover(false);
            } else if (s.mouseOverIndex == index) {
                hyperlink.getModel().setRollover(true);
                list.setCursor(State.handCursor);
            } else {
                hyperlink.getModel().setRollover(false);
            }
        }
//        updateFocusBorder(cellHasFocus);
        return hyperlink;
    }
    
    private void updateSelectionColors(JList table, boolean isSelected) {
        if (isSelected) {
            // linkButton.setForeground(table.getSelectionForeground());
            hyperlink.setBackground(table.getSelectionBackground());
        } else {
            // linkButton.setForeground(table.getForeground());
            hyperlink.setBackground(table.getBackground());
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
        private int mouseOverIndex = -1;
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
            JList list = (JList)e.getSource();
            State s = lists.get(list);
            if (s != null) {
                s.lastMousePoint = e.getPoint();
                s.mouseOverIndex = list.locationToIndex(s.lastMousePoint);
            }
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e) {
            JList list = (JList)e.getSource();
            ActionEvent ae = new ActionEvent(list, ActionEvent.ACTION_PERFORMED, "click_event");
            if (linkAction != null) {
                linkAction.actionPerformed(ae);
            }
        }

        public void mouseExited(MouseEvent e) {
            JList list = (JList)e.getSource();
            //cause the list to be repainted so that the underlined
            //hyperlink cell is removed
            State s = lists.get(list);
            if (s != null) {
                s.lastMousePoint = e.getPoint();
                s.mouseOverIndex = -1;
                list.repaint();
            }
        }
    }
    
//    /**
//     * Overridden for performance reasons.
//     * See the <a href="#override">Implementation Note</a> 
//     * for more information.
//     *
//     * @since 1.5
//     * @return <code>true</code> if the background is completely opaque
//     *         and differs from the JList's background;
//     *         <code>false</code> otherwise
//     */
//    public boolean isOpaque() { 
//	Color back = getBackground();
//	Component p = getParent(); 
//	if (p != null) { 
//	    p = p.getParent(); 
//	}
//	// p should now be the JList. 
//	boolean colorMatch = (back != null) && (p != null) && 
//	    back.equals(p.getBackground()) && 
//			p.isOpaque();
//	return !colorMatch && super.isOpaque(); 
//    }
//
//   /**
//    * Overridden for performance reasons.
//    * See the <a href="#override">Implementation Note</a>
//    * for more information.
//    */
//    public void validate() {}
//
//   /**
//    * Overridden for performance reasons.
//    * See the <a href="#override">Implementation Note</a>
//    * for more information.
//    *
//    * @since 1.5
//    */
//    public void invalidate() {}
//
//   /**
//    * Overridden for performance reasons.
//    * See the <a href="#override">Implementation Note</a>
//    * for more information.
//    *
//    * @since 1.5
//    */
//    public void repaint() {}
//
//   /**
//    * Overridden for performance reasons.
//    * See the <a href="#override">Implementation Note</a>
//    * for more information.
//    */
//    public void revalidate() {}
//   /**
//    * Overridden for performance reasons.
//    * See the <a href="#override">Implementation Note</a>
//    * for more information.
//    */
//    public void repaint(long tm, int x, int y, int width, int height) {}
//
//   /**
//    * Overridden for performance reasons.
//    * See the <a href="#override">Implementation Note</a>
//    * for more information.
//    */
//    public void repaint(Rectangle r) {}
//
//   /**
//    * Overridden for performance reasons.
//    * See the <a href="#override">Implementation Note</a>
//    * for more information.
//    */
//    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
//	// Strings get interned...
//	if (propertyName=="text")
//	    super.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//   /**
//    * Overridden for performance reasons.
//    * See the <a href="#override">Implementation Note</a>
//    * for more information.
//    */
//    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}
//
//   /**
//    * Overridden for performance reasons.
//    * See the <a href="#override">Implementation Note</a>
//    * for more information.
//    */
//    public void firePropertyChange(String propertyName, char oldValue, char newValue) {}
//
//   /**
//    * Overridden for performance reasons.
//    * See the <a href="#override">Implementation Note</a>
//    * for more information.
//    */
//    public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
//
//   /**
//    * Overridden for performance reasons.
//    * See the <a href="#override">Implementation Note</a>
//    * for more information.
//    */
//    public void firePropertyChange(String propertyName, int oldValue, int newValue) {}
//
//   /**
//    * Overridden for performance reasons.
//    * See the <a href="#override">Implementation Note</a>
//    * for more information.
//    */
//    public void firePropertyChange(String propertyName, long oldValue, long newValue) {}
//
//   /**
//    * Overridden for performance reasons.
//    * See the <a href="#override">Implementation Note</a>
//    * for more information.
//    */
//    public void firePropertyChange(String propertyName, float oldValue, float newValue) {}
//
//   /**
//    * Overridden for performance reasons.
//    * See the <a href="#override">Implementation Note</a>
//    * for more information.
//    */
//    public void firePropertyChange(String propertyName, double oldValue, double newValue) {}
//
//   /**
//    * Overridden for performance reasons.
//    * See the <a href="#override">Implementation Note</a>
//    * for more information.
//    */
//    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
//
//    /**
//     * A subclass of DefaultListCellRenderer that implements UIResource.
//     * DefaultListCellRenderer doesn't implement UIResource
//     * directly so that applications can safely override the
//     * cellRenderer property with DefaultListCellRenderer subclasses.
//     * <p>
//     * <strong>Warning:</strong>
//     * Serialized objects of this class will not be compatible with
//     * future Swing releases. The current serialization support is
//     * appropriate for short term storage or RMI between applications running
//     * the same version of Swing.  As of 1.4, support for long term storage
//     * of all JavaBeans<sup><font size="-2">TM</font></sup>
//     * has been added to the <code>java.beans</code> package.
//     * Please see {@link java.beans.XMLEncoder}.
//     */
//    public static class UIResource extends DefaultListCellRenderer
//        implements javax.swing.plaf.UIResource
//    {
//    }
    
}
