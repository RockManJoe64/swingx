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

import java.util.Date;
import org.jdesktop.binding.ScalarBinding;
import org.jdesktop.swingx.JXDatePicker;


/**
 *
 * @author Richard
 */
public class JXDatePickerBinding extends ScalarBinding {
    
    public JXDatePickerBinding(JXDatePicker datePicker) {
        super(datePicker, Date.class);
    }
    
    public JXDatePickerBinding(JXDatePicker datePicker, String fieldName) {
        super(datePicker, fieldName, Date.class);
    }

    protected void initialize() {
        //nothing to init
    }

    public void release() {
        //nothing to release
    }

    protected void setComponentValue(Object value) {
        JXDatePicker dp = (JXDatePicker)getComponent();
        dp.setDate(value == null ? new Date() : (Date)value);
    }
    
    protected Date getComponentValue() {
        //in reality, this should never be called since
        //label components are always read only!!!
        //TODO
        return ((JXDatePicker)getComponent()).getDate();
    }
}
