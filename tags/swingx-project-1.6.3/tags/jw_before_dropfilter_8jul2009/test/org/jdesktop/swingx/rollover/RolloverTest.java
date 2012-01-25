/*
 * Created on 06.10.2005
 *
 */
package org.jdesktop.swingx.rollover;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.InteractiveTestCase;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate.AndHighlightPredicate;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.test.AncientSwingTeam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class RolloverTest extends InteractiveTestCase {

    
    /**
     * @param args
     */
    public static void main(String[] args) {
        RolloverTest test = new RolloverTest();
        try {
            test.runInteractiveTests();
         //   test.runInteractiveTests("interactive.*Rend.*");
          } catch (Exception e) {
              System.err.println("exception when executing interactive tests:");
              e.printStackTrace();
          } 

    }

    private TableModel sortableTableModel;
    private Highlighter backgroundHighlighter;
    private Highlighter foregroundHighlighter;
    private ListModel listModel;
    private FileSystemModel treeTableModel;

    @Test
    public void testDummy() {
        
    }

//---------------------------- interactive tests of rollover effects
    
    public void interactiveTableRollover() {
        JXTable table = new JXTable(sortableTableModel);
        final CompoundHighlighter compoundHighlighter = new CompoundHighlighter(foregroundHighlighter);
        table.setHighlighters(compoundHighlighter);
        JXFrame frame = wrapWithScrollingInFrame(table, "Table with rollover");
        Action toggleAction = new AbstractAction("toggle foreground/background") {
            boolean isBackground;
            public void actionPerformed(ActionEvent e) {
                if (isBackground) {
                    compoundHighlighter.addHighlighter(foregroundHighlighter);
                    compoundHighlighter.removeHighlighter(backgroundHighlighter);
                } else {
                    compoundHighlighter.addHighlighter(backgroundHighlighter);
                    compoundHighlighter.removeHighlighter(foregroundHighlighter);
                    
                }
                isBackground = !isBackground;
                
            }
            
        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }
    
    
    public void interactiveListRollover() {
        final JXList table = new JXList(listModel);
        table.setRolloverEnabled(true);
        final CompoundHighlighter compoundHighlighter = new CompoundHighlighter(foregroundHighlighter);
        table.setHighlighters(compoundHighlighter);
        JXFrame frame = wrapWithScrollingInFrame(table, "List with rollover");
        Action toggleAction = new AbstractAction("toggle foreground/background") {
            boolean isBackground;
            public void actionPerformed(ActionEvent e) {
                if (isBackground) {
                    compoundHighlighter.addHighlighter(foregroundHighlighter);
                    compoundHighlighter.removeHighlighter(backgroundHighlighter);
                } else {
                    compoundHighlighter.addHighlighter(backgroundHighlighter);
                    compoundHighlighter.removeHighlighter(foregroundHighlighter);
                    
                }
                isBackground = !isBackground;
                
            }
            
        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }
    
    public void interactiveTreeRollover() {
        final JXTree table = new JXTree(treeTableModel);
        table.setRolloverEnabled(true);
        table.setComponentPopupMenu(createPopup());
        final CompoundHighlighter compoundHighlighter = new CompoundHighlighter(foregroundHighlighter);
        table.setHighlighters(compoundHighlighter);
        JTree tree = new JTree(treeTableModel);
        tree.setComponentPopupMenu(createPopup());
        JXFrame frame = wrapWithScrollingInFrame(table, tree, "JXTree (at left) with rollover");
        Action toggleAction = new AbstractAction("toggle foreground/background") {
            boolean isBackground;
            public void actionPerformed(ActionEvent e) {
                if (isBackground) {
                    compoundHighlighter.addHighlighter(foregroundHighlighter);
                    compoundHighlighter.removeHighlighter(backgroundHighlighter);
                } else {
                    compoundHighlighter.addHighlighter(backgroundHighlighter);
                    compoundHighlighter.removeHighlighter(foregroundHighlighter);
                    
                }
                isBackground = !isBackground;
                
            }
            
        };
        addAction(frame, toggleAction);
        addMessage(frame, "background highlight not working in JXTree");
        frame.setVisible(true);
    }

    public JPopupMenu createPopup() {
        JPopupMenu popup = new JPopupMenu();
        popup.add("dummy");
        return popup;
    }
    
    public void interactiveTreeTableRollover() {
        final JXTreeTable table = new JXTreeTable(treeTableModel);
        final CompoundHighlighter compoundHighlighter = new CompoundHighlighter(foregroundHighlighter);
        table.setHighlighters(compoundHighlighter);
        JXFrame frame = wrapWithScrollingInFrame(table, "TreeTable with rollover");
        Action toggleAction = new AbstractAction("toggle foreground/background") {
            boolean isBackground;
            public void actionPerformed(ActionEvent e) {
                if (isBackground) {
                    compoundHighlighter.addHighlighter(foregroundHighlighter);
                    compoundHighlighter.removeHighlighter(backgroundHighlighter);
                } else {
                    compoundHighlighter.addHighlighter(backgroundHighlighter);
                    compoundHighlighter.removeHighlighter(foregroundHighlighter);
                    
                }
                isBackground = !isBackground;
                
            }
            
        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }

    /**
     * Example for per-cell rollover decoration in JXTreeTable.
     */
    public void interactiveTreeTableRolloverHierarchical() {
        final JXTreeTable table = new JXTreeTable(treeTableModel);
        HighlightPredicate andPredicate = new AndHighlightPredicate(
                new HighlightPredicate.ColumnHighlightPredicate(0),
                HighlightPredicate.ROLLOVER_ROW
                );
        final Highlighter foregroundHighlighter = new ColorHighlighter(andPredicate, null,
                Color.MAGENTA);
        final Highlighter backgroundHighlighter = new ColorHighlighter(andPredicate, Color.YELLOW,
                null);
        table.setHighlighters(foregroundHighlighter);
        JXFrame frame = wrapWithScrollingInFrame(table, "TreeTable with rollover - effect hierarchical column");
        Action toggleAction = new AbstractAction("toggle foreground/background") {
            boolean isBackground;
            public void actionPerformed(ActionEvent e) {
                if (isBackground) {
                    table.setHighlighters(foregroundHighlighter);
                } else {
                    table.setHighlighters(backgroundHighlighter);
                    
                }
                isBackground = !isBackground;
                
            }
            
        };
        addAction(frame, toggleAction);
        frame.setVisible(true);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sortableTableModel = new AncientSwingTeam();
        listModel = new AbstractListModel() {

            public int getSize() {
                return sortableTableModel.getRowCount();
            }

            public Object getElementAt(int index) {
                return sortableTableModel.getValueAt(index, 0);
            }
            
        };
        treeTableModel = new FileSystemModel();
        foregroundHighlighter = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null,
                Color.MAGENTA);
        backgroundHighlighter = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.YELLOW,
                null);
     }
    

}
