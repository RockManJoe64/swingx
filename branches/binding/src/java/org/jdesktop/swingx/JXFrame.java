/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JRootPane;
import org.jdesktop.binding.BindingContext;
import org.jdesktop.swingx.binding.BindingContextSupport;


/**
 * A smarter JFrame specifically used for top level frames for Applications.
 * This frame uses a JXRootPane.
 */
public class JXFrame extends JFrame implements BindingContext {

    public JXFrame() {
        this(null, true);
    }
    
    public JXFrame(String title, boolean exitOnClose) {
        super(title);
        if (exitOnClose) {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    public JXFrame(String title) {
        this(title, true);
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


    /*************      Data Binding - BindingContext    ****************/
    private BindingContextSupport ctxSupport = new BindingContextSupport(this);

    public Object removeDataSource(String name) {
        return ctxSupport.removeDataSource(name);
    }

    public void bind(Object target, String path) {
        ctxSupport.bind(target, path);
    }

    public void addDataSource(String name, Object dataSource) {
        ctxSupport.addDataSource(name, dataSource);
    }

    public void unbind(Object target) {
        ctxSupport.unbind(target);
    }

    public BindingContext[] getChildrenContexts() {
        return ctxSupport.getChildrenContexts();
    }
    
    public BindingContext getParentContext() {
        return ctxSupport.getParentContext();
    }

    public void loadAll() {
        ctxSupport.loadAll();
    }

    public void loadAll(boolean recurse) {
        ctxSupport.loadAll(recurse);
    }

    public void saveAll() {
        ctxSupport.saveAll();
    }

    public void saveAll(boolean recurse) {
        ctxSupport.saveAll(recurse);
    }
}

