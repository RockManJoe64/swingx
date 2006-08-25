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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author David Strupl
 */
public class ETableColumnModel extends DefaultTableColumnModel {

    /** Used as a key or part of a key by the persistence mechanism. */
    private static final String NUMBER_OF_COLUMNS = "ColumnsNumber";

    /** Used as a key or part of a key by the persistence mechanism. */
    private static final String NUMBER_OF_HIDDEN_COLUMNS = "HiddenColumnsNumber";
    
    /** Used as a key or part of a key by the persistence mechanism. */
    private static final String PROP_HIDDEN_PREFIX = "Hidden";
    
    /**
     * List<ETableColumn>: holds list of sorted columns in this model.
     * If the list is empty if no sorting is applied.
     */
    protected transient List sortedColumns = new ArrayList();
    
    /**
     * List<ETableColumn>: holds list of columns that were hidden by the
     * user. The columns contained here are not contained in the inherited
     * tableColumns list.
     */
    protected List hiddenColumns = new ArrayList();
    
    /** Creates a new instance of ETableColumnModel */
    public ETableColumnModel() {
        super();
    }
    
    /**
     * Method allowing to read stored values.
     * The stored values should be only those that the user has customized,
     * it does not make sense to store the values that were set using 
     * the initialization code because the initialization code can be run
     * in the same way after restart.
     */
    public void readSettings(Properties p, String propertyPrefix, ETable table) {
        tableColumns = new Vector();
        sortedColumns = new ArrayList();
        String s = p.getProperty(propertyPrefix + NUMBER_OF_COLUMNS);
        int numColumns = Integer.parseInt(s);
        for (int i = 0; i < numColumns; i++) {
            ETableColumn etc = (ETableColumn)table.createColumn(i);
            etc.readSettings(p, i, propertyPrefix);
            addColumn(etc);
            if (etc.getComparator() != null) {
                int j = 0;
                for ( ; j < sortedColumns.size(); j++) {
                    ETableColumn setc = (ETableColumn)sortedColumns.get(j);
                    if (setc.getSortRank() > etc.getSortRank()) {
                        break;
                    }
                }
                sortedColumns.add(j, etc);
            }
        }
        hiddenColumns = new ArrayList();
        String sh = p.getProperty(propertyPrefix + NUMBER_OF_HIDDEN_COLUMNS);
        int numHiddenColumns = Integer.parseInt(sh);
        for (int i = 0; i < numHiddenColumns; i++) {
            ETableColumn etc = new ETableColumn(table);
            etc.readSettings(p, i, propertyPrefix + PROP_HIDDEN_PREFIX);
            hiddenColumns.add(etc);
        }
    }

    /**
     * Method allowing to store customization values.
     * The stored values should be only those that the user has customized,
     * it does not make sense to store the values that were set using 
     * the initialization code because the initialization code can be run
     * in the same way after restart.
     */
    public void writeSettings(Properties p, String propertyPrefix) {
        int i = 0;
        int numColumns = tableColumns.size();
        p.setProperty(propertyPrefix + NUMBER_OF_COLUMNS, Integer.toString(numColumns));
        for (Iterator it = tableColumns.iterator(); it.hasNext(); ) {
            Object obj = it.next();
            if (obj instanceof ETableColumn) {
                ETableColumn etc = (ETableColumn) obj;
                etc.writeSettings(p, i++, propertyPrefix);
            }
        }
        i = 0;
        int numHiddenColumns = hiddenColumns.size();
        p.setProperty(propertyPrefix + NUMBER_OF_HIDDEN_COLUMNS, Integer.toString(numHiddenColumns));
        for (Iterator it = hiddenColumns.iterator(); it.hasNext(); ) {
            Object obj = it.next();
            if (obj instanceof ETableColumn) {
                ETableColumn etc = (ETableColumn) obj;
                etc.writeSettings(p, i++, propertyPrefix + PROP_HIDDEN_PREFIX);
            }
        }
    }

    /**
     * @returns a comparator for sorting the rows of the table. The comparator
     * operates over ETable.RowMappings objects.
     */
    Comparator getComparator() {
        if (sortedColumns.isEmpty()) {
            return new ETable.OriginalRowComparator();
        }
        return new CompoundComparator();
    }
    
    /**
     *
     */
    void setColumnSorted(ETableColumn etc, boolean ascending, int newRank) {
        if (! etc.isSortingAllowed()) {
            return;
        }
        
        // TODO: check the implementation!
        
         boolean wasSorted = sortedColumns.contains(etc);
         boolean wasAscending = false;
         if (wasSorted) {
             etc.setAscending(ascending);
             etc.setSortRank(newRank);
             sortedColumns.remove(etc);
         } else {
            etc.setSorted(newRank, ascending);
         }
         sortedColumns.add(newRank-1, etc);
    }
    
    /**
     *
     */
    void toggleSortedColumn(ETableColumn etc, boolean cleanAll) {
        if (! etc.isSortingAllowed()) {
            return;
        }
        boolean wasSorted = sortedColumns.contains(etc);
        if (cleanAll) {
            clearSortedColumns(etc);
        }
        if (wasSorted) {
            if (etc.isAscending()) {
                etc.setAscending(false);
            } else {
                sortedColumns.remove(etc);
                etc.setSorted(0, false);
            }
            updateRanks();
        } else {
            etc.setSorted(sortedColumns.size()+1, true);
            sortedColumns.add(etc);
        }
    }
    
    /** 
     * Makes the given column hidden or visible according to the parameter
     * hidden.
     */
    public void setColumnHidden(TableColumn column, boolean hidden) {
        if (hidden) {
            if (! hiddenColumns.contains(column)) {
                if (tableColumns.contains(column)) {
                    removeColumn(column);
                    hiddenColumns.add(column);
                }
            }
        } else {
            if (! tableColumns.contains(column)) {
                if (hiddenColumns.contains(column)) {
                    hiddenColumns.remove(column);
                    addColumn(column);
                }
            }
        }
    }
    
    public boolean isColumnHidden(TableColumn tc) {
        return hiddenColumns.contains(tc);
    }
    
    /**
     * Makes the whole table unsorted.
     */
    public void clearSortedColumns() {
        for (Iterator it = sortedColumns.iterator(); it.hasNext(); ) {
            Object o = it.next();
            if (o instanceof ETableColumn) {
                ETableColumn etc = (ETableColumn)o;
                etc.setSorted(0, false);
            }
        }
        sortedColumns = new ArrayList();
    }
    
    /**
     * Makes the whole table unsorted except for one column.
     */
    void clearSortedColumns(TableColumn notThisOne) {
        boolean wasSorted = sortedColumns.contains(notThisOne);
        for (Iterator it = sortedColumns.iterator(); it.hasNext(); ) {
            Object o = it.next();
            if ((o instanceof ETableColumn) && (o != notThisOne)) {
                ETableColumn etc = (ETableColumn)o;
                etc.setSorted(0, false);
            }
        }
        sortedColumns = new ArrayList();
        if (wasSorted) {
            sortedColumns.add(notThisOne);
        }
    }
    
    /**
     * Reasigns sorting ranks to ETableColumns contained in sortedColumns list.
     */
    private void updateRanks() {
        int i = 1;
        for (Iterator it = sortedColumns.iterator(); it.hasNext(); i++) {
            Object o = it.next();
            if (o instanceof ETableColumn) {
                ETableColumn etc = (ETableColumn)o;
                if (etc.isSorted()) {
                    etc.setSortRank(i);
                }
            }
        }
    }
    
    /**
     * Comparator that delegates to individual comparators supplied by
     * ETableColumns. It uses only the columns contained in the sortedColumns
     * list.
     */
    private class CompoundComparator implements Comparator {
        private Comparator original;
        public CompoundComparator() {
            original = new ETable.OriginalRowComparator();
        }
        public int compare(Object o1, Object o2) {
            for (Iterator it = sortedColumns.iterator(); it.hasNext(); ) {
                Object o = it.next();
                if (o instanceof ETableColumn) {
                    ETableColumn etc = (ETableColumn)o;
                    Comparator c = etc.getComparator();
                    if (c != null) {
                        int res = c.compare(o1, o2);
                        if (res != 0) {
                            return res;
                        }
                    }
                }
            }
            return original.compare(o1, o2);
        }
    }
}
