/*
 * PainterTreeModel.java
 *
 * Created on July 17, 2006, 5:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painterset;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.editors.ImageEditor;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.effects.AbstractAreaEffect;
import org.jdesktop.swingx.painter.ShapePainter;

/**
 *
 * @author joshy
 */
public class PainterTreeModel implements TreeModel {
    
    private CompoundPainter root;
    
    public PainterTreeModel(CompoundPainter cp) {
        super();
        this.root = cp;
    }
    
    private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
    
    protected void fireNodesRemoved(TreePath path, int index) {
        TreePath parentPath = path.getParentPath();
        int[] childIndices = new int[1];
        childIndices[0] = index;
        Object[] children = new Object[1];
        children[0] = path.getLastPathComponent();
        TreeModelEvent evt = new TreeModelEvent(this, parentPath, childIndices, children);
        for(TreeModelListener l : listeners) {
            l.treeNodesRemoved(evt);
        }
    }
    
    public void fireStructureChanged() {
        TreeModelEvent evt = new TreeModelEvent(this,new TreePath(getRoot()));
        for(TreeModelListener l : listeners) {
            l.treeStructureChanged(evt);
        }
    }
    
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }
    
    public Object getChild(Object parent, int index) {
        if (parent instanceof CompoundPainter) {
            CompoundPainter cp = (CompoundPainter) parent;
            return cp.getPainters()[index];
        }
        if (parent instanceof ShapePainter) {
            ShapePainter ptr = (ShapePainter)parent;
            if(index >= ptr.getFilters().length) {
                return ptr.getPathEffects();
            }
        }
        
        if (parent instanceof AbstractPainter) {
            return ((AbstractPainter)parent).getFilters()[index];
        }
        
        return null;
    }
    
    public int getChildCount(Object parent) {
        
        if (parent instanceof CompoundPainter) {
            CompoundPainter cp = (CompoundPainter) parent;
            return cp.getPainters().length;
        }
        
        if (parent instanceof ShapePainter) {
            ShapePainter ptr = (ShapePainter)parent;
            if(ptr.getPathEffects() != null) {
                return ptr.getFilters().length + 1;
            } else {
                return ptr.getFilters().length;
            }
        }
        
        if (parent instanceof AbstractPainter) {
            return ((AbstractPainter)parent).getFilters().length;
        }
        
        if (parent instanceof BufferedImageOp) {
            return 0;
        }
        
        return -1;
    }
    
    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof CompoundPainter) {
            CompoundPainter cp = (CompoundPainter) parent;
            return Arrays.asList(cp.getPainters()).indexOf(child);
        }
        if (parent instanceof AbstractPainter) {
            AbstractPainter ap = (AbstractPainter) parent;
            return Arrays.asList(ap.getFilters()).indexOf(child);
        }
        
        if (parent instanceof BufferedImageOp) {
            return 0;
        }
        return -1;
    }
    
    public Object getRoot() {
        return root;
    }
    
    public boolean isLeaf(Object node) {
        if(node instanceof CompoundPainter) {
            return false;
        }
        
        if(node instanceof ShapePainter) {
            if(((ShapePainter)node).getPathEffects() != null) {
                return false;
            }
        }
        
        if(node instanceof AbstractPainter) {
            if(((AbstractPainter)node).getFilters().length > 0) {
                return false;
            }
        }
        
        if(node instanceof BufferedImageOp) {
            return true;
        }
        
        return true;
    }
    
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }
    
    public void valueForPathChanged(TreePath path, Object newValue) {
        //u.p("value for path changed: " + path + " " + newValue);
    }
    
    public void insertNodeInto(Object droppedNode, Object newParent, int index) {
        CompoundPainter comp = (CompoundPainter)newParent;
        Painter ptr = (Painter)droppedNode;
        CompoundPainter oldParent = findParent(droppedNode);
        if(oldParent != null) {
            removePainter(oldParent, ptr);
        }
        addPainter(comp, ptr, index);
        fireStructureChanged();
    }
    
    public void addPainter(CompoundPainter comp, Painter painter, int index) {
        Painter[] painters = comp.getPainters();
        List<Painter> pts = Arrays.asList(painters);
        pts = new ArrayList<Painter>(pts);
        pts.add(index,painter);
        comp.setPainters(pts.toArray(new Painter[0]));
        fireStructureChanged();
    }
    
    public void addEffect(AbstractPainter painter, BufferedImageOp effect) {
        BufferedImageOp[] effects = painter.getFilters();
        List<BufferedImageOp> pts = Arrays.asList(effects);
        pts = new ArrayList<BufferedImageOp>(pts);
        pts.add(effect);
        painter.setFilters(pts.toArray(new BufferedImageOp[0]));
        fireStructureChanged();
    }
    
    public void addShapeEffect(ShapePainter shapePainter, AbstractAreaEffect eff) {
        shapePainter.setPathEffects(eff);
        fireStructureChanged();
    }
    
    public void removePainter(CompoundPainter comp, Painter painter) {
        TreePath path = getTreePath(painter);
        Painter[] painters = comp.getPainters();
        List<Painter> pts = Arrays.asList(painters);
        pts = new ArrayList<Painter>(pts);
        int index = pts.indexOf(painter);
        pts.remove(painter);
        comp.setPainters(pts.toArray(new Painter[0]));
        fireNodesRemoved(path,index);
        //fireStructureChanged();
    }
    
    public void removeEffect(AbstractPainter painter, BufferedImageOp effect) {
        BufferedImageOp[] effects = painter.getFilters();
        List<BufferedImageOp> pts = Arrays.asList(effects);
        pts = new ArrayList<BufferedImageOp>(pts);
        pts.remove(effect);
        painter.setFilters(pts.toArray(new BufferedImageOp[0]));
        fireStructureChanged();
    }
    
    
    private CompoundPainter findParent(Object droppedNode) {
        return findParent((CompoundPainter)getRoot(), (Painter)droppedNode);
    }
    
    private CompoundPainter findParent(CompoundPainter parent, Painter painter) {
        Painter[] pts = parent.getPainters();
        //u.p("parent = " + parent);
        for(int i=0; i<pts.length; i++) {
            if(pts[i] == painter) {
                //u.p("found as child");
                return parent;
            }
        }
        for(int i=0; i<pts.length; i++) {
            //u.p("looking at child: " + pts[i]);
            if(pts[i] instanceof CompoundPainter) {
                CompoundPainter found = findParent((CompoundPainter)pts[i],painter);
                if(found != null) {
                    return found;
                }
            }
        }
        return null;
    }
    
    private TreePath getTreePath(Painter node) {
        if(node == getRoot()) {
            return new TreePath(node);
        }
        Painter parent = findParent(node);
        TreePath parentPath = getTreePath(parent);
        TreePath newPath = parentPath.pathByAddingChild(node);
        return newPath;
    }
    
    
}
