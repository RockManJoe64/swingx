/*
 * DnDTree.java
 *
 * Created on July 17, 2006, 5:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painterset.dndtree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author joshy
 */
public abstract class DnDTree extends JTree implements DragSourceListener, DropTargetListener, DragGestureListener {
    static DataFlavor localObjectFlavor;
    
    static {
        try {
            localObjectFlavor =
                    new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
        } catch (ClassNotFoundException cnfe) { cnfe.printStackTrace(); }
    }
    
    static DataFlavor[] supportedFlavors = { localObjectFlavor };
    DragSource dragSource;
    DropTarget dropTarget;
    Object dropTargetNode = null;
    Object draggedNode = null;
    
    
    public DnDTree() {
        super();
        setCellRenderer(new DnDTreeCellRenderer(this));
        setModel(new DefaultTreeModel(new DefaultMutableTreeNode("default")));
        dragSource = new DragSource();
        DragGestureRecognizer dgr =
                dragSource.createDefaultDragGestureRecognizer(this,
                DnDConstants.ACTION_MOVE,
                this);
        dropTarget = new DropTarget(this, this);
    }
    
    // DragGestureListener
    public void dragGestureRecognized(DragGestureEvent dge) {
        //System.out.println ("dragGestureRecognized");
        // find object at this x,y
        Point clickPoint = dge.getDragOrigin();
        TreePath path = getPathForLocation(clickPoint.x, clickPoint.y);
        if (path == null) {
            //System.out.println ("not on a node");
            return;
        }
        draggedNode =  path.getLastPathComponent();
        Transferable trans = new RJLTransferable(draggedNode);
        dragSource.startDrag(dge,Cursor.getDefaultCursor(),
                trans, this);
    }
    
    // DragSourceListener events
    public void dragDropEnd(DragSourceDropEvent dsde) {
        //System.out.println ("dragDropEnd()");
        dropTargetNode = null;
        draggedNode = null;
        repaint();
    }
    
    public void dragEnter(DragSourceDragEvent dsde) {}
    public void dragExit(DragSourceEvent dse) {}
    public void dragOver(DragSourceDragEvent dsde) {}
    public void dropActionChanged(DragSourceDragEvent dsde) {}
    
    // DropTargetListener events
    public void dragEnter(DropTargetDragEvent dtde) {
        //System.out.println ("dragEnter");
        dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        //System.out.println ("accepted dragEnter");
    }
    
    public void dragExit(DropTargetEvent dte) {}
    
    public void dragOver(DropTargetDragEvent dtde) {
        // figure out which cell it's over, no drag to self
        Point dragPoint = dtde.getLocation();
        TreePath path = getPathForLocation(dragPoint.x, dragPoint.y);
        if (path == null)
            dropTargetNode = null;
        else
            dropTargetNode = path.getLastPathComponent();
        repaint();
    }
    
    public void drop(DropTargetDropEvent dtde) {
        //System.out.println ("drop()!");
        Point dropPoint = dtde.getLocation();
        // int index = locationToIndex (dropPoint);
        TreePath path = getPathForLocation(dropPoint.x, dropPoint.y);
        System.out.println("drop path is " + path);
        boolean dropped = false;
        if(path != null) {
            try {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                //System.out.println ("accepted");
                Object droppedObject =
                        dtde.getTransferable().getTransferData(localObjectFlavor);
                MutableTreeNode droppedNode = null;
                if (droppedObject instanceof MutableTreeNode) {
                    // remove from old location
                    droppedNode = (MutableTreeNode) droppedObject;
                    ((DefaultTreeModel)getModel()).removeNodeFromParent(droppedNode);
                } else {
                    droppedNode = new DefaultMutableTreeNode(droppedObject);
                }
                // insert into spec'd path.  if dropped into a parent
                // make it last child of that parent
                Object dropNode = path.getLastPathComponent();
                if (getModel().isLeaf(dropNode)) { //dropNode.isLeaf()) {
                    //DefaultMutableTreeNode parent =
                    //    (DefaultMutableTreeNode) dropNode.getParent();
                    Object parent = path.getParentPath().getLastPathComponent();
                    //int index = parent.getIndex (dropNode);
                    int index = getModel().getIndexOfChild(parent,dropNode);
                    //((DefaultTreeModel)getModel()).insertNodeInto (droppedNode,
                    //                                               parent, index);
                    insertNodeInto(droppedObject, parent, index);
                } else {
                    //((DefaultTreeModel)getModel()).insertNodeInto (droppedNode,
                    //                                 dropNode,
                    //                                 dropNode.getChildCount());
                    insertNodeInto(droppedObject, dropNode, getModel().getChildCount(dropNode));
                }
                dropped = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dtde.dropComplete(dropped);
    }
    
    public abstract void insertNodeInto(Object droppedNode, Object dropNode, int index);
    
    public void dropActionChanged(DropTargetDragEvent dtde) {}
    
    class RJLTransferable implements Transferable {
        Object object;
        public RJLTransferable(Object o) {
            object = o;
        }
        public Object getTransferData(DataFlavor df)
        throws UnsupportedFlavorException, IOException {
            if (isDataFlavorSupported(df))
                return object;
            else
                throw new UnsupportedFlavorException(df);
        }
        public boolean isDataFlavorSupported(DataFlavor df) {
            return (df.equals(localObjectFlavor));
        }
        public DataFlavor[] getTransferDataFlavors() {
            return supportedFlavors;
        }
    }
    
    
}

