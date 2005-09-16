/*
 * JTableBinding.java
 *
 * Created on August 12, 2005, 10:34 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.binding;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.jdesktop.binding.Binding;
import org.jdesktop.binding.DataModel;
import org.jdesktop.binding.metadata.MetaData;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;

/**
 * A binding implementation that binds a JTable to a DataModel.
 *
 * @author rbair
 */
public class JTableBinding extends Binding {
    private TableModel oldModel;
    private ListSelectionListener selectionListener;
    private AbstractTableModel model;
    private String[] columnNames;
    
    /** Creates a new instance of JTableBinding */
    public JTableBinding(JTable table) {
        super(table);
    }

    public void setColumnNames(String[] names) {
        this.columnNames = names;
    }
    
    public String[] getColumnNames() {
        return columnNames;
    }
    
    protected void initialize() {
        JTable table = (JTable)super.getComponent();
        oldModel = table.getModel();
        model = new DataModelToTableModelAdapter(getDataModel());
        table.setModel(model);
        //create the column model adapter
        table.setColumnModel(new TableColumnModelAdapter(getDataModel(), columnNames));
        //create a selection binding
        selectionListener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int index = ((JTable)getComponent()).getSelectionModel().getMinSelectionIndex(); //TODO doesn't do multi selection!!
//                    getDataModel().getSelectionModel().setSelectionIndices(new int[]{index});
                }
            }
        };
        table.getSelectionModel().addListSelectionListener(selectionListener);
    }
    
    public void release() {
        JTable table = (JTable)super.getComponent();
        if (oldModel != null) {
            table.setModel(oldModel);
        }
//        if (oldColumnModel != null) {
//            table.setColumnModel(oldColumnModel);
//        }
        table.getSelectionModel().removeListSelectionListener(selectionListener);
    }

    public boolean loadDataModelFromComponent() {
        return true;
    }

    public boolean loadComponentFromDataModel() {
        model.fireTableDataChanged();
        return true;
    }

    private static final class TableColumnModelAdapter extends DefaultTableColumnModelExt {
        public TableColumnModelAdapter(DataModel dm, String[] fieldNames) {
            if (fieldNames == null) {
                fieldNames = dm.getFieldNames();
            }
            String[] dmFieldNames = dm.getFieldNames();
            for (String fieldName : fieldNames) {
                int index = 0;
                for (int i=0; i<dmFieldNames.length; i++) {
                    if (dmFieldNames[i].equals(fieldName)) {
                        index = i;
                        break;
                    }
                }
                MetaData md = dm.getMetaData(fieldName);
                if (md != null) {
                    TableColumnExt tc = new TableColumnExt(index, md.getDisplayWidth());
                    tc.setHeaderValue(md.getLabel());
                    tc.setIdentifier(md.getName());
                    tc.setTitle(md.getLabel());
                    tc.setEditable(!md.isReadOnly());
                    super.addColumn(tc);
                } else {
                    //TODO need a debug log statement
                    System.err.println("Couldn't get meta data for field named " + fieldName);
                }
            }
        }
    }

    public static final class DataModelToTableModelAdapter extends AbstractTableModel 
        /*implements MetaDataModel*/ {

        protected DataModel dm;

        public DataModelToTableModelAdapter(DataModel dm) {
            this(dm, null);
        }

        public DataModelToTableModelAdapter(DataModel dm, String[] visibleFieldNames) {
            this.dm = dm;
            installDataModelListener();
        }

        public Class getColumnClass(int columnIndex) {
            return getMetaData(columnIndex).getElementClass();
        }

        public String getColumnName(int column) {
            //its possible that the meta data hasn't shown up yet. In this
            //case, use the field name until the meta data arrives
            // JW: when would that be the case?
    //        MetaData md = dm.getMetaData(fieldNames[column]);
    //        return md == null ? fieldNames[column] : md.getLabel();
            MetaData md = getMetaData(column);
            return md.getLabel();
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            MetaData md = getMetaData(columnIndex);
            return !md.isReadOnly();
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            dm.setValue(rowIndex, getMetaData(columnIndex).getName(), aValue);
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            return dm.getRecordCount();
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            return dm.getMetaData().length;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int rowIndex, int columnIndex) {
            return dm.getValue(rowIndex, getMetaData(columnIndex).getName());
        }

        // --------------------------- init listener

        private void installDataModelListener() {
    //        dm.addTableDataListener(new TableDataListener() {
    //            /**
    //             * <p>Process an event indicating that the DataModel has changed in
    //             * a way outside the bounds of the other event methods.</p>
    //             *
    //             * @param provider The DataModel that has changed
    //             */
    //            public void providerChanged(DataModel provider) {
    //                fireTableStructureChanged();
    //            }
    //            public void valueChanged(DataModel provider, DataKey dataKey, Object oldValue, Object newValue){
    //                fireTableDataChanged();
    //            }
    //            public void valueChanged(TabularDataModel provider, int row, DataKey dataKey, Object oldValue, Object newValue){
    //                fireTableDataChanged();
    //            }
    //
    //            /**
    //             * <p>A new row has been added to the {@link TabularDataModel}.</p>
    //             *
    //             * @param provider <code>TabularDataModel</code> that added an row
    //             * @param row The newly added row
    //             */
    //            public void rowAdded(TabularDataModel provider, int row){}
    //
    //            /**
    //             * <p>An row has been removed from the {@link TabularDataModel}.</p>
    //             *
    //             * @param provider <code>TabularDataModel</code> that removed an row
    //             * @param row The recently removed row
    //             */
    //            public void rowRemoved(TabularDataModel provider, int row){
    //            }
    //        });
//                TabularValueChangeListener l = new TabularValueChangeListener() {
//
//                    public void tabularValueChanged(TabularValueChangeEvent e) {
//                        fireTableDataChanged();
//                    }
//                };
//                dm.addTabularValueChangeListener(l);
        }

        private MetaData getMetaData(int index) {
            return dm.getMetaData()[index];
        }
    }
}
