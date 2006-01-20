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
import java.awt.Component;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JRootPane;
import org.jdesktop.binding.Binding;
import org.jdesktop.binding.BindingContext;
import org.jdesktop.binding.DataModel;
import org.jdesktop.binding.SelectionModel;
import org.jdesktop.binding.event.BindingContextListener;
import org.jdesktop.swingx.binding.BindingContextSupport;
import org.jdesktop.validation.ValidationListener;


/**
 * A smarter JFrame specifically used for top level frames for Applications.
 * This frame uses a JXRootPane.
 */
public class JXFrame extends JFrame implements BindingContext, DataAware {

    public JXFrame() {
        this(null, false);
    }
    
    public JXFrame(String title, boolean exitOnClose) {
        super(title);
        if (exitOnClose) {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    public JXFrame(String title) {
        this(title, false);
    }

    /**
     * Overloaded to create a JXRootPane.
     */
    protected JRootPane createRootPane() {
        return new JXRootPane();
    }

    /**
     * Overloaded to make this public.
     */
    public void setRootPane(JRootPane root) {
        super.setRootPane(root);
    }

    /**
     * Add a component to the Frame.
     */
    public void addComponent(Component comp) {
        JXRootPane root = getRootPaneExt();
        if (root != null) {
            root.addComponent(comp);
        }
        // XXX should probably fire some sort of container event.
    }

    /**
     * Removes a component from the frame.
     */
    public void removeComponent(Component comp) {
        JXRootPane root = getRootPaneExt();
        if (root != null) {
            root.removeComponent(comp);
        }
        // XXX should probably fire some sort of container event.
    }

    /**
     * Return the extended root pane. If this frame doesn't contain
     * an extended root pane the root pane should be accessed with
     * getRootPane().
     *
     * @return the extended root pane or null.
     */
    public JXRootPane getRootPaneExt() {
        if (rootPane instanceof JXRootPane) {
            return (JXRootPane)rootPane;
        }
        return null;
    }

    /**
     * Overloaded to pack when visible is true.
     */
//    public void setVisible(boolean visible) {
//        if (visible) {
//            pack();
//        }
//        super.setVisible(visible);
//    }

    /*************      Data Binding  - Data-Aware  ****************/
    private String dataPath = "";
    private BindingContext ctx = null;
    
    /**
     * @param path
     */
    public void setDataPath(String path) {
        path = path == null ? "" : path;
        if (!this.dataPath.equals(path)) {
            DataBoundUtils.unbind(this, ctx);
            String oldPath = this.dataPath;
            this.dataPath = path;
            if (DataBoundUtils.isValidPath(this.dataPath)) {
                ctx = DataBoundUtils.bind(this, this.dataPath);
            }
            firePropertyChange("dataPath", oldPath, this.dataPath);
        }
    }
    
    public String getDataPath() {
        return dataPath;
    }

    public void setBindingContext(BindingContext ctx) {
        if (this.ctx != null) {
            DataBoundUtils.unbind(this, this.ctx);
        }
        this.ctx = ctx;
        if (this.ctx != null) {
            if (DataBoundUtils.isValidPath(this.dataPath)) {
                ctx.bind(this, this.dataPath);
            }
        }
    }

    public BindingContext getBindingContext() {
        return ctx;
    }
    
    /*************      Data Binding - BindingContext    ****************/
    private BindingContextSupport ctxSupport = new BindingContextSupport(this);

    public Object removeDomainData(String name) {
        return ctxSupport.removeDomainData(name);
    }

    public Binding bind(Object target, String path, Object... params) {
        return ctxSupport.bind(target, path, params);
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
}