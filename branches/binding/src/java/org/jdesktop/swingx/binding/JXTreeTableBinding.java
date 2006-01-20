/*
 * JTreeBinding.java
 *
 * Created on September 10, 2005, 9:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.tree.TreeNode;
import org.jdesktop.binding.DataModel;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.tree.TreeNodeExt;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
/**
 * Essentially the same as the tree binding, only also supports columns
 * @author Richard
 */
public class JXTreeTableBinding extends SwingModelBinding {
    private TreeTableModel oldModel;
    private String childName; //name of the child datamodel to recurse on when constructing the tree (should be array??)
    private String[] fieldNames;
    private String displayName;
    
    public JXTreeTableBinding(JXTreeTable tree) {
        super(tree);
    }
    
    public JXTreeTableBinding(JXTreeTable tree, String childName, String displayName, String[] fieldNames) {
        super(tree);
        this.childName = childName;
        this.displayName = displayName;
        this.fieldNames = fieldNames;
    }
    
    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String name) {
        this.displayName = name;
    }
    
    public String[] getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
    }

    protected void doInitialize() {
        JXTreeTable tree = getComponent();
        oldModel = tree.getTreeTableModel();
        TreeTableModel model = new RecursiveTreeTableModel(getDataModel());
        tree.setTreeTableModel(model);
    }

    protected void doRelease() {
        JXTreeTable tree = getComponent();
        tree.setTreeTableModel(oldModel);
    }

    public void doLoad() {
    }

    public void doSave() {
    }
    
    private final class RecursiveTreeTableModel extends DefaultTreeTableModel {
        private DataModel root;
        
        public RecursiveTreeTableModel(DataModel dm) {
            super(null);
            root = dm;
            setRoot(new DataModelTreeNode(root, 0));
            setAsksAllowsChildren(true);
        }

        public Object getValueAt(Object node, int column) {
            DataModelTreeNode n = (DataModelTreeNode)node;
            return n.model.getRow(n.index).getValue(fieldNames[column]);
        }

        public String getColumnName(int column) {
            return fieldNames[column];
        }

        public int getColumnCount() {
            return fieldNames.length;
        }
        
    }
    
    private final class DataModelTreeNode implements TreeNodeExt {
        private DataModel model;
        private DataModel child;
        private Map<Integer,TreeNode> children = new HashMap<Integer,TreeNode>();
        private int index;
        private TreeNode parent;
        
        public DataModelTreeNode(DataModel dm, int index) {
            this.model = dm;
            this.index = index;
            child = dm.getRow(index).getChildModel(childName);
        }
        
        protected DataModelTreeNode(DataModel dm, int index, TreeNode parent) {
            this.model = dm;
            this.parent = parent;
            this.index = index;
        }
        
        public TreeNode getChildAt(int childIndex) {
            TreeNode kid = children.get(childIndex);
            if (kid == null) {
                kid = new DataModelTreeNode(child, childIndex);
                children.put(childIndex, kid);
            }
            return kid;
        }

        public int getChildCount() {
            return child.getRowCount();
        }

        public TreeNode getParent() {
            return parent;
        }

        public int getIndex(TreeNode node) {
            System.out.println("I was called??");
            return -1;
        }

        public boolean getAllowsChildren() {
            return (Boolean)model.getRow(index).getValue("directory");
        }

        public boolean isLeaf() {
            return getChildCount() == 0;
        }

        public Enumeration children() {
            return new Enumeration() {
                int currentIndex;
                public Object nextElement() {
                    return getChildAt(currentIndex++);
                }
                public boolean hasMoreElements() {
                    return currentIndex < getChildCount() -1;
                }
            };
        }
        
        public String toString() {
            Object obj = model.getRow(index).getValue(displayName);
            return obj == null ? "" : obj.toString();
        }
        
        public Object getData() {
            return model.getRow(index).getDomainData();
        }
    }
    
    public JXTreeTable getComponent() {
        return (JXTreeTable)super.getComponent();
    }
}
