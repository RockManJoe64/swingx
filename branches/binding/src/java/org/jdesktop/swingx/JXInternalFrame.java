/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jdesktop.swingx;

import com.jgoodies.validation.ValidationResult;
import java.util.List;
import javax.swing.JInternalFrame;
import org.jdesktop.binding.Binding;
import org.jdesktop.binding.BindingContext;
import org.jdesktop.binding.DataModel;
import org.jdesktop.binding.SelectionModel;
import org.jdesktop.binding.event.BindingContextListener;
import org.jdesktop.swingx.binding.BindingContextSupport;
import org.jdesktop.validation.ValidationListener;


/**
 */
public class JXInternalFrame extends JInternalFrame implements BindingContext {

    /** 
     * Creates a non-resizable, non-closable, non-maximizable,
     * non-iconifiable <code>JInternalFrame</code> with no title.
     */
    public JXInternalFrame() {
        super();
    }

    /** 
     * Creates a non-resizable, non-closable, non-maximizable,
     * non-iconifiable <code>JInternalFrame</code> with the specified title.
     * Note that passing in a <code>null</code> <code>title</code> results in
     * unspecified behavior and possibly an exception.
     *
     * @param title  the non-<code>null</code> <code>String</code>
     *     to display in the title bar
     */
    public JXInternalFrame(String title) {
        super(title);
    }

    /** 
     * Creates a non-closable, non-maximizable, non-iconifiable 
     * <code>JInternalFrame</code> with the specified title
     * and resizability.
     *
     * @param title      the <code>String</code> to display in the title bar
     * @param resizable  if <code>true</code>, the internal frame can be resized
     */
    public JXInternalFrame(String title, boolean resizable) {
        super(title, resizable);
    }

    /** 
     * Creates a non-maximizable, non-iconifiable <code>JInternalFrame</code>
     * with the specified title, resizability, and
     * closability.
     *
     * @param title      the <code>String</code> to display in the title bar
     * @param resizable  if <code>true</code>, the internal frame can be resized
     * @param closable   if <code>true</code>, the internal frame can be closed
     */
    public JXInternalFrame(String title, boolean resizable, boolean closable) {
        super(title, resizable, closable);
    }

    /** 
     * Creates a non-iconifiable <code>JInternalFrame</code>
     * with the specified title,
     * resizability, closability, and maximizability.
     *
     * @param title       the <code>String</code> to display in the title bar
     * @param resizable   if <code>true</code>, the internal frame can be resized
     * @param closable    if <code>true</code>, the internal frame can be closed
     * @param maximizable if <code>true</code>, the internal frame can be maximized
     */
    public JXInternalFrame(String title, boolean resizable, boolean closable,
                          boolean maximizable) {
        super(title, resizable, closable, maximizable);
    }

    /** 
     * Creates a <code>JInternalFrame</code> with the specified title,
     * resizability, closability, maximizability, and iconifiability.
     * All <code>JInternalFrame</code> constructors use this one.
     *
     * @param title       the <code>String</code> to display in the title bar
     * @param resizable   if <code>true</code>, the internal frame can be resized
     * @param closable    if <code>true</code>, the internal frame can be closed
     * @param maximizable if <code>true</code>, the internal frame can be maximized
     * @param iconifiable if <code>true</code>, the internal frame can be iconified
     */
    public JXInternalFrame(String title, boolean resizable, boolean closable, 
                                boolean maximizable, boolean iconifiable) {
        super(title, resizable, closable, maximizable, iconifiable);
    }

    /*************      Data Binding - BindingContext    ****************/
    protected BindingContextSupport ctxSupport = new BindingContextSupport(this);

    public Object removeDomainData(String name) {
        return ctxSupport.removeDomainData(name);
    }

    public Binding bind(Object target, String path) {
        return ctxSupport.bind(target, path);
    }

    public Binding bind(Object component, String propertyName, String path) {
        return ctxSupport.bind(component, propertyName, path);
    }

    public void unbind(Object component, String propertyName) {
        ctxSupport.unbind(component, propertyName);
    }

    public Object getDomainData(String name) {
        return ctxSupport.getDomainData(name);
    }

    public void addDomainData(String name, Object dataSource) {
        ctxSupport.addDomainData(name, dataSource);
    }

    public void unbind(Object target) {
        ctxSupport.unbind(target);
    }

    public List<BindingContext> getChildrenContexts() {
        return ctxSupport.getChildrenContexts();
    }
    
    public BindingContext getParentContext() {
        return ctxSupport.getParentContext();
    }

    public void loadAll() {
        ctxSupport.loadAll();
    }

//    public void loadAll(boolean recurse) {
//        ctxSupport.loadAll(recurse);
//    }

    public void saveAll() {
        ctxSupport.saveAll();
    }

//    public void saveAll(boolean recurse) {
//        ctxSupport.saveAll(recurse);
//    }

    public DataModel getDataModel(String path) {
        return ctxSupport.getDataModel(path);
    }

    public void addSelectionModel(SelectionModel model) {
        ctxSupport.addSelectionModel(model);
    }

    public void removeSelectionModel(SelectionModel model) {
        ctxSupport.removeSelectionModel(model);
    }
    
    public SelectionModel getSelectionModel(String name) {
        return ctxSupport.getSelectionModel(name);
    }

    public Binding getBinding(Object component) {
        return ctxSupport.getBinding(component);
    }

    public void addValidationListener(ValidationListener listener) {
        ctxSupport.addValidationListener(listener);
    }

    public void removeValidationListener(ValidationListener listener) {
        ctxSupport.removeValidationListener(listener);
    }

    public void addBindingContextListener(BindingContextListener listener) {
        ctxSupport.addBindingContextListener(listener);
    }

    public void removeBindingContextListener(BindingContextListener listener) {
        ctxSupport.removeBindingContextListener(listener);
    }

    public void handleValidationResult(ValidationResult result) {
        ctxSupport.handleValidationResult(result);
    }

    public void setValidationGroup(Object component, String group) {
        ctxSupport.setValidationGroup(component, group);
    }
}
