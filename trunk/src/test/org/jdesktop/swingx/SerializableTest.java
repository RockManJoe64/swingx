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
package org.jdesktop.swingx;

import java.io.NotSerializableException;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthLookAndFeel;

import org.jdesktop.swingx.calendar.DatePickerFormatter;
import org.jdesktop.test.SerializableSupport;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;


/**
 * Test serializable of all SwingX components.
 * 
 * @author Jeanette Winzenburg
 */
@RunWith(JUnit4.class)
public class SerializableTest extends InteractiveTestCase {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
            .getLogger(SerializableTest.class.getName());

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable. <p>
     * 
     * Note: this blows as soon as a JXTable is set!
     */
    @Test
    public void testDatePickerFormatter() {
        DatePickerFormatter component = new DatePickerFormatter();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }



    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable. <p>
     * 
     * Note: this blows as soon as a JXTable is set!
     */
    @Test
    public void testTableHeader() throws Exception {
        JXTableHeader component = new JXTableHeader();
        
        if (javax.swing.plaf.synth.SynthLookAndFeel.class.isAssignableFrom(Class.forName(UIManager.getSystemLookAndFeelClassName()))) {
            LOG.warning("Table header is not serializable on Linux when using Synth or derived LaFs due to javax.swing.plaf.synth.SynthLabelUI being non serializable.");
            return;
        }
        try {
            SerializableSupport.serialize(component);
        } catch (NotSerializableException e) {
            fail("not serializable " + e);
        } 
    }

    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable.
     * 
     * 
     */
    @Test
    public void testRadioGroup() {
        JXRadioGroup component = new JXRadioGroup();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }


    /**
     * Issue #423-swingx: all descendants of JComponent must be 
     * serializable. <p>
     * 
     */
    @Test
    public void testHyperlink() {
        JXHyperlink component = new JXHyperlink();
        try {
            SerializableSupport.serialize(component);
        } catch (Exception e) {
            fail("not serializable " + e);
        } 
    }

}
