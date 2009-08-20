/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
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
 *
 */
package org.jdesktop.beans;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import junit.framework.TestCase;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.JXSearchPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.JXLoginPane.SimpleNamePanel;
import org.jdesktop.swingx.renderer.JRendererCheckBox;
import org.jdesktop.swingx.renderer.JRendererLabel;
import org.jdesktop.swingx.renderer.JXRendererHyperlink;
import org.jdesktop.swingx.renderer.WrappingIconPanel;
import org.jdesktop.test.TestUtils;

/**
 * Reflection based test for testing PCE firing.
 * 
 * @author rah003
 */
public class BeanEventsTest extends InteractiveTestCase {
    
    static Logger log = Logger.getAnonymousLogger();

    public void testAllPainterPCEFiring() throws Exception {
        log.setLevel(Level.ALL);
        List<Class<?>> beanClasses = ClassSearchUtils.searchClassPath("org.jdesktop.swingx.");
        MultiMap excludes = new MultiHashMap();
        // shorthand for getModel.setColumnMargin
        excludes.put(JXTable.class, "columnMargin");
        // overwritten method setPreferredScrollableViewportSize from JTable
        // the super implementation fails to fire event. Attempt to do so in JXTable causes other test failures. Needs to be investigated.
        excludes.put(JXTable.class, "preferredScrollableViewportSize");
        // no op due to sorting conflict 
        excludes.put(JXTreeTable.class, "sortable");
        // no op due to sorting conflict 
        excludes.put(JXTreeTable.class, "filters");
        // shorthand for getRenderer.setLargeModel
        excludes.put(JXTreeTable.class, "largeModel");
        // shorthand for getRenderer.setOverwriteRendererIcons
        excludes.put(JXTreeTable.class, "overwriteRendererIcons");
        // shorthand for getRenderer.setRootVisible
        excludes.put(JXTreeTable.class, "rootVisible");
        // shorthand for getRenderer.setToggleClickCount
        excludes.put(JXTreeTable.class, "toggleClickCount");
        // shorthand for getLayout.setDividerSize
        excludes.put(JXMultiSplitPane.class, "dividerSize");
        // shorthand for getLayout.setModel
        excludes.put(JXMultiSplitPane.class, "model");
        // shorthand for getSelectionModel.setSelectionMode
        excludes.put(JXMonthView.class, "selectionMode");
        // shorthand for getSelectionModel.setUpperBound
        excludes.put(JXMonthView.class, "upperBound");
        // shorthand for getSelectionModel.setLowerBound
        excludes.put(JXMonthView.class, "lowerBound");
        // shorthand for getSelectionModel.setSelectionInterval(newDate, newDate);
        excludes.put(JXMonthView.class, "selectionDate");
        // shorthand for JTextField.this.setText 
        excludes.put(SimpleNamePanel.class, "userName");
        // shorthand for getEditor.setFont 
        excludes.put(JXDatePicker.class, "font");
        // according to javadoc: api hack for testing 
        excludes.put(JXDatePicker.class, "linkDay");
        
        // incorrect method name ... shoud be addPatternFilter instead 
        excludes.put(JXSearchPanel.class, "patternFilter");
        // JRendererLabel doesn't fire events for performance reasons 
        excludes.put(JRendererLabel.class, "toolTipText");
        // JRendererLabel doesn't fire events for performance reasons 
        excludes.put(JRendererLabel.class, "painter");
        // JRendererCheckBox doesn't fire events for performance reasons 
        excludes.put(JRendererCheckBox.class, "toolTipText");
        // JRendererCheckBox doesn't fire events for performance reasons 
        excludes.put(JRendererCheckBox.class, "painter");
        // shorthand for getComponent.setPainter()
        excludes.put(WrappingIconPanel.class, "painter");
        // JXRendererHyperlink doesn't fire events for performance reasons 
        excludes.put(JXRendererHyperlink.class, "toolTipText");
        // JXRendererHyperlink doesn't fire events for performance reasons 
        excludes.put(JXRendererHyperlink.class, "painter");
        // shorthand for getModel.setMinimumValue 
        excludes.put(JXMultiThumbSlider.class, "minimumValue");
        // shorthand for getModel.setMaximumValue 
        excludes.put(JXMultiThumbSlider.class, "maximumValue");
        // shorthand for getContentPane.setMinimumSize
        excludes.put(JXCollapsiblePane.class, "minimumSize");
        // shorthand for getContentPane.setPreferredSize
        excludes.put(JXCollapsiblePane.class, "preferredSize");
        // shorthand for getContentPane.setBorder
        excludes.put(JXCollapsiblePane.class, "border");
        // this is a tricky one ... potentially a bug somewhere. In case preferredSize is not set yet, call to getPreferredSize() is propagated all the way up to Container, which in turn requests preferred size from the layout manager. On the other hand when preferred size is set, "old" preferred size for the purpose of event is determined (this time in Component) solely from the previous value of private variable preferredSize and therefore null
        excludes.put(JXImagePanel.class, "preferredSize");
        
        log.fine("Got " + beanClasses.size());
        for (Class beanClass : beanClasses) {
            if (!AbstractBean.class.isAssignableFrom(beanClass) && !JComponent.class.isAssignableFrom(beanClass) || TestCase.class.isAssignableFrom(beanClass)) {
                log.fine("Skipping " + beanClass);
                continue;
            }
            try {
                Object inst = beanClass.newInstance();
                log.info("Testing " + beanClass);
                TestUtils.assertPCEFiring( inst, (Collection<String>) excludes.get(beanClass));
            } catch (Exception e) {
                log.info("ignoring " + beanClass + " because of " + e.getMessage());
            }
        }
    }
}