/*
 * Copyright 2005 Sun Microsystems, Inc., 4150 Network Circle,
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
package org.jdesktop.swingx.plaf;

import javax.swing.BorderFactory;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.plaf.basic.BasicDatePickerUI;
import org.jdesktop.swingx.util.OS;

/**
 * @author Joshua Outwater
 */
public class DatePickerAddon extends AbstractComponentAddon {
    public DatePickerAddon() {
        super("JXDatePicker");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addBasicDefaults(LookAndFeelAddons addon, DefaultsList defaults) {
        super.addBasicDefaults(addon, defaults);
        
        defaults.add(JXDatePicker.uiClassID, BasicDatePickerUI.class.getName());
        defaults.add("JXDatePicker.border",
                new BorderUIResource(BorderFactory.createCompoundBorder(
                        LineBorder.createGrayLineBorder(),
                        BorderFactory.createEmptyBorder(20, 3, 3, 3))));
        
        UIManagerExt.addResourceBundle(
                "org.jdesktop.swingx.plaf.basic.resources.DatePicker");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addWindowsDefaults(LookAndFeelAddons addon, DefaultsList defaults) {
        super.addWindowsDefaults(addon, defaults);
        if (OS.isWindowsXP() && OS.isUsingWindowsVisualStyles()) {
            defaults.add("JXDatePicker.arrowIcon",
                    LookAndFeel.makeIcon(DatePickerAddon.class, "windows/resources/combo-xp.png"));
        } else {
            defaults.add("JXDatePicker.arrowIcon",
                    LookAndFeel.makeIcon(DatePickerAddon.class, "windows/resources/combo-w2k.png"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addLinuxDefaults(LookAndFeelAddons addon, DefaultsList defaults) {
        super.addLinuxDefaults(addon, defaults);
        
        defaults.add("JXDatePicker.arrowIcon",
                LookAndFeel.makeIcon(DatePickerAddon.class, "linux/resources/combo-gtk.png"));
        
        if (isGTK()) {
            // PENDING JW: going dirty: register an illegal value - not a border 
            // results in uimanager.getBorder returning a null
            // which prevents the datePickerUI to install a border on the editor.
            // change to remove once we have api/implementation to remove something
            // from the defaultsList.
           defaults.add("JXDatePicker.border", "none"); 
        }
    }

    /**
     * 
     * @return true if the LF is GTK.
     */
    private boolean isGTK() {
        return "GTK".equals(UIManager.getLookAndFeel().getID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addMacDefaults(LookAndFeelAddons addon, DefaultsList defaults) {
        super.addMacDefaults(addon, defaults);
        
        defaults.add("JXDatePicker.arrowIcon",
                LookAndFeel.makeIcon(DatePickerAddon.class, "macosx/resources/combo-osx.png"));

        defaults.add("JXDatePicker.border", "none");

    }
}

