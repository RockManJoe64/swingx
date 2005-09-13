/*
 * JXDialog.java
 *
 * Created on May 9, 2005, 2:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import javax.swing.JDialog;
import org.jdesktop.binding.BindingContext;
import org.jdesktop.swingx.binding.BindingContextSupport;

/**
 *
 * @author rbair
 */
public class JXDialog extends JDialog implements BindingContext {
    
    /**
     * @inheritDoc
     */
    public JXDialog() throws HeadlessException {
        super();
    }

    /**
     * @inheritDoc
     */
    public JXDialog(Frame owner) throws HeadlessException {
        super(owner);
    }

    /**
     * @inheritDoc
     */
    public JXDialog(Frame owner, boolean modal) throws HeadlessException {
        super(owner, modal);
    }

    /**
     * @inheritDoc
     */
    public JXDialog(Frame owner, String title) throws HeadlessException {
        super(owner, title);     
    }

    /**
     * @inheritDoc
     */
    public JXDialog(Frame owner, String title, boolean modal)
        throws HeadlessException {
        super(owner, title, modal);
    }

    /**
     * @inheritDoc
     */
    public JXDialog(Frame owner, String title, boolean modal,
                   GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
    }

    /**
     * @inheritDoc
     */
    public JXDialog(Dialog owner) throws HeadlessException {
        super(owner);
    }

    /**
     * @inheritDoc
     */
    public JXDialog(Dialog owner, boolean modal) throws HeadlessException {
        super(owner, modal);
    }

    /**
     * @inheritDoc
     */
    public JXDialog(Dialog owner, String title) throws HeadlessException {
        super(owner, title);     
    }

    /**
     * @inheritDoc
     */
    public JXDialog(Dialog owner, String title, boolean modal)
        throws HeadlessException {
        super(owner, title, modal);
    }

    /**
     * @inheritDoc
     */
    public JXDialog(Dialog owner, String title, boolean modal,
                   GraphicsConfiguration gc) throws HeadlessException {

        super(owner, title, modal, gc);
    }

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
