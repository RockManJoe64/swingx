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
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataListener;
import org.jdesktop.binding.BindingException;
import org.jdesktop.binding.DataModel;
import org.jdesktop.binding.event.ValuesChangedEvent;
import org.jdesktop.binding.event.DataModelListener;
import org.jdesktop.binding.event.ModelChangedEvent;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXComboBox.JXComboBoxList;

/**
 *
 * @author rbair
 */
public class JXComboBoxListBinding extends SwingModelBinding {
    private ComboBoxModel oldModel;
    private DataModelListener listener;
    private JXComboBox.JXComboBoxList list;
    private BoundComboBoxModel model;    
    
    public JXComboBoxListBinding(JXComboBox.JXComboBoxList list) {
        super(list.getComboBox());
        this.list = list;
    }
    
    protected void doInitialize() {
        JXComboBox cbox = getComponent();
        oldModel = cbox.getModel();
        model = new BoundComboBoxModel();
        cbox.setModel(model);
        
        listener = new DataModelListener() {
            public void valuesChanged(ValuesChangedEvent evt) {
                model.fireDataChanged();
            }

            public void modelChanged(ModelChangedEvent mce) {
                model.fireDataChanged();
            }
        };
        getDataModel().addDataModelListener(listener);
    }

    protected void doRelease() {
        getComponent().setModel(oldModel);
        getDataModel().removeDataModelListener(listener);
    }

    public JXComboBox getComponent() {
        return (JXComboBox)super.getComponent();
    }

    public final class BoundComboBoxModel extends AbstractListModel implements ComboBoxModel {
        private Object selectedObject;
        
        public void fireDataChanged() {
            fireContentsChanged(this, -1, -1);
        }
        
	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
            return getDataModel().getRowCount();
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
            return getDataModel().getRow(index).getDomainData();
	}

        /**
         * Set the value of the selected item. The selected item may be null.
         * <p>
         * @param anObject The combo box value or null for no selection.
         */
        public void setSelectedItem(Object anObject) {
            if ((selectedObject != null && !selectedObject.equals( anObject )) ||
                selectedObject == null && anObject != null) {
                selectedObject = anObject;
                fireContentsChanged(this, -1, -1);
            }
        }

        // implements javax.swing.ComboBoxModel
        public Object getSelectedItem() {
            return selectedObject;
        }
    }
}
