/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.jdnc.beaninfo;

import java.beans.PropertyDescriptor;
import javax.swing.JEditorPane;
import org.jdesktop.swingx.JXEditorPane;

/**
 *
 * @author rbair
 */
public class JXEditorPaneBeanInfo extends BeanInfoSupport {
    
    public JXEditorPaneBeanInfo() {
        super(JXEditorPane.class);
    }

    protected void initialize() {
        PropertyDescriptor[] array = getPropertyDescriptors();
        for (int i=0; i<array.length; i++) {
            PropertyDescriptor pd = array[i];
        }
        setIconsBasedOn(JEditorPane.class);
    }
}
