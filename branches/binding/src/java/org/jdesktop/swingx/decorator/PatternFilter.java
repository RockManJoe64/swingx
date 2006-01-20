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

package org.jdesktop.swingx.decorator;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Pluggable pattern filter.
 *
 * @author Ramesh Gupta
 */
public class PatternFilter extends Filter implements PatternMatcher {
    private ArrayList<Integer>	toPrevious;
    protected Pattern	pattern = null;

    public PatternFilter() {
        this(null, 0, 0);
    }

    public PatternFilter(String regularExpr, int matchFlags, int col) {
        super(col);
        setPattern(regularExpr, matchFlags);
    }

    protected void init() {
		toPrevious = new ArrayList<Integer>();
    }

    public void setPattern(String regularExpr, int matchFlags) {
        if ((regularExpr == null) || (regularExpr.length() == 0)) {
            regularExpr = ".*";
        }
        setPattern(Pattern.compile(regularExpr, matchFlags));
    }

    /**
     * Sets the pattern used by this filter for matching.
     *
     * @param pattern the pattern used by this filter for matching
     * @see java.util.regex.Pattern
     */
    public void setPattern(Pattern pattern) {
        if (pattern == null) {
            setPattern(null, 0);
        } else {
            this.pattern = pattern;
            refresh();
        }
    }

    /**
     * Returns the pattern used by this filter for matching.
     *
     * @return the pattern used by this filter for matching
     * @see java.util.regex.Pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Resets the internal row mappings from this filter to the previous filter.
     */
    protected void reset() {
        toPrevious.clear();
        int inputSize = getInputSize();
        fromPrevious = new int[inputSize];  // fromPrevious is inherited protected
        for (int i = 0; i < inputSize; i++) {
            fromPrevious[i] = -1;
        }
    }

    protected void filter() {
        if (pattern != null) {
            int inputSize = getInputSize();
            int current = 0;
            for (int i = 0; i < inputSize; i++) {
                if (test(i)) {
                    toPrevious.add(new Integer(i));
                    // generate inverse map entry while we are here
                    fromPrevious[i] = current++;
                }
            }
        }
    }

    /**
     * @param row
     * @return
     */
    public boolean test(int row) {
        // PENDING: wrong false?
        // null pattern should be treated the same as null searchString
        // which is open
        // !testable should be clarified to mean "ignore" when filtering
        if (pattern == null) {
            return false;
        }

        // ask the adapter if the column should be includes
        if (!adapter.isTestable(getColumnIndex())) {
            return false; 
        }

        Object	value = getInputValue(row, getColumnIndex());

        if (value == null) {
            return false;
        }
        else {
            boolean matches = pattern.matcher(value.toString()).find();
            return matches;
        }
    }

    public int getSize() {
        return toPrevious.size();
    }

    protected int mapTowardModel(int row) {
        return toPrevious.get(row);
    }
}
