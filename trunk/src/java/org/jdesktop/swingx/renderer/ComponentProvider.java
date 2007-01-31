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
package org.jdesktop.swingx.renderer;

import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jdesktop.swingx.renderer.SimpleRendererDemo.Contributor;

/**
 * Abstract base class of a provider for a cell rendering component. Configures
 * the component's content and default visuals depending on the renderee's state
 * as captured in a <code>CellContext</code>. It's basically re-usable across
 * all types of renderees (JTable, JList, JTree).
 * <p>
 * 
 * <pre><code>
 * StringValue stringValue = new StringValue() {
 * 
 *     public String getString(Object value) {
 *         if (!(value instanceof Contributor))
 *             return TO_STRING.getString(value);
 *         Contributor contributor = (Contributor) value;
 *         return contributor.lastName + &quot;, &quot; + contributor.firstName;
 *     }
 * 
 * };
 * table.setDefaultRenderer(Contributor.class, new DefaultTableRenderer(
 *         stringValue));
 * list.setCellRenderer(new DefaultListRenderer(stringValue));
 * tree.setCellRenderer(new DefaultTreeRenderer(stringValue));
 * 
 * </code></pre>
 * 
 * <p>
 * 
 * Guarantees to completely configure the visual properties listed below. As a
 * consequence, client code (f.i. in <code>Highlighter</code>s) can safely
 * change them without long-lasting visual artefacts.
 * 
 * <ul>
 * <li> foreground and background, depending on selected and focused state
 * <li> border
 * <li> font
 * <li> Painter (if applicable)
 * <li> enabled
 * <li> componentOrientation
 * <li> horizontal alignment (if applicable)
 * </ul>
 * 
 * As this internally delegates default visual configuration to a
 * <code>DefaultVisuals</code> (which handles the first six items) subclasses
 * have to guarantee the alignment only.
 * 
 * @author Jeanette Winzenburg
 * 
 * @see DefaultVisuals
 * @see CellContext
 * @see DefaultTableRenderer
 * @see DefaultListRenderer
 * @see DefaultTreeRenderer
 */
public abstract class ComponentProvider<T extends JComponent> 
    implements Serializable {
    /** component to render with. */
    protected T rendererComponent;
    /** configurator of default visuals. */
    protected DefaultVisuals<T> defaultVisuals;
    /** horizontal (text) alignment of component. PENDING: useful only for labels, buttons? */
    protected int alignment;
    /** the converter to use for string representation. */
    protected StringValue formatter;
    
    /**
     * Instantiates a default component provider with LEADING
     * horizontal alignment and default to-String converter. <p> 
     *
     */
    public ComponentProvider() {
        setHorizontalAlignment(JLabel.LEADING);
        setToStringConverter(null);
        rendererComponent = createRendererComponent();
        defaultVisuals = createRendererController();
    }
    
    /**
     * Configures and returns an appropriate component to render a cell
     * in the given context. If the context is null, returns the
     * component in its current state.
     * 
     * @param context the cell context to configure from
     * @return a component to render a cell in the given context.
     */
    public T getRendererComponent(CellContext context) {
        if (context != null) {
            configureVisuals(context);
            configureContent(context);
        }
        return rendererComponent;
    }
    
    /**
     * Sets the horizontal alignment property to configure the component with.
     * Allowed values are those accepted by corresponding JLabel setter. The
     * default value is JLabel.LEADING. This controller guarantees to apply the
     * alignment on each request for a configured rendering component, if 
     * possible. Note that not all components have a horizontal alignment
     * property.
     * 
     * @param alignment the horizontal alignment to use when configuring the
     *   rendering component.
     */
    public void setHorizontalAlignment(int alignment) {
       this.alignment = alignment; 
    }
    
    /**
     * Returns the horizontal alignment.
     * 
     * @return the horizontal alignment of the rendering component.
     * 
     * @see #setHorizontalAlignment(int)
     * 
     */
    public int getHorizontalAlignment() {
        return alignment;
    }

    /**
     * Sets the StringValue to use. If the given converter is null,
     * uses the default to_string. 
     * 
     * @param formatter the format to use.
     */
    public void setToStringConverter(StringValue formatter) {
        if (formatter == null) {
            formatter = StringValue.TO_STRING;
        }
        this.formatter = formatter;
    }

    /**
     * Returns the converter to use for obtaining the String representation.
     * 
     * @return the StringValue used by this controller, guaranteed to
     *   be not null.
     */
    public StringValue getToStringConverter() {
        return formatter;
    }

    /**
     * Returns a string representation of the content.<p>
     * 
     * PENDING: This is a first attempt - we need a consistent string representation
     * across all (new and old) theme: rendering, (pattern) filtering/highlighting,
     * searching, auto-complete, what else??   
     * 
     * @param context the cell context.
     * @return a appropriate string representation of the cell's content.
     */
    public String getStringValue(CellContext context) {
        Object value = null;
        if (context != null) {
            value = context.getValue();
        }
        return formatter.getString(value);
    }

    /**
     * Configures the rendering component's default visuals frome
     * the given cell context. Here: delegates to the renderer
     * controller.
     * 
     * @param context the cell context to configure from, must not be null.
     * @see DefaultVisuals
     */
    protected void configureVisuals(CellContext context) {
        defaultVisuals.configureVisuals(rendererComponent, context);
    }

    /**
     * Configures the renderering component's content and state from the
     * given cell context.
     * 
     * @param context the cell context to configure from, must not be null.
     * 
     * @see #configureState(CellContext)
     * @see #format(CellContext)
     */
    protected void configureContent(CellContext context) {
        configureState(context);
        format(context);
    }

    /**
     * Formats the renderering component's content from the
     * given cell context.
     * 
     * @param context the cell context to configure from, must not be null.
     */
    protected abstract void format(CellContext context);

    /**
     * Configures the renderering component's state from the
     * given cell context.
     * @param context the cell context to configure from, must not be null.
     */
    protected abstract void configureState(CellContext context); 

    /**
     * Factory method to create and return the component to use for rendering.<p>
     * 
     * @return the component to use for rendering.
     */
    protected abstract T createRendererComponent();

    /**
     * Factory method to create and return the DefaultVisuals used by this
     * to configure the default visuals. Here: creates the default controller
     * parameterized to the same type as this.
     * 
     * @return the controller used to configure the default visuals of
     *   the rendering component.
     */
    protected DefaultVisuals<T> createRendererController() {
        return new DefaultVisuals<T>();
    }

    /**
     * Intermediate exposure during refactoring...
     * 
     * @return the default visual configurator used by this.
     */
    protected DefaultVisuals<T> getRendererController() {
        return defaultVisuals;
    }

}