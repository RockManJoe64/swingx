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

import java.awt.Component;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.event.WeakEventListenerList;

/**
 * Abstract <code>Highlighter</code> implementation which manages notification
 * and highlights conditionally, controlled by a HighlightPredicate.
 * <p>
 * 
 * Concrete custom implementations should focus on a single (or few) visual
 * attributes to highlight - this enhances re-use. F.i. a custom
 * FontHighlighter:
 * 
 * <pre><code>
 * public static class FontHighlighter extends AbstractHighlighter {
 * 
 *     private Font font;
 * 
 *     public FontHighlighter(Font font, HighlightPredicate predicate) {
 *         super(predicate);
 *         this.font = font;
 *     }
 * 
 *     @Override
 *     protected Component doHighlight(Component component,
 *             ComponentAdapter adapter) {
 *         component.setFont(font);
 *         return component;
 *     }
 * 
 * }
 * 
 * </code></pre>
 * 
 * Client code can combine the effect with a f.i. Color decoration, and use a
 * shared HighlightPredicate to apply both for the same condition.
 * 
 * <pre><code>
 * HighlightPredicate predicate = new HighlightPredicate() {
 *     public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
 *         Object value = adapter.getFilteredValueAt(adapter.row, adapter.column);
 *         return (value instanceof Number) &amp;&amp; ((Number) value).intValue() &lt; 0;
 *     }
 * };
 * table.setHighlighters(new ColorHighlighter(Color.RED, null, predicate),
 *         new FontHighlighter(myBoldFont, predicate));
 * </code></pre>
 * 
 * @author Jeanette Winzenburg
 * 
 * @see HighlightPredicate
 * @see org.jdesktop.swingx.renderer.ComponentProvider
 */
public abstract class AbstractHighlighter implements Highlighter {

    /**
     * Only one <code>ChangeEvent</code> is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    private transient ChangeEvent changeEvent;

    /** The listeners waiting for model changes. */
    protected WeakEventListenerList listenerList = new WeakEventListenerList();

    /** the HighlightPredicate to use. */
    private HighlightPredicate predicate;

    /**
     * Instantiates a Highlighter with default HighlightPredicate.
     *
     * @see #setHighlightPredicate(HighlightPredicate)
     */
    public AbstractHighlighter() {
        this(null);
    }

    /**
     * Instantiates a Highlighter with the given 
     * HighlightPredicate.<p>
     * 
     * @param predicate the HighlightPredicate to use.
     * 
     * @see #setHighlightPredicate(HighlightPredicate)
     */
    public AbstractHighlighter(HighlightPredicate predicate) {
        setHighlightPredicate(predicate);
    }

    /**
     * Set the HighlightPredicate used to decide whether a cell should
     * be highlighted. If null, sets the predicate to HighlightPredicate.ALWAYS.
     * 
     * The default value is HighlightPredicate.ALWAYS. 
     * 
     * @param predicate the HighlightPredicate to use. 
     */
    public void setHighlightPredicate(HighlightPredicate predicate) {
        this.predicate = predicate != null ? predicate
                : HighlightPredicate.ALWAYS;
        fireStateChanged();
    }

    /**
     * Returns the HighlightPredicate used to decide whether a cell 
     * should be highlighted. Guaranteed to be never null.
     * 
     * @return the HighlightPredicate to use, never null.
     */
    public HighlightPredicate getHighlightPredicate() {
        return predicate;
    }

    //----------------------- implement predicate respecting highlight

    /**
     * {@inheritDoc}
     * This implementation checks the HighlightPredicate and 
     * calls doHighlight to apply the decoration. Returns the 
     * undecorated component if false.
     * 
     * @param component the cell renderer component that is to be decorated
     * @param adapter the ComponentAdapter for this decorate operation
     * 
     * @see #doHighlight(Component, ComponentAdapter)
     * @see #getHighlightPredicate()
     */
    public Component highlight(Component component, ComponentAdapter adapter) {
        if (getHighlightPredicate().isHighlighted(component, adapter)) {
            component = doHighlight(component, adapter);
        }
        return component;
    }

    /**
     * Apply the highlights. 
     * 
     * @param component the cell renderer component that is to be decorated
     * @param adapter the ComponentAdapter for this decorate operation
     * 
     * @see #highlight(Component, ComponentAdapter)
     */
    protected abstract Component doHighlight(Component component,
            ComponentAdapter adapter);

    //------------------------ implement Highlighter change notification

    /**
     * Adds a <code>ChangeListener</code>. ChangeListeners are
     * notified after changes of any attribute. 
     *
     * PENDING: make final once the LegacyHighlighters are removed.
     * 
     * @param l the ChangeListener to add
     * @see #removeChangeListener
     */
    public/* final */void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    /**
     * Removes a <code>ChangeListener</code>e. 
     *
     * @param l the <code>ChangeListener</code> to remove
     * @see #addChangeListener
     */
    public final void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    /**
     * Returns an array of all the change listeners
     * registered on this <code>Highlighter</code>.
     *
     * @return all of this model's <code>ChangeListener</code>s 
     *         or an empty
     *         array if no change listeners are currently registered
     *
     * @see #addChangeListener
     * @see #removeChangeListener
     *
     * @since 1.4
     */
    public final ChangeListener[] getChangeListeners() {
        return (ChangeListener[]) listenerList
                .getListeners(ChangeListener.class);
    }

    /** 
     * Notifies registered <code>ChangeListener</code>s about
     * state changes.
     *  
     */
    protected final void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }

}
