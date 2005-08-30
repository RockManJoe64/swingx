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

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.binding.Binding;
import org.jdesktop.binding.DataModel;

/**
 *
 * @author rbair
 */
public class JListBinding extends Binding {
    private ListModel oldModel;
    private ListSelectionListener selectionListener;
    private DataModelToListModelAdapter model;
    
    /** Creates a new instance of JListBinding */
    public JListBinding(JList list) {
        super(list);
    }
    
    protected void initialize() {
        JList list = (JList)super.getComponent();
        oldModel = list.getModel();
        model = new DataModelToListModelAdapter(getDataModel());
        list.setModel(model);

        //create a selection binding
        selectionListener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int[] indices = ((JList)getComponent()).getSelectedIndices();
                    getDataModel().getSelectionModel().setSelectionIndices(indices);
                }
            }
        };
        list.addListSelectionListener(selectionListener);
//        new ListSelectionBinding(sm, list.getSelectionModel());
    }

    public void release() {
        JList list = (JList)super.getComponent();
        list.setModel(oldModel);
        list.removeListSelectionListener(selectionListener);
    }

    public boolean loadComponentFromDataModel() {
        return true;
    }
    
    public boolean loadDataModelFromComponent() {
        return true;
    }
    
    public static final class DataModelToListModelAdapter extends AbstractListModel {
        private DataModel tabModel = null;

        public DataModelToListModelAdapter(DataModel model /*, String fieldName*/) {
            this.tabModel = model;
    //        this.fieldName = fieldName;
            installDataModelListener();
        }

	protected void installDataModelListener() {
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
            fireContentsChanged(this, 0, tabModel.getRecordCount() - 1);
        }
        
	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
            return tabModel.getRecordCount();
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
            return tabModel.getValue(index, "OrderID");
	}
    }
}
