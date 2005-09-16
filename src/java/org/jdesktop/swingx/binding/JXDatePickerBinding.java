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
import org.jdesktop.binding.FieldBinding;
import org.jdesktop.swingx.JXDatePicker;


/**
 *
 * @author Richard
 */
public class JXDatePickerBinding extends FieldBinding {
    private Date oldValue;
    
    public JXDatePickerBinding(JXDatePicker datePicker) {
        super(datePicker, Date.class);
    }
    
    public JXDatePickerBinding(JXDatePicker datePicker, String fieldName) {
        super(datePicker, fieldName, Date.class);
    }

    protected void initialize() {
        oldValue = getComponent().getDate();
    }

    public void release() {
        getComponent().setDate(oldValue);
    }

    protected void setComponentValue(Object value) {
        getComponent().setDate(value == null ? new Date() : (Date)value);
    }
    
    protected Date getComponentValue() {
        return getComponent().getDate();
    }
    
    public JXDatePicker getComponent() {
        return (JXDatePicker)super.getComponent();
    }
}
