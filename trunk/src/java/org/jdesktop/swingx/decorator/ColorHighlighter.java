/*
 * $Id$
 *
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
 *
 */
package org.jdesktop.swingx.decorator;

import java.awt.Color;
import java.awt.Component;

/**
 * TODO add type doc
 * 
 * @author Jeanette Winzenburg
 */
public class ColorHighlighter extends AbstractHighlighter {
    private Color background = null;
    private Color foreground = null;
    private Color selectedBackground = null;
    private Color selectedForeground = null;

    /**
     * Default constructor for mutable Highlighter.
     * Initializes colors to null and uses the default predicate.
     *
     */
    public ColorHighlighter() {
        this(null, null);
    }

    /**
     * Constructs a mutable <code>Highlighter</code> with the specified
     * background and foreground colors, and null selectedBackground/-foreground.
     *
     * @param cellBackground background color for unselected cell state
     * @param cellForeground foreground color for unselected cell state
     */
    public ColorHighlighter(Color cellBackground, Color cellForeground) {
        this(cellBackground, cellForeground, null, null, null);
    }

    /**
     * Constructs a mutable <code>Highlighter</code> with the specified
     * unselected 
     * background/foreground colors and HighlightPredicate. 
     * Initializes selected colors to null.
     *
     * @param cellBackground background color for unselected cell state
     * @param cellForeground foreground color for unselected cell state
     * @param predicate the HighlightPredicate to use.
     */
    public ColorHighlighter(Color cellBackground, Color cellForeground, 
            HighlightPredicate predicate) {
        this(cellBackground, cellForeground, null, null, predicate);
    }

    
    /**
     * Constructs a mutable <code>Highlighter</code> with the specified
     * background and foreground colors for unselected and selected cells.
     *
     * @param cellBackground background color for unselected cell state
     * @param cellForeground foreground color for unselected cell state
     * @param selectedBackground background color for selected cell state
     * @param selectedForeground foreground color for selected cell state
    */
    public ColorHighlighter(Color cellBackground, Color cellForeground, 
            Color selectedBackground, Color selectedForeground) {
        this(cellBackground, cellForeground, selectedBackground, selectedForeground, null);
    }

    /**
     * Constructs a <code>Highlighter</code> with the specified
     * background and foreground colors with mutability depending on
     * given flag.
     *
     * @param cellBackground background color for unselected cell state
     * @param cellForeground foreground color for unselected cell state
     * @param selectedBackground background color for selected cell state
     * @param selectedForeground foreground color for selected cell state
     */
    public ColorHighlighter(Color cellBackground, Color cellForeground, 
            Color selectedBackground, Color selectedForeground, 
            HighlightPredicate predicate) {
        super(predicate);
        this.background = cellBackground; 
        this.foreground = cellForeground; 
        this.selectedBackground = selectedBackground;
        this.selectedForeground = selectedForeground;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Component doHighlight(Component renderer, ComponentAdapter adapter) {
        applyBackground(renderer, adapter);
        applyForeground(renderer, adapter);
        return renderer;
    }
   
    
    /**
    * Applies a suitable background for the renderer component within the
    * specified adapter. <p>
    * 
    * This implementation applies its background or selectedBackground color
    * (depending on the adapter's selected state) if != null. 
    * Otherwise it does nothing.
    *
    * @param renderer the cell renderer component that is to be decorated
    * @param adapter the ComponentAdapter for this decorate operation
    */
    protected void applyBackground(Component renderer, ComponentAdapter adapter) {
        Color color = adapter.isSelected() ? getSelectedBackground() : getBackground();
        if (color != null) {
            renderer.setBackground(color);
        }

    }
    /**
    * Applies a suitable foreground for the renderer component within the
    * specified adapter. <p>
    * 
    * This implementation applies its foreground or selectedfForeground color
    * (depending on the adapter's selected state) if != null. 
    * Otherwise it does nothing.
    *
    * @param renderer the cell renderer component that is to be decorated
    * @param adapter the ComponentAdapter for this decorate operation
     */
    protected void applyForeground(Component renderer, ComponentAdapter adapter) {
        Color color = adapter.isSelected() ? getSelectedForeground() : getForeground();
        if (color != null) {
            renderer.setForeground(color);
        }
    }


//---------------------- state
    
    /**
     * Returns the background color of this <code>LegacyHighlighter</code>.
     *
     * @return the background color of this <code>LegacyHighlighter</code>,
     *          or null, if no background color has been set
     */
    public Color getBackground() {
        return background;
    }

    /**
     * Sets the background color of this <code>LegacyHighlighter</code> and 
     * notifies registered ChangeListeners if this
     * is mutable. Does nothing if immutable.
     *  
     * @param color the background color of this <code>LegacyHighlighter</code>,
     *          or null, to clear any existing background color
     */
    public void setBackground(Color color) {
//        if (isImmutable()) return;
        background = color;
        fireStateChanged();
    }

    /**
     * Returns the foreground color of this <code>LegacyHighlighter</code>.
     *
     * @return the foreground color of this <code>LegacyHighlighter</code>,
     *          or null, if no foreground color has been set
     */
    public Color getForeground() {
        return foreground;
    }

    /**
     * Sets the foreground color of this <code>LegacyHighlighter</code> and notifies
     * registered ChangeListeners if this is mutable. Does nothing if 
     * immutable.
     *
     * @param color the foreground color of this <code>LegacyHighlighter</code>,
     *          or null, to clear any existing foreground color
     */
    public void setForeground(Color color) {
//        if (isImmutable()) return;
        foreground = color;
        fireStateChanged();
    }

    /**
     * Returns the selected background color of this <code>LegacyHighlighter</code>.
     *
     * @return the selected background color of this <code>LegacyHighlighter</code>,
     *          or null, if no selected background color has been set
     */
    public Color getSelectedBackground() {
        return selectedBackground;
    }

    /**
     * Sets the selected background color of this <code>LegacyHighlighter</code>
     * and notifies registered ChangeListeners if this is mutable. Does nothing
     * if immutable.
     *
     * @param color the selected background color of this <code>LegacyHighlighter</code>,
     *          or null, to clear any existing selected background color
     */
    public void setSelectedBackground(Color color) {
//        if (isImmutable()) return;
        selectedBackground = color;
        fireStateChanged();
    }

    /**
     * Returns the selected foreground color of this <code>LegacyHighlighter</code>.
     *
     * @return the selected foreground color of this <code>LegacyHighlighter</code>,
     *          or null, if no selected foreground color has been set
     */
    public Color getSelectedForeground() {
        return selectedForeground;
    }

    /**
     * Sets the selected foreground color of this <code>LegacyHighlighter</code> and
     * notifies registered ChangeListeners if this is mutable. Does nothing if
     * immutable.
     *
     * @param color the selected foreground color of this <code>LegacyHighlighter</code>,
     *          or null, to clear any existing selected foreground color
     */
    public void setSelectedForeground(Color color) {
//        if (isImmutable()) return;
        selectedForeground = color;
        fireStateChanged();
    }



}
