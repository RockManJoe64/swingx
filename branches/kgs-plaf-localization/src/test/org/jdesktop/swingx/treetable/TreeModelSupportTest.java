/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.swingx.treetable;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import junit.framework.TestCase;

import org.jdesktop.test.TreeModelReport;

/**
 * Unit tests around Tree/Table/ModelEvent notification.
 * 
 * @author Jeanette Winzenburg
 */
public class TreeModelSupportTest extends TestCase {

    /** the treeModelSupport to test */
    private TreeModelSupport support;
    /** the TreeTableModel is instantiated with. */ 
    private TreeTableModel model;
    /** the report listening to the support. */
    private TreeModelReport report;

    // TODO - implement and test precondition failure of added/removed 
    // notification
    
    /**
     * test modelSupport pathChanged: 
     * not null path  must not be empty, (checked by TreePath)
     * path elements must not be null (core issue - should be checked
     *   by TreePath but isn't)
     */
    public void testPathChangedNotNullPathElements() {
        TreePath path = new TreePath(new Object[] {null});
        try {
            support.firePathChanged(path);
            fail("must not allow null path elements");
        } catch (NullPointerException e) {
            // expected
        } 
        // unexpected exception
    }
    
    /**
     * test modelSupport pathChanged: 
     * throw on null path
     */
    public void testPathChangedNotNullPath() {
        try {
            support.firePathChanged(null);
            fail("must not allow null path elements");
        } catch (NullPointerException e) {
            // expected
        } 
        // unexpected exception
    }
    

    /**
     * test modelSupport treeStructureChanged: null path.
     *
     */
    public void testTreeStructureChangedNullPath() {
        support.fireTreeStructureChanged(null);
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getStructureEventCount());
        TreeModelEvent structureEvent = report.getLastStructureEvent();
        assertNull(structureEvent.getChildren());
        assertNull(structureEvent.getTreePath());
        assertNull(structureEvent.getPath());
    }

    /**
     * test modelSupport treeStructureChanged: 
     * not null path  must not be empty, (checked by TreePath)
     * path elements must not be null (core issue - should be checked
     *   by TreePath but isn't)
     * first element must be root (? not sure so don't enforce).
     *
     *
     */
    public void testTreeStructureChangedNotNullPathElements() {
        TreePath path = new TreePath(new Object[] {null});
        try {
            support.fireTreeStructureChanged(path);
            fail("must not allow null path elements");
        } catch (NullPointerException e) {
            // expected
        } 
        // unexpected exception
    }
    

    /**
     * test modelSupport treeStructureChanged: 
     * not null path  must not be empty, 
     * first element must be root (? not sure so don't enforce).
     *
     *
     */
    public void testTreeStructureChangedNotNullPath() {
        Object root = model.getRoot();
        Object child = model.getChild(root, 0);
        TreePath path = new TreePath(new Object[] {root, child});
        support.fireTreeStructureChanged(path);
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getStructureEventCount());
        TreeModelEvent structureEvent = report.getLastStructureEvent();
        assertEquals(path, structureEvent.getTreePath());
    }
    

    /**
     * test modelSupport newRoot: not null root.
     *
     */
    public void testNewRootNull() {
        DefaultTreeTableModel model = new DefaultTreeTableModel();
        assertNull(model.getRoot());
        TreeModelSupport support = new TreeModelSupport(model);
        support.addTreeModelListener(report);
        support.fireNewRoot();
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getStructureEventCount());
        TreeModelEvent structureEvent = report.getLastStructureEvent();
        assertNull(structureEvent.getChildren());
        assertNull(structureEvent.getTreePath());
        assertNull(structureEvent.getPath());
    }

    /**
     * test modelSupport newRoot: not null root.
     *
     */
    public void testNewRootNotNull() {
        support.fireNewRoot();
        assertEquals(1, report.getEventCount());
        assertEquals(1, report.getStructureEventCount());
        TreeModelEvent structureEvent = report.getLastStructureEvent();
        assertNull(structureEvent.getChildren());
        assertEquals(model.getRoot(), 
                structureEvent.getTreePath().getLastPathComponent());
        assertEquals(1, structureEvent.getPath().length);
    }
    
    /**
     * sanity to characterize TreeModelEvents: structureChanged
     * constructor has null children but empty childIndices.
     *
     */
    public void testTreeStructureChangedConstructor() {
        TreePath path = new TreePath(model.getRoot());
        TreeModelEvent structureChanged = new TreeModelEvent(model, path);
        assertEquals(model, structureChanged.getSource());
        assertEquals(path, structureChanged.getTreePath());
        assertNull(structureChanged.getChildren());
        // not documented ...
        assertNotNull(structureChanged.getChildIndices());
        assertEquals(0, structureChanged.getChildIndices().length);
    }
 
    /**
     * sanity to characterize TreeModelEvents: constructor for 
     * changed/inserted/removed. The api doc is not overly clear,
     * but the childIndices/children should not be null?
     */
    public void testTreeModifiedConstructor() {
        TreePath path = new TreePath(model.getRoot());
        int[] childIndices = new int[] { 0 };
        Object[] children = new Object[] { model.getChild(model.getRoot(), childIndices[0]) };
        
        TreeModelEvent changed = new TreeModelEvent(model, path, childIndices, children);
        assertEquals(model, changed.getSource());
        // returns the path as-is
        assertEquals(path, changed.getTreePath());
        // returns a defensive copy
        assertEquals(children[0], changed.getChildren()[0]);
        assertEquals(childIndices[0], changed.getChildIndices()[0]);
    }

//------------------------------ factory and housekeeping
    
    //copied from JTree and modified to use TTNs
    protected TreeTableModel getDefaultTreeTableModel() {
        DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode(
                "JXTreeTable");
        DefaultMutableTreeTableNode parent;

        parent = new DefaultMutableTreeTableNode("colors");
        root.add(parent);
        parent.add(new DefaultMutableTreeTableNode("blue"));
        parent.add(new DefaultMutableTreeTableNode("violet"));
        parent.add(new DefaultMutableTreeTableNode("red"));
        parent.add(new DefaultMutableTreeTableNode("yellow"));

        parent = new DefaultMutableTreeTableNode("sports");
        root.add(parent);
        parent.add(new DefaultMutableTreeTableNode("basketball"));
        parent.add(new DefaultMutableTreeTableNode("soccer"));
        parent.add(new DefaultMutableTreeTableNode("football"));
        parent.add(new DefaultMutableTreeTableNode("hockey"));

        parent = new DefaultMutableTreeTableNode("food");
        root.add(parent);
        parent.add(new DefaultMutableTreeTableNode("hot dogs"));
        parent.add(new DefaultMutableTreeTableNode("pizza"));
        parent.add(new DefaultMutableTreeTableNode("ravioli"));
        parent.add(new DefaultMutableTreeTableNode("bananas"));
        return new DefaultTreeTableModel(root);
    }

    @Override
    protected void setUp() throws Exception {
        model = getDefaultTreeTableModel();
        support = new TreeModelSupport(model);
        report = new TreeModelReport();
        support.addTreeModelListener(report);
    }
    
    

}
