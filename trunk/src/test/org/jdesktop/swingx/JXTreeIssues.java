/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.plaf.UIResource;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.renderer.MappedValue;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.WrappingIconPanel;
import org.jdesktop.swingx.renderer.WrappingProvider;
import org.jdesktop.swingx.test.XTestUtils;


/**
 * Test to expose known issues of <code>JXTree</code>.
 * <p>
 * 
 * Ideally, there would be at least one failing test method per open issue in
 * the issue tracker. Plus additional failing test methods for not fully
 * specified or not yet decided upon features/behaviour.
 * <p>
 * 
 * If an issue is fixed and the corresponding methods are passing, they
 * should be moved over to the XXTest.
 * 
 * @author Jeanette Winzenburg
 */
public class JXTreeIssues extends JXTreeUnitTest {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(JXTreeIssues.class
            .getName());
    
    public static void main(String[] args) {
      setSystemLF(true);
      JXTreeIssues test = new JXTreeIssues();
      try {
//          test.runInteractiveTests();
//          test.runInteractiveTests("interactive.*RToL.*");
//          test.runInteractiveTests("interactive.*Edit.*");
        test.runInteractiveTests("interactive.*Bold.*");
      } catch (Exception e) {
          System.err.println("exception when executing interactive tests:");
          e.printStackTrace();
      }
  }

    /**
     * Issue #601-swingx: allow LAF to hook in LAF provided renderers.
     * 
     * Unexpected: plain ol' tree doesn't install UIResource?
     */
    public void testLAFRendererTree() {
        JTree tree = new JTree();
        assertNotNull("default renderer installed", tree.getCellRenderer());
        assertTrue("expected UIResource, but was: " + tree.getCellRenderer().getClass(), 
                tree.getCellRenderer() instanceof UIResource);
    }
    
    /**
     * Issue #601-swingx: allow LAF to hook in LAF provided renderers.
     * 
     * Unexpected: plain ol' tree doesn't install UIResource?
     */
    public void testLAFRendererXTree() {
        JXTree tree = new JXTree();
        assertNotNull("default renderer installed", tree.getCellRenderer());
        assertTrue("expected UIResource, but was: " + tree.getCellRenderer().getClass(), 
                tree.getCellRenderer() instanceof UIResource);
    }
    
    /**
     * Size effecting decoration vs. initial config (in provider).
     * 
     * Decoration: use highlighter 
     * - works correctly only with largeModel (due to caching issues)
     * 
     * Config in Provider (usually not recommended): 
     * - override getRendererComponent, width always correct, height only
     *   if enabled via setRowHeight(0)
     *  
     */
    public void interactiveBold() {
        JXTree tree = new JXTree();
        tree.setCellRenderer(new DefaultTreeRenderer());
//        tree.setRowHeight(0);
        tree.setLargeModel(true);
        final Font bold = tree.getFont().deriveFont(Font.BOLD, 20f);
        Highlighter hl = new AbstractHighlighter(HighlightPredicate.IS_LEAF) {

            @Override
            protected Component doHighlight(Component component,
                    ComponentAdapter adapter) {
                component.setFont(bold);
                return component;
            }
        };
        tree.addHighlighter(hl);
        JXTree treeP = new JXTree();
        treeP.setRowHeight(0);
        WrappingProvider provider = new WrappingProvider() {
            
            @Override
            public WrappingIconPanel getRendererComponent(CellContext context) {
                super.getRendererComponent(context);
                if (context.isLeaf()) {
                    rendererComponent.setFont(bold);
                }
                return rendererComponent;
            }

        };
        treeP.setCellRenderer(new DefaultTreeRenderer(provider));
        showWithScrollingInFrame(tree, treeP, "bold font: decorate vs. config");
    }
    
    /**    
     * Issue #242: CCE when setting icons. Not reproducible? 
     * Another issue: icon setting does not repaint (with core default renderer)
     * Does not work at all with SwingX renderer (not surprisingly, the
     * delegating renderer in JXTree looks for a core default to wrap).
     * Think: tree/table should trigger repaint?
     */    
    public void interactiveTreeIcons() {
        final JXTree tree = new JXTree(treeTableModel);
        final Icon downIcon = XTestUtils.loadDefaultIcon("wellbottom.gif");
        final Icon upIcon = XTestUtils.loadDefaultIcon("welltop.gif");
        Action toggleClosedIcon = new AbstractAction("Toggle closed icon") {
            boolean down;
            public void actionPerformed(ActionEvent e) {
                if (down) {
                    tree.setClosedIcon(downIcon);
                } else {
                    tree.setClosedIcon(upIcon);
                }
                down = !down;
                // need to force - but shouldn't that be done in the
                // tree/table itself? and shouldn't the tree fire a 
                // property change?
                tree.repaint();
            }
            
        };
        tree.setRowHeight(22);
        JXFrame frame = wrapWithScrollingInFrame(tree,
                "Toggle Tree icons ");
        addAction(frame, toggleClosedIcon);
        frame.setVisible(true);
    }


    /**  
     * use WrappingProvider: custom icons
     */    
    public void interactiveProviderIcons() {
        final JXTree tree = new JXTree(treeTableModel);
        final Icon downIcon = XTestUtils.loadDefaultIcon("wellbottom.gif");
        final Icon upIcon = XTestUtils.loadDefaultIcon("welltop.gif");
        final StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof File) {
                    return ((File) value).getName();
                }
                return TO_STRING.getString(value);
            }
            
        };
        IconValue iv = new IconValue() {

            public Icon getIcon(Object value) {
                if (sv.getString(value).startsWith("A")) {
                    return downIcon;
                } else if (sv.getString(value).startsWith("D")) {
                    return upIcon;
                }
                return null;
            }
            
        };
        WrappingProvider provider = new WrappingProvider(sv);
        provider.setStringValue(new MappedValue(null, iv));
        tree.setCellRenderer(new DefaultTreeRenderer(provider));
        tree.setRowHeight(22);
        JXFrame frame = wrapWithScrollingInFrame(tree,
                "IconValue on WrappingProvider");
//        addAction(frame, toggleClosedIcon);
        frame.setVisible(true);
    }

    /**  
     * use WrappingProvider: default icons
     */    
    public void interactiveProviderStringValue() {
        final JXTree tree = new JXTree(treeTableModel);
        final StringValue sv = new StringValue() {

            public String getString(Object value) {
                if (value instanceof File) {
                    return ((File) value).getName();
                }
                return TO_STRING.getString(value);
            }
            
        };
        tree.setCellRenderer(new DefaultTreeRenderer(sv));
        tree.setRowHeight(22);
        JXFrame frame = wrapWithScrollingInFrame(tree,
                "Custom StringValue on WrappingProvider, default icons");
//        addAction(frame, toggleClosedIcon);
        frame.setVisible(true);
    }
    
    public void testDummy() {
        // do nothing - it's here let the test pass
    }

}
