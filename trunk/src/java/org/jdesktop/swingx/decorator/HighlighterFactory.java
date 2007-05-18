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
import java.util.HashMap;

import javax.swing.UIManager;

import org.jdesktop.swingx.decorator.HighlightPredicate.NotHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.RowGroupHighlightPredicate;

/**
 * A Factory which creates common Highlighters. <p>
 * 
 * PENDING JW: really need the alternate striping? That's how the
 * old AlternateRowHighlighter did it, but feels a bit wrong to 
 * have one stripe hardcoded to WHITE. Would prefer to remove.
 * 
 * @author Jeanette Winzenburg
 */
public final class HighlighterFactory {

    /**
     * Creates and returns a Highlighter which highlights every second row
     * background with a color depending on the LookAndFeel. The rows between
     * are not highlighted, that is typically, they will show the container's
     * background.
     * 
     * @return a Highlighter striping every second row background.
     */
    public static Highlighter createSimpleStriping() {
        ColorHighlighter hl = new UIColorHighlighter(HighlightPredicate.ODD);
        return hl;
    }
    
    /**
     * Creates and returns a Highlighter which highlights every second row group
     * background with a color depending on LF. The row groups between are not
     * highlighted, that is typically, they will show the container's
     * background.
     * 
     * @param rowsPerGroup the number of rows in a group
     * @return a Highlighter striping every second row group background.
     */
    public static Highlighter createSimpleStriping(int rowsPerGroup) {
        return new UIColorHighlighter(new RowGroupHighlightPredicate(
                rowsPerGroup));
    }

    /**
     * Creates and returns a Highlighter which highlights every second row
     * background with the given color. The rows between are not highlighted
     * that is typically, they will show the container's background.
     * 
     * @param stripeBackground the background color for the striping.
     * @return a Highlighter striping every second row background. 
     */
    public static Highlighter createSimpleStriping(Color stripeBackground) {
        ColorHighlighter hl = new ColorHighlighter(stripeBackground, null, HighlightPredicate.ODD);
        return hl;
    }
    
    /**
     * Creates and returns a Highlighter which highlights every second row group
     * background with the given color. The row groups between are not
     * highlighted, that is they typically will show the container's background.
     * 
     * @param stripeBackground the background color for the striping.
     * @param rowsPerGroup the number of rows in a group
     * @return a Highlighter striping every second row group background.
     */
    public static Highlighter createSimpleStriping(Color stripeBackground,
            int rowsPerGroup) {
        HighlightPredicate predicate = new RowGroupHighlightPredicate(
                rowsPerGroup);
        ColorHighlighter hl = new ColorHighlighter(stripeBackground, null,
                predicate);
        return hl;
    }

    /**
     * Creates and returns a Highlighter which highlights 
     * with alternate background. The first is Color.WHITE, the second
     * with the color depending on LF. 
     * 
     * @return a Highlighter striping every second row background. 
     */
    public static Highlighter createAlternateStriping() {
        ColorHighlighter first = new ColorHighlighter(Color.WHITE, null, HighlightPredicate.EVEN);
        ColorHighlighter hl = new UIColorHighlighter(HighlightPredicate.ODD);
        return new CompoundHighlighter(first, hl);
    }

    /**
     * Creates and returns a Highlighter which highlights 
     * with alternate background. the first Color.WHITE, the second
     * with the color depending on LF. 
     * 
     * @param rowsPerGroup the number of rows in a group
     * @return a Highlighter striping every second row group background.
     */
    public static Highlighter createAlternateStriping(int rowsPerGroup) {
        HighlightPredicate predicate = new RowGroupHighlightPredicate(rowsPerGroup);
        ColorHighlighter first = new ColorHighlighter(Color.WHITE, null, new NotHighlightPredicate(predicate));
        ColorHighlighter hl = new UIColorHighlighter(predicate);
        return new CompoundHighlighter(first, hl);
    }
    
    /**
     * Creates and returns a Highlighter which highlights with
     * alternating background, starting with the base.
     * 
     * @param baseBackground the background color for the even rows.
     * @param alternateBackground background color for odd rows.
     * @return a Highlighter striping alternating background. 
     */
    public static Highlighter createAlternateStriping(Color baseBackground, Color alternateBackground) {
        ColorHighlighter base = new ColorHighlighter(baseBackground, null, HighlightPredicate.EVEN);
        ColorHighlighter alternate = new ColorHighlighter(alternateBackground, null, HighlightPredicate.ODD);
        return new CompoundHighlighter(base, alternate);
    }

    /**
     * Creates and returns a Highlighter which highlights with
     * alternating background, starting with the base.
     * 
     * @param baseBackground the background color for the even rows.
     * @param alternateBackground background color for odd rows.
     * @param rowsPerGroup the number of rows in a group
     * @return a Highlighter striping every second row group background. 
     */
    public static Highlighter createAlternateStriping(Color baseBackground, Color alternateBackground, int linesPerStripe) {
        HighlightPredicate predicate = new RowGroupHighlightPredicate(linesPerStripe);
        ColorHighlighter base = new ColorHighlighter(baseBackground, null, new NotHighlightPredicate(predicate));
        ColorHighlighter alternate = new ColorHighlighter(alternateBackground, null, predicate);
        
        return new CompoundHighlighter(base, alternate);
    }
 
//--------------------------- UI dependent
    
    /**
     * A ColorHighlighter with UI-dependent background.
     * 
     * PENDING: move color lookup into UI!
     */
    public static class UIColorHighlighter extends ColorHighlighter 
        implements UIDependent {

     private HashMap<Color, Color> colorMap;
     
     /**
      * Instantiates a ColorHighlighter with LF provided unselected
      * background and default predicate. All other colors are null.
      *
      */
     public UIColorHighlighter() {
         this(null);
     }
     

     /**
      * Instantiates a ColorHighlighter with LF provided unselected
      * background and the given predicate. All other colors are null.
     * @param odd the predicate to use
     */
    public UIColorHighlighter(HighlightPredicate odd) {
        super(null, null, odd);
        initColorMap();
        updateUI();
    }


    /**
     * @inheritDoc
     */
    public void updateUI() {
         
         Color selection = UIManager.getColor("Table.selectionBackground");
         Color highlight = getMappedColor(selection);
         
         setBackground(highlight);
     }

     private Color getMappedColor(Color selection) {
         Color color = colorMap.get(selection);
         if (color == null) {
             color = HighlighterFactory.GENERIC_GRAY;
         }
         return color;
     }
     /** 
      * this is a hack until we can think about something better!
      * we map all known selection colors to highlighter colors.
      *
      */
     private void initColorMap() {
         colorMap = new HashMap<Color, Color>();
         // Ocean
         colorMap.put(new Color(184, 207, 229), new Color(230, 238, 246));
         // xp blue
         colorMap.put(new Color(49, 106, 197), new Color(224, 233, 246));
         // xp silver
         colorMap.put(new Color(178, 180, 191), new Color(235, 235, 236));
         // xp olive
         colorMap.put(new Color(147, 160, 112), new Color(228, 231, 219));
         // win classic
         colorMap.put(new Color(10, 36, 106), new Color(218, 222, 233));
         // win 2k?
         colorMap.put(new Color(0, 0, 128), new Color(218, 222, 233));
         // default metal
         colorMap.put(new Color(205, 205, 255), new Color(235, 235, 255));
         // mac OS X
         colorMap.put(new Color(56, 117, 215), new Color(237, 243, 254));
         
     }
     
 }

    /** predefined colors - from old alternateRow. */
    public final static Color BEIGE = new Color(245, 245, 220);
    public final static Color LINE_PRINTER = new Color(0xCC, 0xCC, 0xFF);
    public final static Color CLASSIC_LINE_PRINTER = new Color(0xCC, 0xFF, 0xCC);
    public final static Color FLORAL_WHITE = new Color(255, 250, 240);
    public final static Color QUICKSILVER = new Color(0xF0, 0xF0, 0xE0);
    public final static Color GENERIC_GRAY = new Color(229, 229, 229);
    public final static Color LEDGER = new Color(0xF5, 0xFF, 0xF5);
    public final static Color NOTEPAD = new Color(0xFF, 0xFF, 0xCC);

}
