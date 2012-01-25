/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
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
        this.pattern = pattern;
        refresh();
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

    public boolean test(int row) {
        if (pattern == null) {
            return false;
        }

        // If column index in view coordinates is negative, the column is hidden.
        if (adapter.modelToView(getColumnIndex()) < 0) {
            return false; // column is not being displayed; obviously no match!
        }

        Object	value = getInputValue(row, getColumnIndex());

        if (value == null) {
            return false;
        }
        else {
            boolean	matches = pattern.matcher(value.toString()).matches();
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
