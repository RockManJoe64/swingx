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

package org.jdesktop.swingx.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.UIResource;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.LabelProperties;
import org.jdesktop.swingx.border.IconBorder;
import org.jdesktop.swingx.decorator.Sorter;
import org.jdesktop.swingx.icon.SortArrowIcon;
import org.jdesktop.swingx.plaf.ColumnHeaderRendererAddon;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;

/**
 * Header renderer class which renders column sort feedback (arrows).
 * 
 * PENDING: #25, #169 - Header doesn't look right in winXP/mac
 * 
 * 
 * @author Amy Fowler
 * @author Ramesh Gupta
 * @author Jeanette Winzenburg
 */
public class ColumnHeaderRenderer extends JComponent implements TableCellRenderer {
    // the inheritance is only to make sure we are updated on LF change
    public static final String UP_ICON_KEY = "ColumnHeaderRenderer.upIcon";
    public static final String DOWN_ICON_KEY = "ColumnHeaderRenderer.downIcon";

    static {
        LookAndFeelAddons.contribute(new ColumnHeaderRendererAddon());
    }
    private static TableCellRenderer sharedInstance = null;

    private static Icon defaultDownIcon = new SortArrowIcon(false);

    private static Icon defaultUpIcon = new SortArrowIcon(true);

    private Icon downIcon = defaultDownIcon;

    private Icon upIcon = defaultUpIcon;

    private IconBorder iconBorder = new IconBorder();
    private boolean antiAliasedText = false;

    private TableCellRenderer delegateRenderer;

    private LabelProperties label;

    public static TableCellRenderer getSharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new ColumnHeaderRenderer();
        }
        return sharedInstance;
    }

    public static ColumnHeaderRenderer createColumnHeaderRenderer() {
        return new ColumnHeaderRenderer();
    }

    /*
     * JW: a story ...
     *
     * latest: don't use a custom component and don't add the original
     * and the arrow - use the original only and compound a border with 
     * arrow icon. How does it look in XP/Mac?
     * 
     * 
     * ----------------- below is the comment as of ColumnHeaderRenderer
     * Original used a Label to show the typical text/icon part and another
     * Label to show the up/down arrows, added both to this and configured both
     * directly in getTableCellRendererComponent.
     * 
     * My first shot to solve the issues was to delegate the text/icon part to
     * the defaultRenderer as returned by the JTableHeader: replace the first
     * label with the rendererComponent of the renderer. In
     * getTableCellRendererComponent let the renderer configure the comp and
     * "move" the border from the delegateComp to this - so it's bordering both
     * the comp and the arrow.
     * 
     * Besides not working (WinXP style headers are still not shown :-( it has
     * issues with opaqueness: different combinations of this.opaque and
     * delegate.opaque all have issues 
     *  1. if the delegate is not explicitly set to false the border looks wrong 
     *  2. if this is set to true we can have custom background 
     *     per cell but no setting the header background has no
     *     effect - and changing LF doesn't take up the LF default background ...
     *  3. if this is set to false we can't have custom cell background
     * 
     * Any ideas?
     * 
     * 
     */

    private ColumnHeaderRenderer() {
        label = new LabelProperties();
        initDelegate();
        
    }


    private void initDelegate() {
        JTableHeader header = new JTableHeader();
        delegateRenderer = header.getDefaultRenderer();

    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) {
        Component comp = configureDelegate(table, value, isSelected, hasFocus, rowIndex,
                columnIndex);

        if ((table instanceof JXTable) && (comp instanceof JComponent)) {
            // We no longer limit ourselves to a single "currently sorted
            // column"
            Sorter sorter = ((JXTable) table).getSorter(columnIndex);

            Border border = UIManager.getBorder("TableHeader.cellBorder");
            if (sorter != null) {
                iconBorder.setIcon(sorter.isAscending() ? upIcon : downIcon);
                Border origBorder = ((JComponent) comp).getBorder();
                border = new CompoundBorder(origBorder, iconBorder);
                ((JComponent) comp).setBorder(border);
            }
        }
        adjustComponentOrientation(comp);
        return comp;
    }

    /**
     * adjusts the Component's orientation to JXTable's CO if appropriate.
     * Here: always.
     * 
     * @param stamp
     */
    protected void adjustComponentOrientation(Component stamp) {
        if (stamp.getComponentOrientation().equals(getComponentOrientation())) return;
        stamp.applyComponentOrientation(getComponentOrientation());
    }

    private Component configureDelegate(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) {
        Component comp = delegateRenderer.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, rowIndex, columnIndex);

        applyLabelProperties(comp);
        return comp;
    }

    private void applyLabelProperties(Component delegateRendererComponent) {
        if (delegateRendererComponent instanceof JLabel) {
            label.applyPropertiesTo((JLabel) delegateRendererComponent);
        } else {
            label.applyPropertiesTo(delegateRenderer);
        }
    }

    public void setAntiAliasedText(boolean antiAlias) {
        this.antiAliasedText = antiAlias;
    }

    public boolean getAntiAliasedText() {
        return antiAliasedText;
    }

    public void setBackground(Color background) {
        // this is called somewhere along initialization of super?
        if (label != null) {
            label.setBackground(background);
        }
    }

    public void setForeground(Color foreground) {
         if (label != null) {
            label.setForeground(foreground);
        }
    }

    public void setFont(Font font) {
        if (label != null) {
            label.setFont(font);
        }
    }

    public void setDownIcon(Icon icon) {
        this.downIcon = icon;
    }

    public Icon getDownIcon() {
        return downIcon;
    }

    public void setUpIcon(Icon icon) {
        this.upIcon = icon;
    }

    public Icon getUpIcon() {
        return upIcon;
    }

    public void setHorizontalAlignment(int alignment) {
        label.setHorizontalAlignment(alignment);
    }

    public int getHorizontalAlignment() {
        return label.getHorizontalAlignment();
    }

    public void setHorizontalTextPosition(int textPosition) {
        label.setHorizontalTextPosition(textPosition);
    }

    public int getHorizontalTextPosition() {
        return label.getHorizontalTextPosition();
    }

    public void setIcon(Icon icon) {
        label.setIcon(icon);
    }

    public Icon getIcon() {
        return label.getIcon();
    }

    public void setIconTextGap(int iconTextGap) {
        label.setIconTextGap(iconTextGap);
    }

    public int getIconTextGap() {
        return label.getIconTextGap();
    }

    public void setVerticalAlignment(int alignment) {
        label.setVerticalAlignment(alignment);
    }

    public int getVerticalAlignment() {
        return label.getVerticalAlignment();
    }

    public void setVerticalTextPosition(int textPosition) {
        label.setVerticalTextPosition(textPosition);
    }

    public int getVerticalTextPosition() {
        return label.getVerticalTextPosition();
    }

//    public void paint(Graphics g) {
//        if (antiAliasedText) {
//            Graphics2D g2 = (Graphics2D) g;
//            Object save = g2
//                    .getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
//            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//
//            super.paint(g2);
//
//            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, save);
//        } else {
//            super.paint(g);
//        }
//    }
//
    public void updateUI() {
        super.updateUI();
        initDelegate();
        updateIconUI();
    }

    private void updateIconUI() {
        if (getUpIcon() instanceof UIResource) {
            Icon icon = UIManager.getIcon(UP_ICON_KEY);
            setUpIcon(icon != null ? icon : defaultUpIcon);
            
        }
        if (getDownIcon() instanceof UIResource) {
            Icon icon = UIManager.getIcon(DOWN_ICON_KEY);
            setDownIcon(icon != null ? icon : defaultDownIcon);
            
        }
    }
}
