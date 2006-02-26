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
import javax.swing.DefaultComboBoxModel;
import org.jdesktop.binding.BindingException;
import org.jdesktop.binding.DataModel;
import org.jdesktop.binding.event.ValuesChangedEvent;
import org.jdesktop.binding.event.DataModelListener;
import org.jdesktop.binding.event.ModelChangedEvent;

/**
 *
 * @author rbair
 */
public class JComboBoxListBinding extends SwingModelBinding {
    private Object[] oldElements;
    private DataModelListener listener;
    private String fieldName;
    
    public JComboBoxListBinding(DefaultComboBoxModel model) {
        super(model);
    }
    
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    protected void doInitialize() {
        DefaultComboBoxModel comp = getComponent();
        oldElements = new Object[comp.getSize()];
        for (int i=0; i<oldElements.length; i++) {
            oldElements[i] = getComponent().getElementAt(i);
        }
        listener = new DataModelListener() {
            public void valuesChanged(ValuesChangedEvent evt) {
                refresh();
            }

            public void modelChanged(ModelChangedEvent mce) {
                refresh();
            }
            
            public void refresh() {
                DefaultComboBoxModel comp = getComponent();
                DataModel dm = getDataModel();
                comp.removeAllElements();
                for (int i=0; i<dm.getRowCount(); i++) {
                    //if fieldName is null, then load the element with the data for the whole row
                    //(very useful if being used with a ListCellRenderer)
                    //otherwise, only the value for the given field
                    //TODO this behavior should be default for all Swing bindings
                    //that require a field name
                    comp.addElement(fieldName == null ? dm.getRow(i).getDomainData() : dm.getRow(i).getValue(fieldName));
                }
            }
        };
        getDataModel().addDataModelListener(listener);
    }

    protected void doRelease() {
        DefaultComboBoxModel comp = getComponent();
        comp.removeAllElements();
        for (Object el : oldElements) {
            comp.addElement(el);
        }
        getDataModel().removeDataModelListener(listener);
    }

    public DefaultComboBoxModel getComponent() {
        return (DefaultComboBoxModel)super.getComponent();
    }

    protected void doLoad() throws BindingException {
    }

    protected void doSave() throws BindingException {
    }
}
