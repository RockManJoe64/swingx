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

    /*************      Data Binding    ****************/
    private BindingContextSupport ctx = new BindingContextSupport(this);

    public Object removeDataSource(String name) {
        return ctx.removeDataSource(name);
    }

    public void bind(Object target, String path) {
        ctx.bind(target, path);
    }

    public void addDataSource(String name, Object dataSource) {
        ctx.addDataSource(name, dataSource);
    }

    public void unbind(Object target) {
        ctx.unbind(target);
    }

    public BindingContext[] getChildrenContexts() {
        return ctx.getChildrenContexts();
    }
    
    public BindingContext getParentContext() {
        return ctx.getParentContext();
    }

    public void loadAll() {
        ctx.loadAll();
    }

    public void loadAll(boolean recurse) {
        ctx.loadAll(recurse);
    }

    public void saveAll() {
        ctx.saveAll();
    }

    public void saveAll(boolean recurse) {
        ctx.saveAll(recurse);
    }
}

