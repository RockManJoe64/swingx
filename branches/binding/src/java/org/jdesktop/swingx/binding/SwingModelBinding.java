/*
 * SwingModelBinding.java
 *
 * Created on September 28, 2005, 3:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;
import com.jgoodies.validation.ValidationResult;
import org.jdesktop.binding.impl.ModelBinding;
/**
 *
 * @author rbair
 */
public abstract class SwingModelBinding extends ModelBinding {
    
    /** Creates a new instance of SwingModelBinding */
    public SwingModelBinding(Object c) {
        super(c);
    }
    
    protected ValidationResult doValidation() {
        return new ValidationResult();
    }
}
