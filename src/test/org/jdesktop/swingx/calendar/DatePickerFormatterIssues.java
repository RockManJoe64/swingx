/*
 * $Id$
 *
 * Copyright 2007 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.calendar;

import java.util.Calendar;

import junit.framework.TestCase;

import org.jdesktop.swingx.JXDatePicker;

/**
 * Unit tests for <code>DatePickerFormatter</code>.
 * 
 * @author Jeanette Winzenburg
 */
public class DatePickerFormatterIssues extends TestCase {

    @SuppressWarnings("unused")
    private Calendar cal;

    public void setUp() {
        cal = Calendar.getInstance();
        // force loading of resources
        new JXDatePicker();
    }

    public void tearDown() {
    }
    
    /**
     * Dummy method to keep the testRunner happy ...
     */
    public void testDummy() {
        
    }

}
