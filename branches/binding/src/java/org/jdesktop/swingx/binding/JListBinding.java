/*
 * JListBinding.java
 *
 * Created on August 12, 2005, 10:50 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.binding;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.binding.DataModel;
import org.jdesktop.binding.SelectionModel;
import org.jdesktop.binding.event.DataModelListener;
import org.jdesktop.binding.event.ModelChangedEvent;
import org.jdesktop.binding.event.ValuesChangedEvent;
import org.jdesktop.binding.impl.DefaultSelectionModel;

/**
 *
 * @author rbair
 */
public class JListBinding extends SwingModelBinding {
    private ListModel oldModel;
    private ListSelectionListener selectionListener;
    private DataModelToListModelAdapter model;
    private String displayName;
    private DefaultSelectionModel listSelectionModel;
    private List<SelectionModel> selectionModels;
    
    /** Creates a new instance of JListBinding */
    public JListBinding(JList list) {
        this(list, null);
    }
    
    public JListBinding(JList list, String displayName) {
        super(list);
        this.displayName = displayName;
        listSelectionModel = new DefaultSelectionModel();
        selectionModels = new ArrayList<SelectionModel>(1);
        selectionModels.add(listSelectionModel);
    }
    
    public void setDisplayName(String name) {
        this.displayName = name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public SelectionModel getListSelectionModel() {
        return listSelectionModel;
    }
    
    public List<SelectionModel> getSelectionModels() {
        return selectionModels;
    }
    
    public void setSelectionModelName(String name) {
        listSelectionModel.setName(name);
    }
    
    protected void doInitialize() {
        JList list = (JList)super.getComponent();
        oldModel = list.getModel();
        model = new DataModelToListModelAdapter(getDataModel());
        list.setModel(model);

        //create a selection binding
        selectionListener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int[] indices = ((JList)getComponent()).getSelectedIndices();
                    List<DataModel.Row> rows = new ArrayList<DataModel.Row>(indices.length);
                    for (int i=0; i<indices.length; i++) {
                        rows.add(getDataModel().getRow(indices[i]));
                    }
                    listSelectionModel.setSelection(rows);
                }
            }
        };
        list.addListSelectionListener(selectionListener);
//        new ListSelectionBinding(sm, list.getSelectionModel());
    }

    protected void doRelease() {
        JList list = (JList)super.getComponent();
        list.setModel(oldModel);
        list.removeListSelectionListener(selectionListener);
    }

    public void doSave() {
    }
    
    public void doLoad() {
    }
    
    public final class DataModelToListModelAdapter extends AbstractListModel {
        private DataModel tabModel = null;

        public DataModelToListModelAdapter(DataModel model /*, String fieldName*/) {
            this.tabModel = model;
    //        this.fieldName = fieldName;
            installDataModelListener();
        }

	protected void installDataModelListener() {
            tabModel.addDataModelListener(new DataModelListener() {
                public void modelChanged(ModelChangedEvent mce) {
                    JList list = (JList)getComponent();
                    list.firePropertyChange("fixedCellHeight", -20, list.getFixedCellHeight());
                }
                public void valuesChanged(ValuesChangedEvent evt) {
                    JList list = (JList)getComponent();
                    list.firePropertyChange("fixedCellHeight", -20, list.getFixedCellHeight());
                }
            });
//        tabModel.addTableDataListener(new TableDataListener() {
//            /**
//             * <p>Process an event indicating that the DataModel has changed in
//             * a way outside the bounds of the other event methods.</p>
//             *
//             * @param provider The DataModel that has changed
//             */
//            public void providerChanged(DataModel provider) {
//                fireContentsChanged(DataModelToListModelAdapter.this, 0, tabModel.getRowCount() - 1);
//            }
//            public void valueChanged(DataModel provider, DataKey dataKey, Object oldValue, Object newValue){
//                fireContentsChanged(DataModelToListModelAdapter.this, 0, tabModel.getRowCount() - 1);
//            }
//            public void valueChanged(TabularDataModel provider, int row, DataKey dataKey, Object oldValue, Object newValue){
//                fireContentsChanged(DataModelToListModelAdapter.this, 0, tabModel.getRowCount() - 1);
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
//            TabularValueChangeListener l = new TabularValueChangeListener() {
//                public void tabularValueChanged(TabularValueChangeEvent e) {
//                    fireContentsChanged(DataModelToListModelAdapter.this, 0, tabModel.getRecordCount() - 1);
//                }
//            };
//            tabModel.addTabularValueChangeListener(l);
	}
	
        public void fireDataChanged() {
            fireContentsChanged(this, 0, tabModel.getRowCount() - 1);
        }
        
	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
            return tabModel.getRowCount();
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
            if (displayName == null) {
                return tabModel.getRow(index).getDomainData();
            } else {
                return tabModel.getRow(index).getValue(displayName);
            }
	}
    }
}
