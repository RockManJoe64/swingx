/*
 * DataAware.java
 *
 * Created on November 29, 2005, 8:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import org.jdesktop.binding.Binding;
import org.jdesktop.binding.BindingContext;

/**
 *
 * @author Richard
 */
public interface DataAware {
    public void setDataPath(String path);
    public String getDataPath();
    public void setBindingContext(BindingContext ctx);
    public BindingContext getBindingContext();
    public Binding getBinding();
    public Object getDomainData();
}
