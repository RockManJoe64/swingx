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
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import org.jdesktop.binding.Binding;
import org.jdesktop.binding.DataModel;
import org.jdesktop.swingx.tree.TreeNodeExt;

/**
 *
 * @author Richard
 */
public class JTreeBinding extends Binding {
    private TreeModel oldModel;
    private String childName; //name of the child datamodel to recurse on when constructing the tree (should be array??)
    private String displayName;
    
    /** Creates a new instance of JTreeBinding */
    public JTreeBinding(JTree tree) {
        super(tree);
    }
    
    public JTreeBinding(JTree tree, String childName, String displayName) {
        super(tree);
        this.childName = childName;
        this.displayName = displayName;
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

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    protected void initialize() {
        JTree tree = (JTree)getComponent();
        oldModel = tree.getModel();
        TreeModel model = new RecursiveTreeModel(getDataModel());
        tree.setModel(model);
    }

    public void release() {
        JTree tree = (JTree)getComponent();
        tree.setModel(oldModel);
    }

    public boolean loadComponentFromDataModel() {
        return true;
    }

    public boolean loadDataModelFromComponent() {
        return false; //shouldn't be called!
    }
    
    private final class RecursiveTreeModel extends DefaultTreeModel {
        private DataModel root;
        
        public RecursiveTreeModel(DataModel dm) {
            super(null);
            root = dm;
            setRoot(new DataModelTreeNode(root, 0));
            setAsksAllowsChildren(true);
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
            child = dm.getChild(childName, index);
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
            return child.getRecordCount();
        }

        public TreeNode getParent() {
            return parent;
        }

        public int getIndex(TreeNode node) {
            System.out.println("I was called??");
            return -1;
        }

        public boolean getAllowsChildren() {
            return (Boolean)model.getValue(index, "directory");
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
            Object obj = model.getValue(index, displayName);
            return obj == null ? "" : obj.toString();
        }
        
        public Object getData() {
            return model.getRowData(index);
        }
    }
}
