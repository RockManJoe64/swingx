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
import java.util.List;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.jdesktop.binding.DataModel;
import org.jdesktop.binding.SelectionModel;
import org.jdesktop.binding.impl.DefaultSelectionModel;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.binding.JTableBinding.TableColumnModelAdapter;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.tree.TreeNodeExt;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
/**
 * Essentially the same as the tree binding, only also supports columns
 * @author Richard
 */
public class JXTreeTableBinding extends JXTableBinding {
    /**
     * The original tree model
     */
    private TreeTableModel oldModel;
    private TableColumnModel oldColModel;
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
    
    public JXTreeTableBinding(JXTreeTable tree) {
        super(tree);
    }
    
    public JXTreeTableBinding(JXTreeTable tree, String childName, String displayName, String[] fieldNames) {
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
        JXTreeTable tree = getComponent();
        oldModel = tree.getTreeTableModel();
        oldColModel = JTableBinding.copyTableColumnModel(tree.getColumnModel());
        
        //create the column model adapter
        TableColumnModelAdapter colModel = new TableColumnModelAdapter(getDataModel(), oldColModel);
        
        TreeTableModel model = new BoundTreeTableModel(getDataModel(), colModel);
        tree.setTreeTableModel(model);
        tree.addTreeSelectionListener(listener);
        
        tree.setColumnModel(colModel); //the column model MUST be specified after
                                        //the model or else the JTable will throw
                                        //away the model and create a new one
                                        //based on the TableModel
    }

    public void doRelease() {
        JXTreeTable tree = getComponent();
        tree.removeTreeSelectionListener(listener);
        tree.setTreeTableModel(oldModel);
        if (oldColModel != null) {
            tree.setColumnModel(oldColModel);
        }
    }

    
    public JXTreeTable getComponent() {
        return (JXTreeTable)super.getComponent();
    }

    /**
     */
    private final class BoundTreeTableModel extends DefaultTreeTableModel {
        protected DataModel dm;
        protected TableColumnModelAdapter columnModel;
        /**
         * A cache of the columnNames, according to model indices
         */
        protected String[] columnNames;

        public BoundTreeTableModel(DataModel dm, TableColumnModelAdapter columnModel) {
            super(null);
            this.dm = dm;
            this.columnModel = columnModel;
            columnNames = new String[columnModel.getColumnCount()];
            for (int i=0; i<columnModel.getColumnCount(); i++) {
                int modelIndex = columnModel.getColumn(i).getModelIndex();
                Object id = columnModel.getColumn(i).getIdentifier();
                columnNames[modelIndex] = id.toString();
            }
            
            if (dm.getRowCount() == 0) {
                setRoot(null);
            } else {
                setRoot(new RootTreeNode(dm));
            }
            setAsksAllowsChildren(true);
        }

        public Class getColumnClass(int columnIndex) {
            return ((TableColumnExt)columnModel.getColumn(columnIndex)).getColumnClass();
        }

        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return !dm.getRow(rowIndex).isReadOnly(getColumnName(columnIndex));
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            dm.getRow(rowIndex).setValue(getColumnName(columnIndex), aValue);
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            return dm.getRowCount();
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            return columnModel.getColumnCount();
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int rowIndex, int columnIndex) {
            try {
                return dm.getRow(rowIndex).getValue(getColumnName(columnIndex));
            } catch (Throwable th) {
                //hmmm log and return null
//                LOG.log(Level.WARNING, "Failed to get data from a bound column", th);
                return null;
            }
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
                for (String col : row.getDataModel().getColumnNames()) {
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
    
}
