/*
 * Created on 28.03.2006
 *
 */
package org.jdesktop.swingx.hyperlink;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import junit.framework.TestCase;

import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.test.PropertyChangeReport;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;


/**
 * 
 * @author Jeanette Winzenburg, Berlin
 */
@RunWith(JUnit4.class)
public class HyperlinkActionTest extends TestCase {

    
    private PropertyChangeReport report;

    @Before
    public void setUpJ4() throws Exception {
        setUp();
    }
    
    @After
    public void tearDownJ4() throws Exception {
        tearDown();
    }
    

    /**
     * test if auto-installed visited property is respected.
     *
     */
    @Test
    public void testConstructorsAndCustomTargetInstall() {
        Object target = new Object();
        final boolean visitedIsTrue = true;
        AbstractHyperlinkAction linkAction = new AbstractHyperlinkAction<Object>(target) {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            protected void installTarget() {
                super.installTarget();
                setVisited(visitedIsTrue);
            }
            
            
            
        };
        assertEquals(visitedIsTrue, linkAction.isVisited());
        
    }
    /**
     * test constructors with parameters
     *
     */
    @Test
    public void testConstructors() {
        Object target = new Object();
        AbstractHyperlinkAction<Object> linkAction = new AbstractHyperlinkAction<Object>(target) {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        };
        assertEquals(target, linkAction.getTarget());
        assertFalse(linkAction.isVisited());
    }
    /**
     * test visited/target properties of LinkAction.
     *
     */
    @Test
    public void testLinkAction() {
       AbstractHyperlinkAction<Object> linkAction = new AbstractHyperlinkAction<Object>(null) {

        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            
        }
           
       };
       linkAction.addPropertyChangeListener(report);
       
       boolean visited = linkAction.isVisited();
       assertFalse(visited);
       linkAction.setVisited(!visited);
       assertEquals(!visited, linkAction.isVisited());
       assertEquals(1, report.getEventCount(AbstractHyperlinkAction.VISITED_KEY));
       
       report.clear();
       // testing target property
       assertNull(linkAction.getTarget());
       Object target = new Object();
       linkAction.setTarget(target);
       assertEquals(target, linkAction.getTarget());
       assertEquals(1, report.getEventCount("target"));
       // testing documented default side-effects of un/installTarget
       assertEquals(target.toString(), linkAction.getName());
       assertFalse(linkAction.isVisited());
       assertEquals(1, report.getEventCount(Action.NAME));
       assertEquals(1, report.getEventCount(AbstractHyperlinkAction.VISITED_KEY));
       // fired the expected events only.
       assertEquals(3, report.getEventCount());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        report = new PropertyChangeReport();
    }

    
}