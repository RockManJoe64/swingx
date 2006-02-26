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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JDialog;
import org.jdesktop.binding.Binding;
import org.jdesktop.binding.BindingContext;
import org.jdesktop.binding.DataModel;
import org.jdesktop.binding.SelectionModel;
import org.jdesktop.binding.event.BindingContextListener;
import org.jdesktop.swingx.binding.BindingContextSupport;
import org.jdesktop.validation.ValidationListener;

/**
 * First cut for enhanced Dialog.
 *
 * <ul>
 * <li> registers stand-in actions for close/execute with the dialog's RootPane
 * <li> registers keyStrokes for esc/enter to trigger the close/execute actions
 * <li> takes care of building the button panel using the close/execute actions.
 * <li> accepts a content and configures itself from content's properties -
 *  replaces the execute action from the appropriate action in content's action map (if any)
 *  and set's its title from the content's name.
 * </ul>
 *
 *
 * PENDING: add support for vetoing the close.
 * PENDING: add complete set of constructors
 * PENDING: add windowListener to delegate to close action
 *
 * @author Jeanette Winzenburg
 */
public class JXDialog extends JDialog implements BindingContext {
    public static final String EXECUTE_ACTION_COMMAND = "execute";
    public static final String CLOSE_ACTION_COMMAND = "close";
    public static final String UIPREFIX = "XDialog.";
    
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
    public JXDialog(Frame owner, String title) throws HeadlessException {
        super(owner, title);
    }
    
    /**
     * @inheritDoc
     */
    public JXDialog(Frame owner, boolean modal) throws HeadlessException {
        super(owner, modal);
    }
    
    JComponent content;
    
    public JXDialog(Frame frame, JComponent content) {
        super(frame);
//        setContent(content);
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
    
//    /**
//     *
//     */
//    private void locate() {
//        GraphicsConfiguration gc =
//            GraphicsEnvironment.getLocalGraphicsEnvironment().
//            getDefaultScreenDevice().getDefaultConfiguration();
//        Rectangle bounds = gc.getBounds();
//        int x = bounds.x+bounds.width/3;
//        int y = bounds.y+bounds.height/3;
//
//        setLocation(x, y);
//    }
    
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
    
    /**
     * Creates a non-modal dialog without a title with the
     * specified <code>Dialog</code> as its owner.
     * <p>
     * This constructor sets the component's locale property to the value
     * returned by <code>JComponent.getDefaultLocale</code>.
     *
     * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public JXDialog(Dialog owner) throws HeadlessException {
        super(owner);
    }
    
    /**
     * Creates a modal or non-modal dialog without a title and
     * with the specified owner dialog.
     * <p>
     * This constructor sets the component's locale property to the value
     * returned by <code>JComponent.getDefaultLocale</code>.
     *
     * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
     * @param modal  true for a modal dialog, false for one that allows
     *               other windows to be active at the same time
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see JComponent#getDefaultLocale
     */
    public JXDialog(Dialog owner, boolean modal) throws HeadlessException {
        super(owner, modal);
    }
    
    /*************      Data Binding - BindingContext    ****************/
    private BindingContextSupport ctxSupport = new BindingContextSupport(this);
    
    public Object removeDomainData(String name) {
        return ctxSupport.removeDomainData(name);
    }
    
    public Binding bind(Object target, String path) {
        return ctxSupport.bind(target, path);
    }
    
    public void unbind(Object target) {
        ctxSupport.unbind(target);
    }
    
    public Binding bind(Object component, String propertyName, String path) {
        return ctxSupport.bind(component, propertyName, path);
    }

    public void unbind(Object component, String propertyName) {
        ctxSupport.unbind(component, propertyName);
    }

    public void addDomainData(String name, Object dataSource) {
        ctxSupport.addDomainData(name, dataSource);
    }
    
    public Object getDomainData(String name) {
        return ctxSupport.getDomainData(name);
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
