/*
 * BeanArrayList.java
 *
 * Created on October 28, 2006, 1:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.util;

/*
 * BeanArrayList.java
 *
 * Created on May 21, 2004, 7:23 PM
 *
 * Copyright (C) 2004, 2005  Robert Cooper, Temple of the Screaming Penguin
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;

import java.lang.Comparable;
import java.lang.reflect.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.ArrayList;


/** This is an extension of ArrayList that provides some handy utilities for working with JavaBeans.
 * Includes basic statistical information, page chunking and storing and can also be used as a manager for
 * indexed properies and supports PropertyChangeEvents.
 *
 * @version $Rev: 79 $
 * @author  <a href="cooper@screaming-penguin.com">Robert Cooper</a>
 */
public class BeanArrayList<T> extends ArrayList<T> {
    /**
     * DOCUMENT ME!
     */
    Collection source;

    /**
     * DOCUMENT ME!
     */
    private PropertyChangeSupport changes;

    /**
     * DOCUMENT ME!
     */
    private String indexPropertyName;

    /**
     * DOCUMENT ME!
     */
    private int nextChunk = -1;

    /**
     * DOCUMENT ME!
     */
    private int numberOfChunks = 1;

    /**
     * DOCUMENT ME!
     */
    private int previousChunk = -1;

    /** No Args Contstructor */
    public BeanArrayList() {
        super();
    }

    /**
     * This contructs a new BeanArrayList with PropertyChangeEvent support.
     * @param indexPropertyName the property name of the parent object to fire events for
     * @param changeOwner the owner object that change events should "come from"
     */
    public BeanArrayList(String indexPropertyName,Object changeOwner) {
        this();
        this.indexPropertyName = indexPropertyName;
        this.changes = new PropertyChangeSupport(changeOwner);
    }

    /**
     * Creates a new instance of BeanArrayList prepopulating off a
     * collection, limited to only the desired chunk.
     * @param source Collection to read from
     * @param chunkSize int number of object to return.
     * @param currentChunk int value of the chunk to get (zero index)
     */
    public BeanArrayList(int chunkSize,int currentChunk,Collection<T> source) {
        super();
        this.source = source;

        Iterator<T> itr = source.iterator();

        if(source.size() > 0) {
            this.numberOfChunks = source.size() / chunkSize;

            if((source.size() % chunkSize) > 0) {
                this.numberOfChunks++;
            }

            //spin
            for(int i = 0; i < (chunkSize * currentChunk); i++) {
                if(!itr.hasNext()) {
                    continue;
                }

                itr.next();
            }

            for(int i = 0; i < chunkSize; i++) {
                if(!itr.hasNext()) {
                    continue;
                }

                this.add(itr.next());
            }

            if(currentChunk != 0) {
                previousChunk = currentChunk - 1;
            }

            if(source.size() > ((currentChunk + 1) * chunkSize)) {
                nextChunk = currentChunk + 1;
            }
        }
    }

    /**
     * Creates a new instance of BeanArrayList prepopulating off a
     * collection, limited to only the desired chunk, and includes
     * PropertyChangeEvent support.
     * @param indexPropertyName the property name of the parent object to fire events for
     * @param changeOwner the object change events should come from
     * @param chunkSize number of objects to return
     * @param currentChunk int value of the chunk to get (zero index)
     * @param source source Collection to read from.
     */
    public BeanArrayList(String indexPropertyName,Object changeOwner,int chunkSize,int currentChunk,Collection<T> source) {
        this(chunkSize,currentChunk,source);
        this.indexPropertyName = indexPropertyName;
        this.changes = new PropertyChangeSupport(changeOwner);
    }

    /** Creates a new instance of BeanArrayList
     * @param source Collection containing the initial values.
     */
    public BeanArrayList(Collection<T> source) {
        this(source.size(),0,source);
    }

    /**
     * This contructs a new BeanArrayList with PropertyChangeEvent support.
     * @param indexPropertyName the property name of the parent object to fire events for
     * @param changeOwner the object change events should come from
     * @param source Collection to prepopulate from.
     */
    public BeanArrayList(String indexPropertyName,Object changeOwner,Collection<T> source) {
        this(source);
        this.indexPropertyName = indexPropertyName;
        this.changes = new PropertyChangeSupport(changeOwner);
    }

    /**
     * Gets a chunk of this ArrayList.
     * @param chunkSize int number of object to return.
     * @param currentChunk int value of the chunk to get (zero index)
     * @return new BeanArrayList representing the chunk requested.
     */
    public BeanArrayList getChunk(int chunkSize,int currentChunk) {
        return new BeanArrayList(chunkSize,currentChunk,this);
    }

    /**
     * Overrides the parent to support PropertyChangeEvents
     * @param obj new object value
     * @param index index position to place the object
     */
    public void setElementAt(T obj,int index) {
        T old = null;

        if((this.indexPropertyName != null)&&(this.changes != null)) {
            old = this.get(index);
        }

        super.set(index, obj);

        if(old != null) {
            changes.fireIndexedPropertyChange(this.indexPropertyName,index,old,obj);
        }
    }

    /**
     * Filters a property using the Comparable.compareTo() on the porperty to
     * test for a range
     * @param propertyName property to filter on
     * @param inclusive include the values of the range limiters
     * @param fromValue low range value
     * @param toValue high range value
     * @throws java.lang.IllegalAccessException reflection exception
     * @throws java.beans.IntrospectionException reflection exception
     * @throws java.lang.reflect.InvocationTargetException reflection exception
     * @return new BeanArrayList filtered on the range
     */
    public BeanArrayList<T> getFiltered(String propertyName,boolean inclusive,Comparable fromValue,Comparable toValue) throws java.lang.IllegalAccessException,java.beans.IntrospectionException,java.lang.reflect.InvocationTargetException {
        HashMap cache = new HashMap();
        String currentClass = "";
        PropertyDescriptor pd = null;
        BeanArrayList<T> results = new BeanArrayList<T>();

        for(int i = 0; i < this.size(); i++) {
            T o = this.get(i);

            if(!currentClass.equals(o.getClass().getName())) {
                pd = (PropertyDescriptor)cache.get(o.getClass().getName());

                if(pd == null) {
                    PropertyDescriptor[] pds = Introspector.getBeanInfo(o.getClass()).getPropertyDescriptors();
                    boolean foundProperty = false;

                    for(int pdi = 0; (pdi < pds.length)&&!foundProperty;
                            pdi++) {
                        if(pds[pdi].getName().equals(propertyName)) {
                            pd = pds[pdi];
                            cache.put(o.getClass().getName(),pd);
                            foundProperty = true;
                        }
                    }
                }
            }

            Comparable value = (Comparable)pd.getReadMethod().invoke(o);

            if((value.compareTo(fromValue) > 0)&&(value.compareTo(toValue) < 0)) {
                results.add(o);
            } else if(inclusive&&((value.compareTo(fromValue) == 0)||(value.compareTo(toValue) == 0))) {
                results.add(o);
            }
        }

        return results;
    }

    /**
     * This method does a string match on values of a property.
     * @param propertyName String value containing the name of the property to match.
     * @param match Value to search for. This is a case-insensitive value that takes % as a multiple character wildcard value.
     * @throws java.lang.IllegalAccessException reflection exception
     * @throws java.beans.IntrospectionException reflection exception
     * @throws java.lang.reflect.InvocationTargetException reflection exception
     * @return a new BeanArrayList filtered on the specified property
     */
    public BeanArrayList<T> getFilteredStringMatch(String propertyName,String match) throws java.lang.IllegalAccessException,java.beans.IntrospectionException,java.lang.reflect.InvocationTargetException {
        HashMap cache = new HashMap();
        String currentClass = "";
        PropertyDescriptor pd = null;
        BeanArrayList<T> results = new BeanArrayList<T>();

        for(int i = 0; i < this.size(); i++) {
            T o = this.get(i);

            if(!currentClass.equals(o.getClass().getName())) {
                pd = (PropertyDescriptor)cache.get(o.getClass().getName());

                if(pd == null) {
                    PropertyDescriptor[] pds = Introspector.getBeanInfo(o.getClass()).getPropertyDescriptors();
                    boolean foundProperty = false;

                    for(int pdi = 0; (pdi < pds.length)&&!foundProperty;
                            pdi++) {
                        if(pds[pdi].getName().equals(propertyName)) {
                            pd = pds[pdi];
                            cache.put(o.getClass().getName(),pd);
                            foundProperty = true;
                        }
                    }
                }
            }

            String value = pd.getReadMethod().invoke(o).toString().toLowerCase();
            StringTokenizer st = new StringTokenizer(match.toLowerCase(),"%");
            boolean isMatch = true;
            int matchIndex = 0;

            while(st.hasMoreTokens()&&isMatch) {
                String tk = st.nextToken();

                if(value.indexOf(tk,matchIndex) == -1) {
                    isMatch = false;
                } else {
                    matchIndex = value.indexOf(tk,matchIndex) + tk.length();
                }
            }

            if(isMatch) {
                results.add(o);
            }
        }

        return results;
    }

    /**
     * This method returns the average value of a numerical property.
     * @param propertyName String value of the property name to calculate.
     * @throws java.lang.IllegalAccessException reflection exception
     * @throws java.beans.IntrospectionException reflection exception
     * @throws java.lang.reflect.InvocationTargetException reflection exception
     * @return Average of property values.
     */
    public Number getMeanOfProperty(String propertyName) throws java.lang.IllegalAccessException,java.beans.IntrospectionException,java.lang.reflect.InvocationTargetException {
        double mean = this.getSumOfProperty(propertyName).doubleValue() / this.size();

        return new Double(mean);
    }

    /**
     * This method returns the index of the bean with the median value
     * on the specified property.
     *
     * <p>If there is an odd number of items in the dataset, the one below
     * the 50% mark will be returned. The true mathmatical mean, therefore
     * would be:
     * <code>
     * (beanArrayList.get(x).getProperty() + beanArrayList.get(x+1).getProperty() )/2
     * </code></p>
     * @param propertyName String value of the property name to calculate.
     * @throws java.lang.IllegalAccessException reflection exception
     * @throws java.beans.IntrospectionException reflection exception
     * @throws java.lang.reflect.InvocationTargetException reflection exception
     * @return int value of the median index of the ArrayList
     */
    public int getMedianIndex(String propertyName) throws java.lang.IllegalAccessException,java.beans.IntrospectionException,java.lang.reflect.InvocationTargetException {
        BeanArrayList bv = new BeanArrayList(this.size(),0,this);
        bv.sortOnProperty(propertyName);

        int orderedIndex = bv.size() / 2;
        Object o = bv.get(orderedIndex);

        return this.indexOf(o);
    }

    /**
     * This method returns the index of an object representing the
     * mode value of a property name.
     * @param propertyName String value of the property name to calculate.
     * @throws java.lang.IllegalAccessException reflection exception
     * @throws java.beans.IntrospectionException reflection exception
     * @throws java.lang.reflect.InvocationTargetException reflection exception
     * @return int value of the mode index
     */
    public int getModeIndex(String propertyName) throws java.lang.IllegalAccessException,java.beans.IntrospectionException,java.lang.reflect.InvocationTargetException {
        int index = -1;
        int max = 0;
        int count = 0;
        Object o = null;
        Object hold = null;
        HashMap cache = new HashMap();
        String currentClass = "";
        PropertyDescriptor pd = null;
        BeanArrayList bv = new BeanArrayList(this.size(),0,this);
        bv.sortOnProperty(propertyName);

        for(int i = 0; i < bv.size(); i++) {
            if(!currentClass.equals(bv.get(i).getClass().getName())) {
                pd = (PropertyDescriptor)cache.get(bv.get(i).getClass().getName());

                if(pd == null) {
                    PropertyDescriptor[] pds = Introspector.getBeanInfo(bv.get(i).getClass()).getPropertyDescriptors();
                    boolean foundProperty = false;

                    for(int pdi = 0; (pdi < pds.length)&&!foundProperty;
                            pdi++) {
                        if(pds[pdi].getName().equals(propertyName)) {
                            pd = pds[pdi];
                            cache.put(bv.get(i).getClass().getName(),pd);
                            foundProperty = true;
                        }
                    }
                }
            }

            if(hold == null) {
                hold = pd.getReadMethod().invoke(bv.get(i));
            } else {
                o = pd.getReadMethod().invoke(bv.get(i));

                if((o != null)&&o.equals(hold)) {
                    count++;

                    if(count > max) {
                        max = count;
                        index = this.indexOf(bv.get(i));
                    }
                } else {
                    count = 1;
                }

                hold = o;
            }
        }

        return index;
    }

    /** Returns -1 or the the index of the next chunk after the current
     * ArrayList.
     * @return -1 or the the index of the next chunk after the current ArrayList
     */
    public int getNextChunk() {
        return this.nextChunk;
    }

    /**
     * returns the number of chunks in the ArrayList
     * @return int value number of chunks
     */
    public int getNumberOfChunks() {
        return this.numberOfChunks;
    }

    /** Returns -1 or the the index of the previous chunk before the
     * current ArrayList.
     * @return -1 or the the index of the previous chunk before the
     * current ArrayList
     */
    public int getPreviousChunk() {
        return this.previousChunk;
    }

    /**
     * This method returns the sum of all values of a numerical
     * property.
     * @param propertyName String value of the property name to calculate.
     * @throws java.lang.IllegalAccessException reflection exception
     * @throws java.beans.IntrospectionException reflection exception
     * @throws java.lang.reflect.InvocationTargetException reflection exception
     * @return sum of a numerical property
     */
    public Number getSumOfProperty(String propertyName) throws java.lang.IllegalAccessException,java.beans.IntrospectionException,java.lang.reflect.InvocationTargetException {
        double d = 0.0;
        String currentClass = "";
        PropertyDescriptor pd = null;

        for(int i = 0; i < this.size(); i++) {
            T o = this.get(i);

            if(!currentClass.equals(o.getClass().getName())) {
                PropertyDescriptor[] pds = Introspector.getBeanInfo(o.getClass()).getPropertyDescriptors();
                boolean foundProperty = false;

                for(int pdi = 0; (pdi < pds.length)&&!foundProperty; pdi++) {
                    if(pds[pdi].getName().equals(propertyName)) {
                        pd = pds[pdi];
                        foundProperty = true;
                    }
                }
            }

            if(o != null) {
                Number n = (Number)pd.getReadMethod().invoke(o);
                d += n.doubleValue();
            }
        }

        return new Double(d);
    }

    /**
     * Inserts the specified element at the specified position in this ArrayList.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     * @since 1.2
     * @param index index at which the specified element is to be inserted.
     * @param element element to be inserted.
     */
    public void add(int index,T element) {
        T[] old = null;

        if((this.indexPropertyName != null)&&(this.changes != null)) {
            old = this.toTypedArray();
        }

        super.add(index,element);

        if(old != null) {
            changes.firePropertyChange(this.indexPropertyName,old,this.toTypedArray());
        }
    }

    /**
     * Appends the specified element to the end of this ArrayList.
     *
     * @param o element to be appended to this ArrayList.
     * @return true (as per the general contract of Collection.add).
     * @since 1.2
     */
    public boolean add(T o) {
        boolean retValue;
        retValue = super.add(o);

        if((this.indexPropertyName != null)&&(this.changes != null)) {
            changes.fireIndexedPropertyChange(this.indexPropertyName,this.size() - 1,null,o);
        }

        return retValue;
    }

    /**
     * Appends all of the elements in the specified Collection to the end of
     * this ArrayList, in the order that they are returned by the specified
     * Collection's Iterator.  The behavior of this operation is undefined if
     * the specified Collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified Collection is this ArrayList, and this ArrayList is nonempty.)
     *
     * @return <tt>true</tt> if this ArrayList changed as a result of the call.
     * @since 1.2
     * @param c elements to be inserted into this ArrayList.
     */
    public boolean addAll(Collection<? extends T> c) {
        boolean retValue;
        T[] old = null;

        if((this.indexPropertyName != null)&&(this.changes != null)) {
            old = this.toTypedArray();
        }

        retValue = super.addAll(c);

        if(retValue&&(old != null)) {
            changes.firePropertyChange(this.indexPropertyName,old,this.toTypedArray());
        }

        return retValue;
    }

    /**
     * Inserts all of the elements in the specified Collection into this
     * ArrayList at the specified position.  Shifts the element currently at
     * that position (if any) and any subsequent elements to the right
     * (increases their indices).  The new elements will appear in the ArrayList
     * in the order that they are returned by the specified Collection's
     * iterator.
     *
     * @return <tt>true</tt> if this ArrayList changed as a result of the call.
     * @since 1.2
     * @param index index at which to insert first element
     *                     from the specified collection.
     * @param c elements to be inserted into this ArrayList.
     */
    public boolean addAll(int index,Collection<? extends T> c) {
        boolean retValue;
        T[] old = null;

        if((this.indexPropertyName != null)&&(this.changes != null)) {
            old = this.toTypedArray();
        }

        retValue = super.addAll(index,c);

        if(retValue&&(old != null)) {
            changes.firePropertyChange(this.indexPropertyName,old,this.toTypedArray());
        }

        return retValue;
    }
  

    /**
     * Registers propertyChangeListeners to be fired when something changes in the ArrayList.
     * Note, the whole ArrayList is an "indexedProperty" name specified and parent delineated in
     * the constructor.
     * @param listener PropertyChangeListener to register
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    /**
     * Registers propertyChangeListeners to be fired when something changes in the ArrayList.
     * Note, the whole ArrayList is an "indexedProperty" name specified and parent delineated in
     * the constructor.
     * @param property propteryName must match what the property name of this ArrayList is or the listener will be ignored.
     * @param listener the listener to register
     */
    public void addPropertyChangeListener(String property,PropertyChangeListener listener) {
        if((property != null)&&property.equals(this.indexPropertyName)) {
            changes.addPropertyChangeListener(property,listener);
        }
    }

    /**
     * This method reverses the order of the ArrayList.
     */
    public synchronized void invert() {
        BeanArrayList<T> temp = new BeanArrayList<T>(this);

        for(int i = 0; i < this.size(); i++) {
            this.set(i, temp.get( temp.size() -1 ) );
            temp.remove( temp.get( temp.size() -1 ) );
        }
    }

    /**
     * Removes the first occurrence of the specified element in this ArrayList
     * If the ArrayList does not contain the element, it is unchanged.  More
     * formally, removes the element with the lowest index i such that
     * <code>(o==null ? get(i)==null : o.equals(get(i)))</code> (if such
     * an element exists).
     *
     * @param o element to be removed from this ArrayList, if present.
     * @return true if the ArrayList contained the specified element.
     * @since 1.2
     */
    public boolean remove(Object o) {
        boolean retValue;
        T[] old = null;

        if((this.indexPropertyName != null)&&(this.changes != null)) {
            old = this.toTypedArray();
        }

        retValue = super.remove(o);

        if(retValue&&(old != null)) {
            changes.firePropertyChange(this.indexPropertyName,old,this.toTypedArray());
        }

        return retValue;
    }

    /**
     * Removes the element at the specified position in this ArrayList.
     * shifts any subsequent elements to the left (subtracts one from their
     * indices).  Returns the element that was removed from the ArrayList.
     *
     * @return element that was removed
     * @since 1.2
     * @param index the index of the element to removed.
     */
    public T remove(int index) {
        T retValue;
        T[] old = null;

        if((this.indexPropertyName != null)&&(this.changes != null)) {
            old = this.toTypedArray();
        }

        retValue = super.remove(index);

        if((retValue != null)&&(old != null)) {
            changes.firePropertyChange(this.indexPropertyName,old,this.toTypedArray());
        }

        return retValue;
    }

    /**
     * Removes from this ArrayList all of its elements that are contained in the
     * specified Collection.
     *
     * @return true if this ArrayList changed as a result of the call.
     * @since 1.2
     * @param c a collection of elements to be removed from the ArrayList
     */
    public boolean removeAll(Collection<? > c) {
        boolean retValue;
        T[] old = null;

        if((this.indexPropertyName != null)&&(this.changes != null)) {
            old = this.toTypedArray();
        }

        retValue = super.removeAll(c);

        if(retValue&&(old != null)) {
            changes.firePropertyChange(this.indexPropertyName,old,this.toTypedArray());
        }

        return retValue;
    }

    /**
     * Removes all components from this ArrayList and sets its size to zero.<p>
     *
     * This method is identical in functionality to the clear method
     * (which is part of the List interface).
     *
     * @see        #clear
     * @see        List
     */
    public void removeAllElements() {
        T[] old = null;

        if((this.indexPropertyName != null)&&(this.changes != null)) {
            old = this.toTypedArray();
        }

        super.clear();

        if(old != null) {
            changes.firePropertyChange(this.indexPropertyName,old,this.toTypedArray());
        }
    }

    /**
     * Removes the first (lowest-indexed) occurrence of the argument
     * from this ArrayList. If the object is found in this ArrayList, each
     * component in the ArrayList with an index greater or equal to the
     * object's index is shifted downward to have an index one smaller
     * than the value it had previously.<p>
     *
     * This method is identical in functionality to the remove(Object)
     * method (which is part of the List interface).
     *
     * @param   obj   the component to be removed.
     * @return  <code>true</code> if the argument was a component of this
     *          ArrayList; <code>false</code> otherwise.
     * @see        List#remove(Object)
     * @see        List
     */
    public boolean removeElement(Object obj) {
        boolean retValue;
        T[] old = null;

        if((this.indexPropertyName != null)&&(this.changes != null)) {
            old = this.toTypedArray();
        }

        retValue = super.remove(obj);
        
        if(retValue&&(old != null)) {
            changes.firePropertyChange(this.indexPropertyName,old,this.toTypedArray());
        }

        return retValue;
    }

    /**
     * Deletes the component at the specified index. Each component in
     * this ArrayList with an index greater or equal to the specified
     * <code>index</code> is shifted downward to have an index one
     * smaller than the value it had previously. The size of this ArrayList
     * is decreased by <tt>1</tt>.<p>
     *
     * The index must be a value greater than or equal to <code>0</code>
     * and less than the current size of the ArrayList. <p>
     *
     * This method is identical in functionality to the remove method
     * (which is part of the List interface).  Note that the remove method
     * returns the old value that was stored at the specified position.
     *
     * @see #size()
     * @see #remove(int)
     * @see List
     * @param index the index of the object to remove.
     */
    public void removeElementAt(int index) {
        T[] old = null;

        if((this.indexPropertyName != null)&&(this.changes != null)) {
            old = this.toTypedArray();
        }

        super.remove(index);

        if(old != null) {
            changes.firePropertyChange(this.indexPropertyName,old,this.toTypedArray());
        }
    }

    /**
     * Removes propertyChangeListeners to be fired when something changes in the ArrayList.
     * Note, the whole ArrayList is an "indexedProperty" name specified and parent delineated in
     * the constructor.
     * @param listener listener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }

    /**
     * Removes propertyChangeListeners to be fired when something changes in the ArrayList.
     * Note, the whole ArrayList is an "indexedProperty" name specified and parent delineated in
     * the constructor.
     * @param property must match the current property name for the ArrayList or will be ignored
     * @param listener listener to remove
     */
    public void removePropertyChangeListener(String property,PropertyChangeListener listener) {
        if((property != null)&&property.equals(this.indexPropertyName)) {
            changes.removePropertyChangeListener(property,listener);
        }
    }

    /**
     * Resets the contents of the ArrayList to the values provided.
     * @param contents Array of object to replace the current contents with
     */
    public synchronized void resetContents(T[] contents) {
        if((this.indexPropertyName != null)&&(this.changes != null)) {
            changes.firePropertyChange(this.indexPropertyName,this.toTypedArray(),contents);
        }

        this.removeAllElements();

        for(T t : contents)
            this.add(t);
    }

    /**
     * Retains only the elements in this ArrayList that are contained in the
     * specified Collection.  In other words, removes from this ArrayList all
     * of its elements that are not contained in the specified Collection.
     *
     * @return true if this ArrayList changed as a result of the call.
     * @since 1.2
     * @param c a collection of elements to be retained in this ArrayList
     *          (all other elements are removed)
     */
    public boolean retainAll(Collection<? > c) {
        boolean retValue;
        T[] old = null;

        if((this.indexPropertyName != null)&&(this.changes != null)) {
            old = this.toTypedArray();
        }

        retValue = super.retainAll(c);

        if(retValue&&(old != null)) {
            changes.firePropertyChange(this.indexPropertyName,old,this.toTypedArray());
        }

        return retValue;
    }

    /**
     * Replaces the element at the specified position in this ArrayList with the
     * specified element.
     *
     * @return the element previously at the specified position.
     * @since 1.2
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     */
    public T set(int index,T element) {
        T retValue;
        retValue = super.set(index,element);

        if((this.indexPropertyName != null)&&(this.changes != null)) {
            changes.fireIndexedPropertyChange(this.indexPropertyName,index,retValue,element);
        }

        return retValue;
    }

    /**
     * performs a selection sort on all the beans in the ArrayList by PropertyName
     *
     * <p>You can use a mixture of bean classes as long as all the beans support
     * the same property (getName() for instance), and all have the same return
     * type.</p>
     *
     * <p>For optimal performance, it is recommended that if you have a
     * mixed class set the you have them grouped with like classes together as this
     * will minimize reflection inspections.</p>
     * @param propertyName String value containing the property to sort
     * on.
     * @throws java.lang.IllegalAccessException Property was not accessible
     * @throws java.beans.IntrospectionException Couldn't introspect
     * @throws java.lang.reflect.InvocationTargetException Is the proper signature getProperty() ?
     */
    public void sortOnProperty(String propertyName) throws java.lang.IllegalAccessException,java.beans.IntrospectionException,java.lang.reflect.InvocationTargetException {
        this.sortOnProperty(propertyName,true);
    }

    /**
     * performs a selection sort on all the beans in the ArrayList by
     * PropertyName
     *
     * <p>You can use a mixture of bean classes as long as all the beans
     * support the same property (getName() for instance), and all have the
     * same return type, or can be compareTo()ed each other.</p>
     *
     * <p>For optimal performance, it is recommended that if you have a
     * mixed class set the you have them grouped with like classes together
     * as this will minimize reflection inspections.</p>
     * @param propertyName String value containing the property to sort on.
     * @param ascending == sorts up if true, down if not.
     * @throws java.lang.IllegalAccessException reflection exception
     * @throws java.beans.IntrospectionException reflection exception
     * @throws java.lang.reflect.InvocationTargetException reflection exception
     */
    public synchronized void sortOnProperty(String propertyName,boolean ascending) throws java.lang.IllegalAccessException,java.beans.IntrospectionException,java.lang.reflect.InvocationTargetException {
        T[] old = null;

        if((this.indexPropertyName != null)&&(this.changes != null)) {
            old = this.toTypedArray();
        }

        T temp = null;
        String currentClass = "";
        PropertyDescriptor pd = null;
        HashMap cache = new HashMap();

        for(int i = 0; i < (this.size() - 1); i++) {
            for(int j = i + 1; j < this.size(); j++) {
                T o1 = this.get(i);

                if(!currentClass.equals(o1.getClass().getName())) {
                    pd = (PropertyDescriptor)cache.get(o1.getClass().getName());

                    if(pd == null) {
                        PropertyDescriptor[] pds = Introspector.getBeanInfo(o1.getClass()).getPropertyDescriptors();
                        boolean foundProperty = false;

                        for(int pdi = 0; (pdi < pds.length)&&!foundProperty;
                                pdi++) {
                            if(pds[pdi].getName().equals(propertyName)) {
                                pd = pds[pdi];
                                cache.put(o1.getClass().getName(),pd);
                                foundProperty = true;
                            }
                        }
                    }
                }

                //System.out.println( "o1: "+o1+" "+pd);
                //System.out.println( propertyName +" "+ (pd == null ));
                Comparable oc1 = (Comparable)pd.getReadMethod().invoke(o1);

                T o2 = this.get(j);

                if(!currentClass.equals(o2.getClass().getName())) {
                    pd = (PropertyDescriptor)cache.get(o2.getClass().getName());

                    if(pd == null) {
                        PropertyDescriptor[] pds = Introspector.getBeanInfo(o2.getClass()).getPropertyDescriptors();
                        boolean foundProperty = false;

                        for(int pdi = 0; (pdi < pds.length)&&!foundProperty;
                                pdi++) {
                            if(pds[pdi].getName().equals(propertyName)) {
                                pd = pds[pdi];
                                foundProperty = true;
                            }
                        }
                    }
                }

                Comparable oc2 = (Comparable)pd.getReadMethod().invoke(o2);

                if(ascending) {
                    if((oc1 != oc2)&&((oc2 == null)||((oc1 != null)&&(oc2 != null)&&(oc2.compareTo(oc1) < 0)))) { //swap
                        this.setElementAt(o2,i);
                        this.setElementAt(o1,j);
                    }
                } else {
                    if((oc1 != oc2)&&((oc1 == null)||((oc1 != null)&&(oc2 != null)&&(oc1.compareTo(oc2) < 0)))) { //swap
                        this.setElementAt(o2,i);
                        this.setElementAt(o1,j);
                    }
                }
            }

            if(old != null) {
                changes.firePropertyChange(this.indexPropertyName,old,this.toTypedArray());
            }
        }
    }

    /**
     * Returns an Array of the generic type associated with this ArrayList.
     * @return Array representation of the current ArrayList.
     */
    public T[] toTypedArray() {
        return (T[])this.toArray();
    }

    /**
     * Removes from this List all of the elements whose index is between
     * fromIndex, inclusive and toIndex, exclusive.  Shifts any succeeding
     * elements to the left (reduces their index).
     * This call shortens the ArrayList by (toIndex - fromIndex) elements.  (If
     * toIndex==fromIndex, this operation has no effect.)
     *
     * @param fromIndex index of first element to be removed.
     * @param toIndex index after last element to be removed.
     */
    protected void removeRange(int fromIndex,int toIndex) {
        T[] old = null;

        if((this.indexPropertyName != null)&&(this.changes != null)) {
            old = this.toTypedArray();
        }

        super.removeRange(fromIndex,toIndex);

        if(old != null) {
            changes.firePropertyChange(this.indexPropertyName,old,this.toTypedArray());
        }
    }
}
