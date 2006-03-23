/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */

package org.jdesktop.swingx;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.action.EditorPaneLinkAction;

import org.jdesktop.swingx.action.LinkAction;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter.UIAlternateRowHighlighter;

/**
 * @author Jeanette Winzenburg
 */
public class JXHyperlinkTest extends InteractiveTestCase {

    public JXHyperlinkTest() {
        super("JXHyperlinkLabel Test");
    }

    public void testDummy() {
        
    }
    
    public void interactiveTestUnderlineButton() {
        Action action = new AbstractAction("LinkModel@somewhere") {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        };
        JXHyperlink hyperlink = new JXHyperlink(action );
        JFrame frame = wrapInFrame(hyperlink, "show underline - no link action");
        frame.setSize(200, 200);
        frame.setVisible(true);
        
    }
    
 
    public void interactiveTestLink() throws Exception {
        JXEditorPane editor = new JXEditorPane();
        EditorPaneLinkAction linkAction = new EditorPaneLinkAction(editor);
        linkAction.setName("Click me!");
        linkAction.setURL(JXEditorPaneTest.class.getResource("resources/test.html"));

        JXHyperlink hyperlink = new JXHyperlink(linkAction);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(editor));
        panel.add(hyperlink, BorderLayout.SOUTH);
        JFrame frame = wrapInFrame(panel, "simple hyperlink");
        frame.setSize(200, 200);
        frame.setVisible(true);
        
    }

  
    public void interactiveTestTableLinkRenderer() {
        JXEditorPane editor = new JXEditorPane();
        JXTable table = new JXTable(createModelWithLinks(editor));
        JFrame frame = wrapWithScrollingInFrame(table, editor, "show link renderer in table");
        frame.setVisible(true);

    }
    
    public void interactiveTestTableLinkRendererEmptyHighlighterPipeline() {
        JXEditorPane editor = new JXEditorPane();
        JXTable table = new JXTable(createModelWithLinks(editor));
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] { }));
        JFrame frame = wrapWithScrollingInFrame(table, editor, 
                "show link renderer in table with empty highlighterPipeline");
        frame.setVisible(true);

    }

    public void interactiveTestTableLinkRendererNullHighlighter() {
        JXEditorPane editor = new JXEditorPane();
        JXTable table = new JXTable(createModelWithLinks(editor));
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] {new Highlighter() }));
        JFrame frame = wrapWithScrollingInFrame(table, editor, 
                "show link renderer in table with null highlighter");
        frame.setVisible(true);

    }

    public void interactiveTestTableLinkRendererLFStripingHighlighter() {
        JXEditorPane editor = new JXEditorPane();
        JXTable table = new JXTable(createModelWithLinks(editor));
        table.setHighlighters(new HighlighterPipeline(new Highlighter[] { 
                new UIAlternateRowHighlighter()}));
        JFrame frame = wrapWithScrollingInFrame(table, editor, 
                "show link renderer in table with LF striping highlighter");
        frame.setVisible(true);

    }
    public void interactiveTestListLinkRenderer() {
        JXEditorPane editor = new JXEditorPane();
        JXList list = new JXList(createListModelWithLinks(editor, 20));
//        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        JFrame frame = wrapWithScrollingInFrame(list, editor, "show link renderer in list");
        frame.setVisible(true);

    }
    
    public void interactiveTestListLinkRendererLFStripingHighlighter() {
        JXEditorPane editor = new JXEditorPane();
        JXList list = new JXList(createListModelWithLinks(editor, 20));
//        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {
                new UIAlternateRowHighlighter()}));
        JFrame frame = wrapWithScrollingInFrame(list, editor, 
                "show link renderer in list with LFStriping highlighter");
        frame.setVisible(true);

    }
    
    public void interactiveTestListLinkRendererEmptyHighlighterPipeline() {
        JXEditorPane editor = new JXEditorPane();
        JXList list = new JXList(createListModelWithLinks(editor, 20));
//        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] { }));
        JFrame frame = wrapWithScrollingInFrame(list, editor, 
                "show link renderer in list empty highlighterPipeline");
        frame.setVisible(true);

    }

    public void interactiveTestListLinkRendererNullHighlighter() {
        JXEditorPane editor = new JXEditorPane();
        JXList list = new JXList(createListModelWithLinks(editor, 20));
//        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setHighlighters(new HighlighterPipeline(new Highlighter[] {new Highlighter() }));
        JFrame frame = wrapWithScrollingInFrame(list, editor, 
                "show link renderer in list null highlighter");
        frame.setVisible(true);

    }

    private ListModel createListModelWithLinks(JXEditorPane editor, int count) {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < count; i++) {
            try {
                EditorPaneLinkAction link = new EditorPaneLinkAction(editor);
                link.setName("a link text " + i);
                link.setURL(new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    URL url = JXEditorPaneTest.class.getResource("resources/test.html");
                    link.setURL(url);
                }
                model.addElement(link);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
 
        return model;
    }
    
    private TableModel createModelWithLinks(JXEditorPane editor) {
        String[] columnNames = { "text only", "Link editable", "Link not-editable", "Bool editable", "Bool not-editable" };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                    return !getColumnName(column).contains("not");
            }
            
        };
        for (int i = 0; i < 4; i++) {
            try {
                EditorPaneLinkAction link = new EditorPaneLinkAction(editor);
                link.setName("a link text " + i);
                link.setURL(new URL("http://some.dummy.url" + i));
                if (i == 1) {
                    link.setURL(JXEditorPaneTest.class.getResource("resources/test.html"));
                }
                model.addRow(new Object[] {"text only " + i, link, link, Boolean.TRUE, Boolean.TRUE });
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return model;
    }

    public static void main(String[] args) throws Exception {
//        setSystemLF(true);
        JXHyperlinkTest test = new JXHyperlinkTest();
        try {
//            test.runInteractiveTests();
            test.runInteractiveTests("interactive.*Table.*");
            test.runInteractiveTests("interactive.*List.*");
          } catch (Exception e) {
              System.err.println("exception when executing interactive tests:");
              e.printStackTrace();
          } 
    }
}
