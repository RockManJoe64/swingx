/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the ETable module. The Initial Developer of the Original
 * Software is Nokia. Portions Copyright 2004 Nokia. All Rights Reserved.
 */
package org.netbeans.swing.etable;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumnModel;

/**
 * Panel containing checkboxes for selecting visible columns
 * of a table.
 * @author David Strupl
 */
class ColumnSelectionPanel extends JPanel {

    /**
     * Map: ETableColumn --> JCheckBox
     */
    private Map checkBoxes = new HashMap();
    
    /**
     * Model allowing to show/hide columns.
     */
    private ETableColumnModel columnModel;
    
    /** Creates a new instance of ColumnSelectionPanel */
    public ColumnSelectionPanel(TableColumnModel columnModel, ETable table) {
        setLayout(new GridBagLayout());
        if (! (columnModel instanceof ETableColumnModel)) {
            return;
        }
        ETableColumnModel etcm = (ETableColumnModel)columnModel;
        this.columnModel = etcm;
        List columns = Collections.list(etcm.getColumns());
        columns.addAll(etcm.hiddenColumns);
        Collections.sort(columns);
        int width = columns.size() / 10 + 1;
        layoutPanel(columns, width, table);
    }
    
    /**
     * Adds checkbox for each ETableColumn contained in the columns parameter.
     */
    private void layoutPanel(List columns, int width, ETable table) {
        Map displayNameToCheckBox = new HashMap();
        ArrayList displayNames = new ArrayList();
        for (Iterator it = columns.iterator(); it.hasNext(); ) {
            ETableColumn etc = (ETableColumn)it.next();
            JCheckBox checkBox = new JCheckBox();
            checkBoxes.put(etc, checkBox);
            String dName = table.getColumnDisplayName(etc.getHeaderValue().toString());
            checkBox.setText(dName);
            checkBox.setSelected(! columnModel.isColumnHidden(etc));
            checkBox.setEnabled(etc.isHidingAllowed());
            if (! displayNames.contains(dName)) {
                // the expected case
                displayNameToCheckBox.put(dName, checkBox);
            } else {
                // the same display name is used for more columns - fuj
                ArrayList al = null;
                Object theFirstOne = displayNameToCheckBox.get(dName);
                if (theFirstOne instanceof JCheckBox) {
                    JCheckBox firstCheckBox = (JCheckBox)theFirstOne;
                    al = new ArrayList();
                    al.add(firstCheckBox);
                } else {
                    // already a list there
                    if (theFirstOne instanceof ArrayList) {
                        al = (ArrayList)theFirstOne;
                    } else {
                        throw new IllegalStateException("Wrong object theFirstOne is " + theFirstOne);
                    }
                }
                al.add(checkBox);
                displayNameToCheckBox.put(dName, al);
            }
            displayNames.add(dName);
        }
        Collections.sort(displayNames, Collator.getInstance());
        int i = 0;
        int j = 0;
        int index = 0;
        int rows = columns.size() / width;
        for (Iterator it = displayNames.iterator(); it.hasNext(); i++) {
            if (i >= rows) {
                i = 0;
                j++;
            }
            String displayName = (String)it.next();
            Object obj = displayNameToCheckBox.get(displayName);
            JCheckBox checkBox = null;
            if (obj instanceof JCheckBox) {
                checkBox = (JCheckBox)obj;
            } else {
                // in case there are duplicate names we store ArrayLists
                // of JCheckBoxes
                if (obj instanceof ArrayList) {
                    ArrayList al = (ArrayList)obj;
                    if (index >= al.size()) {
                        index = 0;
                    }
                    checkBox = (JCheckBox)al.get(index++);
                } else {
                    throw new IllegalStateException("Wrong object obj is " + obj);
                }
            }
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = j;
            gridBagConstraints.gridy = i;
            gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints.weightx = 1;
            add(checkBox, gridBagConstraints);
        }
    }
    
    /**
     * After the user clicks Ok this method will hide/un-hide the
     * columns according to the selected checkboxes.
     */
    public void changeColumnVisibility() {
        if (columnModel == null) {
            return;
        }
        for (Iterator it = checkBoxes.keySet().iterator(); it.hasNext(); ) {
            ETableColumn etc = (ETableColumn) it.next();
            JCheckBox checkBox = (JCheckBox)checkBoxes.get(etc);
            columnModel.setColumnHidden(etc,! checkBox.isSelected());
        }
    }
}
