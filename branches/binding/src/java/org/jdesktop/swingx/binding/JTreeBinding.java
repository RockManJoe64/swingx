/*
 * JTreeBinding.java
 *
 * Created on September 10, 2005, 9:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.jdesktop.binding.DataModel;
import org.jdesktop.binding.SelectionModel;
import org.jdesktop.binding.event.ValuesChangedEvent;
import org.jdesktop.binding.event.DataModelListener;
import org.jdesktop.binding.event.ModelChangedEvent;
import org.jdesktop.binding.impl.DefaultSelectionModel;
import org.jdesktop.binding.impl.beans.ContentType;
import org.jdesktop.swingx.tree.TreeNodeExt;

/**
 * Binds a set of heirarchical data models to a JTree
 * component. This binding installs a custom TreeModel and SelectionModelListener
 * on the JTree.
 *
 * <p>DataModels are a Tree of Tables. This design facilitates mixing
 * scalar, vector, tabular and heirarchical views  on the same DataModel instances.
 * The JTreeBinding is given a "root" DataModel in its <code>setDataModel</code> method.
 * For each DataModel.Row in the DataModel, a different child node is modeled. Each of those
 * nodes has a child node corrosponding to the child DataModel at each column within the Row.</p>
 *
 * <p>For example, if I have a DataModel "Customer" which has the following data:
 * <pre>
 *   firstName   |  age   | address
 *   -------------------------------
 *   Adam        |  27    | address@cd23fa
 *   Laura       |  42    | address@1a7d4a
 * </pre>
 *
 * Then a tree representation would look like the following:
 * <pre>
 *   Customer@af23da (Customer.toString() produced this)
 *       Adam
 *       27
 *       address@cd23fa (Address.toString() produced this)
 *           123 Someplace St
 *           San Jose
 *           CA
 *   Customer@129a3d
 *       Laura
 *       42
 *       address@1a7d4a
 *           732 Elsewhere Ct
 *           Santa Cruz
 *           CA
 * </pre>
 *
 * <p>There is a constant tension between DataModel providers and UI consumers.
 * DataModel service providers want to expose as much data through the DataModel
 * as possible. ObjectDataModel, for example, includes a synthetic column to expose
 * the results of the "toString()" method of an encapsulated Object. Also, the
 * class of the object is exposed naturally as a JavaBean property, even though it
 * is often an artifact of the data and not related to the data itself.</p>
 * 
 * <p>On the other hand, exposing all of the internals of the Java programming
 * language (such as the properties of the Class object) provides a poor UI in
 * most circumstances. For a JTextField or similar scalar type component, this
 * is not an issue since it binds only to the column in the DataModel it is
 * interested in. The JTable component is a little more difficult because by
 * default it would display the results of the "toString()" and "getClass()"
 * methods in the table. This is obviously undesirable in most cases, so it
 * becomes necessary to provide a list of column names for the table to expose.</p>
 *
 * <p>Likewise, the JTree must provide some way of filtering the DataModel columns
 * to only expose that data that is desireable in the UI. For example, I really wanted
 * the following tree based on the previous example data:
 * <pre>
 *   Customer@af23da
 *       address@cd23fa
 *   Customer@129a3d
 *       address@1a7d4a
 * </pre>
 *
 * Or, with proper TreeCellRenderers installed:
 * <pre>
 *   Adam
 *       123 Someplace St, San Jose CA
 *   Laura
 *       732 Elsewhere Ct, Santa Cruz CA
 * </pre>
 *
 * <p>To achieve this filtering, there are two different sets of XPath expressions which
 * are evaluated. First, there is a Set of XPath expressions called the "includeExpressions".
 * These expressions indicate which nodes should be included. Likewise, there are
 * "excludeExpressions". These expressions indicate which rows should be excluded. These
 * are both inserted into a single list of expressions. They are evaluated from top
 * to bottom.
 * Then the desired Tree will be produced
 *
 * @author Richard
 */
public class JTreeBinding extends SwingModelBinding {
    /**
     * The original tree model
     */
    private TreeModel oldModel;
    /**
     * The SelectionModel for this tree
     */
    private DefaultSelectionModel treeSelectionModel;
    /**
     * A listener on the JTree's TreeSelectionModel
     */
    private TreeSelectionListener listener;
    /**
     * Cache of the (one) selection model
     */
    private List<SelectionModel> selectionModels;
    /**
     * Helps define the tree, by describing which nodes will be included,
     * whether they are leaves or branches, and whether they allow any
     * children.
     */
    private NodeDescriptor descriptor;
    
    /** Creates a new instance of JTreeBinding */
    public JTreeBinding(JTree tree) {
        super(tree);
        treeSelectionModel = new DefaultSelectionModel();
        selectionModels = new ArrayList<SelectionModel>();
        listener = new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                //update the treeSelectionModel with the last path component
                //of each tree path.
                TreePath[] paths = e.getPaths();
                for (TreePath path : paths) {
                    Object obj = path.getLastPathComponent();
                    if (obj instanceof RootTreeNode) {
                        treeSelectionModel.setSelected(new ArrayList<DataModel.Row>(), e.isAddedPath(path));
                    } else {
                        DataModelRowTreeNode node = (DataModelRowTreeNode)path.getLastPathComponent();
                        treeSelectionModel.setSelected(node.row, e.isAddedPath(path));
                    }
                }
                treeSelectionModel.fireSelectionChangedEvent(treeSelectionModel);
            }
        };
        selectionModels.add(treeSelectionModel);
    }
    
    public void setSelectionModelName(String name) {
        treeSelectionModel.setName(name);
    }
    
    public List<SelectionModel> getSelectionModels() {
        return selectionModels;
    }
    
    public void setNodeDescriptor(NodeDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    
    public NodeDescriptor getNodeDescriptor() {
        return descriptor;
    }
    
    protected void doInitialize() {
        JTree tree = (JTree)getComponent();
        oldModel = tree.getModel();
        TreeModel model = new BoundTreeModel(getDataModel());
        tree.setModel(model);
        tree.addTreeSelectionListener(listener);
    }

    public void doRelease() {
        JTree tree = (JTree)getComponent();
        tree.removeTreeSelectionListener(listener);
        tree.setModel(oldModel);
    }

    public void doLoad() {
//        ((DataModelToListModelAdapter)getComponent().getModel()).fireDataChanged();
        //OK, I'm dropping the hammer here. I'm not sure how to be gentle about this,
        //the fact is, the root *may* have changed (or did it? hard to say). So I'm
        //reloading the whole tree. Ya, like I said, I'm dropping the hammer here
        TreeModel model = new BoundTreeModel(getDataModel());
        getComponent().setModel(model);
    }

    public JTree getComponent() {
        return (JTree)super.getComponent();
    }
    
    /**
     */
    private final class BoundTreeModel extends DefaultTreeModel {
        public BoundTreeModel(DataModel dm) {
            super(null);
            if (dm.getRowCount() == 0) {
                setRoot(null);
            } else {
                setRoot(new RootTreeNode(dm));
            }
            setAsksAllowsChildren(true);
        }
    }
    
    private final class RootTreeNode implements TreeNodeExt {

        public RootTreeNode(DataModel model) {
            this.model = model;
            //construct the child nodes
            rebuildChildList();
        }
        
        private void rebuildChildList() {
            children.clear();
            //get the rows from the data model
            for (int i=0; i<model.getRowCount(); i++) {
                children.add(new DataModelRowTreeNode(model.getRow(i), this));
            }
        }
        
        public Object getData() {
            return model.getDomainData();
        }

        public TreeNode getChildAt(int childIndex) {
            return children.get(childIndex);
        }

        public int getChildCount() {
            return children.size();
        }

        public TreeNode getParent() {
            return null;
        }

        public int getIndex(TreeNode node) {
            return children.indexOf(node);
        }

        public boolean getAllowsChildren() {
            return true;
        }

        public boolean isLeaf() {
            return false;
        }

        public Enumeration children() {
            return Collections.enumeration(children);
        }
        
        private DataModel model;
        private List<DataModelRowTreeNode> children = new ArrayList<DataModelRowTreeNode>();
        
    }
    
    private final class DataModelRowTreeNode implements TreeNodeExt {
        private DataModel.Row row;
        
        private List<DataModelRowTreeNode> children;
        
        private TreeNode parent;
//        private DataModelListener listener;
        
        public DataModelRowTreeNode(DataModel.Row row, TreeNode parent) {
            this.row = row;
            this.parent = parent;
//            listener = new DataModelListener() {
//                public void valuesChanged(ValuesChangedEvent evt) {
//                    //need to refresh the tree, somehow...
//                    ((JTree)getComponent()).invalidate();
//                    ((JTree)getComponent()).invalidate();
//                    ((JTree)getComponent()).repaint();
//                }
//                public void modelChanged(ModelChangedEvent mce) {
//                }
//            };
//            row.getDataModel().addDataModelListener(listener);
        }
        
        private void initChildren() {
            if (children == null) {
                children = new ArrayList<DataModelRowTreeNode>();
                for (String col : row.getColumnNames()) {
                    if (row.hasChildModel(col) &&
                            (descriptor !=  null && descriptor.include(row.getValue(col)))) {
                        DataModel child = row.getChildModel(col);
    //                    child.addDataModelListener(listener);
                        for (int i=0; i<child.getRowCount(); i++) {
                            children.add(new DataModelRowTreeNode(child.getRow(i), this));
                        }
                    }
                }
            }
        }
        
        public TreeNode getChildAt(int childIndex) {
            initChildren();
            return children.get(childIndex);
        }

        public int getChildCount() {
            initChildren();
            return children.size();
        }

        public TreeNode getParent() {
            return parent;
        }

        public int getIndex(TreeNode node) {
            return children.indexOf(node);
        }

        public boolean getAllowsChildren() {
            return descriptor == null ? true : descriptor.allowsChildren(row.getDomainData());
        }

        public boolean isLeaf() {
            return descriptor == null ? getChildCount() == 0 : descriptor.isLeaf(row.getDomainData());
        }

        public Enumeration children() {
            initChildren();
            return Collections.enumeration(children);
        }
        
        public String toString() {
            Object data = getData();
            return data == null ? null : data.toString();
        }
        
        public Object getData() {
            return row.getDomainData();
        }
    }
    
    public static void main(String... args) {
        try {
            java.io.File f = new java.io.File("d:\\");
            JTree tree = new JTree();
            JTreeBinding b = new JTreeBinding(tree);
            FileBean file = new FileBean();
            file.f = f;
            b.setDataModel(new org.jdesktop.binding.impl.beans.ObjectDataModel(file));
            b.setNodeDescriptor(new NodeDescriptor() {
                public boolean allowsChildren(Object nodeData) {
                    return nodeData == null ? false : ((FileBean)nodeData).f.isDirectory();
                }

                public boolean isLeaf(Object nodeData) {
                    return nodeData == null ? true : !((FileBean)nodeData).f.isDirectory();
                }

                public boolean include(Object nodeData) {
                    return nodeData instanceof FileBean || nodeData instanceof FileList;
                }
            });
//            RecursiveTreeModel model = (RecursiveTreeModel)tree.getModel();
//            DataModelTreeNode node = (DataModelTreeNode)model.getRoot();
//            System.out.println(node.getChildCount());
            
            javax.swing.JFrame frame = new javax.swing.JFrame("Test Tree Binding");
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.add(new javax.swing.JScrollPane(tree));
            frame.setSize(300, 500);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    @ContentType(value=FileBean.class)
    public static final class FileList<T> extends ArrayList<T> {
    }
    
    public static final class FileBean {
        private java.io.File f;
        private FileList<FileBean> blar;
        
        public FileList getChildren() {
            if (blar == null) {
                blar = new FileList<FileBean>();                
                for (java.io.File file : f.listFiles()) {
                    FileBean kid = new FileBean();
                    kid.f = file;
                    blar.add(kid);
                }
            }
            return blar;
        }
        
        public String toString() {
            return f.getName();
        }
    }
}
