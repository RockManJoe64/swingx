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
package org.jdesktop.swingx.calendar;

import org.jdesktop.swingx.DateSelectionModel;
import org.jdesktop.swingx.DefaultDateSelectionModel;
import org.jdesktop.swingx.event.EventListenerMap;
import org.jdesktop.swingx.plaf.JXMonthViewAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.MonthViewUI;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;


/**
 * Component that displays a month calendar which can be used to select a day
 * or range of days.  By default the <code>JXMonthView</code> will display a
 * single calendar using the current month and year, using
 * <code>Calendar.SUNDAY</code> as the first day of the week.
 * <p>
 * The <code>JXMonthView</code> can be configured to display more than one
 * calendar at a time by calling
 * <code>setPreferredCalCols</code>/<code>setPreferredCalRows</code>.  These
 * methods will set the preferred number of calendars to use in each
 * column/row.  As these values change, the <code>Dimension</code> returned
 * from <code>getMinimumSize</code> and <code>getPreferredSize</code> will
 * be updated.  The following example shows how to create a 2x2 view which is
 * contained within a <code>JFrame</code>:
 * <pre>
 *     JXMonthView monthView = new JXMonthView();
 *     monthView.setPreferredCols(2);
 *     monthView.setPreferredRows(2);
 *
 *     JFrame frame = new JFrame();
 *     frame.getContentPane().add(monthView);
 *     frame.pack();
 *     frame.setVisible(true);
 * </pre>
 * <p>
 * <code>JXMonthView</code> can be further configured to allow any day of the
 * week to be considered the first day of the week.  Character
 * representation of those days may also be set by providing an array of
 * strings.
 * <pre>
 *    monthView.setFirstDayOfWeek(Calendar.MONDAY);
 *    monthView.setDaysOfTheWeek(
 *            new String[]{"S", "M", "T", "W", "Th", "F", "S"});
 * </pre>
 * <p>
 * This component supports flagging days.  These flagged days are displayed
 * in a bold font.  This can be used to inform the user of such things as
 * scheduled appointment.
 * <pre>
 *    // Create some dates that we want to flag as being important.
 *    Calendar cal1 = Calendar.getInstance();
 *    cal1.set(2004, 1, 1);
 *    Calendar cal2 = Calendar.getInstance();
 *    cal2.set(2004, 1, 5);
 *
 *    long[] flaggedDates = new long[] {
 *        cal1.getTimeInMillis(),
 *        cal2.getTimeInMillis(),
 *        System.currentTimeMillis()
 *    };
 *
 *    monthView.setFlaggedDates(flaggedDates);
 * </pre>
 * Applications may have the need to allow users to select different ranges of
 * dates.  There are four modes of selection that are supported, single,
 * multiple, week and no selection.  Once a selection is made an action is
 * fired, with exception of the no selection mode, to inform listeners that
 * selection has changed.
 * <pre>
 *    // Change the selection mode to select full weeks.
 *    monthView.setSelectionMode(JXMonthView.WEEK_INTERVAL_SELECTION);
 *
 *    // Add an action listener that will be notified when the user
 *    // changes selection via the mouse.
 *    monthView.getSelectionModel().addDateSelectionListener(new DateSelectionListener {
 *        public void valueChanged(DateSelectionEvent e) {
 *            System.out.println(e.getSelection());
 *        }
 *    });
 * </pre>
 *
 * @author Joshua Outwater
 * @version  $Revision$
 */
public class JXMonthView extends JComponent {
    public static enum SelectionMode {
        /**
         * Mode that disallows selection of days from the calendar.
         */
        NO_SELECTION,
        /**
         * Mode that allows for selection of a single day.
         */
        SINGLE_SELECTION,
        /**
         * Mode that allows for selecting of multiple consecutive days.
         */
        SINGLE_INTERVAL_SELECTION,
        /**
         * Mode that allows for selecting disjoint days.
         */
        MULTIPLE_INTERVAL_SELECTION,
        /**
         * Mode where selections consisting of more than 7 days will
         * snap to a full week.
         */
        WEEK_INTERVAL_SELECTION
    }

    /** Return value used to identify when the month down button is pressed. */
    public static final int MONTH_DOWN = 1;
    /** Return value used to identify when the month up button is pressed. */
    public static final int MONTH_UP = 2;

    @SuppressWarnings({"UNUSED_SYMBOL"})
    private static final int MONTH_TRAVERSABLE = 1;
    @SuppressWarnings({"UNUSED_SYMBOL"})
    private static final int YEAR_TRAVERSABLE = 2;

    static {
        LookAndFeelAddons.contribute(new JXMonthViewAddon());
    }

    /**
     * UI Class ID
     */
    public static final String uiClassID = "MonthViewUI";

    public static final int DAYS_IN_WEEK = 7;
    public static final int MONTHS_IN_YEAR = 12;

    /**
     * Insets used in determining the rectangle for the month string
     * background.
     */
    protected Insets _monthStringInsets = new Insets(0, 0, 0, 0);

    /**
     * Keeps track of the first date we are displaying.  We use this as a
     * restore point for the calendar.
     */
    private long firstDisplayedDate;
    private int firstDisplayedMonth;
    private int firstDisplayedYear;
    private long lastDisplayedDate;

    private int boxPaddingX;
    private int boxPaddingY;
    private int minCalCols = 1;
    private int minCalRows = 1;
    private long today;
    private TreeSet<Long> flaggedDates;
    private TreeSet<Long> unselectableDates;
    private int firstDayOfWeek;
    private boolean antiAlias;
    private boolean traversable;
    private Calendar cal;
    private String[] _daysOfTheWeek;
    private Color todayBackgroundColor;
    private Color monthStringBackground;
    private Color monthStringForeground;
    private Color daysOfTheWeekForeground;
    private Color selectedBackground;
    private String actionCommand = "selectionChanged";
    private Timer todayTimer = null;
    private Hashtable<Integer, Color> dayToColorTable = new Hashtable<Integer, Color>();
    private Color flaggedDayForeground;
    private boolean showWeekNumber;
    private DateSelectionModel model;
    private EventListenerMap listenerMap;
    private SelectionMode selectionMode;

    /**
     * Create a new instance of the <code>JXMonthView</code> class using the
     * month and year of the current day as the first date to display.
     */
    public JXMonthView() {
        this(System.currentTimeMillis(), null);
    }

    /**
     * Create a new instance of the <code>JXMonthView</code> class using the
     * month and year from <code>initialTime</code> as the first date to
     * display.
     *
     * @param firstDisplayedDate The first month to display.
     */
    public JXMonthView(long firstDisplayedDate) {
        this(firstDisplayedDate, null);
    }

    public JXMonthView(long firstDisplayedDate, final DateSelectionModel model) {
        this.antiAlias = false;
        this.traversable = false;
        this.firstDayOfWeek = Calendar.SUNDAY;
        this.listenerMap = new EventListenerMap();
        this.selectionMode = SelectionMode.SINGLE_SELECTION;
        this.model = model;
        if (this.model == null) {
            this.model = new DefaultDateSelectionModel();
        }

        updateUI();

        // Set up calendar instance
        cal = Calendar.getInstance(getLocale());
        cal.setFirstDayOfWeek(firstDayOfWeek);
        cal.setMinimalDaysInFirstWeek(1);

        // Keep track of today
        setToday(cleanupDate(cal.getTimeInMillis()));

        // Set the first displayed date
        setFirstDisplayedDate(cleanupDate(firstDisplayedDate));

        setBackground(Color.WHITE);
        setFocusable(true);
        todayBackgroundColor = getForeground();

        // Restore original time value
        cal.setTimeInMillis(this.firstDisplayedDate);
    }

    /**
     * @inheritDoc
     */
    public MonthViewUI getUI() {
        return (MonthViewUI)ui;
    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui
     */
    public void setUI(MonthViewUI ui) {
        super.setUI(ui);
    }

    /**
     * Resets the UI property with the value from the current look and feel.
     *
     * @see UIManager#getUI
     */
    @Override
    public void updateUI() {
        setUI((MonthViewUI)UIManager.getUI(this));
        invalidate();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Returns the first displayed date.
     *
     * @return long The first displayed date.
     */
    public long getFirstDisplayedDate() {
        return firstDisplayedDate;
    }

    /**
     * Set the first displayed date.  We only use the month and year of
     * this date.  The <code>Calendar.DAY_OF_MONTH</code> field is reset to
     * 1 and all other fields, with exception of the year and month ,
     * are reset to 0.
     *
     * @param date The first displayed date.
     */
    public void setFirstDisplayedDate(long date) {
        long oldFirstDisplayedDate = firstDisplayedDate;
        int oldFirstDisplayedMonth = firstDisplayedMonth;
        int oldFirstDisplayedYear = firstDisplayedYear;

        cal.setTimeInMillis(cleanupDate(date));
        cal.set(Calendar.DAY_OF_MONTH, 1);
        firstDisplayedDate = cal.getTimeInMillis();
        firstDisplayedMonth = cal.get(Calendar.MONTH);
        firstDisplayedYear = cal.get(Calendar.YEAR);

        firePropertyChange("firstDisplayedDate", oldFirstDisplayedDate, firstDisplayedDate);
        firePropertyChange("firstDisplayedMonth", oldFirstDisplayedMonth, firstDisplayedMonth);
        firePropertyChange("firstDisplayedYear", oldFirstDisplayedYear, firstDisplayedYear);

        calculateLastDisplayedDate();

        repaint();
    }

    /**
     * Returns the last date able to be displayed.  For example, if the last
     * visible month was April the time returned would be April 30, 23:59:59.
     *
     * @return long The last displayed date.
     */
    public long getLastDisplayedDate() {
        return lastDisplayedDate;
    }

    private void calculateLastDisplayedDate() {
        lastDisplayedDate = getUI().calculateLastDisplayedDate();
    }

    /**
     * Moves the <code>date</code> into the visible region of the calendar.
     * If the date is greater than the last visible date it will become the
     * last visible date.  While if it is less than the first visible date
     * it will become the first visible date.
     *
     * @param date Date to make visible.
     */
    public void ensureDateVisible(long date) {
        if (date < firstDisplayedDate) {
            setFirstDisplayedDate(date);
        } else if (date > lastDisplayedDate) {
            cal.setTimeInMillis(date);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);

            cal.setTimeInMillis(lastDisplayedDate);
            int lastMonth = cal.get(Calendar.MONTH);
            int lastYear = cal.get(Calendar.YEAR);

            int diffMonths = month - lastMonth +
                    ((year - lastYear) * MONTHS_IN_YEAR);

            cal.setTimeInMillis(firstDisplayedDate);
            cal.add(Calendar.MONTH, diffMonths);
            setFirstDisplayedDate(cal.getTimeInMillis());
        }

        firePropertyChange("ensureDateVisibility", null, date);
    }

    /**
     * Returns a date span of the selected dates.  The result will be null if
     * no dates are selected.
     *
     * @deprecated see #getSelection
     */
    @Deprecated
    public DateSpan getSelectedDateSpan() {
        DateSpan result = null;
        Iterator<Date> itr = getSelectionModel().getSelection().iterator();
        if (itr.hasNext()) {
            Date date = itr.next();
            result = new DateSpan(date, date);
        }
        return result;
    }

    /**
     * Selects the dates in the DateSpan.  This method will not change the
     * initial date displayed so the caller must update this if necessary.
     * If we are in SINGLE_SELECTION mode only the start time from the DateSpan
     * will be used.  If we are in WEEK_INTERVAL_SELECTION mode the span will be
     * modified to be valid if necessary.
     *
     * @param dateSpan DateSpan defining the selected dates.  Passing
     * <code>null</code> will clear the selection.
     *
     * @deprecated see #setSelectionInterval
     */
    @Deprecated
    public void setSelectedDateSpan(DateSpan dateSpan) {
        setSelectionInterval(dateSpan.getStartAsDate(), dateSpan.getEndAsDate());
    }

    public void clearSelection() {
        getSelectionModel().clearSelection();
    }

    public SortedSet<Date> getSelection() {
        return getSelectionModel().getSelection();
    }

    /**
     * Adds the selection interval to the selection model.  All dates are modified to remove their hour of
     * day, minute, second, and millisecond before being added to the selection model.
     *
     * @param startDate
     * @param endDate
     */
    public void addSelectionInterval(final Date startDate, final Date endDate) {
        getSelectionModel().addSelectionInterval(cleanupDate(startDate), cleanupDate(endDate));
    }

    /**
     * Sets the selection interval to the selection model.  All dates are modified to remove their hour of
     * day, minute, second, and millisecond before being added to the selection model.
     *
     * @param startDate
     * @param endDate
     */
    public void setSelectionInterval(final Date startDate, final Date endDate) {
        getSelectionModel().setSelectionInterval(cleanupDate(startDate), cleanupDate(endDate));
    }

    /**
     * Removes the selection interval from the selection model.  All dates are modified to remove their hour of
     * day, minute, second, and millisecond before being added to the selection model.
     *
     * @param startDate
     * @param endDate
     */
    public void removeSelectionInterval(final Date startDate, final Date endDate) {
        getSelectionModel().removeSelectionInterval(cleanupDate(startDate), cleanupDate(endDate));
    }

    public DateSelectionModel getSelectionModel() {
        return model;
    }

    public void setSelectionModel(DateSelectionModel model) {
        DateSelectionModel oldModel = this.model;
        this.model = model;
        firePropertyChange("selectionModel", oldModel, model);
    }

    /**
     * Returns the current selection mode for this JXMonthView.
     *
     * @return int Selection mode.
     */
    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    /**
     * Set the selection mode for this JXMonthView.
     */
    public void setSelectionMode(final SelectionMode selectionMode) {
        SelectionMode oldSelectionMode = this.selectionMode;
        this.selectionMode = selectionMode;
        if (selectionMode == SelectionMode.NO_SELECTION || selectionMode == SelectionMode.SINGLE_SELECTION) {
            getSelectionModel().setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_SELECTION);
        } else if (selectionMode == SelectionMode.SINGLE_INTERVAL_SELECTION) {
            getSelectionModel().setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION);
        } else {
            getSelectionModel().setSelectionMode(DateSelectionModel.SelectionMode.MULTIPLE_INTERVAL_SELECTION);
        }
        firePropertyChange("selectionMode", oldSelectionMode, this.selectionMode);
    }


    /**
     * Returns true if the specified date falls within the _startSelectedDate
     * and _endSelectedDate range.
     */
    public boolean isSelectedDate(long date) {
        return getSelectionModel().isSelected(new Date(date));
    }

    /**
     * Identifies whether or not the date passed is a flagged date.
     *
     * @param date date which to test for flagged status
     * @return true if the date is flagged, false otherwise
     */
    public boolean isFlaggedDate(long date) {
        boolean result = false;
        if (flaggedDates != null) {
            result = flaggedDates.contains(date);
        }
        return result;
    }

    /**
     * An array of longs defining days that should be flagged.
     *
     * @param flaggedDates the dates to be flagged
     */
    public void setFlaggedDates(long[] flaggedDates) {
        if (flaggedDates == null) {
            this.flaggedDates = null;
        } else {
            this.flaggedDates = new TreeSet<Long>();
            // Loop through the flaggedDates and clean them up so
            // the hour, minute, seconds and milliseconds to 0 so
            // we can compare times later.
            for (long flaggedDate : flaggedDates) {
                this.flaggedDates.add(cleanupDate(flaggedDate));
            }
        }
        firePropertyChange("flaggedDates", null, this.flaggedDates);
        repaint();
    }

    /**
     * Identifies whether or not the date passed is an unselectable date.
     *
     * @param date date which to test for unselectable status
     * @return true if the date is unselectable, false otherwise
     */
    public boolean isUnselectableDate(long date) {
        boolean result = false;
        if (unselectableDates != null) {
            result = unselectableDates.contains(date);
        }
        return result;
    }

    /**
     * An array of longs defining days that should be unselectable.
     *
     * @param unselectableDates the dates that should be unselectable
     */
    public void setUnselectableDates(long[] unselectableDates) {
        if (unselectableDates == null) {
            this.unselectableDates = null;
        } else {
            this.unselectableDates = new TreeSet<Long>();
            // Loop through the unselectableDates and clean them up so
            // the hour, minute, seconds and milliseconds to 0 so
            // we can compare times later.
            for (long date : unselectableDates) {
                this.unselectableDates.add(cleanupDate(date));
            }
        }
        firePropertyChange("unselectableDates", null, this.unselectableDates);
        repaint();
    }

    private Date cleanupDate(Date date) {
        date.setTime(cleanupDate(date.getTime()));
        return date;
    }

    private long cleanupDate(long date) {
        cal.setTimeInMillis(date);
        // We only want to compare the day, month and year
        // so reset all other values to 0.
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * Returns the padding used between days in the calendar.
     */
    public int getBoxPaddingX() {
        return boxPaddingX;
    }

    /**
     * Sets the number of pixels used to pad the left and right side of a day.
     * The padding is applied to both sides of the days.  Therefore, if you
     * used the padding value of 3, the number of pixels between any two days
     * would be 6.
     */
    public void setBoxPaddingX(int boxPaddingX) {
        int oldBoxPadding = this.boxPaddingX;
        this.boxPaddingX = boxPaddingX;
        firePropertyChange("boxPaddingX", oldBoxPadding, this.boxPaddingX);
    }

    /**
     * Returns the padding used above and below days in the calendar.
     */
    public int getBoxPaddingY() {
        return boxPaddingY;
    }

    /**
     * Sets the number of pixels used to pad the top and bottom of a day.
     * The padding is applied to both the top and bottom of a day.  Therefore,
     * if you used the padding value of 3, the number of pixels between any
     * two days would be 6.
     */
    public void setBoxPaddingY(int boxPaddingY) {
        int oldBoxPadding = this.boxPaddingY;
        this.boxPaddingY = boxPaddingY;
        firePropertyChange("boxPaddingY", oldBoxPadding, this.boxPaddingY);
    }

    /**
     * Returns whether or not the month view supports traversing months.
     *
     * @return <code>true</code> if month traversing is enabled.
     */
    public boolean isTraversable() {
        return traversable;
    }

    /**
     * Set whether or not the month view will display buttons to allow the
     * user to traverse to previous or next months.
     *
     * @param traversable set to true to enable month traversing,
     *        false otherwise.
     */
    public void setTraversable(boolean traversable) {
        if (traversable != this.traversable) {
            this.traversable = traversable;
            firePropertyChange("traversable", !this.traversable, this.traversable);
            repaint();
        }
    }

    /**
     * Returns whether or not this <code>JXMonthView</code> should display
     * week number.
     *
     * @return <code>true</code> if week numbers should be displayed
     */
    public boolean isShowingWeekNumber() {
        return showWeekNumber;
    }

    /**
     * Set whether or not this <code>JXMonthView</code> will display week
     * numbers or not.
     *
     * @param showWeekNumber true if week numbers should be displayed,
     *        false otherwise
     */
    public void setShowingWeekNumber(boolean showWeekNumber) {
        if (this.showWeekNumber != showWeekNumber) {
            this.showWeekNumber = showWeekNumber;
            firePropertyChange("weekNumber", !this.showWeekNumber, showWeekNumber);
            repaint();
        }
    }

    /**
     * Sets the single character representation for each day of the
     * week.  For this method the first days of the week days[0] is assumed to
     * be <code>Calendar.SUNDAY</code>.
     *
     * @throws IllegalArgumentException if <code>days.length</code> != DAYS_IN_WEEK
     * @throws NullPointerException if <code>days</code> == null
     */
    public void setDaysOfTheWeek(String[] days)
            throws IllegalArgumentException, NullPointerException {
        if (days == null) {
            throw new NullPointerException("Array of days is null.");
        } else if (days.length != DAYS_IN_WEEK) {
            throw new IllegalArgumentException(
                    "Array of days is not of length " + DAYS_IN_WEEK + " as expected.");
        }

        String[] oldValue = _daysOfTheWeek;
        _daysOfTheWeek = days;
        firePropertyChange("daysOfTheWeek", oldValue, _daysOfTheWeek);
        repaint();
    }

    /**
     * Returns the single character representation for each day of the
     * week.
     *
     * @return Single character representation for the days of the week
     */
    public String[] getDaysOfTheWeek() {
        String[] days = new String[DAYS_IN_WEEK];
        System.arraycopy(_daysOfTheWeek, 0, days, 0, DAYS_IN_WEEK);
        return days;
    }

    /**
     * Gets what the first day of the week is; e.g.,
     * <code>Calendar.SUNDAY</code> in the U.S., <code>Calendar.MONDAY</code>
     * in France.
     *
     * @return int The first day of the week.
     */
    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    /**
     * Sets what the first day of the week is; e.g.,
     * <code>Calendar.SUNDAY</code> in US, <code>Calendar.MONDAY</code>
     * in France.
     *
     * @param firstDayOfWeek The first day of the week.
     * @see java.util.Calendar
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        if (firstDayOfWeek == this.firstDayOfWeek) {
            return;
        }

        int oldFirstDayOfWeek = this.firstDayOfWeek;

        this.firstDayOfWeek = firstDayOfWeek;
        cal.setFirstDayOfWeek(this.firstDayOfWeek);
        model.setFirstDayOfWeek(this.firstDayOfWeek);

        firePropertyChange("firstDayOfWeek", oldFirstDayOfWeek, this.firstDayOfWeek);

        repaint();
    }

    /**
     * Gets the time zone.
     *
     * @return The <code>TimeZone</code> used by the <code>JXMonthView</code>.
     */
    public TimeZone getTimeZone() {
        return cal.getTimeZone();
    }

    /**
     * Sets the time zone with the given time zone value.
     *
     * @param tz The <code>TimeZone</code>.
     */
    public void setTimeZone(TimeZone tz) {
        cal.setTimeZone(tz);
    }

    /**
     * Returns true if anti-aliased text is enabled for this component, false
     * otherwise.
     *
     * @return boolean <code>true</code> if anti-aliased text is enabled,
     * <code>false</code> otherwise.
     */
    public boolean isAntialiased() {
        return antiAlias;
    }

    /**
     * Turns on/off anti-aliased text for this component.
     *
     * @param antiAlias <code>true</code> for anti-aliased text,
     * <code>false</code> to turn it off.
     */
    public void setAntialiased(boolean antiAlias) {
        if (this.antiAlias == antiAlias) {
            return;
        }
        this.antiAlias = antiAlias;
        firePropertyChange("antialiased", !this.antiAlias, this.antiAlias);
        repaint();
    }

    /**
     * Returns the selected background color.
     *
     * @return the selected background color.
     */
    public Color getSelectedBackground() {
        return selectedBackground;
    }

    /**
     * Sets the selected background color to <code>c</code>.  The default color
     * is <code>138, 173, 209 (Blue-ish)</code>
     *
     * @param c Selected background.
     */
    public void setSelectedBackground(Color c) {
        selectedBackground = c;
    }

    /**
     * Returns the color used when painting the today background.
     *
     * @return Color Color
     */
    public Color getTodayBackground() {
        return todayBackgroundColor;
    }

    /**
     * Sets the color used to draw the bounding box around today.  The default
     * is the background of the <code>JXMonthView</code> component.
     *
     * @param c color to set
     */
    public void setTodayBackground(Color c) {
        todayBackgroundColor = c;
        repaint();
    }

    /**
     * Returns the color used to paint the month string background.
     *
     * @return Color Color.
     */
    public Color getMonthStringBackground() {
        return monthStringBackground;
    }

    /**
     * Sets the color used to draw the background of the month string.  The
     * default is <code>138, 173, 209 (Blue-ish)</code>.
     *
     * @param c color to set
     */
    public void setMonthStringBackground(Color c) {
        monthStringBackground = c;
        repaint();
    }

    /**
     * Returns the color used to paint the month string foreground.
     *
     * @return Color Color.
     */
    public Color getMonthStringForeground() {
        return monthStringForeground;
    }

    /**
     * Sets the color used to draw the foreground of the month string.  The
     * default is <code>Color.WHITE</code>.
     *
     * @param c color to set
     */
    public void setMonthStringForeground(Color c) {
        monthStringForeground = c;
        repaint();
    }

    /**
     * Sets the color used to draw the foreground of each day of the week. These
     * are the titles
     *
     * @param c color to set
     */
    public void setDaysOfTheWeekForeground(Color c) {
        daysOfTheWeekForeground = c;
        repaint();
    }

    /**
     * @return Color Color
     */
    public Color getDaysOfTheWeekForeground() {
        return daysOfTheWeekForeground;
    }

    /**
     * Set the color to be used for painting the specified day of the week.
     * Acceptable values are Calendar.SUNDAY - Calendar.SATURDAY.
     *
     * @param dayOfWeek constant value defining the day of the week.
     * @param c         The color to be used for painting the numeric day of the week.
     */
    public void setDayForeground(int dayOfWeek, Color c) {
        dayToColorTable.put(dayOfWeek, c);
    }

    /**
     * Return the color that should be used for painting the numerical day of the week.
     *
     * @param dayOfWeek The day of week to get the color for.
     * @return The color to be used for painting the numeric day of the week.
     *         If this was no color has yet been defined the component foreground color
     *         will be returned.
     */
    public Color getDayForeground(int dayOfWeek) {
        Color c;
        c = dayToColorTable.get(dayOfWeek);
        if (c == null) {
            c = getForeground();
        }
        return c;
    }

    /**
     * Set the color to be used for painting the foregroudn of a flagged day.
     *
     * @param c The color to be used for painting.
     */
    public void setFlaggedDayForeground(Color c) {
        flaggedDayForeground = c;
    }

    /**
     * Return the color that should be used for painting the foreground of the flagged day.
     *
     * @return The color to be used for painting
     */
    public Color getFlaggedDayForeground() {
        return flaggedDayForeground;
    }

    /**
     * Returns a copy of the insets used to paint the month string background.
     *
     * @return Insets Month string insets.
     */
    public Insets getMonthStringInsets() {
        return (Insets) _monthStringInsets.clone();
    }

    /**
     * Insets used to modify the width/height when painting the background
     * of the month string area.
     *
     * @param insets Insets
     */
    public void setMonthStringInsets(Insets insets) {
        if (insets == null) {
            _monthStringInsets.top = 0;
            _monthStringInsets.left = 0;
            _monthStringInsets.bottom = 0;
            _monthStringInsets.right = 0;
        } else {
            _monthStringInsets.top = insets.top;
            _monthStringInsets.left = insets.left;
            _monthStringInsets.bottom = insets.bottom;
            _monthStringInsets.right = insets.right;
        }
        repaint();
    }

    /**
     * Returns the preferred number of columns to paint calendars in.
     *
     * @return int Columns of calendars.
     */
    public int getPreferredCols() {
        return minCalCols;
    }

    /**
     * The preferred number of columns to paint calendars.
     *
     * @param cols The number of columns of calendars.
     */
    public void setPreferredCols(int cols) {
        if (cols <= 0) {
            return;
        }
        minCalCols = cols;
        revalidate();
        repaint();
    }

    /**
     * Returns the preferred number of rows to paint calendars in.
     *
     * @return int Rows of calendars.
     */
    public int getPreferredRows() {
        return minCalRows;
    }

    /**
     * Sets the preferred number of rows to paint calendars.
     *
     * @param rows The number of rows of calendars.
     */
    public void setPreferredRows(int rows) {
        if (rows <= 0) {
            return;
        }
        minCalRows = rows;
        revalidate();
        repaint();
    }


    private void updateToday() {
        // Update today.
        cal.setTimeInMillis(today);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        setToday(cal.getTimeInMillis());

        // Restore calendar.
        cal.setTimeInMillis(firstDisplayedDate);
        repaint();
    }

    private void setToday(long today) {
        long oldToday = this.today;
        this.today = today;
        firePropertyChange("today", oldToday, this.today);
    }

    /**
     * Moves and resizes this component to conform to the new bounding
     * rectangle r. This component's new position is specified by r.x and
     * r.y, and its new size is specified by r.width and r.height
     *
     * @param r The new bounding rectangle for this component
     */
    @Override
    public void setBounds(Rectangle r) {
        setBounds(r.x, r.y, r.width, r.height);
    }

    /**
     * Sets the font of this component.
     *
     * @param font The font to become this component's font; if this parameter
     *             is null then this component will inherit the font of its parent.
     */
    @Override
    public void setFont(Font font) {
        Font old = getFont();
        super.setFont(font);
        firePropertyChange("font", old, font);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNotify() {
        todayTimer.stop();
        super.removeNotify();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNotify() {
        super.addNotify();

        // Setup timer to update the value of today.
        int secondsTillTomorrow = 86400;

        if (todayTimer == null) {
            todayTimer = new Timer(secondsTillTomorrow * 1000,
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            updateToday();
                        }
                    });
        }

        // Modify the initial delay by the current time.
        cal.setTimeInMillis(System.currentTimeMillis());
        secondsTillTomorrow = secondsTillTomorrow -
                (cal.get(Calendar.HOUR_OF_DAY) * 3600) -
                (cal.get(Calendar.MINUTE) * 60) -
                cal.get(Calendar.SECOND);
        todayTimer.setInitialDelay(secondsTillTomorrow * 1000);
        todayTimer.start();

        // Restore calendar
        cal.setTimeInMillis(firstDisplayedDate);
    }

    public Calendar getCalendar() {
        return cal;
    }

    /**
     * Return a long representing the date at the specified x/y position.
     * The date returned will have a valid day, month and year.  Other fields
     * such as hour, minute, second and milli-second will be set to 0.
     *
     * @param x X position
     * @param y Y position
     * @return long The date, -1 if position does not contain a date.
     */
    public long getDayAt(int x, int y) {
        return getUI().getDayAt(x, y);
    }

    /**
     * Returns the string currently used to identiy fired ActionEvents.
     *
     * @return String The string used for identifying ActionEvents.
     */
    public String getActionCommand() {
        return actionCommand;
    }

    /**
     * Sets the string used to identify fired ActionEvents.
     *
     * @param actionCommand The string used for identifying ActionEvents.
     */
    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    /**
     * Adds an ActionListener.
     * <p/>
     * The ActionListener will receive an ActionEvent when a selection has
     * been made.
     *
     * @param l The ActionListener that is to be notified
     */
    public void addActionListener(ActionListener l) {
        listenerMap.add(ActionListener.class, l);
    }

    /**
     * Removes an ActionListener.
     *
     * @param l The action listener to remove.
     */
    public void removeActionListener(ActionListener l) {
        listenerMap.remove(ActionListener.class, l);
    }

    @Override
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        java.util.List<T> listeners = listenerMap.getListeners(listenerType);
        T[] result;
        if (!listeners.isEmpty()) {
            result = (T[]) java.lang.reflect.Array.newInstance(listenerType, listeners.size());
            result = listeners.toArray(result);
        } else {
            result = super.getListeners(listenerType);
        }
        return result;
    }

    /**
     * Fires an ActionEvent to all listeners.
     */
    protected void fireActionPerformed() {
        ActionListener[] listeners = getListeners(ActionListener.class);
        ActionEvent e = null;

        for (ActionListener listener : listeners) {
            if (e == null) {
                e = new ActionEvent(JXMonthView.this,
                        ActionEvent.ACTION_PERFORMED,
                        actionCommand);
            }
            listener.actionPerformed(e);
        }
    }

    public void postActionEvent() {
        fireActionPerformed();
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JXMonthView mv = new JXMonthView();
                mv.setShowingWeekNumber(true);
                mv.setTraversable(true);
                Calendar cal = Calendar.getInstance();
                cal.set(2006, 5, 20);
                mv.setUnselectableDates(new long[] { cal.getTimeInMillis() });
                mv.setPreferredRows(2);
                mv.setSelectionMode(SelectionMode.SINGLE_INTERVAL_SELECTION);
                cal.setTimeInMillis(System.currentTimeMillis());
                mv.setSelectionInterval(cal.getTime(), cal.getTime());
                mv.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.out.println(
                                ((JXMonthView) e.getSource()).getSelection());
                    }
                });
                frame.getContentPane().add(mv);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
