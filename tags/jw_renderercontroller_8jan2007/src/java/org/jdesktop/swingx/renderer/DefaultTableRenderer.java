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


import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.RolloverRenderer;


/**
 * Abstract base class of all extended TableCellRenderers in SwingX.
 * <p>
 * 
 * PENDING: this is not really serializable - no default constructor
 * 
 * @author Jeanette Winzenburg
 * 
 * @see JRendererLabel
 * 
 */
public class DefaultTableRenderer <T extends JComponent>
        implements TableCellRenderer, RolloverRenderer, Serializable {

    private RendererController rendererController;
    private CellContext<JTable> cellContext;
    
    public static DefaultTableRenderer<JLabel> createDefaultTableRenderer() {
        return createDefaultTableRenderer(null);
    }

    public static DefaultTableRenderer<JLabel> createDefaultTableRenderer(ToStringConverter converter) {
        return new DefaultTableRenderer<JLabel>(new RenderingLabelController(converter));
    }
    
    /**
     * @param componentController
     */
    public DefaultTableRenderer(RenderingComponentController<T> componentController) {
        this.rendererController = new RendererController<T, JTable>(componentController);
        this.cellContext = new TableCellContext();
    }


    /**
     * @param rendererController
     */
    public DefaultTableRenderer(RendererController rendererController) {
        this.rendererController = rendererController;
        this.cellContext = new TableCellContext();
    }


    // -------------- implements javax.swing.table.TableCellRenderer
    /**
     * 
     * Returns the default table cell renderer.
     * 
     * @param table the <code>JTable</code>
     * @param value the value to assign to the cell at
     *        <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        cellContext.installContext(table, value, row, column, isSelected, hasFocus,
                true, true);
        rendererController.configure(cellContext);
        return rendererController.getRendererComponent();
    }
    /**
     * @param background
     */
    public void setBackground(Color background) {
        rendererController.setBackground(background);
        
    }
    /**
     * @param foreground
     */
    public void setForeground(Color foreground) {
        rendererController.setForeground(foreground);
    }

//----------------- RolloverRenderer
    
    public void doClick() {
        rendererController.doClick();
        
    }
    public boolean isEnabled() {
        return rendererController.isEnabled();
    }

}

