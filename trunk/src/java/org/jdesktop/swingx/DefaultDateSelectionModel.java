/**
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
 */
package org.jdesktop.swingx;

import static org.jdesktop.swingx.event.DateSelectionEvent.EventType;
import org.jdesktop.swingx.calendar.JXMonthView;
import org.jdesktop.swingx.event.EventListenerMap;
import org.jdesktop.swingx.event.DateSelectionEvent;

import java.util.*;

/**
 * @author Joshua Outwater
 */
public class DefaultDateSelectionModel implements DateSelectionModel {
    private JXMonthView monthView;
    private EventListenerMap listenerList;
    private SelectionMode selectionMode;
    private Set<Date> selectedDates;
    private Calendar cal;

    public DefaultDateSelectionModel(JXMonthView monthView) {
        this.listenerList = new EventListenerMap();
        this.monthView = monthView;
        selectionMode = SelectionMode.SINGLE_SELECTION;
        selectedDates = new TreeSet<Date>();
        cal = Calendar.getInstance();
    }

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(final SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
        clearSelection();
    }

    public void addSelectionInterval(final Date startDate, final Date endDate) {
        if (startDate.after(endDate)) {
            return;
        }

        switch (selectionMode) {
            case NO_SELECTION:
                return;
            case SINGLE_SELECTION:
                clearSelectionImpl();
                selectedDates.add(startDate);
                break;
            case SINGLE_INTERVAL_SELECTION:
                clearSelectionImpl();
                setSingleIntervalSelection(startDate, endDate);
                break;
            case WEEK_INTERVAL_SELECTION:
                clearSelection();
                setWeekIntervalSelection(startDate, endDate);
                break;
            case MULTIPLE_INTERVAL_SELECTION:
                addSelectionImpl(startDate, endDate);
                break;
            default:
                break;
        }
        fireValueChanged(EventType.DATES_ADDED);
    }

    public void setSelectionInterval(final Date startDate, final Date endDate) {
        switch (selectionMode) {
            case NO_SELECTION:
                return;
            case SINGLE_SELECTION:
                clearSelectionImpl();
                selectedDates.add(startDate);
                break;
            case SINGLE_INTERVAL_SELECTION:
                clearSelectionImpl();
                setSingleIntervalSelection(startDate, endDate);
                break;
            case WEEK_INTERVAL_SELECTION:
                clearSelectionImpl();
                setWeekIntervalSelection(startDate, endDate);
                break;
            case MULTIPLE_INTERVAL_SELECTION:
                clearSelectionImpl();
                addSelectionImpl(startDate, endDate);
                break;
            default:
                break;
        }
        fireValueChanged(EventType.DATES_SET);
    }

    /** TODO: This is really only useful for multiple selection.  Maybe restrict to that mode??? */
    public void removeSelectionInterval(final Date startDate, final Date endDate) {
        if (startDate.after(endDate)) {
            return;
        }

        cal.setTime(startDate);
        Date date = cal.getTime();
        while(date.before(endDate) || date.equals(endDate)) {
            selectedDates.remove(date);
            cal.add(Calendar.DATE, 1);
            date = cal.getTime();
        }
        fireValueChanged(EventType.DATES_REMOVED);
    }

    public void clearSelection() {
        clearSelectionImpl();
        fireValueChanged(EventType.SELECTION_CLEARED);
    }

    private void clearSelectionImpl() {
        selectedDates.clear();
    }

    public SortedSet<Date> getSelection() {
        return new TreeSet<Date>(selectedDates);
    }

    public boolean isSelected(final Date date) {
        return selectedDates.contains(date);
    }

    public void addDateSelectionListener(DateSelectionListener l) {
        listenerList.add(DateSelectionListener.class, l);
    }

    public void removeDateSelectionListener(DateSelectionListener l) {
        listenerList.remove(DateSelectionListener.class, l);
    }

    public List<DateSelectionListener> getDateSelectionListeners() {
        return listenerList.getListeners(DateSelectionListener.class);
    }

    protected void fireValueChanged(DateSelectionEvent.EventType eventType) {
        List<DateSelectionListener> listeners = getDateSelectionListeners();
        DateSelectionEvent e = null;

        for (DateSelectionListener listener : listeners) {
            if (e == null) {
                e = new DateSelectionEvent(this, eventType);
            }
            listener.valueChanged(e);
        }
    }

    private void setWeekIntervalSelection(Date startDate, Date endDate) {
        Date newStart = startDate;
        Date newEnd = endDate;

        // Make sure if we are over 7 days we span full weeks.
        cal.setTime(startDate);
        int firstDayOfWeek = monthView.getFirstDayOfWeek();
        cal.setFirstDayOfWeek(firstDayOfWeek);
        int count = 1;
        while (cal.getTime().before(endDate)) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            count++;
        }
        if (count > JXMonthView.DAYS_IN_WEEK) {
            // Make sure start date is on the beginning of the
            // week.
            cal.setTime(startDate);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek != firstDayOfWeek) {
                // Move the start date back to the first day of the
                // week.
                int daysFromStart = dayOfWeek - firstDayOfWeek;
                if (daysFromStart < 0) {
                    daysFromStart += JXMonthView.DAYS_IN_WEEK;
                }
                cal.add(Calendar.DAY_OF_MONTH, -daysFromStart);
                count += daysFromStart;
                newStart = cal.getTime();
            }

            // Make sure we have full weeks.  Otherwise modify the
            // end date.
            int remainder = count % JXMonthView.DAYS_IN_WEEK;
            if (remainder != 0) {
                cal.setTime(endDate);
                cal.add(Calendar.DAY_OF_MONTH, (JXMonthView.DAYS_IN_WEEK - remainder));
                newEnd = cal.getTime();
            }
        }
        addSelectionImpl(newStart, newEnd);
    }

    private void setSingleIntervalSelection(Date startDate, Date endDate) {
        clearSelectionImpl();
        cal.setTime(startDate);
        Date date = cal.getTime();
        while(date.before(endDate) || date.equals(endDate)) {
            selectedDates.add(date);
            cal.add(Calendar.DATE, 1);
            date = cal.getTime();
        }
    }

    private void addSelectionImpl(final Date startDate, final Date endDate) {
        cal.setTime(startDate);
        Date date = cal.getTime();
        while(date.before(endDate) || date.equals(endDate)) {
            selectedDates.add(date);
            cal.add(Calendar.DATE, 1);
            date = cal.getTime();
        }
    }


}
