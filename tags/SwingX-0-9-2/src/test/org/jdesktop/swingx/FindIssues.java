/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.SearchPredicate;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.test.AncientSwingTeam;


/**
 * Exposing open issues in Searchable implementations.
 * 
 * @author Jeanette Winzenburg
 */
public class FindIssues extends FindTest {

    public static void main(String args[]) {
        setSystemLF(true);
//        Locale.setDefault(new Locale("es"));
        FindIssues test = new FindIssues();
        try {
//          test.runInteractiveTests();
            test.runInteractiveTests("interactive.*Mark.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    

    /**
     * Issue #487-swingx: NPE if setting a not-null Searchable before 
     * showing. Hack around ...
     */
    public void testFindBarNPEComponents() {
        Searchable searchable = new JXTable().getSearchable();
        JXFindBar findBar = new JXFindBar();
        // old hack
//        findBar.addNotify();
        findBar.setSearchable(searchable);
        fail("quick hack ... remove me if really fixed");
    }


    /**
     * Issue #236-swingx: backwards match in first row shows not-found-message.
     * Trackdown from Nicfagn - findPanel.doSearch always returns the next startIndex
     * in backwards search that's -1 which is interpreted as "not-found"
     * 
     */
    public void testFindPanelFirstRowBackwards() {
        JXList list = new JXList( new AbstractListModel() {
            private String[] data = { "a", "b", "c" };
            public Object getElementAt(int index) {
                return data[ index ];
            }
            public int getSize() {
                return data.length;
            }
        });
        JXFindPanel findPanel = new JXFindPanel(list.getSearchable());
        findPanel.init();
        PatternModel patternModel = findPanel.getPatternModel();
        patternModel.setBackwards(true);
        patternModel.setRawText("a");
        int matchIndex = list.getSearchable().search(patternModel.getPattern(),
                patternModel.getFoundIndex(), patternModel.isBackwards());
        assertEquals("found match", matchIndex, findPanel.doSearch());
    }

    // -------------------- interactive tests

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on JXTable.
     */
    public void testGetStringUsedInSearch() {
        JXTable table = new JXTable(new AncientSwingTeam());
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return TO_STRING.getString(value);
            }
            
        };
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        String text = sv.getString(table.getValueAt(2, 2));
        int matchRow = table.getSearchable().search(text);
        assertEquals(2, matchRow);
    }

    /**
     * Issue #767-swingx: consistent string representation.
     * 
     * Here: test api on JXTable.
     */
    public void testGetStringUsedInSearchPredicate() {
        JXTable table = new JXTable(new AncientSwingTeam());
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return TO_STRING.getString(value);
            }
            
        };
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        table.putClientProperty(AbstractSearchable.MATCH_HIGHLIGHTER, Boolean.TRUE);
        String text = sv.getString(table.getValueAt(2, 2));
        int matchRow = table.getSearchable().search(text);
        Highlighter[] highlighters = table.getHighlighters();
        ColorHighlighter last = (ColorHighlighter) highlighters[highlighters.length - 1];
        assertEquals("sanity - found match", 2, matchRow);
        assertTrue("sanity - use searchPredicate", last.getHighlightPredicate() instanceof SearchPredicate);
        Component comp = table.prepareRenderer(table.getCellRenderer(2, 2), 2, 2);
        assertEquals(last.getBackground(), comp.getBackground());
    }
    
    public void interactiveGetStringUsedInFind() {
        JXTable table = new JXTable(new AncientSwingTeam());
        StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof Color) {
                    Color color = (Color) value;
                    return "R/G/B: " + color.getRGB();
                }
                return TO_STRING.getString(value);
            }
            
        };
        table.setDefaultRenderer(Color.class, new DefaultTableRenderer(sv));
        table.setColumnControlVisible(true);
        Action action = new AbstractAction("toggle batch/incremental"){
            boolean useFindBar;
            public void actionPerformed(ActionEvent e) {
                useFindBar = !useFindBar;
                SearchFactory.getInstance().setUseFindBar(useFindBar);
            }
            
        };
        
        JXFrame frame = wrapWithScrollingInFrame(table, "Batch/Incremental Search");
        addAction(frame, action);
        addMessage(frame, "Press ctrl-F to open search widget");
        show(frame);
    }

    
    public void interactiveTableMarkAllMatches() {
        JXTable table = new XXTable();
        table.setModel(new TestTableModel());
        SearchFactory.getInstance().setUseFindBar(true);
        showWithScrollingInFrame(table, "quick sample for mark all matches");
    }

    public static class XXTable extends JXTable {

        @Override
        public Searchable getSearchable() {
            if (searchable == null) {
                searchable = new XTableSearchable();
            }
            return searchable;
        }

        public class XTableSearchable extends TableSearchable {

            
            @Override
            protected AbstractHighlighter getConfiguredMatchHighlighter() {
                CompoundHighlighter searchHL = (CompoundHighlighter) getMatchHighlighter();
                if (!hasMatch(lastSearchResult)) {
                    searchHL.setHighlightPredicate(HighlightPredicate.NEVER);
                } else {
                    searchHL.setHighlightPredicate(new SearchPredicate(lastSearchResult.getPattern()));
                    ((AbstractHighlighter) searchHL.getHighlighters()[1]).setHighlightPredicate(
                            new SearchPredicate(lastSearchResult.getPattern(), 
                                    lastSearchResult.getFoundRow(), lastSearchResult.getFoundColumn()));
                }
                return searchHL;
            }

            @Override
            protected AbstractHighlighter createMatchHighlighter() {
                ColorHighlighter base = new ColorHighlighter(Color.YELLOW.brighter(), null, 
                        Color.YELLOW.darker(), null);
                ColorHighlighter cell = new ColorHighlighter(Color.YELLOW.darker(), null);
                CompoundHighlighter match = new CompoundHighlighter(base, cell);
//                match.setHighlightPredicate(HighlightPredicate.NEVER);
                return match;
            }

            
            
//            protected SearchHighlighter createSearchHighlighter() {
//                SearchHighlighter highlighter = new SearchHighlighter() {
//                    int currentViewRow;
//
//                    int currentModelColumn;
//
//                    /**
//                     * Overridden to always mark all.
//                     */
//                    @Override
//                    public void setHighlightCell(int row, int modelColumn) {
//                        currentViewRow = row;
//                        currentModelColumn = modelColumn;
//                        super.setHighlightCell(-1, -1);
//                    }
//
//                    @Override
//                    protected Color computeUnselectedBackground(
//                            Component renderer, ComponentAdapter adapter) {
//                        Color color = super.computeUnselectedBackground(
//                                renderer, adapter);
//                        if ((adapter.row == currentViewRow)
//                                && (adapter.column >= 0)
//                                && (adapter.viewToModel(adapter.column) == currentModelColumn)) {
//                            return color.darker();
//                        }
//                        return color;
//                    }
//
//                };
//                return highlighter;
//            }
//
        }
    }
    /**
     * #463-swingx: batch find and cellSelection don't play nicely.
     *
     */
    public void interactiveTableBatchWithCellSelectionIssue() {
        JXTable table = new JXTable(new TestTableModel());
        table.setCellSelectionEnabled(true);
        showWithScrollingInFrame(table, "batch find with cell selection");
    }

}