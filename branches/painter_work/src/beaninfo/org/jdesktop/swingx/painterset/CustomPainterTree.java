package org.jdesktop.swingx.painterset;

import org.jdesktop.swingx.painterset.dndtree.DnDTree;


class CustomPainterTree extends DnDTree {
    public void insertNodeInto(Object droppedNode, Object dropNode, int index) {
        ((PainterTreeModel) getModel()).insertNodeInto(
                droppedNode, dropNode, index);
    }
}