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
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.jdesktop.binding.DataModel;
import org.jdesktop.binding.DisplayHints;
import org.jdesktop.binding.SelectionModel;
import org.jdesktop.binding.impl.DefaultSelectionModel;
import org.jdesktop.dataset.DataRow;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;

/**
 * A binding implementation that binds a JTable to a DataModel.
 *
 * @author rbair
 */
public class JTableBinding extends SwingModelBinding {
    private static final Logger LOG = Logger.getLogger(JTableBinding.class.getName());
    private TableModel oldModel;
    private TableColumnModel oldColModel;
    
    private ListSelectionListener selectionListener;
    private DataModelToTableModelAdapter model;
    protected DefaultSelectionModel tableSelectionModel;
    private List<SelectionModel> selectionModels;
    
    /** Creates a new instance of JTableBinding */
    public JTableBinding(JTable table) {
        super(table);
        tableSelectionModel = new DefaultSelectionModel();
        tableSelectionModel.setName("selected");
        selectionModels = new ArrayList<SelectionModel>(1);
        selectionModels.add(tableSelectionModel);
    }
    
    public SelectionModel getTableSelectionModel() {
        return tableSelectionModel;
    }
    
    public List<SelectionModel> getSelectionModels() {
        return selectionModels;
    }

    public void setSelectionModelName(String name) {
        tableSelectionModel.setName(name);
    }
    
    protected void doInitialize() {
        JTable table = (JTable)super.getComponent();
        oldModel = table.getModel();
        oldColModel = copyTableColumnModel(table.getColumnModel());
        
        //create the column model adapter
        TableColumnModelAdapter colModel = new TableColumnModelAdapter(getDataModel(), oldColModel);
        
        model = new DataModelToTableModelAdapter(getDataModel(), colModel);
        table.setModel(model);
        table.setColumnModel(colModel); //the column model MUST be specified after
                                        //the model or else the JTable will throw
                                        //away the model and create a new one
                                        //based on the TableModel

        //create a selection binding
        selectionListener = createSelectionListener();
        table.getSelectionModel().addListSelectionListener(selectionListener);
    }
    
    protected ListSelectionListener createSelectionListener() {
        return new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int[] indices = ((JTable)getComponent()).getSelectedRows();
                    List<DataModel.Row> rows = new ArrayList<DataModel.Row>();
                    for (int i=0; i<indices.length; i++) {
                        rows.add(getDataModel().getRow(indices[i]));
                    }
                    tableSelectionModel.setSelection(rows);
                }
            }
        };
    }
    
    public void doRelease() {
        JTable table = (JTable)super.getComponent();
        if (oldModel != null) {
            table.setModel(oldModel);
        }
        if (oldColModel != null) {
            table.setColumnModel(oldColModel);
        }
        table.getSelectionModel().removeListSelectionListener(selectionListener);
    }

    public void doSave() {
    }

    public void doLoad() {
        model.fireTableDataChanged();
    }

    public Object getDomainData(int rowIndex) {
        return model.getDomainData(rowIndex);
    }
    
    static final class TableColumnModelAdapter extends DefaultTableColumnModelExt {
        /**
         * Uses the given ColumnModel as the basis for the new column model. If
         * the oldColModel is null, or has no columns, then the dm is used as the
         * basis for the column model. Otherwise, the given column model is used
         * as the basis for the new column model. Any fields that are in the
         * table column model but NOT in the data model are still included.
         *
         * The column identifier is used to match against the column names in the
         * DataModel.
         */
        public TableColumnModelAdapter(DataModel dm, TableColumnModel colModel) {
            if (colModel != null && colModel.getColumnCount() > 0) {
                //grab and store the data model Column names. We'll be iterating
                //over these to discover which columns in the colModel need to
                //be included and bound in the new column model
                String[] dmColNames = dm.getColumnNames();
                for (int i=0; i<colModel.getColumnCount(); i++) {
                    TableColumn c = colModel.getColumn(i);
                    
                    //look for the column in the DataModel that matches the
                    //identifier of the TableColumn c. If one is found, then
                    //bind that column in the new TableColumnModel. If one is
                    //not found (denoted by index == -1), still include it,
                    //but it won't be "bound" to anything in the DataModel
                    int index = -1;
                    //the column name in the DataModel that this TableColumn is
                    //based on
                    String dmColName = null;
                    for (int j=0; j<dmColNames.length; j++) {
                        Object id = c.getIdentifier();
                        if (id != null && dmColNames[j].equals(id.toString())) {
                            index = j;
                            dmColName = id.toString();
                            break;
                        }
                    }
                    
                    TableColumnExt newColumn = new TableColumnExt(i);
                    
                    //grab the display hints for this column in the DataModel
                    Map<Object,Object> displayHints = dm.getDisplayHints(dmColName);

                    //first, get the width. If the width is
                    //still the default size of 75 OR its pref width,
                    //then fetch the display hints
                    //don't change the width if it has been set by the user
                    //(that is, it isn't 75 or its prefWidth)
                    int width = c.getWidth();
                    int prefWidth = c.getPreferredWidth();
                    if (width == 75 || prefWidth == width && index >= 0) {
                        Object hint = displayHints.get(DisplayHints.DISPLAY_WIDTH);
                        width = hint instanceof Number ? ((Number)hint).intValue() : width;
                        prefWidth = width;
                    }

                    //copy attributes (such as the editor and renderer) over
                    //from the old column model to the new one
                    newColumn.setWidth(width);
                    newColumn.setCellRenderer(c.getCellRenderer());
                    newColumn.setCellEditor(c.getCellEditor());
                    newColumn.setPreferredWidth(prefWidth);
                    newColumn.setResizable(c.getResizable());
                    newColumn.setMinWidth(c.getMinWidth());
                    newColumn.setMaxWidth(c.getMaxWidth());
                    newColumn.setHeaderRenderer(c.getHeaderRenderer());
                    for (PropertyChangeListener l : c.getPropertyChangeListeners()) {
                        newColumn.addPropertyChangeListener(l);
                    }

                    Object headerValue = c.getHeaderValue();
                    //only replace the header value if the header value is null
                    //(and then attempt to use a display hint)
                    if (headerValue == null && index >= 0) {
                        Object hint = displayHints.get(DisplayHints.LABEL);
                        hint = hint == null ? dmColName : hint;
                    }
                    newColumn.setHeaderValue(headerValue);
                    newColumn.setIdentifier(c.getIdentifier());
                    
                    //map over TableColumnExt properties
                    if (c instanceof TableColumnExt) {
                        TableColumnExt oldColumn = (TableColumnExt)c;
                        newColumn.setColumnClass(oldColumn.getColumnClass());
                        //TODO Also, need to resolve
                        //against the rendering hint about editability...
                        newColumn.setEditable(oldColumn.isEditable());
                        newColumn.setPrototypeValue(oldColumn.getPrototypeValue());
                        newColumn.setSorterClass(oldColumn.getSorterClass());
                        newColumn.setVisible(oldColumn.isVisible());
                    }
                    
                    super.addColumn(newColumn);
                }
            } else {
                //generate the column model based entirely on the DataModel
                String[] dmColNames = dm.getColumnNames();
                for (int i=0; i<dmColNames.length; i++) {
                    //grab the display hints for this column in the DataModel
                    Map<Object,Object> displayHints = dm.getDisplayHints(dmColNames[i]);

                    Object hint = displayHints.get(DisplayHints.DISPLAY_WIDTH);
                    int width = hint instanceof Number ? ((Number)hint).intValue() : 75;

                    TableColumnExt newColumn = new TableColumnExt(i, width);
                    newColumn.setPreferredWidth(width);
                    //TODO get from hints...?
//                    newColumn.setResizable(true);
//                    newColumn.setMinWidth(c.getMinWidth());
//                    newColumn.setMaxWidth(c.getMaxWidth());

                    hint = displayHints.get(DisplayHints.LABEL);
                    hint = hint == null ? dmColNames[i] : hint;
                    newColumn.setHeaderValue(hint);
                    newColumn.setIdentifier(dmColNames[i]);
                    //TODO...
//                        newColumn.setEditable();
                    super.addColumn(newColumn);
                }
            }
        }
    }

    public static final class DataModelToTableModelAdapter extends AbstractTableModel {
        protected DataModel dm;
        protected TableColumnModelAdapter columnModel;
        /**
         * A cache of the columnNames, according to model indices
         */
        protected String[] columnNames;

        public DataModelToTableModelAdapter(DataModel dm, TableColumnModelAdapter columnModel) {
            this.dm = dm;
            this.columnModel = columnModel;
            columnNames = new String[columnModel.getColumnCount()];
            for (int i=0; i<columnModel.getColumnCount(); i++) {
                int modelIndex = columnModel.getColumn(i).getModelIndex();
                Object id = columnModel.getColumn(i).getIdentifier();
                columnNames[modelIndex] = id.toString();
            }
            installDataModelListener();
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

        public Object getDomainData() {
            return dm.getDomainData();
        }
        
        public Object getDomainData(int rowIndex) {
            return dm.getRow(rowIndex).getDomainData();
        }
    }
    
    static TableColumnModel copyTableColumnModel(TableColumnModel colModel) {
        TableColumnModelExt newModel = new DefaultTableColumnModelExt();
        for (int i=0; i<colModel.getColumnCount(); i++) {
            TableColumn c = colModel.getColumn(i);

            TableColumnExt newColumn = new TableColumnExt(i);

            //copy attributes (such as the editor and renderer) over
            //from the old column model to the new one
            newColumn.setWidth(c.getWidth());
            newColumn.setCellRenderer(c.getCellRenderer());
            newColumn.setCellEditor(c.getCellEditor());
            newColumn.setPreferredWidth(c.getPreferredWidth());
            newColumn.setResizable(c.getResizable());
            newColumn.setMinWidth(c.getMinWidth());
            newColumn.setMaxWidth(c.getMaxWidth());
            newColumn.setHeaderRenderer(c.getHeaderRenderer());
            for (PropertyChangeListener l : c.getPropertyChangeListeners()) {
                newColumn.addPropertyChangeListener(l);
            }

            newColumn.setHeaderValue(c.getHeaderValue());
            newColumn.setIdentifier(c.getIdentifier());
            //TODO if the newColumn is a TableColumnExt then this
            //info needs to be carried over. Also, need to resolve
            //against the rendering hint about editability...
//                    newColumn.setEditable();
            
            if (c instanceof TableColumnExt) {
                newColumn.setVisible(((TableColumnExt)c).isVisible());
                newColumn.setEditable(((TableColumnExt)c).isEditable());
                newColumn.setPrototypeValue(((TableColumnExt)c).getPrototypeValue());
                newColumn.setSorterClass(((TableColumnExt)c).getSorterClass());
            }
            newModel.addColumn(newColumn);
        }
        return newModel;
    }
}
