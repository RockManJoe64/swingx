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
