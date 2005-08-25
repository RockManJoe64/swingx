/*
 * JLabelBinding.java
 *
 * Created on August 24, 2005, 5:14 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.binding;

import javax.swing.JLabel;
import org.jdesktop.binding.DataModel;
import org.jdesktop.binding.SelectionModel;

/**
 *
 * @author Richard
 */
public class JLabelBinding extends SwingBinding {
    private String fieldName;
    
    /** Creates a new instance of JLabelBinding */
    public JLabelBinding(JLabel label, String fieldName) {
        super(label);
        this.fieldName = fieldName;
    }

    protected void initialize() {
        //nothing to init
    }

    public void release() {
        //nothing to release
    }

    public boolean loadComponentFromDataModel() {
        //for now, load the label with the first selection's
        //data
        DataModel dm = getDataModel();
        SelectionModel sm = dm.getSelectionModel();
        int indices[] = sm.getSelectionIndices();
        if (indices != null && indices.length > 0) {
            JLabel label = (JLabel)getComponent();
            Object value = dm.getValue(indices[0], fieldName);
            label.setText(value == null ? "" : value.toString());
        }
        return true;
    }

    public boolean loadDataModelFromComponent() {
        return false; //should never happen from a label!
    }
}
