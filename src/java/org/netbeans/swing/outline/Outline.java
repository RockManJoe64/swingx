/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.swing.outline;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EventObject;
import java.util.List;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreePath;
import org.netbeans.swing.etable.ETable;
import org.netbeans.swing.etable.ETableColumn;

/** An Outline, or tree-table component.  Takes an instance of OutlineModel,
 * an interface which merges TreeModel and TableModel.
 * <p>
 * Simplest usage:  
 * <ol>
 * <li>Create a standard tree model for the tree node portion of the outline.</li>
 * <li>Implement RowModel.  RowModel is a subset of TableModel - it is passed
 * the value in column 0 of the Outline and a column index, and returns the 
 * value in the column in question.</li>
 * <li>Pass the TreeModel and the RowModel to <code>DefaultOutlineModel.createModel()</code>
 * </ol>
 * This will generate an instance of DefaultOutlineModel which will use the
 * TreeModel for the rows/tree column content, and use the RowModel to provide
 * the additional table columns.
 * <p>
 * It is also useful to provide an implementation of <code>RenderDataProvider</code>
 * to supply icons and affect text display of cells - this covers most of the 
 * needs for which it is necessary to write a custom cell renderer in JTable/JTree.
 * <p>
 * <b>Example usage:</b><br>
 * Assume FileTreeModel is a model which, given a root directory, will 
 * expose the files and folders underneath it.  We will implement a 
 * RowModel to expose the file size and date, and a RenderDataProvider which
 * will use a gray color for uneditable files and expose the full file path as
 * a tooltip.  Assume the class this is implemented in is a 
 * JPanel subclass or other Swing container.
 * <br>
 * XXX todo: clean up formatting & edit for style
 * <pre>
 * public void initComponents() {
 *   setLayout (new BorderLayout());
 *   TreeModel treeMdl = new FileTreeModel (someDirectory);
 *
 *   OutlineModel mdl = DefaultOutlineModel.createOutlineModel(treeMdl, 
 *       new FileRowModel(), true);
 *   outline = new Outline();
 *   outline.setRenderDataProvider(new FileDataProvider()); 
 *   outline.setRootVisible (true);
 *   outline.setModel (mdl);
 *   add (outline, BorderLayout.CENTER);
 * }
 *  private class FileRowModel implements RowModel {
 *     public Class getColumnClass(int column) {
 *          switch (column) {
 *              case 0 : return Date.class;
 *              case 1 : return Long.class;
 *              default : assert false;
 *          }
 *          return null;
 *      }
 *      
 *      public int getColumnCount() {
 *          return 2;
 *      }
 *      
 *      public String getColumnName(int column) {
 *          return column == 0 ? "Date" : "Size";
 *      }
 *      
 *      public Object getValueFor(Object node, int column) {
 *          File f = (File) node;
 *          switch (column) {
 *              case 0 : return new Date (f.lastModified());
 *              case 1 : return new Long (f.length());
 *              default : assert false;
 *          }
 *          return null;
 *      }
 *      
 *      public boolean isCellEditable(Object node, int column) {
 *          return false;
 *      }
 *      
 *      public void setValueFor(Object node, int column, Object value) {
 *          //do nothing, nothing is editable
 *      }
 *  }
 *  
 *  private class FileDataProvider implements RenderDataProvider {
 *      public java.awt.Color getBackground(Object o) {
 *          return null;
 *      }
 *      
 *      public String getDisplayName(Object o) {
 *          return ((File) o).getName();
 *      }
 *      
 *      public java.awt.Color getForeground(Object o) {
 *          File f = (File) o;
 *          if (!f.isDirectory() && !f.canWrite()) {
 *              return UIManager.getColor ("controlShadow");
 *          }
 *          return null;
 *      }
 *      
 *      public javax.swing.Icon getIcon(Object o) {
 *          return null;
 *      }
 *      
 *      public String getTooltipText(Object o) {
 *          return ((File) o).getAbsolutePath();
 *      }
 *      
 *      public boolean isHtmlDisplayName(Object o) {
 *          return false;
 *      }
 *   }
 * </pre>
 *
 * @author  Tim Boudreau
 */
public class Outline extends ETable {
    //XXX plenty of methods missing here - add/remove tree expansion listeners,
    //better path info/queries, etc.
    
    private boolean initialized = false;
    private Boolean cachedRootVisible = null;
    private RenderDataProvider renderDataProvider = null;
    private ComponentListener componentListener = null;
    /** Creates a new instance of Outline */
    public Outline() {
        init();
    }
    
    public Outline(OutlineModel mdl) {
        super (mdl);
        init();
    }
    
    private void init() {
        initialized = true;
        setDefaultRenderer(Object.class, new DefaultOutlineCellRenderer());
    }
    
    /** Always returns the default renderer for Object.class for the tree column */
    public TableCellRenderer getCellRenderer(int row, int column) {
        int c = convertColumnIndexToModel(column);
        TableCellRenderer result;
        if (c == 0) {
            result = getDefaultRenderer(Object.class);
        } else {
            result = super.getCellRenderer(row, column);
        }
        return result;
    }
    
    /** Get the RenderDataProvider which is providing text, icons and tooltips
     * for items in the tree column.  The default property for this value is
     * null, in which case standard JTable/JTree object -> icon/string 
     * conventions are used */
    public RenderDataProvider getRenderDataProvider() {
        return renderDataProvider;
    }
    
    /** Set the RenderDataProvider which will provide text, icons and tooltips
     * for items in the tree column.  The default is null.  If null, 
     * the data displayed will be generated in the standard JTable/JTree way - 
     * calling <code>toString()</code> on objects in the tree model and 
     * using the look and feel's default tree folder and tree leaf icons.  */
    public void setRenderDataProvider (RenderDataProvider provider) {
        if (provider != renderDataProvider) {
            RenderDataProvider old = renderDataProvider;
            renderDataProvider = provider;
            firePropertyChange ("renderDataProvider", old, provider); //NOI18N
        }
    }
    
    /** Get the TreePathSupport object which manages path expansion for this
     * Outline. */
    TreePathSupport getTreePathSupport () {
        OutlineModel mdl = getOutlineModel();
        if (mdl != null) {
            return mdl.getTreePathSupport();
        } else {
            return null;
        }
    }
    
    /** Get the layout cache which manages layout data for the Outline.
     * <strong>Under no circumstances directly call the methods on the
     * layout cache which change the expanded state - such changes will not
     * be propagated into the table model, and will leave the model and
     * its layout in inconsistent states.  Any calls that affect expanded
     * state must go through <code>getTreePathSupport()</code>.</strong> */
    public final AbstractLayoutCache getLayoutCache () {
        OutlineModel mdl = getOutlineModel();
        if (mdl != null) {
            return mdl.getLayout();
        } else {
            return null;
        }
    }
    
    boolean isTreeColumnIndex (int column) {
        int c = convertColumnIndexToModel(column);
        return c == 0;
    }
    
    public boolean isVisible (TreePath path) {
        if (getTreePathSupport() != null) {
            return getTreePathSupport().isVisible(path);
        }
        return false;
    }
    
    /** Overridden to pass the fixed row height to the tree layout cache */
    public void setRowHeight(int val) {
        super.setRowHeight(val);
        if (getLayoutCache() != null) {
            getLayoutCache().setRowHeight(val);
        }
    }
    
    /** Set whether or not the root is visible */
    public void setRootVisible (boolean val) {
        if (getOutlineModel() == null) {
            cachedRootVisible = val ? Boolean.TRUE : Boolean.FALSE;
        }
        if (val != isRootVisible()) {
            //TODO - need to force a property change on the model,
            //the layout cache doesn't have direct listener support
            getLayoutCache().setRootVisible(val);
            firePropertyChange("rootVisible", !val, val); //NOI18N
        }
    }
    
    /** Is the tree root visible.  Default value is true. */
    public boolean isRootVisible() {
        if (getLayoutCache() == null) {
            return cachedRootVisible != null ? 
                cachedRootVisible.booleanValue() : true;
        } else {
            return getLayoutCache().isRootVisible();
        }
    }

    /**
     */
    protected TableColumn createColumn(int modelIndex) {
        return new OutlineColumn(modelIndex);
    }

    protected class OutlineColumn extends ETableColumn {
        public OutlineColumn(int modleIndex) {
            super(modleIndex, Outline.this);
        }
        protected Comparator getRowComparator(int column, boolean ascending) {
            return new OutlineRowComparator(column, ascending);
        }
        public boolean isHidingAllowed() {
            return getModelIndex() != 0;
        }
        public boolean isSortingAllowed() {
            return getModelIndex() != 0;
        }
        /**
         * Comparator used for sorting the rows according to value in
         * a given column. Operates on the RowMapping objects.
         */
        protected class OutlineRowComparator extends RowComparator {
            private boolean ascending = true;
            public OutlineRowComparator(int column, boolean ascending) {
                super(column);
                this.ascending = ascending;
            }
            public int compare(Object o1, Object o2) {
                RowMapping rm1 = (RowMapping)o1;
                RowMapping rm2 = (RowMapping)o2;
                int index1 = rm1.getModelRowIndex();
                int index2 = rm2.getModelRowIndex();
                if (index1 == index2) {
                    return 0;
                }
                TreePath tp1 = getLayoutCache().getPathForRow(index1);
                TreePath tp2 = getLayoutCache().getPathForRow(index2);
                if (tp1.isDescendant(tp2)) {
                    return -1;
                }
                if (tp2.isDescendant(tp1)) {
                    return 1;
                }
                TreePath parent1 = tp1.getParentPath();
                TreePath parent2 = tp2.getParentPath();
                if (parent1 != null && parent2 != null && parent1.equals(parent2) &&
                        getOutlineModel().isLeaf(tp1.getLastPathComponent()) &&
                        getOutlineModel().isLeaf(tp2.getLastPathComponent())) {
                    return ascending ? super.compare(o1, o2) : - super.compare(o1, o2);
                }
                while (tp1.getPathCount() < tp2.getPathCount()) {
                    tp2 = tp2.getParentPath();
                }
                while (tp1.getPathCount() > tp2.getPathCount()) {
                    tp1 = tp1.getParentPath();
                }
                parent1 = tp1.getParentPath();
                parent2 = tp2.getParentPath();
                while (parent1 != null && parent2 != null && !parent1.equals(parent2)) {
                    tp1 = parent1;
                    tp2 = parent2;
                    parent1 = tp1.getParentPath();
                    parent2 = tp2.getParentPath();
                }
                int r1 = getLayoutCache().getRowForPath(tp1);
                int r2 = getLayoutCache().getRowForPath(tp2);
                
                Object obj1 = getModel().getValueAt(r1, column);
                Object obj2 = getModel().getValueAt(r2, column);
                obj1 = transformValue(obj1);
                obj2 = transformValue(obj2);
                if (obj1 == null && obj2 == null) {
                    return 0;
                }
                if (obj1 == null) {
                    return -1;
                }
                if (obj2 == null) {
                    return 1;
                }
                if ((obj1 instanceof Comparable) && (obj1.getClass().isAssignableFrom(obj2.getClass()))){
                    Comparable c1 = (Comparable) obj1;
                    return ascending ? c1.compareTo(obj2) : - c1.compareTo(obj2);
                }
                return 0;
            }
        }
    }
    
    /** Overridden to throw an exception if the passed model is not an instance
     * of <code>OutlineModel</code> (with the exception of calls from the 
     * superclass constructor) */
    public void setModel (TableModel mdl) {
        if (initialized && (!(mdl instanceof OutlineModel))) {
            throw new IllegalArgumentException (
                "Table model for an Outline must be an instance of " +
                "OutlineModel"); //NOI18N
        }
        if (mdl instanceof OutlineModel) {
            AbstractLayoutCache layout = ((OutlineModel) mdl).getLayout();
            if (cachedRootVisible != null) {
                
                layout.setRootVisible(
                    cachedRootVisible.booleanValue());
                
            }
            
            layout.setRowHeight(getRowHeight());
            
            if (((OutlineModel) mdl).isLargeModel()) {
                addComponentListener (getComponentListener());
                layout.setNodeDimensions(new ND());
            } else {
                if (componentListener != null) {
                    removeComponentListener (componentListener);
                    componentListener = null;
                }
            }
        }
        
        super.setModel(mdl);
    }
    
    /** Convenience getter for the <code>TableModel</code> as an instance of
     * OutlineModel.  If no OutlineModel has been set, returns null. */
    public OutlineModel getOutlineModel() {
        TableModel mdl = getModel();
        if (mdl instanceof OutlineModel) {
            return (OutlineModel) getModel();
        } else {
            return null;
        }
    }
    
    /** Expand a tree path */
    public void expandPath (TreePath path) {
        getTreePathSupport().expandPath (path);
    }
    
    public void collapsePath (TreePath path) {
        getTreePathSupport().collapsePath (path);
    }
    
    public Rectangle getPathBounds(TreePath path) {
        Insets i = getInsets();
        Rectangle bounds = getLayoutCache().getBounds(path, null);

        if(bounds != null && i != null) {
            bounds.x += i.left;
            bounds.y += i.top;
        }
        return bounds;
    }   
    
    public TreePath getClosestPathForLocation(int x, int y) {
        Insets i = getInsets();
        if (i != null) {
            return getLayoutCache().getPathClosestTo(x - i.left, y - i.top);
        } else {
            return getLayoutCache().getPathClosestTo(x,y);
        }
    }
    
    public boolean editCellAt (int row, int column, EventObject e) {
        //If it was on column 0, it may be a request to expand a tree
        //node - check for that first.
        if (isTreeColumnIndex (column) && e instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) e;
            TreePath path = getLayoutCache().getPathForRow(convertRowIndexToModel(row));
            if (!getOutlineModel().isLeaf(path.getLastPathComponent())) {
                int handleWidth = DefaultOutlineCellRenderer.getExpansionHandleWidth();
                Insets ins = getInsets();
                int nd = path.getPathCount() - (isRootVisible() ? 1 : 2);
                if (nd < 0) {
                    nd = 0;
                }
                int handleStart = ins.left + (nd * DefaultOutlineCellRenderer.getNestingWidth());
                int handleEnd = ins.left + handleStart + handleWidth;
                //TODO: Translate x/y to position of column if non-0
                
                if ((me.getX() > ins.left && me.getX() >= handleStart && me.getX() <= handleEnd) ||
                     me.getClickCount() > 1) {

                    boolean expanded = getLayoutCache().isExpanded(path);
                    if (!expanded) {
                        getTreePathSupport().expandPath(path);
                        
                        Object ourObject = path.getLastPathComponent();
                        int cCount = getOutlineModel().getChildCount(ourObject);
                        if (cCount > 0) {
                            Object lastChild = getOutlineModel().getChild(ourObject, cCount - 1);
                            TreePath lastChildPath = path.pathByAddingChild(lastChild);
                            int lastRow = getLayoutCache().getRowForPath(lastChildPath);
                            Rectangle rect = getCellRect(lastRow, 0, true);
                            scrollRectToVisible(rect);
                        }
                        
                    } else {
                        getTreePathSupport().collapsePath(path);
                    }
                    return false;
                }
            }
        }
            
        return super.editCellAt(row, column, e);
    }
    
    /** Computes row height ...
     */
    public void addNotify () {
        super.addNotify ();
        calcRowHeight();
    }

    /** Calculate the height of rows based on the current font. */
    private void calcRowHeight() {
        //Users of themes can set an explicit row height, so check for it
        Integer i = (Integer) UIManager.get("netbeans.outline.rowHeight"); //NOI18N
        
        int rowHeight;
        if (i != null) {
            rowHeight = i.intValue();
        } else {
            //Derive a row height to accomodate the font and expando icon
            Font f = getFont();
            FontMetrics fm = getFontMetrics(f);
            rowHeight = Math.max(fm.getHeight()+3,
                DefaultOutlineCellRenderer.getExpansionHandleHeight());
        }
        //Set row height.  If displayable, this will generate a new call
        //to paint()
        setRowHeight(rowHeight);
    }    
    
    public void tableChanged(TableModelEvent e) {
//        System.err.println("Table got tableChanged " + e);
        super.tableChanged(e);
//        System.err.println("row count is " + getRowCount());
    }
    
    /** Create a component listener to handle size changes if the table model
     * is large-model */
    private ComponentListener getComponentListener() {
        if (componentListener == null) {
            componentListener = new SizeManager();
        }
        return componentListener;
    }
    
    private JScrollPane getScrollPane() {
        JScrollPane result = null;
        if (getParent() instanceof JViewport) {
            if (((JViewport) getParent()).getParent() instanceof JScrollPane) {
                result = (JScrollPane) ((JViewport) getParent()).getParent();
            }
        }
        return result;
    }
    
    private void change() {
        revalidate();
        repaint();
    }
    
    private class ND extends AbstractLayoutCache.NodeDimensions {
        
        public Rectangle getNodeDimensions(Object value, int row, int depth, 
            boolean expanded, Rectangle bounds) {
                int wid = Outline.this.getColumnModel().getColumn(0).getPreferredWidth();
                bounds.setBounds (0, row * getRowHeight(), wid, getRowHeight());
                return bounds;
        }
        
    }
    
    
    /** A component listener.  If we're a large model table, we need
     * to inform the FixedHeightLayoutCache when the size changes, so it
     * can update its mapping of visible nodes */
    private class SizeManager extends ComponentAdapter implements ActionListener {
	protected Timer timer = null;
	protected JScrollBar scrollBar = null;
        
        public void componentMoved(ComponentEvent e) {
	    if(timer == null) {
		JScrollPane   scrollPane = getScrollPane();

		if(scrollPane == null) {
		    change();
                } else {
		    scrollBar = scrollPane.getVerticalScrollBar();
		    if(scrollBar == null || 
			!scrollBar.getValueIsAdjusting()) {
			// Try the horizontal scrollbar.
			if((scrollBar = scrollPane.getHorizontalScrollBar())
			    != null && scrollBar.getValueIsAdjusting()) {
                                
			    startTimer();
                        } else {
			    change();
                        }
		    } else {
			startTimer();
                    }
		}
	    }
        }
        
	protected void startTimer() {
	    if(timer == null) {
		timer = new Timer(200, this);
		timer.setRepeats(true);
	    }
	    timer.start();
	}        
        
	public void actionPerformed(ActionEvent ae) {
	    if(scrollBar == null || !scrollBar.getValueIsAdjusting()) {
		if(timer != null)
		    timer.stop();
		change();
		timer = null;
		scrollBar = null;
	    }
	}        
        
        public void componentHidden(ComponentEvent e) {
        }
        
        public void componentResized(ComponentEvent e) {
        }
        
        public void componentShown(ComponentEvent e) {
        }
    }
}
