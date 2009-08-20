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

package org.jdesktop.swingx.sort;

import java.util.Comparator;

import javax.swing.RowFilter;
import javax.swing.SortOrder;

/**
 * Defines the interactive sort control for sortable collection components (like 
 * JXList, JXTable). All sort gesture requests from their sort api 
 * are routed through the SortController.
 * <p>
 * 
 * This is very-much work-in-progress: while moving from ol' SwingX sorting to 
 * core jdk6 sorting we need a hook for sorting api on the view. So in terms of
 * jdk6 classes, this is something like:<p>
 * 
 * <code><pre>
 * SortController == DefaultRowSorter - RowSorter + XX
 * </pre></code>
 * All methods which change sort state must respect per-controller and per-column 
 * sortable property, as follows
 * <ol>
 * <li> if per-controller sortable is false, do nothing
 * <li> if per-controller sortable is true, if per-column sortable is false, do nothing
 * <li> if both are true toggle the SortOrder of the given column
 * </ol>
 *
 * PENDING JW: add RowFilter support<p>
 * PENDING JW: generics are introduced in the process of adding filter support and
 *   are experimental ... right now we get tons of warnings ...<p>
 * 
 *  @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 *  @author Jeanette Winzenburg
 * 
 */
public interface SortController<M> {

    /**
     * Sets whether or not this controller is sortable.<p>
     * 
     * The default is true.<p>
     * 
     * PENDING JW: define behaviour if sortable is disabled while has sortOrders.
     * In this case JXTable resets all sorts.
     * 
     * @param sortable whether or not this controller is sortable
     * @see #isSortable()        
     */
    void setSortable(boolean sortable);
    
    /**
     * Returns true if this controller is sortable; otherwise, false.
     *
     * @return true if this controller is sortable
     *         
     * @see #isSortable()
     */
    boolean isSortable();
    
    /**
     * Sets whether or not the specified column is sortable.<p>
     * 
     * The default is true.<p>
     * 
     * PENDING JW: define behaviour if sortable is disabled while has sortOrders.
     * In this case JXTable removes the sort of the column.<p>
     * 
     * PENDING JW: decide whether or not this method should trigger a resort
     * DefaultRowSorter explicitly doesn't, JXTable does.
     * 
     * @param column the column to enable or disable sorting on, in terms
     *        of the underlying model
     * @param sortable whether or not the specified column is sortable
     * @throws IndexOutOfBoundsException if <code>column</code> is outside
     *         the range of the model
     * @see #isSortable(int)        
     * @see #toggleSortOrder(int)
     * @see #setSortOrder(int, SortOrder)
     */
    void setSortable(int column, boolean sortable);
    
    /**
     * Returns true if the specified column is sortable. <p>
     * This returns true if both the controller's sortable property and
     * the column's sortable property is true. Returns false if any of
     * them is false.
     *
     * @param column the column to check sorting for, in terms of the
     *        underlying model
     * @return true if the column is sortable
     * @throws IndexOutOfBoundsException if column is outside
     *         the range of the underlying model
     *         
     * @see #isSortable(int)
     */
    boolean isSortable(int column);

    /**
     * Sets the <code>Comparator</code> to use when sorting the specified
     * column.  This does not trigger a sort.  If you want to sort after
     * setting the comparator you need to explicitly invoke <code>sort</code>.
     *
     * @param column the index of the column the <code>Comparator</code> is
     *        to be used for, in terms of the underlying model
     * @param comparator the <code>Comparator</code> to use
     * @throws IndexOutOfBoundsException if <code>column</code> is outside
     *         the range of the underlying model
     */
    public void setComparator(int column, Comparator<?> comparator);

    /**
     * Returns the <code>Comparator</code> for the specified 
     * column.  This will return <code>null</code> if a <code>Comparator</code>
     * has not been specified for the column.
     *
     * @param column the column to fetch the <code>Comparator</code> for, in
     *        terms of the underlying model
     * @return the <code>Comparator</code> for the specified column
     * @throws IndexOutOfBoundsException if column is outside
     *         the range of the underlying model
     */
    public Comparator<?> getComparator(int column);
    
    /**
     * Reverses the sort order of the specified column. The exact behaviour is
     * up to implementations.<p>
     * 
     * Implementations must respect the per-controller and per-column-sortable 
     * property.
     * 
     * @param column the model index of the column to toggle
     * @see #isSortable(int) 
     * @see #isSortable()
     */
    void toggleSortOrder(int column);

    /**
     * Sets the sort order of the specified column. <p>
     * 
     * Implementations must respect the per-controller and per-column-sortable 
     * property.
     *
     * @param column the model index of the column to set
     * @param sortOrder the SortOrder to set for the column
     * 
     * @see #isSortable(int) 
     * @see #isSortable()
     */
    void setSortOrder(int column, SortOrder sortOrder);
 
    /**
     * Returns the sort order of the specified column.
     * 
     * 
     * @return one of {@link SortOrder#ASCENDING},
     *     {@link SortOrder#DESCENDING} or {@link SortOrder#UNSORTED}.
     */
    SortOrder getSortOrder(int column);

    
    /**
     * Resets all interactive sorting. <p>
     * 
     * Implementations must respect the per-controller and per-column-sortable 
     * property.
     * 
     */
    void resetSortOrders();
    
    /**
     * Sets the cycle of sort ordes to toggle through. Zero or more SortOrders which
     * must not be null.
     * 
     * @param cycle the SortOrders to cycle through, may be empty
     * @throws NullPointerException if the array or any of its elements is null
     */
    void setSortOrderCycle(SortOrder... cycle);
    
    /**
     * Returns the cycle of sort orders to cycle through.
     * 
     * @return
     */
    SortOrder[] getSortOrderCycle();
    
    /**
     * If true, specifies that a sort should happen when the underlying
     * model is updated (<code>rowsUpdated</code> is invoked).  For
     * example, if this is true and the user edits an entry the
     * location of that item in the view may change.  The default is
     * true.
     *
     * @param sortsOnUpdates whether or not to sort on update events
     */
    void setSortsOnUpdates(boolean sortsOnUpdates);

    /**
     * Returns true if  a sort should happen when the underlying
     * model is updated; otherwise, returns false.
     *
     * @return whether or not to sort when the model is updated
     */
    boolean getSortsOnUpdates();
    
//-------------------- filter
    
    /**
     * Sets the filter that determines which rows, if any, should be
     * hidden from the view.  The filter is applied before sorting.  A value
     * of <code>null</code> indicates all values from the model should be
     * included.
     * <p>
     * <code>RowFilter</code>'s <code>include</code> method is passed an
     * <code>Entry</code> that wraps the underlying model.  The number
     * of columns in the <code>Entry</code> corresponds to the
     * number of columns in the underlying model.  The identifier
     * comes from the underlying model as well.
     * <p>
     * This method triggers a sort.
     *
     * PENDING JW: the "underlying model" is the ModelWrapper ... want to 
     * expose here as well? Otherwise, the second paramter doesn't make much sense.
     * 
     * @param filter the filter used to determine what entries should be
     *        included
     */
     void setRowFilter(RowFilter<? super M, ? super Integer> filter);

    /**
     * Returns the filter that determines which rows, if any, should
     * be hidden from view.
     *
     * @return the filter
     */
     RowFilter<? super M,? super Integer> getRowFilter();

    /**
     * Sets the StringValueProvider to look up the StringValues. If the value
     * is not-null, it guarantees to use it exclusively for string conversion. <p>
     * 
     * PENDING JW: this is more or less parallel to TableStringConverter. Need to think
     * about merging somehow.
     * 
     * @param provider the look up for StringValues, may be null.
     */
    void setStringValueProvider(StringValueProvider provider);

    /**
     * @return
     */
    StringValueProvider getStringValueProvider();


}