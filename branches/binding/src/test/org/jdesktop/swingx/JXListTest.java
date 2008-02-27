/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.ConditionalHighlighter;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.PatternHighlighter;
import org.jdesktop.swingx.decorator.ShuttleSorter;
import org.jdesktop.swingx.decorator.Sorter;

/**
 * Testing JXList.
 * 
 * @author Jeanette Winzenburg
 */
public class JXListTest extends InteractiveTestCase {

    private ListModel listModel;
    protected DefaultListModel ascendingListModel;

    
    /**
     * test if LinkController/executeButtonAction is properly registered/unregistered on
     * setRolloverEnabled.
     *
     */
    public void testLinkControllerListening() {
        JXList table = new JXList();
        table.setRolloverEnabled(true);
        assertNotNull("LinkController must be listening", getLinkControllerAsPropertyChangeListener(table));
        assertNotNull("execute button action must be registered", table.getActionMap().get(JXList.EXECUTE_BUTTON_ACTIONCOMMAND));
        table.setRolloverEnabled(false);
        assertNull("LinkController must not be listening", getLinkControllerAsPropertyChangeListener(table));
        assertNull("execute button action must be de-registered", table.getActionMap().get(JXList.EXECUTE_BUTTON_ACTIONCOMMAND));
    }

    private PropertyChangeListener getLinkControllerAsPropertyChangeListener(JXList table) {
        PropertyChangeListener[] listeners = table.getPropertyChangeListeners();
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] instanceof JXList.LinkController) {
                return (JXList.LinkController) listeners[i];
            }
        }
        return null;
    }

    public void testConvertToModelPreconditions() {
        final JXList list = new JXList(ascendingListModel);
        // a side-effect of setFilterEnabled is to clear the selection!
        // this is done in JList.setModel(..) which is called when 
        // changing filterEnabled!
        list.setFilterEnabled(true);
        assertEquals(20, list.getElementCount());
        list.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
        assertEquals(2, list.getElementCount());
        try {
            list.convertIndexToModel(list.getElementCount());
            fail("accessing list out of range index must throw execption");
        } catch (IndexOutOfBoundsException ex) {
            // this is correct behaviour
        } catch (Exception ex) {
            fail("got " + ex);
        }
        
    }
 

    public void testElementAtPreconditions() {
        final JXList list = new JXList(ascendingListModel);
        // a side-effect of setFilterEnabled is to clear the selection!
        // this is done in JList.setModel(..) which is called when 
        // changing filterEnabled!
        list.setFilterEnabled(true);
        assertEquals(20, list.getElementCount());
        list.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
        assertEquals(2, list.getElementCount());
        try {
            list.getElementAt(list.getElementCount());
            fail("accessing list out of range index must throw execption");
        } catch (IndexOutOfBoundsException ex) {
            // this is correct behaviour
        } catch (Exception ex) {
            fail("got " + ex);
        }
        
    }
    
    /**
     * 
     * Issue #173-swingx.
     * 
     * table.setFilters() leads to selectionListener
     * notification while internal table state not yet stable.
     * 
     * example (second one, from Nicola):
     * http://www.javadesktop.org/forums/thread.jspa?messageID=117814
     *
     */
    public void testSelectionListenerNotification() {
        final JXList list = new JXList(ascendingListModel);
        // a side-effect of setFilterEnabled is to clear the selection!
        // this is done in JList.setModel(..) which is called when 
        // changing filterEnabled!
        list.setFilterEnabled(true);
        assertEquals(20, list.getElementCount());
        final int modelRow = 0;
        // set a selection 
        list.setSelectedIndex(modelRow);
        ListSelectionListener l = new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                int viewRow = list.getSelectedIndex(); 
                assertEquals("view index visible", 0, viewRow);
                // JW: the following checks if the reverse conversion succeeds
                list.convertIndexToModel(viewRow);
                
            }
            
        };
        list.getSelectionModel().addListSelectionListener(l);
        list.setFilters(new FilterPipeline(new Filter[] {new PatternFilter("0", 0, 0) }));
        assertEquals(2, list.getElementCount());
    }


    /**
     * setFilterEnabled throws NPE if formerly had selection.
     * 
     * Reason is internal state mismanagement... filterEnabled must be
     * set before calling super.setModel!
     *
     */
    public void testSetFilterEnabledWithSelection() {
        final JXList list = new JXList(ascendingListModel);
        // a side-effect of setFilterEnabled is to clear the selection!
        // this is done in JList.setModel(..) which is called when 
        // changing filterEnabled!
        assertEquals(20, list.getElementCount());
        final int modelRow = 0;
        // set a selection 
        list.setSelectedIndex(modelRow);
        list.setFilterEnabled(true);
        
    }

    public void testEmptyFilter() {
        JXList list = new JXList();
        list.setModel(ascendingListModel);
        assertEquals(ascendingListModel.getSize(), list.getElementCount());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
    }
    
    public void testFilterEnabled() {
        JXList list = new JXList();
        list.setFilterEnabled(true);
        list.setModel(ascendingListModel);
        assertNotSame(ascendingListModel, list.getModel());
        assertEquals(ascendingListModel.getSize(), list.getElementCount());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
        
    }

    public void testFilterEnabledAndDisabled() {
        JXList list = new JXList();
        list.setFilterEnabled(true);
        list.setModel(ascendingListModel);
        Sorter sorter = new ShuttleSorter(0, false);
        FilterPipeline pipeline = list.getFilters();
        pipeline.setSorter(sorter);
        list.setFilterEnabled(false);
        assertSame(ascendingListModel, list.getModel());
        assertEquals(ascendingListModel.getSize(), list.getElementCount());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
        
    }
    public void testSortingFilterEnabled() {
        JXList list = new JXList();
        list.setFilterEnabled(true);
        list.setModel(ascendingListModel);
        Sorter sorter = new ShuttleSorter(0, false);
        FilterPipeline pipeline = list.getFilters();
        assertNotNull(pipeline);
        pipeline.setSorter(sorter);
        assertEquals(ascendingListModel.getSize(), list.getElementCount());
        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(list.getElementCount() - 1));
        
    }
    
    public void testSortingKeepsModelSelection() {
        JXList list = new JXList();
        list.setFilterEnabled(true);
        list.setModel(ascendingListModel);
        list.setSelectedIndex(0);
        Sorter sorter = new ShuttleSorter(0, false);
        FilterPipeline pipeline = list.getFilters();
        pipeline.setSorter(sorter);
        assertEquals("last row must be selected after sorting", 
                ascendingListModel.getSize() - 1, list.getSelectedIndex());
    }

    public void testSelectionAfterDeleteAbove() {
        JXList list = new JXList();
        list.setFilterEnabled(true);
        list.setModel(ascendingListModel);
        list.setSelectedIndex(1);
        ascendingListModel.remove(0);
        assertEquals("first row must be selected removing old first", 
                0, list.getSelectedIndex());
        
    }
    
    /**
     * related to #2-swinglabs: clarify behaviour to expect if 
     * filtering disabled?
     *
     */
    public void testSortingFilterDisabled() {
        JXList list = new JXList();
        list.setModel(ascendingListModel);
        Sorter sorter = new ShuttleSorter(0, false);
        FilterPipeline pipeline = list.getFilters();
        // Probably wrong assumption for disabled filtering
//        assertNotNull(pipeline);
//        pipeline.setSorter(sorter);
//        assertSame(ascendingListModel, list.getModel());
//        assertEquals(ascendingListModel.getSize(), list.getModelSize());
//        assertEquals(ascendingListModel.getElementAt(0), list.getElementAt(0));
        
    }

    /**
     * Issue #2-swinglabs: setting filter if not enabled throws exception on selection.
     * Reported by Kim.
     * 
     * Fix: should not accept filter if not enabled.
     *
     */
    public void interactiveTestFilterDisabled() {
        JXList list = new JXList();
        list.setModel(ascendingListModel);
        Filter[] filter = new Filter[] { new PatternFilter("1", 0, 0) };
        list.setFilters(new FilterPipeline(filter));
        JXFrame frame = wrapWithScrollingInFrame(list, "filter disabled");
        frame.setVisible(true);
    }
    
    public void interactiveTestSorter() {
        JXList list = new JXList();
        list.setFilterEnabled(true);
        list.setModel(ascendingListModel);
        final Sorter sorter = new ShuttleSorter(0, false);
        FilterPipeline pipeline = list.getFilters();
        pipeline.setSorter(sorter);
        Action action = new AbstractAction("Toggle Sort Order") {

            public void actionPerformed(ActionEvent e) {
                sorter.setAscending(!sorter.isAscending());
                
            }
            
        };
        JXFrame frame = wrapWithScrollingInFrame(list, "Toggle sorter");
        addAction(frame, action);
        frame.setVisible(true);
        
    }
    
    public void interactiveTestCompareFocusedCellBackground() {
        JXList xlist = new JXList(listModel);
        xlist.setBackground(new Color(0xF5, 0xFF, 0xF5));
        JList list = new JList(listModel);
        list.setBackground(new Color(0xF5, 0xFF, 0xF5));
        JFrame frame = wrapWithScrollingInFrame(xlist, list, "unselectedd focused background: JXList/JList");
        frame.setVisible(true);
    }

    public void interactiveTestTablePatternFilter5() {
        JXList list = new JXList(listModel);
        String pattern = "Row";
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            new PatternHighlighter(null, Color.red, pattern, 0, 1),
        }));
        JFrame frame = wrapWithScrollingInFrame(list, "PatternHighlighter: " + pattern);
        frame.setVisible(true);
    }

    public void interactiveTestTableAlternateHighlighter1() {
        JXList list = new JXList(listModel);
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            AlternateRowHighlighter.
            linePrinter,
        }));

        JFrame frame = wrapWithScrollingInFrame(list, "AlternateRowHighlighter - lineprinter");
        frame.setVisible(true);
    }

    public void interactiveTestRolloverHighlight() {
        JXList list = new JXList(listModel);
    //    table.setLinkVisitor(new EditorPaneLinkVisitor());
        list.setRolloverEnabled(true);
        Highlighter conditional = new ConditionalHighlighter(
                new Color(0xF0, 0xF0, 0xE0), null, -1, -1) {

            protected boolean test(ComponentAdapter adapter) {
                Point p = (Point) adapter.getComponent().getClientProperty(RolloverProducer.ROLLOVER_KEY);
     
                return p != null &&  p.y == adapter.row;
            }
            
        };
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {conditional }));
        JFrame frame = wrapWithScrollingInFrame(list, "rollover highlight");
        frame.setVisible(true);

    }

    /**
     * Issue #20: Highlighters and LinkRenderers don't work together
     *
     */
    public void interactiveTestRolloverHighlightAndLink() {
        JXList list = new JXList(createListModelWithLinks());
        list.setLinkVisitor(new EditorPaneLinkVisitor());
    //    table.setRolloverEnabled(true);
        Highlighter conditional = new ConditionalHighlighter(
                new Color(0xF0, 0xF0, 0xE0), null, -1, -1) {

            protected boolean test(ComponentAdapter adapter) {
                Point p = (Point) adapter.getComponent().getClientProperty(RolloverProducer.ROLLOVER_KEY);
     
                return p != null &&  p.y == adapter.row;
            }
            
        };
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {conditional }));
        JFrame frame = wrapWithScrollingInFrame(list, "rollover highlight with links");
        frame.setVisible(true);

    }
    private ListModel createListModel() {
        JXList list = new JXList();
        return new DefaultComboBoxModel(list.getActionMap().allKeys());
    }

    private DefaultListModel createAscendingListModel(int startRow, int count) {
        DefaultListModel l = new DefaultListModel();
        for (int row = startRow; row < startRow  + count; row++) {
            l.addElement(new Integer(row));
        }
        return l;
    }
    private DefaultListModel createListModelWithLinks() {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < 20; i++) {
            try {
                LinkModel link = new LinkModel("a link text " + i, null, new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");

                    link = new LinkModel("a resource", null, url);
                }
                model.addElement(link);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
 
        return model;
    }

    protected void setUp() throws Exception {
        super.setUp();
        listModel = createListModel();
        ascendingListModel = createAscendingListModel(0, 20);
    }
    public JXListTest() {
        super("JXList Tests");
    }

    
    public static void main(String[] args) {
        setSystemLF(true);
        JXListTest test = new JXListTest();
        try {
         // test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*Column.*");
         //   test.runInteractiveTests("interactive.*TableHeader.*");
         //   test.runInteractiveTests("interactive.*Render.*");
            test.runInteractiveTests("interactive.*Disab.*");
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
}