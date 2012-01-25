package org.jdesktop.swingx.painterset;

import org.jdesktop.swingx.util.BeanArrayList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.jdesktop.swingx.combobox.ListComboBoxModel;



class BeanArrayComboBoxModel extends ListComboBoxModel implements PropertyChangeListener {
    public BeanArrayComboBoxModel(BeanArrayList list) {
        super(list);
        list.addPropertyChangeListener(this);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        fireUpdate();
    }
}